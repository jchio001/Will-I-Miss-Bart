package com.app.jonathan.willimissbart.Activities.Onboarding;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Callbacks.StationsCallback;
import com.app.jonathan.willimissbart.API.Models.Generic.FailureEvent;
import com.app.jonathan.willimissbart.API.Models.Station.StationsResp;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Adapters.OriginDestStationsAdapter;
import com.app.jonathan.willimissbart.Listeners.Animations.Onboarding.InitialAnimation.HideProgressBarAnimListener;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.SPManager;
import com.app.jonathan.willimissbart.Persistence.StationsManager;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.StationGridViewHolder;
import com.app.jonathan.willimissbart.ViewHolders.StationsFooterViewHolder;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class OnboardingActivity extends AppCompatActivity {
    @Bind(R.id.activity_station_input) CoordinatorLayout parent;
    @Bind(R.id.stn_grid_layout) LinearLayout stationGridLayout;
    @Bind(R.id.stations_footer) LinearLayout stationsFooter;
    @Bind(R.id.onboarding_blurb) TextView onboardingBlurb;
    @Bind(R.id.progress_bar) ProgressBar progressBar;

    private StationGridViewHolder stationGridViewHolder;
    private StationsFooterViewHolder footer;
    private OriginDestStationsAdapter adapter;

    public static final int PERMISSIONS_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);
        footer = new StationsFooterViewHolder(stationsFooter);
        footer.done.setEnabled(false);
        EventBus.getDefault().register(this);

        fetchOrLoadPersistedStations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnItemClick(R.id.stn_grid)
    public void onItemSelected(AdapterView<?> parent, int position) {
        adapter.setOriginOrDest(adapter.getItem(position).getIndex());
    }

    @Subscribe
    public void onStationsListEvent(StationsResp stationsResp) {
        StationsManager.getInstance().setStations(
                stationsResp.getStationsRoot().getStations().getStationList()
        );
        persistStations();
        setUpActivityLayout();
    }

    @Subscribe
    public void onFailedToFetchStations(FailureEvent event) {
        Toast.makeText(this, String.valueOf(event.code), Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("unchecked")
    private void setUpActivityLayout() {
        adapter = new OriginDestStationsAdapter(StationsManager.getStations(), null, footer);
        stationGridViewHolder = new StationGridViewHolder(stationGridLayout, adapter, 0, true);
        AlphaAnimation hideProgressBar = new AlphaAnimation(1.0f, 0.0f);
        hideProgressBar.setDuration(Constants.LONG_DURATION);
        hideProgressBar.setAnimationListener(new HideProgressBarAnimListener()
            .setProgressBar(progressBar)
            .setTextView(onboardingBlurb)
            .setGridLayout(stationGridLayout)
            .setFooter(footer));
        progressBar.startAnimation(hideProgressBar);
    }

    private void persistStations() {
        SPManager.getInstance(getApplicationContext()).persistStations(
                new Gson().toJson(StationsManager.getStations())
        );
    }

    private void fetchOrLoadPersistedStations()  {
        String stationsJSON =
            SPManager.getPersistedStations(this);
        if (stationsJSON.isEmpty()) {
            RetrofitClient.getInstance()
                .getMatchingService()
                .getStations("stns", APIConstants.API_KEY, 'y')
                .enqueue(new StationsCallback());
        } else {
            Utils.loadStations(stationsJSON);
            setUpActivityLayout();
        }
    }
}
