package com.app.jonathan.willimissbart.ViewHolders;


import android.view.View;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ErrorTextViewViewHolder {
    @Bind(R.id.error_tv) TextView errorTV;

    public ErrorTextViewViewHolder(View v, boolean success) {
        ButterKnife.bind(this, v);
        errorTV.setText(success ? APIConstants.NO_ETDS : APIConstants.FAILED_TO_GET_ETDS);
    }
}
