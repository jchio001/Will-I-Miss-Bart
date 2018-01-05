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
import com.app.jonathan.willimissbart.Fragments.RoutesFragment;
import com.app.jonathan.willimissbart.Fragments.StationsFragment;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.SPManager;
import com.app.jonathan.willimissbart.PopUpWindows.NotificationWindowManager;
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
import butterknife.OnPageChange;
import butterknife.OnPageChange.Callback;

public class MainActivity extends AppCompatActivity {
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
        NotificationWindowManager.isChecked = SPManager.getString(this, Constants.MUTE_NOTIF)
            .equals(NotificationWindowManager.dateFormat.format(new Date()));
        Utils.loadStations(SPManager.getPersistedStations(this));

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
        Utils.hideKeyboard(context);
    }

    @SuppressWarnings("unchecked")
    @Subscribe
    public void onBsaResponse(BsaResp bsaResp) {
        if (bsaResp.getRoot().getBsaList().size() > 1 ||
            !bsaResp.getRoot().getBsaList().get(0).getStation().isEmpty()) {
            if (!NotificationWindowManager.isChecked) {
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
                    new NotificationWindowManager(v.getContext(), bsas);
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
}
