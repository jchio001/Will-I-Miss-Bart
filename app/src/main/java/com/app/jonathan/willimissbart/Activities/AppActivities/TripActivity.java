package com.app.jonathan.willimissbart.Activities.AppActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.API.Models.Routes.Leg;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.EstimatesListener;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.LegViewHolder;
import com.google.common.collect.Sets;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TripActivity extends AppCompatActivity implements EstimatesListener {
    @Bind(R.id.orig_time) TextView originTime;
    @Bind(R.id.dest_time) TextView destTime;
    @Bind(R.id.fare_info) TextView fareInfo;
    @Bind(R.id.leg_list) LinearLayout legsLayout;

    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trip = getIntent().getExtras().getParcelable(Constants.TRIP);
        long timeOfResp = getIntent().getExtras().getLong(Constants.TIME_OF_RESP);

        setTitle(getString(R.string.trip_title, trip.getOrigin(), trip.getDestination()));
        originTime.setText(getString(R.string.departing, trip.getOrigTimeMin()));
        destTime.setText(getString(R.string.arriving, trip.getDestTimeMin()));
        fareInfo.setText(getString(R.string.fare_info, trip.getFare(), trip.getClipper()));

        synchronized (this) {
            for (Leg leg : trip.getLegList()) {
                View v = LayoutInflater.from(this).inflate(R.layout.leg_elem, null, false);
                LegViewHolder viewHolder = new LegViewHolder(v);
                viewHolder.setUp(leg, timeOfResp);
                v.setTag(viewHolder);
                legsLayout.addView(v);
            }
            EstimatesManager.register(this);
        }

        if (trip.getLegList().size() == 2 && !checkIfEstimatesLoaded(legsLayout.getChildAt(1))) {
            Log.i("TripActivity", "Loading estimates for 2nd leg...");
            RetrofitClient.getRealTimeEstimates(trip.getLegList().get(1).getOrigin(),
                Sets.newHashSet(trip.getLegList().get(1).getTrainHeadStation()));
        }
    }

    @Override
    protected void onDestroy() {
        EstimatesManager.unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public synchronized void onReceiveEstimates(EtdRespWrapper etdRespWrapper) {
        for (int i = 0; i < legsLayout.getChildCount(); ++i) {
            View child = legsLayout.getChildAt(i);
            LegViewHolder legViewHolder = (LegViewHolder) child.getTag();
            legViewHolder.displayEstimates(
                trip.getLegList().get(i).getOrigin(), etdRespWrapper.getRespTime());
        }
    }

    public boolean checkIfEstimatesLoaded(View v) {
        return ((ViewGroup) v).getChildCount() != 2;
    }
}
