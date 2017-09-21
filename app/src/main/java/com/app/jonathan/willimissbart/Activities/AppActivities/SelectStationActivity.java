package com.app.jonathan.willimissbart.Activities.AppActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;

import com.app.jonathan.willimissbart.Adapters.AbstractStationsAdapter;
import com.app.jonathan.willimissbart.Adapters.OriginDestStationsAdapter;
import com.app.jonathan.willimissbart.Adapters.SingleElemStationsAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class SelectStationActivity extends AppCompatActivity {
    @Bind(R.id.stn_grid) GridView stationGrid;

    private SingleElemStationsAdapter adapter;
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

        adapter = new SingleElemStationsAdapter(StationsSingleton.getStations(), true)
            .setSelectingOrigin(titleId == R.string.select_origin);
        stationGrid.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    @OnItemClick(R.id.stn_grid)
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
    }
}
