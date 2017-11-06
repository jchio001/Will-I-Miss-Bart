package com.app.jonathan.willimissbart.ViewHolders;


import android.view.View;
import android.widget.TextView;

import com.app.jonathan.willimissbart.Misc.MyApplication;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ErrorTextViewViewHolder {
    @Bind(R.id.error_tv) TextView errorTV;

    private static final String NO_ETDS = MyApplication.getContext().getString(R.string.no_etds);
    private static final String FAILED_TO_GET_ETDS =
            MyApplication.getContext().getString(R.string.failed_to_get_etds);

    private UserStationData data;
    private boolean success;

    public ErrorTextViewViewHolder(View v, UserStationData data, boolean success) {
        ButterKnife.bind(this, v);
        errorTV.setText(success ? NO_ETDS : FAILED_TO_GET_ETDS);
        this.data = data;
        this.success = success;
    }

    @OnClick(R.id.error_tv)
    public void tryAgain() {
        if (!success) {
            /*RetrofitClient.getInstance()
                    .getMatchingService()
                    .getEtd("etd", APIConstants.API_KEY, 'y', abbr,
                            Utils.directionToUrlParam(data.getDirection())
                    )
                    .clone()
                    .enqueue(
                            new EtdCallback()
                                    .setStationName(data.getStations())
                                    .setStationAbbr(data.getAbbr())
                                    .setIndex(i)
                    );*/
        }
    }
}
