package com.app.jonathan.willimissbart.Activities.Onboarding;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.Models.Station.Station;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class OnboardingActivity extends AppCompatActivity {
    @Bind(R.id.activity_station_input) CoordinatorLayout parent;
    @Bind(R.id.stn_grid_layout) LinearLayout stationGridLayout;
    @Bind(R.id.stations_footer) LinearLayout stationsFooter;
    @Bind(R.id.onboarding_blurb) TextView onboardingBlurb;
    @Bind(R.id.progress_bar) ProgressBar progressBar;

    private StationGridViewHolder stationGridViewHolder;
    private StationsFooterViewHolder footer;
    private OriginDestStationsAdapter adapter;

    private Disposable stationDisposable;

    private SingleObserver<List<Station>> stationsObserver = new SingleObserver<List<Station>>() {
        @Override
        public void onSubscribe(Disposable d) {
            stationDisposable = d;
        }

        @Override
        public void onSuccess(List<Station> stations) {
            StationsManager.getInstance().setStations(stations);
            persistStations();
            setUpActivityLayout();
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(OnboardingActivity.this, "STUBBED", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        footer = new StationsFooterViewHolder(stationsFooter);
        footer.done.setEnabled(false);

        fetchAndHandleStations();
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

    private void fetchAndHandleStations()  {
        SPManager.ayncGetPersistedStations(this)
            .flatMap(stationsJson -> {
               if (stationsJson.isEmpty()) {
                   return RetrofitClient.getStations()
                       .doOnSuccess(stations -> {
                           for (int i = 0; i < stations.size(); ++i) {
                               stations.get(i).setIndex(i);
                           }
                       });
               } else {
                   return Single.just(Utils.loadStations(stationsJson));
               }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(stationsObserver);
    }
}
