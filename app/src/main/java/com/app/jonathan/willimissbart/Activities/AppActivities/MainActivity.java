package com.app.jonathan.willimissbart.Activities.AppActivities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

import com.app.jonathan.willimissbart.API.Models.BSAModels.Bsa;
import com.app.jonathan.willimissbart.API.Models.BSAModels.BsaResp;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Adapters.ViewPagerAdapter;
import com.app.jonathan.willimissbart.Fragments.DeparturesFragment;
import com.app.jonathan.willimissbart.Fragments.MyStationsFragment;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.PopUpWindows.NotificationPopUpWindow;
import com.app.jonathan.willimissbart.R;
import com.google.common.collect.Lists;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.drawer_layout) CoordinatorLayout parent;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tabs) TabLayout tabs;
    @Bind(R.id.view_pager) ViewPager viewPager;

    private View notifIcon;
    private View redCircle;

    private DeparturesFragment departuresFragment = new DeparturesFragment();
    private MyStationsFragment myStationsFragment = new MyStationsFragment();
    private List<Bsa> bsas = Lists.newArrayList();

    protected final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);

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
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        setUpNotifIcon(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    @Subscribe
    public void onBsaResponse(BsaResp bsaResp) {
        if (bsaResp.getRoot().getBsaList().size() > 1 ||
            !bsaResp.getRoot().getBsaList().get(0).getStation().isEmpty()) {
            redCircle.setVisibility(View.VISIBLE);
        }

        bsas = bsaResp.getRoot().getBsaList();
    }

    // TODO: probably don't need this anymore
    private void setOnPageListener() {
        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int lastPage = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lastPage != 0 && position == 0) {
                            departuresFragment.refreshOnNewData(myStationsFragment.extractNewData());
                        }
                        lastPage = position;
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    private void setUpViewPager(Bundle bundle) {
        ArrayList<String> titles = new ArrayList<String>(Arrays.asList(getResources()
            .getStringArray(R.array.tab_headers)));

        ArrayList<Fragment> fragments = new ArrayList<>();
        departuresFragment.setArguments(bundle);
        fragments.add(departuresFragment);
        fragments.add(myStationsFragment);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager())
            .setTitles(titles)
            .setFragments(fragments);

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
