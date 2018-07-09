package com.app.jonathan.willimissbart.activity.onboarding;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.adapter.OriginDestStationsAdapter;
import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.api.RetrofitClient;
import com.app.jonathan.willimissbart.fragment.UserDataManager;
import com.app.jonathan.willimissbart.listener.animation.Onboarding.HideProgressBarAnimListener;
import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.persistence.SPManager;
import com.app.jonathan.willimissbart.persistence.StationsManager;
import com.app.jonathan.willimissbart.viewholder.StationGridViewHolder;
import com.app.jonathan.willimissbart.viewholder.StationsFooterViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class OnboardingActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Onboarding";

    @Bind(R.id.activity_station_input) CoordinatorLayout parent;
    @Bind(R.id.stn_grid_layout) LinearLayout stationGridLayout;
    @Bind(R.id.stations_footer) LinearLayout stationsFooter;
    @Bind(R.id.onboarding_blurb) TextView onboardingBlurb;
    @Bind(R.id.progress_bar) ProgressBar progressBar;

    private StationGridViewHolder stationGridViewHolder;
    private StationsFooterViewHolder footer;
    private OriginDestStationsAdapter adapter;

    private UserDataManager userDataManager;
    private SPManager spManager;
    private OnboardingStationsManager onboardingStationsManager;
    private StationsManager stationsManager;

    protected Disposable stationDisposable;

    private SingleObserver<List<Station>> stationsObserver = new SingleObserver<List<Station>>() {
        @Override
        public void onSubscribe(Disposable d) {
            stationDisposable = d;
        }

        @Override
        public void onSuccess(List<Station> stations) {
            setUpActivityLayout();
        }

        @Override
        public void onError(Throwable e) {
            Log.w(LOG_TAG, String.format("Error fetching stations: %s", e.getMessage()));
            progressBar.setVisibility(View.GONE);
            Utils.showSnackbar(OnboardingActivity.this,
                parent, R.color.red, R.string.failed_stations_req);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        this.spManager = new SPManager(this);
        this.userDataManager = new UserDataManager(spManager);
        this.stationsManager = StationsManager.get();
        this.onboardingStationsManager = new OnboardingStationsManager(
            RetrofitClient.get(),
            spManager,
           stationsManager);

        footer = new StationsFooterViewHolder(stationsFooter, userDataManager);
        footer.done.setEnabled(false);

        onboardingStationsManager.getStations().subscribeWith(stationsObserver);
    }

    @Override
    protected void onDestroy() {
        if (stationDisposable != null) {
            stationDisposable.dispose();
        }

        super.onDestroy();
    }

    @OnItemClick(R.id.stn_grid)
    public void onItemSelected(AdapterView<?> parent, int position) {
        adapter.setOriginOrDest(adapter.getItem(position).getIndex());
    }

    @SuppressWarnings("unchecked")
    @UiThread
    protected void setUpActivityLayout() {
        adapter = new OriginDestStationsAdapter(
            stationsManager.getStations(),
            null,
            userDataManager);
        stationGridViewHolder = new StationGridViewHolder(
            stationGridLayout,
            adapter,
            StationsManager.get(),
            0,
            true);

        AlphaAnimation hideProgressBar = new AlphaAnimation(1.0f, 0.0f);
        hideProgressBar.setDuration(Constants.LONG_DURATION);
        hideProgressBar.setAnimationListener(new HideProgressBarAnimListener()
            .setProgressBar(progressBar)
            .setTextView(onboardingBlurb)
            .setGridLayout(stationGridLayout)
            .setFooter(footer));
        progressBar.startAnimation(hideProgressBar);
    }
}
