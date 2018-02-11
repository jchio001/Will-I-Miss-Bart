package com.app.jonathan.willimissbart.Activities.AppActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.app.jonathan.willimissbart.Adapters.SingleElemStationsAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.StationsManager;
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
    private StationInfoViewHolder stationInfoViewHolder;
    private SingleElemStationsAdapter adapter;
    private StationGridViewHolder stationGridViewHolder;

    // This is the index chosen for the current origin if the user wants to select a new
    // destination, and the current destination if the useer wants to select a new origin
    private int notThisIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_station);
        Bundle bundle = getIntent().getExtras();
        int titleId = bundle.getInt(Constants.TITLE);
        notThisIndex = bundle.getInt(Constants.NOT_THIS_INDEX);
        getSupportActionBar().setTitle(getIntent().getExtras().getInt(Constants.TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        stationInfoViewHolder = new StationInfoViewHolder(stationInfoLayout,
            Utils.getStationInfoLayoutHeight(this));
        adapter = new SingleElemStationsAdapter(StationsManager.getStations(),
            stationInfoViewHolder, true)
            .setSelectingOrigin(titleId == R.string.select_origin);
        stationGridViewHolder = new StationGridViewHolder(stationGridLayout, adapter, 0, false);

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
        if (isStationInfoLayoutOpen()) {
            stationInfoViewHolder.close();
        } else {
            super.onBackPressed();
        }
    }

    @OnItemClick(R.id.stn_grid)
    public void onStationSelected(AdapterView<?> parent, int position) {
        if (position != notThisIndex) {
            Intent intent = new Intent();
            intent.putExtra(Constants.STATION_INDEX,
                adapter.getItem(position).getIndex());
            setResult(adapter.isSelectingOrigin() ?
                Constants.UPDATED_ORIGIN : Constants.UPDATED_DEST, intent);
            finish();
        } else {
            Utils.showSnackbar(this, parent, R.color.red, R.string.going_in_a_loop);
        }
    }

    public boolean isStationInfoLayoutOpen() {
        return stationInfoLayout.getVisibility() == View.VISIBLE &&
            stationInfoLayout.getHeight() == Utils.getStationInfoLayoutHeight(this);
    }
}
