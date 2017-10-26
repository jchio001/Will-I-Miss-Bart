package com.app.jonathan.willimissbart.ViewHolders;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;

import com.app.jonathan.willimissbart.Adapters.AbstractStationsAdapter;
import com.app.jonathan.willimissbart.R;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static butterknife.OnTextChanged.Callback.TEXT_CHANGED;

public class StationGridViewHolder extends ViewHolder {
    @Bind(R.id.stn_search) EditText stationEditText;
    @Bind(R.id.stn_grid) GridView stationGrid;

    private WeakReference<AbstractStationsAdapter> adapter;
    private static LocationManager locationManager;

    public StationGridViewHolder(View v, AbstractStationsAdapter adapter, boolean isOnboarding) {
        super(v);
        ButterKnife.bind(this, v);

        if (locationManager == null) {
            locationManager = (LocationManager) v.getContext()
                .getSystemService(Context.LOCATION_SERVICE);
        }

        if (isOnboarding) {
            stationEditText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));
        }

        stationGrid.setAdapter(adapter);
        this.adapter = new WeakReference<>(adapter);
    }

    @OnClick(R.id.get_location)
    public void getNearestStation() {
    }

    @OnTextChanged(value = R.id.stn_search, callback = TEXT_CHANGED)
    public void onTextChanged(CharSequence s) {
        if (adapter != null && adapter.get() != null) {
            adapter.get().filter(s.toString().toUpperCase());
        }
    }
}
