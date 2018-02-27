package com.app.jonathan.willimissbart.ViewHolders;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.Models.StationInfo.Station;
import com.app.jonathan.willimissbart.API.Models.StationInfo.StationInfoResp;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Listeners.Animations.Generic.ShowOrHideAnimListener;
import com.app.jonathan.willimissbart.Listeners.Animations.StationInfo.HideProgressBarAnimListener;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.R;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public class StationInfoViewHolder {

    public ScrollView stationInfoParent;
    @Bind(R.id.stn_addr_layout) public RelativeLayout stationAddrLayout;
    @Bind(R.id.stn_info) public LinearLayout stationInfoLayout;
    @Bind(R.id.activity_table) LinearLayout activityTable;
    @Bind(R.id.stn_info_progress_bar) public ProgressBar progressBar;
    @Bind(R.id.stn_info_title) TextView stationInfoTitle;
    @Bind(R.id.stn_info_blurb) TextView stationInfoBlurb;
    @Bind(R.id.stn_info_addr) TextView stationInfoAddress;

    private ValueAnimator expandAnimation;
    private ValueAnimator collapseAnimation;
    private AlphaAnimation showInfoAnim;
    private AlphaAnimation hideProgressBar;

    private String stationAbbr;

    // I don't need to reset the values because by the time the user clicks on this, new lat long
    // values will already be stored.
    private double lat;
    private double lng;
    private int height;
    private boolean closed = true;

    private Disposable disposable;

    private final AnimatorListener expandListener = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            stationInfoParent.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            stationInfoParent.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            RetrofitClient.getStationInfo(stationAbbr)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(stationInfoSubscriber);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    private final AnimatorListener collapseListener = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            stationAddrLayout.setEnabled(false);
            stationInfoLayout.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            stationInfoParent.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    private final AnimatorUpdateListener updateListener = new AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            stationInfoParent.getLayoutParams().height = (Integer) animation.getAnimatedValue();
            stationInfoParent.requestLayout();
        }
    };

    private final SingleObserver<Response<StationInfoResp>> stationInfoSubscriber =
        new SingleObserver<Response<StationInfoResp>>() {
            @Override
            public void onSubscribe(Disposable d) {
                synchronized (StationInfoViewHolder.this) {
                    if (disposable != null) {
                        disposable.dispose();
                    }

                    disposable = d;
                }
            }

            @Override
            public void onSuccess(Response<StationInfoResp> stationInfoResp) {
                onStationInfoResp(stationInfoResp.body().getRoot().getStations().getStation());
            }

            @Override
            public void onError(Throwable e) {
                Log.w("StationInfo", e.getMessage());
            }
        };

    public StationInfoViewHolder(ScrollView stationInfoParent, int height) {
        this.stationInfoParent = stationInfoParent;
        this.height = height;
        ButterKnife.bind(this, stationInfoParent);
        initAnimations();
    }

    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @OnClick(R.id.stn_info_close)
    public synchronized void close() {
        if (!closed) {
            if (disposable != null) {
                disposable.dispose();
            }

            hideProgressBar.setAnimationListener(null);
            hideProgressBar.cancel();

            showInfoAnim.setAnimationListener(null);
            showInfoAnim.cancel();

            progressBar.setVisibility(View.INVISIBLE);

            collapseAnimation.start();
            closed = true;
        }
    }

    @OnClick(R.id.stn_addr_layout)
    public void openMaps() {
        Uri googleMapsUri = Uri.parse(String.format(Locale.ENGLISH,
            "google.navigation:q=%f,%f", lat, lng));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleMapsUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        stationInfoParent.getContext().startActivity(mapIntent);
    }

    private void onStationInfoResp(Station stationInfo) {
        lat = stationInfo.getLatitude();
        lng = stationInfo.getLongitude();
        stationInfoAddress.setText(stationInfo.getFullAddress());
        stationInfoBlurb.setText(stationInfo.getIntro().getcDataSection().trim());

        // Yes, I did check BART's API and no, I can't do this with a loop. Each one of these
        // pieces of information is contained in their own classes rather than a list.
        new StationInfoRowViewHolder(activityTable.getChildAt(0),
            stationInfo.getFood().getcDataSection(), 0);
        new StationInfoRowViewHolder(activityTable.getChildAt(1),
            stationInfo.getShopping().getcDataSection(), 1);
        new StationInfoRowViewHolder(activityTable.getChildAt(2),
            stationInfo.getAttraction().getcDataSection(), 2);

        stationAddrLayout.setEnabled(true);
        
        showInfoAnim = new AlphaAnimation(0.0f, 1.0f);
        showInfoAnim.setDuration(Constants.STANDARD_DURATION);
        showInfoAnim.setAnimationListener(new ShowOrHideAnimListener(
            stationInfoLayout, View.VISIBLE));

        hideProgressBar = new AlphaAnimation(1.0f, 0.0f);
        hideProgressBar.setDuration(Constants.STANDARD_DURATION);
        hideProgressBar.setAnimationListener(new HideProgressBarAnimListener(progressBar,
            showInfoAnim, stationInfoLayout));
        progressBar.startAnimation(hideProgressBar);
    }

    public synchronized void show(String abbr) {
        if (closed) {
            closed = false;
            stationInfoParent.setVisibility(View.VISIBLE);
            stationInfoTitle.setText(stationInfoParent.getContext()
                .getString(R.string.stn_info_title, abbr));
            stationAbbr = abbr;
            expandAnimation.start();
        }
    }

    public void initAnimations() {
        expandAnimation = ValueAnimator.ofInt(0, height);
        expandAnimation.setDuration(Constants.SHORT_DURATION);
        expandAnimation.addUpdateListener(updateListener);
        expandAnimation.addListener(expandListener);

        collapseAnimation = ValueAnimator.ofInt(height, 0);
        collapseAnimation.setDuration(Constants.SHORT_DURATION);
        collapseAnimation.addUpdateListener(updateListener);
        collapseAnimation.addListener(collapseListener);
    }
}
