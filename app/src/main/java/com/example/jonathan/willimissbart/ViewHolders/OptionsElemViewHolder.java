package com.example.jonathan.willimissbart.ViewHolders;

import android.view.View;
import android.widget.TextView;

import com.example.jonathan.willimissbart.R;
import com.joanzapata.iconify.widget.IconTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OptionsElemViewHolder {
    @Bind(R.id.icon) IconTextView icon;
    @Bind(R.id.action_text) TextView actionText;

    public OptionsElemViewHolder(View v, int iconId, String text) {
        ButterKnife.bind(this, v);
        icon.setText(iconId);
        actionText.setText(text);
        ButterKnife.unbind(this);
    }
}
