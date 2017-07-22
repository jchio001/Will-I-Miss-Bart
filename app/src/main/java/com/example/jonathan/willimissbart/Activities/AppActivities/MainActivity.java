package com.example.jonathan.willimissbart.Activities.AppActivities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jonathan.willimissbart.Adapters.ViewPagerAdapter;
import com.example.jonathan.willimissbart.Fragments.BsaFragment;
import com.example.jonathan.willimissbart.Fragments.EtdsFragment;
import com.example.jonathan.willimissbart.Fragments.MyStationsFragment;
import com.example.jonathan.willimissbart.Misc.Constants;
import com.example.jonathan.willimissbart.Misc.Utils;
import com.example.jonathan.willimissbart.Persistence.SPSingleton;
import com.example.jonathan.willimissbart.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tabs) TabLayout tabs;
    @Bind(R.id.view_pager) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        Utils.loadStations(
                SPSingleton.getInstance(getApplicationContext()).getPersistedStations()
        );

        setUpViewPager(Utils.getUserBartData(bundle, getApplicationContext()));
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    private void setUpViewPager(String serializedUserData) {
        System.out.println(serializedUserData);
        ArrayList<String> titles = new ArrayList<String>(Arrays.asList(getResources()
                .getStringArray(R.array.tab_headers)));

        ArrayList<Fragment> fragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_DATA, serializedUserData);
        EtdsFragment etfsFRagment = new EtdsFragment();
        etfsFRagment.setArguments(bundle);
        fragments.add(etfsFRagment);
        fragments.add(new BsaFragment());
        MyStationsFragment myStationsFragment = new MyStationsFragment();
        myStationsFragment.setArguments(bundle);
        fragments.add(myStationsFragment);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager())
                .setTitles(titles)
                .setFragments(fragments);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
    }
}
