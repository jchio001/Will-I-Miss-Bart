package com.app.jonathan.willimissbart.Activities.AppActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.Adapters.LegsAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TripActivity extends AppCompatActivity {
    @Bind(R.id.orig_time) TextView originTime;
    @Bind(R.id.dest_time) TextView destTime;
    @Bind(R.id.fare_info) TextView fareInfo;
    @Bind(R.id.leg_list) ListView legListView;

    private Trip trip;
    private LegsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trip = getIntent().getExtras().getParcelable(Constants.TRIP);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.trip_title, trip.getOrigin(), trip.getDestination()));

        originTime.setText(getString(R.string.departing, trip.getOrigTimeMin()));
        destTime.setText(getString(R.string.arriving, trip.getDestTimeMin()));
        fareInfo.setText(getString(R.string.fare_info, trip.getFare(), trip.getClipper()));

        adapter = new LegsAdapter(trip.getLegList());
        legListView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
