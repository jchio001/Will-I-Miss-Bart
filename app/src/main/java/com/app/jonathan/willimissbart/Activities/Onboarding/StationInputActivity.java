package com.app.jonathan.willimissbart.Activities.Onboarding;

import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Callbacks.StationsCallback;
import com.app.jonathan.willimissbart.API.Models.Generic.FailureEvent;
import com.app.jonathan.willimissbart.API.Models.StationModels.StationsResp;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Adapters.StationsAdapter;
import com.app.jonathan.willimissbart.Dialogs.DeleteAlertDialog;
import com.app.jonathan.willimissbart.Listeners.Animations.StationInputAnimationListeners.InitialAnimation.HideProgressBarAnimListener;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.PopUpWindows.InfoPopUpWindow;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.BartDataElemViewHolder;
import com.app.jonathan.willimissbart.ViewHolders.StationsFooterViewHolder;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class StationInputActivity extends AppCompatActivity
        implements DeleteAlertDialog.DeleteDataElemListener {
    @Bind(R.id.activity_station_input) CoordinatorLayout parent;
    @Bind(R.id.info_layout) LinearLayout infoLayout;
    @Bind(R.id.stn_grid) GridView stationGrid;
    @Bind(R.id.stations_footer) CardView stationsFooter;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.help_tv) TextView helpTextView;

    // View modules
    private StationsFooterViewHolder footer;

    StationsAdapter adapter;
    Point point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_input);
        ButterKnife.bind(this);
        footer = new StationsFooterViewHolder(stationsFooter);
        footer.done.setEnabled(false);
        EventBus.getDefault().register(this);

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

    @Deprecated
    @Override
    public void deleteDataElem(int index) {
        /*Log.i("StationInputActivity", String.format("Deleting %d", index));
        for (int i = index + 1; i < bartDataElemViewHolders.size(); ++i) {
            bartDataElemViewHolders.get(i).decrementIndex();
        }
        bartDataElemViewHolders.remove(index);
        dataElemLayout.removeViewAt(index);
        --nextIndex;*/
    }

    @OnClick(R.id.help_tv)
    public void onHelpRequested() {
        InfoPopUpWindow info = new InfoPopUpWindow(this);
        info.showAtLocation(parent, Gravity.NO_GRAVITY, point.x, point.y);
    }

    @OnItemClick(R.id.stn_grid)
    public void onItemSelected(AdapterView<?> parent, int position) {
        adapter.setOriginOrDest(position);
    }

    /*@OnClick(R.id.done)
    public void done() {
        *//*doneButton.setEnabled(false);

        String serializedUserData = parseAndPersistData(bartDataElemViewHolders);
        if (!serializedUserData.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.USER_DATA, parseAndPersistData(bartDataElemViewHolders));
            startActivity(intent);
            finish();
        } else {
            Utils.showSnackbar(this, parent, R.color.red, R.string.all_invalid_data);
        }

        doneButton.setEnabled(true);*//*
    }*/

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
        adapter = new StationsAdapter(StationsSingleton.getInstance().getStationElems(), footer);
        stationGrid.setAdapter(adapter);
        AlphaAnimation hideProgressBar = new AlphaAnimation(1.0f, 0.0f);
        hideProgressBar.setDuration(Constants.LONG_DURATION);
        hideProgressBar.setAnimationListener(new HideProgressBarAnimListener()
            .setProgressBar(progressBar)
            .setLinearLayout(infoLayout)
            .setGrid(stationGrid)
            .setFooter(footer));
        progressBar.startAnimation(hideProgressBar);
    }

    private void persistStations() {
        SPSingleton.getInstance(getApplicationContext()).persistStations(
                new Gson().toJson(StationsSingleton.getInstance().getStationElems())
        );
    }
}
