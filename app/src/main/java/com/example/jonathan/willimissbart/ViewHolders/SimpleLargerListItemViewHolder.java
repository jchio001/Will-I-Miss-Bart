package com.example.jonathan.willimissbart.ViewHolders;


import android.view.View;
import android.widget.TextView;

import com.example.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SimpleLargerListItemViewHolder {
    @Bind(R.id.simple_textview) public TextView textView;

    public SimpleLargerListItemViewHolder(View v) {
        ButterKnife.bind(this, v);
    }
}
