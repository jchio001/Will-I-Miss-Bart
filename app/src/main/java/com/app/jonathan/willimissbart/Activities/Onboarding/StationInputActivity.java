package com.app.jonathan.willimissbart.Activities.Onboarding;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Callbacks.StationsCallback;
import com.app.jonathan.willimissbart.API.Models.Generic.FailureEvent;
import com.app.jonathan.willimissbart.API.Models.StationModels.StationsResp;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Activities.AppActivities.MainActivity;
import com.app.jonathan.willimissbart.Adapters.SimpleLargeTextListAdapter;
import com.app.jonathan.willimissbart.Adapters.StringAdapter;
import com.app.jonathan.willimissbart.Dialogs.DeleteAlertDialog;
import com.app.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.AddDataElemAnimation.HideAddButtonAnimListener;
import com.app.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.InitialAnimation.HideProgressBarAnimListener;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.PopUpWindows.InfoPopUpWindow;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.BartDataElemViewHolder;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StationInputActivity extends AppCompatActivity
        implements DeleteAlertDialog.DeleteDataElemListener {
    @Bind(R.id.activity_station_input) CoordinatorLayout parent;
    @Bind(R.id.info_layout) LinearLayout infoLayout;
    @Bind(R.id.data_elem_layout) LinearLayout dataElemLayout;
    @Bind(R.id.bart_data_elem_parent) LinearLayout firstSelectBartLayout;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.add_station) Button addStationButton;
    @Bind(R.id.done) Button doneButton;
    @Bind(R.id.help_tv) TextView helpTextView;

    private SimpleLargeTextListAdapter simpleLargeTextListAdapter;
    private StringAdapter directionsAdapter;
    private List<BartDataElemViewHolder> bartDataElemViewHolders;

    Point point;

    private int nextIndex = 0;

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
                new BartDataElemViewHolder(firstSelectBartLayout, this, this, nextIndex++)
                        .build(null)
        );

        String stationsJSON =
                SPSingleton.getInstance(getApplicationContext()).getPersistedStations();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        int[] loc = new int[2];
        helpTextView.getLocationOnScreen(loc);
        point = new Point(
                loc[0] - helpTextView.getMeasuredWidth(),
                loc[1] + helpTextView.getMeasuredHeight() + 10);
    }

    @Override
    public void deleteDataElem(int index) {
        Log.i("StationInputActivity", String.format("Deleting %d", index));
        for (int i = index + 1; i < bartDataElemViewHolders.size(); ++i) {
            bartDataElemViewHolders.get(i).decrementIndex();
        }
        bartDataElemViewHolders.remove(index);
        dataElemLayout.removeViewAt(index);
        --nextIndex;
    }

    @OnClick(R.id.help_tv)
    public void onHelpRequested() {
        InfoPopUpWindow info = new InfoPopUpWindow(this);
        info.showAtLocation(parent, Gravity.NO_GRAVITY, point.x, point.y);
    }

    @OnClick(R.id.add_station)
    public void addDataElem() {
        addStationButton.setEnabled(false);
        doneButton.setEnabled(false);

        // stop spamming the button idiot
        if (bartDataElemViewHolders.size() >= 5) {
            Utils.showSnackBar(this, parent, R.color.red, getString(R.string.plz_stop));
            addStationButton.setEnabled(true);
            doneButton.setEnabled(true);
            return;
        }

        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout newDataElem = (LinearLayout) vi.inflate(R.layout.bart_data_elem, null);
        newDataElem.setVisibility(View.GONE);
        bartDataElemViewHolders.add(
                new BartDataElemViewHolder(newDataElem, this, this, nextIndex)
                        .setColorSelected(getResources().getColor(R.color.colorPrimaryDark))
                        .setColorNotSelected(getResources().getColor(android.R.color.transparent))
        );
        bartDataElemViewHolders.get(nextIndex)
                .setBartSpinnerAdapter(simpleLargeTextListAdapter)
                .setDirectionSpinnerAdapter(directionsAdapter);
        dataElemLayout.addView(newDataElem, nextIndex);
        ++nextIndex;

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

        String serializedUserData = parseAndPersistData(bartDataElemViewHolders);
        if (!serializedUserData.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.USER_DATA, parseAndPersistData(bartDataElemViewHolders));
            startActivity(intent);
            finish();
        } else {
            Utils.showSnackBar(this, parent, R.color.red, getString(R.string.all_invalid_data));
        }
    }

    @Subscribe
    public void onStationsListEvent(StationsResp stationsResp) {
        StationsSingleton.getInstance().setStationElems(
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
        simpleLargeTextListAdapter = new SimpleLargeTextListAdapter(
                this, StationsSingleton.getInstance().getStationElems()
        );
        bartDataElemViewHolders.get(0)
                .setBartSpinnerAdapter(simpleLargeTextListAdapter)
                .setDirectionSpinnerAdapter(directionsAdapter);

        AlphaAnimation hideProgressBar = new AlphaAnimation(1.0f, 0.0f);
        hideProgressBar.setDuration(Constants.LONG_DURATION);
        hideProgressBar.setAnimationListener(
                new HideProgressBarAnimListener()
                        .setProgressBar(progressBar)
                        .setLinearLayout1(infoLayout)
                        .setLinearLayout2(firstSelectBartLayout)
                        .setButton1(addStationButton)
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
                                .setStationIndex(bartDataElemViewHolder.getStationIndex())
                                .setAbbr(bartDataElemViewHolder.getStationAbbr())
                                .setDirection(bartDataElemViewHolder.getDirection())
                                .setDirectionIndex(bartDataElemViewHolder.getDirectionIndex())
                                .setDays(days)
                );
            }
        }

        if (!userBartData.isEmpty()) {
            String serializedUserData = new Gson().toJson(userBartData);
            SPSingleton.getInstance(getApplicationContext())
                    .persistUserData(new Gson().toJson(userBartData));
            return serializedUserData;
        } else {
            return "";
        }
    }
}
