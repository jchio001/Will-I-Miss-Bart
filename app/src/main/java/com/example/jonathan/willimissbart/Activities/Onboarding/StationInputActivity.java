package com.example.jonathan.willimissbart.Activities.Onboarding;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonathan.willimissbart.API.APIConstants;
import com.example.jonathan.willimissbart.API.Callbacks.StationsCallback;
import com.example.jonathan.willimissbart.API.Models.FailureEvent;
import com.example.jonathan.willimissbart.API.Models.StationModels.Station;
import com.example.jonathan.willimissbart.API.Models.StationModels.StationsResp;
import com.example.jonathan.willimissbart.API.RetrofitClient;
import com.example.jonathan.willimissbart.Activities.AppActivities.MainActivity;
import com.example.jonathan.willimissbart.Adapters.StationSpinnerAdapter;
import com.example.jonathan.willimissbart.Adapters.StringAdapter;
import com.example.jonathan.willimissbart.AnimationListeners.StationInputAnimationListeners.AddDataElemAnimation.HideAddButtonAnimListener;
import com.example.jonathan.willimissbart.AnimationListeners.StationInputAnimationListeners.InitialAnimation.HideProgressBarAnimListener;
import com.example.jonathan.willimissbart.Misc.Constants;
import com.example.jonathan.willimissbart.Misc.Utils;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.example.jonathan.willimissbart.Persistence.SPSingleton;
import com.example.jonathan.willimissbart.Persistence.StationsSingleton;
import com.example.jonathan.willimissbart.R;
import com.example.jonathan.willimissbart.ViewHolders.BartDataElemViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StationInputActivity extends AppCompatActivity {
    @Bind(R.id.activity_station_input) CoordinatorLayout parent;
    @Bind(R.id.data_elem_layout) LinearLayout dataElemLayout;
    @Bind(R.id.select_bart_layout) LinearLayout firstSelectBartLayout;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.input_bart_info_tv) TextView textView;
    @Bind(R.id.add_station) Button addStationButton;
    @Bind(R.id.done) Button doneButton;

    private StationSpinnerAdapter stationSpinnerAdapter;
    private StringAdapter directionsAdapter;
    private List<BartDataElemViewHolder> bartDataElemViewHolders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_input);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        directionsAdapter = new StringAdapter(
                this, Arrays.asList(getResources().getStringArray(R.array.directions))
        );

        bartDataElemViewHolders = new ArrayList<>();
        bartDataElemViewHolders.add(
                new BartDataElemViewHolder(firstSelectBartLayout)
                        .setColorSelected(getResources().getColor(R.color.colorPrimaryDark))
                        .setColorNotSelected(getResources().getColor(R.color.bartBlue))
        );

        String stationsJSON =
                SPSingleton.getInstance(getApplicationContext()).getPersistedStations();
        if (stationsJSON.isEmpty()) {
            RetrofitClient.getInstance()
                    .getMatchingService()
                    .getStations("stns", APIConstants.API_KEY, "y")
                    .enqueue(new StationsCallback());
        } else {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Station>>(){}.getType();
            List<Station> stations = gson.fromJson(stationsJSON, listType);
            setUpActivityLayout(stations);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.add_station)
    public void addDataElem() {
        addStationButton.setEnabled(false);
        doneButton.setEnabled(false);

        //stop spamming the button idiot
        if (bartDataElemViewHolders.size() >= 5) {
            Utils.showSnackBar(this, parent, R.color.red, getString(R.string.plz_stop));
            addStationButton.setEnabled(true);
            doneButton.setEnabled(true);
            return;
        }

        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout newDataElem = (LinearLayout) vi.inflate(R.layout.bart_data_elem, null);
        newDataElem.setVisibility(View.GONE);
        int index = bartDataElemViewHolders.size();
        bartDataElemViewHolders.add(
                new BartDataElemViewHolder(newDataElem)
                        .setColorSelected(getResources().getColor(R.color.colorPrimaryDark))
                        .setColorNotSelected(getResources().getColor(R.color.bartBlue))
        );
        bartDataElemViewHolders.get(index)
                .setBartSpinnerAdapter(stationSpinnerAdapter)
                .setDirectionSpinnerAdapter(directionsAdapter);
        dataElemLayout.addView(newDataElem, index);

        AlphaAnimation hideAddMoreButton = new AlphaAnimation(1.0f, 0.0f);
        hideAddMoreButton.setDuration(Constants.STANDARD_DURATION);
        hideAddMoreButton.setAnimationListener(
                new HideAddButtonAnimListener()
                        .setButton(addStationButton)
                        .setButton2(doneButton)
                        .setLinearLayout(newDataElem)
        );

        addStationButton.startAnimation(hideAddMoreButton);
    }

    @OnClick(R.id.done)
    public void done() {
        doneButton.setEnabled(false);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.USER_DATA, parseAndPersistData(bartDataElemViewHolders));
        startActivity(intent);
        finish();
    }

    @Subscribe
    public void onStationsListEvent(StationsResp stationsResp) {
        persistStations();
        setUpActivityLayout(stationsResp.getStationsRoot().getStations().getStationList());
    }

    @Subscribe
    public void onFailedToFetchStations(FailureEvent event) {
        Toast.makeText(this, String.valueOf(event.code), Toast.LENGTH_SHORT).show();
    }

    private void setUpActivityLayout(List<Station> stations) {
        StationsSingleton.getInstance().setStationElems(stations);

        stationSpinnerAdapter = new StationSpinnerAdapter(
                this, StationsSingleton.getInstance().getStationElems()
        );
        bartDataElemViewHolders.get(0)
                .setBartSpinnerAdapter(stationSpinnerAdapter)
                .setDirectionSpinnerAdapter(directionsAdapter);

        AlphaAnimation hideProgressBar = new AlphaAnimation(1.0f, 0.0f);
        hideProgressBar.setDuration(Constants.LONG_DURATION);
        hideProgressBar.setAnimationListener(
                new HideProgressBarAnimListener()
                        .setProgressBar(progressBar)
                        .setTextView(textView)
                        .setLinearLayout(firstSelectBartLayout)
                        .setButton(addStationButton)
                        .setButton2(doneButton)
        );
        progressBar.startAnimation(hideProgressBar);
    }

    private void persistStations() {
        SPSingleton.getInstance(getApplicationContext()).persistStations(
                new Gson().toJson(StationsSingleton.getInstance().getStationElems())
        );
    }

    private String parseAndPersistData(List<BartDataElemViewHolder> bartDataElemViewHolders) {
        List<UserBartData> userBartData = new ArrayList<>();

        for (BartDataElemViewHolder bartDataElemViewHolder : bartDataElemViewHolders) {
            String name = bartDataElemViewHolder.getStationName();
            boolean[] days = bartDataElemViewHolder.getDaysOfWeekOfInterest();
            if (!name.equals("Select a station") && !Utils.noDaysSelected(days)) {
                userBartData.add(
                        new UserBartData()
                                .setStation(name)
                                .setDays(days)
                                .setDirection(bartDataElemViewHolder.getDirection())
                );
            }
        }

        String serializedUserData = new Gson().toJson(userBartData);
        SPSingleton.getInstance(getApplicationContext())
                .persistUserData(new Gson().toJson(userBartData));
        return serializedUserData;
    }
}
