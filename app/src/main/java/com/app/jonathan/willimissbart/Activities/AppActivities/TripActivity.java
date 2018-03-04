package com.app.jonathan.willimissbart.Activities.AppActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.API.Models.Routes.Leg;
import com.app.jonathan.willimissbart.API.Models.Routes.Trip;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.EstimatesManager;
import com.app.jonathan.willimissbart.Misc.NotGuava;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.LegViewHolder;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class TripActivity extends AppCompatActivity implements EstimatesManager.EstimatesListener {
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

        renderEstimatesForTripLegs(timeOfResp);
    }

    @Override
    protected void onDestroy() {
        EstimatesManager.unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.trip_menu, menu);
        menu.findItem(R.id.share)
            .setIcon(new IconDrawable(this, IoniconsIcons.ion_android_share_alt)
            .colorRes(R.color.white)
            .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.share) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, formatTripForIntent());
            intent.setType("text/plain");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public synchronized void onReceiveEstimates(EtdRespWrapper etdRespWrapper) {
        for (int i = 0; i < legsLayout.getChildCount(); ++i) {
            View child = legsLayout.getChildAt(i);
            if (i % 2 == 0) {
                LegViewHolder legViewHolder = (LegViewHolder) child.getTag();
                legViewHolder.displayEstimates(
                    trip.getLegList().get(i / 2).getOrigin(), etdRespWrapper.getRespTime());
            }
        }
    }

    // TODO: do things here
    @Override
    public synchronized void onEstimatesUpdated() {
    }

    private boolean hasEstimatesLoaded(View v) {
        return ((LegViewHolder) v.getTag()).getChildCount() != 2;
    }

    private void renderEstimatesForTripLegs(long timeOfResp) {
        EstimatesManager.updateEstimates(System.currentTimeMillis() / 1000);
        synchronized (this) {
            for (int i = 0; i < trip.getLegList().size(); ++i) {
                Leg leg = trip.getLegList().get(i);

                maybeLoadTransferLayout(i);

                View v = LayoutInflater.from(this).inflate(R.layout.leg_cell, legsLayout, false);
                LegViewHolder viewHolder = new LegViewHolder(v);
                viewHolder.setUp(leg, timeOfResp);
                v.setTag(viewHolder);
                legsLayout.addView(v);
            }
            EstimatesManager.register(this);
        }

        if (trip.getLegList().size() == 2 && !hasEstimatesLoaded(legsLayout.getChildAt(2))) {
            Log.i("TripActivity", "Loading estimates for 2nd leg...");
            RetrofitClient.getRealTimeEstimates(trip.getLegList().get(1).getOrigin(),
                NotGuava.newHashSet(trip.getLegList().get(1).getTrainHeadStation()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(etdRespWrapper -> Toast.makeText(this, "REEEE", Toast.LENGTH_SHORT).show())
                .doOnError(e ->
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void maybeLoadTransferLayout(int i) {
        if (i > 0) {
            legsLayout.addView(LayoutInflater.from(this)
                .inflate(R.layout.layout_transfer, legsLayout, false));
        }
    }

    private String formatTripForIntent() {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.trip_string_format,
            trip.getOrigin(), trip.getDestination(),
            trip.getOrigTimeMin(), trip.getDestTimeMin()));

        int legCnt = trip.getLegList().size();
        for (int i = 0; i < legCnt; ++i) {
            Leg leg = trip.getLegList().get(i);
            sb.append(getString(R.string.trip_leg_format, i + 1, leg.getOrigin(),
                leg.getTrainHeadStation(), leg.getOrigTimeMin(), leg.getDestination()));

            if (i < legCnt - 1) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }
}
