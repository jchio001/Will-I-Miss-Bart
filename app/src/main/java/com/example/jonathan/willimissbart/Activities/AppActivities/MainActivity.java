package com.example.jonathan.willimissbart.Activities.AppActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jonathan.willimissbart.Adapters.ViewPagerAdapter;
import com.example.jonathan.willimissbart.Fragments.MyStationsFragment;
import com.example.jonathan.willimissbart.Misc.Constants;
import com.example.jonathan.willimissbart.Misc.Utils;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.example.jonathan.willimissbart.Persistence.SPSingleton;
import com.example.jonathan.willimissbart.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        MyStationsFragment myStationsFragment = new MyStationsFragment();
        myStationsFragment.setArguments(bundle);
        fragments.add(myStationsFragment);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager())
                .setTitles(titles)
                .setFragments(fragments);

        viewPager.setAdapter(adapter);
    }
}
