package com.app.jonathan.willimissbart.Activities.AppActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.app.jonathan.willimissbart.Adapters.AbstractStationsAdapter;
import com.app.jonathan.willimissbart.Adapters.OriginDestStationsAdapter;
import com.app.jonathan.willimissbart.Adapters.SingleElemStationsAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectStationActivity extends AppCompatActivity {
    @Bind(R.id.stn_grid) GridView stationGrid;

    AbstractStationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_station);
        int titleId = getIntent().getExtras().getInt(Constants.TITLE);
        getSupportActionBar().setTitle(getIntent().getExtras().getInt(Constants.TITLE));
        ButterKnife.bind(this);

        adapter = new SingleElemStationsAdapter(StationsSingleton.getStations(), true)
            .setSelectingOrigin(titleId == R.string.select_origin);
        stationGrid.setAdapter(adapter);
    }
}
