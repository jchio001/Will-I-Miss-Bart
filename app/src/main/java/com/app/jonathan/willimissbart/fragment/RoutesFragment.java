package com.app.jonathan.willimissbart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.adapter.TripsAdapter;
import com.app.jonathan.willimissbart.api.Models.Routes.Trip;
import com.app.jonathan.willimissbart.api.RetrofitClient;
import com.app.jonathan.willimissbart.misc.EstimatesManager;
import com.app.jonathan.willimissbart.misc.EstimatesManager.EstimatesEvent;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.persistence.SPManager;
import com.app.jonathan.willimissbart.persistence.StationsManager;
import com.app.jonathan.willimissbart.viewholder.UserRouteFooter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RoutesFragment extends Fragment {

    @Bind(R.id.route_recycler) RecyclerView recyclerView;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.user_route_footer) UserRouteFooter userRouteFooter;
    @Bind(R.id.failure_text) TextView failureText;
    
    private TripsAdapter adapter;

    private UserDataManager userDataManager;

    protected EstimatesManager estimatesManager = EstimatesManager.get();

    private TripManager tripManager;

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final SingleObserver<ArrayList<Trip>> tripSubscriber =
        new SingleObserver<ArrayList<Trip>>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(ArrayList<Trip> mergedTrips) {
                progressBar.setVisibility(View.GONE);
                failureText.setVisibility(View.GONE);
                adapter.refresh(mergedTrips);
                estimatesManager
                    .populateWithFeedEstimates(tripManager.getRouteBundles(mergedTrips));
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(),
                    String.format("Trips Wah wah %s", e.getMessage()), Toast.LENGTH_SHORT).show();
            }
        };

    private final Observer<EstimatesEvent> estimatesManagerObserver =
        new Observer<EstimatesEvent>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(EstimatesEvent estimatesEvent) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        userDataManager = new UserDataManager(new SPManager(getContext()), getArguments());
        tripManager = new TripManager(userDataManager, RetrofitClient.get());

        userDataManager.subscribe(() -> {
            compositeDisposable.dispose();
            compositeDisposable = new CompositeDisposable();
            tripManager.getTrips().subscribeWith(tripSubscriber);
            Utils.showSnackbar(getActivity(), userRouteFooter,
                R.color.bartBlue, R.string.updated_data);
        });

        userRouteFooter.withUserDataManager(userDataManager);
        renderFooter();

        adapter = new TripsAdapter(userDataManager);
        recyclerView.setAdapter(adapter);

        tripManager.getTrips().subscribeWith(tripSubscriber);
        estimatesManager.subscribe(estimatesManagerObserver);
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.dispose();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void renderFooter() {
        userRouteFooter.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, userRouteFooter.getMeasuredHeight());
        recyclerView.setLayoutParams(params);
    }

    public void updateUserStations(int resultCode, int stationIndex) {
        userRouteFooter.updateStations(resultCode,
            StationsManager.getStations().get(stationIndex).getAbbr());
    }
}
