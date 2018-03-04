package com.app.jonathan.willimissbart.ViewHolders;

import android.view.View;
import android.widget.TextView;

import com.app.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DigitViewHolder {

    @Bind(R.id.digit) TextView digit;
    TextView errorTV;

    private int val = 0;
    private int base = 0;
    
    public DigitViewHolder(View v, TextView errorTV, int baseVal, int base) {
        ButterKnife.bind(this, v);
        this.errorTV = errorTV;
        this.val = baseVal;
        this.base = base;

        digit.setText(String.valueOf(baseVal));
    }

    @OnClick(R.id.increment)
    public void onIncrement() {
        errorTV.setVisibility(View.INVISIBLE);
        val = (val + 1) % base;
        digit.setText(String.valueOf(val));
    }

    @OnClick(R.id.decrement)
    public void onDecrement() {
        errorTV.setVisibility(View.INVISIBLE);
        val = (val - 1 + base) % base;
        digit.setText(String.valueOf(val));
    }

    public int getValue() {
        return this.val;
    }
}
