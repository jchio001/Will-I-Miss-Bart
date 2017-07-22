package com.example.jonathan.willimissbart.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jonathan.willimissbart.API.APIConstants;
import com.example.jonathan.willimissbart.API.Callbacks.BsaCallback;
import com.example.jonathan.willimissbart.API.Models.BSAModels.Bsa;
import com.example.jonathan.willimissbart.API.Models.BSAModels.BsaResp;
import com.example.jonathan.willimissbart.API.Models.Generic.FailureEvent;
import com.example.jonathan.willimissbart.API.Models.Generic.SimpleListItem;
import com.example.jonathan.willimissbart.API.RetrofitClient;
import com.example.jonathan.willimissbart.Adapters.SimpleLargeTextListAdapter;
import com.example.jonathan.willimissbart.Enums.RefreshStateEnum;
import com.example.jonathan.willimissbart.Enums.StyleEnum;
import com.example.jonathan.willimissbart.Listeners.SwipeRefresh.BsaRefreshListener;
import com.example.jonathan.willimissbart.Misc.Utils;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.example.jonathan.willimissbart.Persistence.SPSingleton;
import com.example.jonathan.willimissbart.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BsaFragment extends Fragment {
    @Bind(R.id.bsa_srl) SwipeRefreshLayout bsaSWL;
    @Bind(R.id.bsa_lv) ListView bsaLV;

    SimpleLargeTextListAdapter adapter;
    BsaRefreshListener bsaRefreshListener;

    boolean doInitialFetch = true;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        adapter = new SimpleLargeTextListAdapter(getActivity(), new ArrayList<>())
                .setStyle(StyleEnum.NO_STYLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bsa, container, false);
        ButterKnife.bind(this, v);

        bsaSWL.setRefreshing(true);
        bsaSWL.setEnabled(false);
        bsaLV.setAdapter(adapter);
        bsaRefreshListener = new BsaRefreshListener(bsaSWL);
        bsaSWL.setOnRefreshListener(bsaRefreshListener);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible() && isVisibleToUser && doInitialFetch) {
            doInitialFetch = false;
            RetrofitClient.getInstance()
                    .getMatchingService()
                    .getBsa("bsa", APIConstants.API_KEY, 'y')
                    .enqueue(new BsaCallback());
        }
    }

    @SuppressWarnings("unchecked")
    @Subscribe
    public void onBsaResponse(BsaResp bsaResp) {
        //Todo: filter here

        adapter.refresh(bsaResp.getRoot().getBsaList());
        bsaSWL.setRefreshing(false);
        bsaSWL.setEnabled(true);
        bsaRefreshListener.setRefreshState(RefreshStateEnum.INACTIVE);
    }

    @Subscribe
    public void onBsaFailure(FailureEvent failure) {
        if (failure.tag.equals(BsaCallback.tag)) {
            bsaSWL.setEnabled(true);
            Toast.makeText(getActivity(), R.string.failed_to_get_bsa, Toast.LENGTH_SHORT).show();
        }
    }
}
