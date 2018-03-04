package com.app.jonathan.willimissbart.viewholder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.adapter.AbstractStationsAdapter;
import com.app.jonathan.willimissbart.fragment.StationsFragment;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.persistence.StationsManager;
import com.app.jonathan.willimissbart.R;
import com.joanzapata.iconify.widget.IconTextView;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.nlopez.smartlocation.SmartLocation;

import static butterknife.OnTextChanged.Callback.TEXT_CHANGED;

public class StationGridViewHolder extends ViewHolder {

    @Bind(R.id.get_location) IconTextView getLocation;
    @Bind(R.id.stn_search) EditText stationEditText;
    @Bind(R.id.stn_grid) GridView stationGrid;

    private WeakReference<AbstractStationsAdapter> adapter;
    private boolean fetchingLocation = false;
    private Handler handler = new Handler();

    private int permissionCode = 0;
    private int closestIndex = -1;

    public StationGridViewHolder(View v,
                                 AbstractStationsAdapter adapter,
                                 int permissionCode,
                                 boolean isOnboarding) {
        super(v);
        ButterKnife.bind(this, v);
        this.permissionCode = permissionCode;

        if (isOnboarding) {
            stationEditText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));
            stationEditText
                .setHintTextColor(ContextCompat.getColor(v.getContext(), R.color.hint_white));
        }

        if (permissionCode != StationsFragment.PERMISSIONS_CODE) {
            getLocation.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));
        }

        stationGrid.setAdapter(adapter);
        this.adapter = new WeakReference<>(adapter);
    }

    @OnClick(R.id.get_location)
    public void getNearestStation() {
        if (fetchingLocation) {
            return;
        }

        if (ContextCompat.checkSelfPermission(itemView.getContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) itemView.getContext(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionCode);
        } else {
            fetchLocationAndLoadClosestStation();
        }
    }

    @OnTextChanged(value = R.id.stn_search, callback = TEXT_CHANGED)
    public void onTextChanged(CharSequence s) {
        if (adapter != null && adapter.get() != null) {
            adapter.get().filter(s.toString().toUpperCase());
        }
    }

    public void fetchLocationAndLoadClosestStation() {
        if (!Utils.isLocationEnabled(itemView.getContext())) {
            Toast.makeText(itemView.getContext(),
                R.string.turn_on_location, Toast.LENGTH_SHORT).show();
            return;
        }

        fetchingLocation = true;
        SmartLocation.with(itemView.getContext()).location()
            .oneFix()
            .start(location -> {
                SmartLocation.with(itemView.getContext()).location().stop();
                fetchingLocation = false;
                handler.post(() -> {
                    closestIndex = Utils.getClosestStation(location);
                    loadClosestStation(closestIndex);
                });
            });
    }

    public void loadClosestStation(int index) {
        stationEditText.setText(StationsManager.getStations().get(index).getAbbr());
    }
}
