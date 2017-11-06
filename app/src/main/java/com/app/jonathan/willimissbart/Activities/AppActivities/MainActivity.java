package com.app.jonathan.willimissbart.Activities.AppActivities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.app.jonathan.willimissbart.API.Models.BSA.Bsa;
import com.app.jonathan.willimissbart.API.Models.BSA.BsaResp;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Adapters.ViewPagerAdapter;
import com.app.jonathan.willimissbart.Fragments.RouteFragment;
import com.app.jonathan.willimissbart.Fragments.StationsFragment;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.PopUpWindows.NotificationPopUpWindow;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.StationInfoViewHolder;
import com.google.common.collect.Lists;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.drawer_layout) CoordinatorLayout parent;
    @Bind(R.id.stn_info_parent) ScrollView stationInfoLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tabs) TabLayout tabs;
    @Bind(R.id.view_pager) ViewPager viewPager;

    private StationInfoViewHolder stationInfoViewHolder;
    private View notifIcon;
    private View redCircle;

    private RouteFragment routeFragment = new RouteFragment();
    // private DeparturesFragment departuresFragment = new DeparturesFragment();
    private StationsFragment stationsFragment = new StationsFragment();
    private List<Bsa> bsas = Lists.newArrayList();
    protected final Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);

        stationInfoViewHolder = new StationInfoViewHolder(stationInfoLayout,
            Utils.getStationInfoLayoutHeight(this));
        NotificationPopUpWindow.isChecked = SPSingleton.getString(this, Constants.MUTE_NOTIF)
            .equals(NotificationPopUpWindow.dateFormat.format(new Date()));
        Utils.loadStations(SPSingleton.getInstance(getApplicationContext()).getPersistedStations());

        setUpViewPager(getIntent().getExtras());
        tabs.setupWithViewPager(viewPager);
        RetrofitClient.getBsas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (stationInfoLayout.getVisibility() == View.VISIBLE) {
            stationInfoViewHolder.onClose();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        setUpNotifIcon(menu);
        menu.findItem(R.id.map).setIcon(new IconDrawable(this, IoniconsIcons.ion_map)
            .colorRes(R.color.white)
            .actionBarSize());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case StationsFragment.PERMISSIONS_CODE:
                if (permissions.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    stationsFragment.loadStation();
                }
        }
    }

    @SuppressWarnings("unchecked")
    @Subscribe
    public void onBsaResponse(BsaResp bsaResp) {
        if (bsaResp.getRoot().getBsaList().size() > 1 ||
            !bsaResp.getRoot().getBsaList().get(0).getStation().isEmpty()) {
            if (!NotificationPopUpWindow.isChecked) {
                redCircle.setVisibility(View.VISIBLE);
            }
        }

        bsas = bsaResp.getRoot().getBsaList();
    }

    public StationInfoViewHolder getStationInfoViewHolder() {
        return stationInfoViewHolder;
    }

    private void setUpViewPager(Bundle bundle) {
        ArrayList<String> titles = new ArrayList<String>(Arrays.asList(getResources()
            .getStringArray(R.array.tab_headers)));

        ArrayList<Fragment> fragments = new ArrayList<>();
        routeFragment.setArguments(bundle);
        // departuresFragment.setArguments(bundle);
        fragments.add(routeFragment);
        // fragments.add(departuresFragment);
        fragments.add(stationsFragment);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager())
            .setTitles(titles)
            .setFragments(fragments);

        // TODO: Butterknife this (again)
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Utils.hideKeyboard(context);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    private void setUpNotifIcon(Menu menu) {
        MenuItem notifItem = menu.findItem(R.id.notifications);
        notifItem.setActionView(R.layout.layout_notif);
        notifIcon = MenuItemCompat.getActionView(notifItem);
        notifIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] pos = new int[2];
                notifIcon.getLocationOnScreen(pos);
                NotificationPopUpWindow popUpWindow =
                    new NotificationPopUpWindow(v.getContext(), bsas);
                redCircle.setVisibility(View.INVISIBLE);
                popUpWindow.showAtLocation(parent, Gravity.NO_GRAVITY,
                    pos[0] - notifIcon.getWidth(), pos[1] + notifIcon.getHeight() + 10);
            }
        });
        redCircle = notifIcon.findViewById(R.id.notif_circle);
    }
}
