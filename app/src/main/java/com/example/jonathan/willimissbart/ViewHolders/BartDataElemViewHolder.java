package com.example.jonathan.willimissbart.ViewHolders;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jonathan.willimissbart.API.Models.StationModels.Station;
import com.example.jonathan.willimissbart.Adapters.StationSpinnerAdapter;
import com.example.jonathan.willimissbart.Adapters.StringAdapter;
import com.example.jonathan.willimissbart.R;

import org.w3c.dom.Text;

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

    private int colorSelected;
    private int colorNotSelected;

    public BartDataElemViewHolder(View v) {
        ButterKnife.bind(this, v);
    }

    public BartDataElemViewHolder setColorSelected(int colorSelected) {
        this.colorSelected = colorSelected;
        return this;
    }

    public BartDataElemViewHolder setColorNotSelected(int colorNotSelected) {
        this.colorNotSelected = colorNotSelected;
        return this;
    }

    public BartDataElemViewHolder setBartSpinnerAdapter(StationSpinnerAdapter adapter) {
        bartSpinner.setAdapter(adapter);
        return this;
    }

    public BartDataElemViewHolder setDirectionSpinnerAdapter(StringAdapter adapter) {
        directionSpinner.setAdapter(adapter);
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
