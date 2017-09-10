package com.app.jonathan.willimissbart.Activities.AppActivities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.app.jonathan.willimissbart.Adapters.ViewPagerAdapter;
import com.app.jonathan.willimissbart.Fragments.BsaFragment;
import com.app.jonathan.willimissbart.Fragments.DeparturesFragment;
import com.app.jonathan.willimissbart.Fragments.MyStationsFragment;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tabs) TabLayout tabs;
    @Bind(R.id.view_pager) ViewPager viewPager;

    private DeparturesFragment departuresFragment = new DeparturesFragment();
    private BsaFragment bsaFragment = new BsaFragment();
    private MyStationsFragment myStationsFragment = new MyStationsFragment();
    private Handler handler = new Handler();

    protected final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setOnPageListener();

        Bundle bundle = getIntent().getExtras();
        Utils.loadStations(
                SPSingleton.getInstance(getApplicationContext()).getPersistedStations());

        setUpViewPager(Utils.getUserBartData(bundle, getApplicationContext()));
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Don't need a menu as of now, so this does nothing
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setOnPageListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });
    }

    private void setUpViewPager(String serializedUserData) {
        System.out.println(serializedUserData);
        ArrayList<String> titles = new ArrayList<String>(Arrays.asList(getResources()
                .getStringArray(R.array.tab_headers)));

        ArrayList<Fragment> fragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_DATA, serializedUserData);
        departuresFragment.setArguments(bundle);
        fragments.add(departuresFragment);
        fragments.add(bsaFragment);
        myStationsFragment.setArguments(bundle);
        fragments.add(myStationsFragment);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager())
                .setTitles(titles)
                .setFragments(fragments);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
    }
}
