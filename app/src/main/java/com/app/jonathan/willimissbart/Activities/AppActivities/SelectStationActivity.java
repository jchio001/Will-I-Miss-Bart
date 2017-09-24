package com.app.jonathan.willimissbart.Activities.AppActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.app.jonathan.willimissbart.Adapters.SingleElemStationsAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.StationGridViewHolder;
import com.app.jonathan.willimissbart.ViewHolders.StationInfoViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class SelectStationActivity extends AppCompatActivity {
    @Bind(R.id.select_station_parent) CoordinatorLayout selectStationParent;
    @Bind(R.id.stn_grid_layout) LinearLayout stationGridLayout;
    @Bind(R.id.stn_grid) GridView stationGrid;
    @Bind(R.id.stn_info_parent) ScrollView stationInfoLayout;

    // The following need to be initialized in the following order
    private SingleElemStationsAdapter adapter;
    private StationInfoViewHolder stationInfoViewHolder;
    private StationGridViewHolder stationGridViewHolder;

    private static int height = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_station);
        Bundle bundle = getIntent().getExtras();
        int titleId = bundle.getInt(Constants.TITLE);
        getSupportActionBar().setTitle(getIntent().getExtras().getInt(Constants.TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        if (height == -1) {
            height = getStationInfoLayoutHeight();
        }

        stationInfoViewHolder = new StationInfoViewHolder(stationInfoLayout,
            getStationInfoLayoutHeight());
        adapter = new SingleElemStationsAdapter(StationsSingleton.getStations(),
            stationInfoViewHolder, true)
            .setSelectingOrigin(titleId == R.string.select_origin);
        stationGridViewHolder = new StationGridViewHolder(stationGridLayout, adapter,
            bundle.getInt(Constants.NOT_THIS_INDEX));

        stationGrid.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stationInfoViewHolder.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (stationInfoLayout.getVisibility() == View.VISIBLE &&
            stationInfoLayout.getHeight() == height) {
            stationInfoViewHolder.onClose();
        } else {

            super.onBackPressed();
        }
    }

    /*@OnItemClick(R.id.stn_grid)
    public void onStationSelected(AdapterView<?> parent, int position) {
        if (position != notThisIndex) {
            Intent intent = new Intent();
            intent.putExtra(Constants.STATION_INDEX, position);
            setResult(adapter.isSelectingOrigin() ?
                Constants.UPDATED_ORIGIN : Constants.UPDATED_DEST, intent);
            finish();
        } else {
            Utils.showSnackbar(this, parent, R.color.red, R.string.going_in_a_loop);
        }
    }*/

    public int getStationInfoLayoutHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
        return displayMetrics.heightPixels - TypedValue.complexToDimensionPixelSize(
            tv.data, getResources().getDisplayMetrics()) - statusBarHeight;
    }
}
