package com.app.jonathan.willimissbart.ViewHolders;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.app.jonathan.willimissbart.Adapters.SingleElemStationsAdapter;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.R;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

import static butterknife.OnTextChanged.Callback.TEXT_CHANGED;

public class StationGridViewHolder {
    @Bind(R.id.stn_grid) GridView stationGrid;

    private WeakReference<SingleElemStationsAdapter> adapter;
    private int notThisIndex = -1;

    public StationGridViewHolder(View v, SingleElemStationsAdapter adapter, int notThisIndex) {
        ButterKnife.bind(this, v);
        stationGrid.setAdapter(adapter);
        this.adapter = new WeakReference<>(adapter);
        this.notThisIndex = notThisIndex;
    }

    @OnTextChanged(value = R.id.stn_search, callback = TEXT_CHANGED)
    public void onTextChanged(CharSequence s) {
        if (adapter != null && adapter.get() != null) {
            adapter.get().filter(s.toString().toUpperCase());
        }
    }

    @OnItemClick(R.id.stn_grid)
    public void onStationSelected(AdapterView<?> parent, int position) {
        Activity activity = (Activity) stationGrid.getContext();
        if (adapter != null && adapter.get() != null) {
            if (position != notThisIndex) {
                Intent intent = new Intent();
                intent.putExtra(Constants.STATION_INDEX,
                    adapter.get().getItem(position).getIndex());
                activity.setResult(adapter.get().isSelectingOrigin() ?
                    Constants.UPDATED_ORIGIN : Constants.UPDATED_DEST, intent);
                activity.finish();
            } else {
                Utils.showSnackbar(activity, parent, R.color.red, R.string.going_in_a_loop);
            }
        }
    }
}
