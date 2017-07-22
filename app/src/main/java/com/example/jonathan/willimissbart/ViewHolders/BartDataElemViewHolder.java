package com.example.jonathan.willimissbart.ViewHolders;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jonathan.willimissbart.API.Models.StationModels.Station;
import com.example.jonathan.willimissbart.Adapters.SimpleLargeTextListAdapter;
import com.example.jonathan.willimissbart.Adapters.StringAdapter;
import com.example.jonathan.willimissbart.Enums.StyleEnum;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.example.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BartDataElemViewHolder {
    @Bind(R.id.day_of_week_layout) public LinearLayout dayOfWeekLayout;
    @Bind(R.id.bart_spinner) public Spinner bartSpinner;
    @Bind(R.id.direction_spinner) public Spinner directionSpinner;
    @Bind({R.id.sunday_box, R.id.monday_box,
            R.id.tuesday_box, R.id.wednesday_box,
            R.id.thursday_box, R.id.friday_box,
            R.id.saturday_box}) public TextView[] dayBoxes;

    private Context context;
    private StyleEnum style = StyleEnum.BART_STYLE;

    private int colorSelected = -1;
    private int colorNotSelected = -1;

    public BartDataElemViewHolder(View v, Context context) {
        ButterKnife.bind(this, v);
        this.context = context;
    }

    //ghetto builder pattern
    public BartDataElemViewHolder build(UserBartData data) {
        colorNotSelected = ContextCompat.getColor(context, android.R.color.transparent);
        if (style != StyleEnum.BART_STYLE) {
            colorSelected = ContextCompat.getColor(context, R.color.lightGrey);

            GradientDrawable drawable =
                    (GradientDrawable) ContextCompat.getDrawable(
                            context,
                            R.drawable.white_rounded_background
                    );
            drawable.setStroke(2, context.getResources().getColor(R.color.black));
            bartSpinner.setBackground(drawable);
            directionSpinner.setBackground(drawable);

            for (TextView dayBox : dayBoxes) {
                dayBox.setTextColor(context.getResources().getColor(R.color.black));
            }
        } else {
            colorSelected = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        }

        if (data !=  null) {
            for (int i = 0; i < data.getDays().length; ++i) {
                dayBoxes[i].setBackgroundColor(data.getDays()[i] ? colorSelected : colorNotSelected);
            }
        }

        return this;
    }

    public BartDataElemViewHolder setColorSelected(int colorSelected) {
        this.colorSelected = colorSelected;
        return this;
    }

    public BartDataElemViewHolder setColorNotSelected(int colorNotSelected) {
        this.colorNotSelected = colorNotSelected;
        return this;
    }

    public BartDataElemViewHolder setBartSpinnerAdapter(SimpleLargeTextListAdapter adapter) {
        bartSpinner.setAdapter(adapter);
        return this;
    }

    public BartDataElemViewHolder setDirectionSpinnerAdapter(StringAdapter adapter) {
        directionSpinner.setAdapter(adapter);
        return this;
    }

    public BartDataElemViewHolder setStyle(StyleEnum style) {
        this.style = style;
        return this;
    }

    public String getStationName() {
        return ((Station) bartSpinner.getSelectedItem()).getName();
    }

    public String getStationAbbr() {
        return ((Station) bartSpinner.getSelectedItem()).getAbbr();
    }

    public String getDirection() {
        return directionSpinner.getSelectedItem().toString();
    }

    public boolean[] getDaysOfWeekOfInterest() {
        boolean[] daysOfWeek = new boolean[7];
        for (int i = 0; i < dayBoxes.length; ++i) {
            daysOfWeek[i] =
                    ((ColorDrawable) dayBoxes[i].getBackground()).getColor() == colorSelected;
        }
        return daysOfWeek;
    }

    @OnClick({R.id.sunday_box, R.id.monday_box, R.id.tuesday_box, R.id.wednesday_box,
            R.id.thursday_box, R.id.friday_box, R.id.saturday_box})
    public void onDayBoxClicked(TextView v) {
        int backgroundColor = ((ColorDrawable) v.getBackground()).getColor();
        if (backgroundColor == colorNotSelected) {
            v.setBackgroundColor(colorSelected);
        } else {
            v.setBackgroundColor(colorNotSelected);
        }
    }
}
