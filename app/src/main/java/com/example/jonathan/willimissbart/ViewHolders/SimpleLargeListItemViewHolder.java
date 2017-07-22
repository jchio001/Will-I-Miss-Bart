package com.example.jonathan.willimissbart.ViewHolders;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jonathan.willimissbart.Enums.StyleEnum;
import com.example.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.jonathan.willimissbart.Enums.StyleEnum.BART_STYLE;

public class SimpleLargeListItemViewHolder {
    @Bind(R.id.simple_large_list_item) LinearLayout parent;
    @Bind(R.id.simple_textview) public TextView textView;

    private Context context;

    public SimpleLargeListItemViewHolder(View v) {
        ButterKnife.bind(this, v);
    }

    public SimpleLargeListItemViewHolder setContext(Context context) {
        this.context = context;
        return this;
    }

    public SimpleLargeListItemViewHolder setUpStyle(StyleEnum style) {
        if (style != BART_STYLE) {
            parent.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            textView.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        return this;
    }
}
