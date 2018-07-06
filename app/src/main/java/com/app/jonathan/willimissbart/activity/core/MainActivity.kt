package com.app.jonathan.willimissbart.activity.core

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnPageChange
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.adapter.ViewPagerAdapter
import com.app.jonathan.willimissbart.api.Models.BSA.Bsa
import com.app.jonathan.willimissbart.api.Models.Generic.CDataSection
import com.app.jonathan.willimissbart.api.Models.Station.Station
import com.app.jonathan.willimissbart.api.RetrofitClient
import com.app.jonathan.willimissbart.fragment.RoutesFragment
import com.app.jonathan.willimissbart.fragment.StationsFragment
import com.app.jonathan.willimissbart.misc.Constants
import com.app.jonathan.willimissbart.misc.EstimatesManager
import com.app.jonathan.willimissbart.misc.NotGuava
import com.app.jonathan.willimissbart.misc.Utils
import com.app.jonathan.willimissbart.persistence.SPManager
import com.app.jonathan.willimissbart.viewholder.StationInfoViewHolder
import com.app.jonathan.willimissbart.window.NotificationWindowManager
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.IoniconsIcons
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import retrofit2.Response
import java.util.*

const val SHOW_DIALOG_THRESHOLD = 6

class MainActivity : AppCompatActivity() {

    @Bind(R.id.drawer_layout) lateinit var parent : CoordinatorLayout
    @Bind(R.id.stn_info_parent) lateinit var stationInfoLayout : ScrollView
    @Bind(R.id.toolbar) lateinit var toolbar : Toolbar
    @Bind(R.id.tabs) lateinit var tabs : TabLayout
    @Bind(R.id.view_pager) lateinit var viewPager : ViewPager

    public lateinit var stationInfoViewHolder : StationInfoViewHolder
    protected lateinit var notifIcon : View
    protected lateinit var redCircle : View

    private var routesFragment = RoutesFragment()
    private var stationsFragment = StationsFragment()

    private lateinit var spManager : SPManager

    protected var announcements = NotGuava.newArrayList<Bsa>()

    protected var disposable : Disposable? = null;

    private val bsaObserver = (object:SingleObserver<List<Bsa>> {
        override fun onSubscribe(d: Disposable) {
            this@MainActivity.disposable = d
        }

        override fun onSuccess(t: List<Bsa>) {

        }

        override fun onError(e: Throwable) {
            this@MainActivity.announcements = getFailureBsa()
        }
    })

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        spManager = SPManager(this)

        stationInfoViewHolder = StationInfoViewHolder(stationInfoLayout,
                Utils.getStationInfoLayoutHeight(this))
        NotificationWindowManager.isChecked =
                spManager
                        .fetchString(Constants.MUTE_NOTIF)
                        .equals(NotificationWindowManager.dateFormat.format(Date()))

        setUpViewPager(intent.extras)
        tabs.setupWithViewPager(viewPager)

        // Rather than parse the stations and then get the announcements, I can just save some time
        // and just do both at the same time and just zip the result.
        Single.zip<List<Station>, List<Bsa>, List<Bsa>>(spManager.fetchStations(this),
                fetchAnnouncements(), BiFunction { _, resp ->  resp  } )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith<SingleObserver<List<Bsa>>>(bsaObserver)

        if (spManager.incrementUsageCounter() == SHOW_DIALOG_THRESHOLD) {
            createPleaseRateDialog().show();
        }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    override fun onBackPressed() {
       if (stationInfoLayout.visibility == View.VISIBLE) {
           stationInfoViewHolder.close()
       } else {
           super.onBackPressed()
       }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu,menu)
        setUpMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.map) {
            startActivity(Intent(this, MapActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.UPDATING_STATIONS && data != null) {
            routesFragment.updateUserStations(resultCode,
                    data.getIntExtra(Constants.STATION_INDEX, -1))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            StationsFragment.PERMISSIONS_CODE ->
                if (!permissions.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    stationsFragment.loadStation()
                }
        }
    }

    @OnPageChange(value = R.id.view_pager, callback = OnPageChange.Callback.PAGE_SELECTED)
    fun onPageChanged() {
        Utils.hideKeyboard(this)
    }

    private fun fetchAnnouncements(): Single<List<Bsa>> {
        return  RetrofitClient.getBsas()
                .onErrorReturnItem(Response.success(null))
                .flatMap { bsaResp ->
                    if (bsaResp.body() != null) {
                        return@flatMap Single.just(bsaResp.body()?.root?.bsaList!!)
                    } else
                        return@flatMap Single.just(getFailureBsa())
                }
                .observeOn(AndroidSchedulers.mainThread());
    }

    private fun setUpViewPager(bundle: Bundle?) {
        val titles = ArrayList(Arrays.asList(*resources
                .getStringArray(R.array.tab_headers)))

        val fragments = NotGuava.newArrayList<Fragment>(routesFragment, stationsFragment)
        routesFragment.setArguments(bundle)

        val adapter = ViewPagerAdapter(supportFragmentManager)
                .setTitles(titles)
                .setFragments(fragments)

        viewPager.offscreenPageLimit = 2
        viewPager.adapter = adapter
    }

    private fun setUpMenu(menu: Menu) {
        val notifItem = menu.findItem(R.id.notifications)
        notifItem.setActionView(R.layout.red_circle_notif_icon)
        notifIcon = MenuItemCompat.getActionView(notifItem)
        notifIcon.setOnClickListener(object : View.OnClickListener {
            private fun hideNotificationCircle(pos: IntArray) {
                notifIcon.getLocationOnScreen(pos)
                redCircle.visibility = View.INVISIBLE
            }

            private fun showBSAWindow(v: View, pos: IntArray) {
                val popUpWindow = NotificationWindowManager(v.context, announcements)
                popUpWindow.showAtLocation(parent, Gravity.NO_GRAVITY,
                        pos[0] - notifIcon.width, pos[1] + notifIcon.height + 10)
            }

            override fun onClick(v: View) {
                val pos = IntArray(2)
                hideNotificationCircle(pos)
                showBSAWindow(v, pos)
            }
        })
        redCircle = notifIcon.findViewById(R.id.notif_circle)

        menu.findItem(R.id.map).icon = IconDrawable(this, IoniconsIcons.ion_map)
                .colorRes(R.color.white)
                .actionBarSize()
    }

    private fun getFailureBsa(): ArrayList<Bsa> {
        return NotGuava.newArrayList(Bsa()
                .setStation("")
                .setDescription(CDataSection()
                        .setcDataSection(getString(R.string.failed_announcement_req))))
    }

    private fun createPleaseRateDialog(): Dialog {
        return AlertDialog.Builder(this)
                .setMessage(R.string.can_has_rating)
                .setPositiveButton(R.string.yes) { _, _ ->
                    val uri = Uri.parse("market://details?id="
                            + applicationContext.packageName)
                    val intent = Intent(Intent.ACTION_VIEW, uri)

                    if (packageManager.queryIntentActivities(intent, 0).size <= 0) {
                        Toast.makeText(this, R.string.play_store_error, Toast.LENGTH_SHORT)
                                .show()
                        return@setPositiveButton
                    }

                    startActivity(intent)
                }
                .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                .create()
    }
}