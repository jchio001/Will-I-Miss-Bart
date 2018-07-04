package com.app.jonathan.willimissbart.activity.core;

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

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.api.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.api.Models.Routes.Leg;
import com.app.jonathan.willimissbart.api.Models.Routes.Trip;
import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.misc.EstimatesManager;
import com.app.jonathan.willimissbart.misc.EstimatesManager.EstimateConsumer;
import com.app.jonathan.willimissbart.misc.NotGuava;
import com.app.jonathan.willimissbart.misc.RouteBundle;
import com.app.jonathan.willimissbart.viewholder.LegViewHolder;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class TripActivity extends AppCompatActivity {

    @Bind(R.id.orig_time) TextView originTime;
    @Bind(R.id.dest_time) TextView destTime;
    @Bind(R.id.fare_info) TextView fareInfo;
    @Bind(R.id.leg_list) LinearLayout legsLayout;

    private Trip trip;

    protected Disposable disposable;

    protected EstimatesManager estimatesManager = EstimatesManager.get();

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
        if (disposable != null) {
            disposable.dispose();
        }

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

    private boolean hasEstimatesLoaded(View v) {
        return ((LegViewHolder) v.getTag()).getChildCount() != 2;
    }

    private void renderEstimatesForTripLegs(long timeOfResp) {
        synchronized (this) {
            for (int i = 0, size = trip.getLegList().size(); i < size; ++i) {
                Leg leg = trip.getLegList().get(i);

                maybeLoadTransferLayout(i);

                View v = LayoutInflater.from(this).inflate(R.layout.leg_cell, legsLayout, false);
                LegViewHolder viewHolder = new LegViewHolder(v);
                viewHolder.setUp(leg, timeOfResp);
                v.setTag(viewHolder);
                legsLayout.addView(v);
            }
        }

        if (trip.getLegList().size() == 2 && !hasEstimatesLoaded(legsLayout.getChildAt(2))) {
            Log.i("TripActivity", "Loading estimates for 2nd leg...");

            Leg nextLeg = trip.getLegList().get(1);
            estimatesManager.requestEstimates(
                new RouteBundle(nextLeg.getOrigin(),
                    NotGuava.newHashSet(nextLeg.getTrainHeadStation())),
                new EstimateConsumer() {
                    @Override
                    public void onPendingEstimates() {
                    }

                    @Override
                    public void consumeEstimates(List<Estimate> estimates) {
                        LegViewHolder legViewHolder = (LegViewHolder) legsLayout
                            .getChildAt(2).getTag();
                        legViewHolder.displayEstimates(
                            nextLeg.getOrigin(), 0);
                    }
                });
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

        for (int i = 0, legCnt = trip.getLegList().size(); i < legCnt; ++i) {
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
