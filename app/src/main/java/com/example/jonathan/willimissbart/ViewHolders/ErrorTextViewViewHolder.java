package com.example.jonathan.willimissbart.ViewHolders;


import android.view.View;
import android.widget.TextView;

import com.example.jonathan.willimissbart.API.APIConstants;
import com.example.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ErrorTextViewViewHolder {
    @Bind(R.id.error_tv) TextView errorTV;

    public ErrorTextViewViewHolder(View v, boolean success) {
        ButterKnife.bind(this, v);
        errorTV.setText(success ? APIConstants.NO_ETDS : APIConstants.FAILED_TO_GET_ETDS);
    }
}
