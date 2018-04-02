package com.app.jonathan.willimissbart.activity.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.api.Models.BSA.Bsa;
import com.app.jonathan.willimissbart.api.Models.Generic.CDataSection;
import com.app.jonathan.willimissbart.api.RetrofitClient;
import com.app.jonathan.willimissbart.adapter.ViewPagerAdapter;
import com.app.jonathan.willimissbart.fragment.RoutesFragment;
import com.app.jonathan.willimissbart.fragment.StationsFragment;
import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.misc.NotGuava;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.persistence.SPManager;
import com.app.jonathan.willimissbart.window.NotificationWindowManager;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.viewholder.StationInfoViewHolder;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
import butterknife.OnPageChange.Callback;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int SHOW_DIALOG_THRESHOLD = 6;

    @Bind(R.id.drawer_layout) CoordinatorLayout parent;
    @Bind(R.id.stn_info_parent) ScrollView stationInfoLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tabs) TabLayout tabs;
    @Bind(R.id.view_pager) ViewPager viewPager;

    private StationInfoViewHolder stationInfoViewHolder;
    private View notifIcon;
    private View redCircle;

    private RoutesFragment routeFragment = new RoutesFragment();
    private StationsFragment stationsFragment = new StationsFragment();
    private List<Bsa> announcements = NotGuava.newArrayList();

    private Disposable disposable;

    private final SingleObserver<List<Bsa>> bsaObserver = new SingleObserver<List<Bsa>>() {
        @Override
        public void onSubscribe(Disposable d) {
            MainActivity.this.disposable = d;
        }

        @Override
        public void onSuccess(List<Bsa> announcements) {
            if (announcements.size() >= 1 &&
                !announcements.get(0).getStation().isEmpty()) {
                if (!NotificationWindowManager.isChecked) {
                    redCircle.setVisibility(View.VISIBLE);
                }
            }

            MainActivity.this.announcements = announcements;
        }

        @Override
        public void onError(Throwable e) {
            MainActivity.this.announcements = NotGuava.newArrayList(getFailureBsa());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        stationInfoViewHolder = new StationInfoViewHolder(stationInfoLayout,
            Utils.getStationInfoLayoutHeight(this));
        NotificationWindowManager.isChecked = SPManager.fetchString(this, Constants.MUTE_NOTIF)
            .equals(NotificationWindowManager.dateFormat.format(new Date()));

        setUpViewPager(getIntent().getExtras());
        tabs.setupWithViewPager(viewPager);

        // Rather than parse the stations and then get the announcements, I can just save some time
        // and just do both at the same time and just zip the result.
        Single.zip(SPManager.fetchStations(this),
            fetchAnnouncements(), (stations, bsa) -> bsa)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(bsaObserver);

        if (SPManager.incrementUsageCounter(this) == SHOW_DIALOG_THRESHOLD) {
            createPleaseRateDialog().show();
        }
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (stationInfoLayout.getVisibility() == View.VISIBLE) {
            stationInfoViewHolder.close();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        setUpMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.map) {
            startActivity(new Intent(this, MapActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.UPDATING_STATIONS && data != null) {
            routeFragment.updateUserStations(resultCode,
                data.getIntExtra(Constants.STATION_INDEX, -1));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case StationsFragment.PERMISSIONS_CODE:
                if (permissions.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    stationsFragment.loadStation();
                }
        }
    }

    @OnPageChange(value = R.id.view_pager, callback = Callback.PAGE_SELECTED)
    public void onPageChanged() {
        Utils.hideKeyboard(this);
    }

    // TODO: handle failed BSA call
    public Single<List<Bsa>> fetchAnnouncements() {
         return RetrofitClient.getBsas()
            .onErrorReturnItem(Response.success(null))
            .flatMap(bsaResp -> {
                if (bsaResp != null && bsaResp.body() != null) {
                    return Single.just(bsaResp.body().getRoot().getBsaList());
                } else {
                    return Single.just(NotGuava.newArrayList(getFailureBsa()));
                }
            })
            .observeOn(AndroidSchedulers.mainThread());
    }

    public StationInfoViewHolder getStationInfoViewHolder() {
        return stationInfoViewHolder;
    }

    private void setUpViewPager(Bundle bundle) {
        ArrayList<String> titles = new ArrayList<String>(Arrays.asList(getResources()
            .getStringArray(R.array.tab_headers)));

        ArrayList<Fragment> fragments = new ArrayList<>();
        routeFragment.setArguments(bundle);
        fragments.add(routeFragment);
        fragments.add(stationsFragment);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager())
            .setTitles(titles)
            .setFragments(fragments);

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    private void setUpMenu(Menu menu) {
        MenuItem notifItem = menu.findItem(R.id.notifications);
        notifItem.setActionView(R.layout.red_circle_notif_icon);
        notifIcon = MenuItemCompat.getActionView(notifItem);
        notifIcon.setOnClickListener(new View.OnClickListener() {
            private void hideNotificationCircle(int[] pos) {
                notifIcon.getLocationOnScreen(pos);
                redCircle.setVisibility(View.INVISIBLE);
            }

            private void showBSAWindow(View v, int[] pos) {
                NotificationWindowManager popUpWindow =
                    new NotificationWindowManager(v.getContext(), announcements);
                popUpWindow.showAtLocation(parent, Gravity.NO_GRAVITY,
                    pos[0] - notifIcon.getWidth(), pos[1] + notifIcon.getHeight() + 10);
            }

            @Override
            public void onClick(View v) {
                int[] pos = new int[2];
                hideNotificationCircle(pos);
                showBSAWindow(v, pos);
            }
        });
        redCircle = notifIcon.findViewById(R.id.notif_circle);

        menu.findItem(R.id.map).setIcon(new IconDrawable(this, IoniconsIcons.ion_map)
            .colorRes(R.color.white)
            .actionBarSize());
    }

    private Bsa getFailureBsa() {
        return new Bsa()
            .setStation("")
            .setDescription(new CDataSection()
                .setcDataSection(MainActivity.this.getString(R.string.failed_announcement_req)));
    }

    private Dialog createPleaseRateDialog() {
        return new AlertDialog.Builder(this)
            .setMessage(R.string.can_has_rating)
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                Uri uri =  Uri.parse("market://details?id="
                    + getApplicationContext().getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                    Toast.makeText(this, R.string.play_store_error, Toast.LENGTH_SHORT)
                        .show();
                    return;
                }

                startActivity(intent);
            })
            .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
            .create();
    }
}
