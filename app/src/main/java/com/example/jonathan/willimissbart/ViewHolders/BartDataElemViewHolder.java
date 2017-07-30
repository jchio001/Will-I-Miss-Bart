package com.example.jonathan.willimissbart.ViewHolders;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jonathan.willimissbart.API.Models.StationModels.Station;
import com.example.jonathan.willimissbart.Adapters.SimpleLargeTextListAdapter;
import com.example.jonathan.willimissbart.Adapters.StringAdapter;
import com.example.jonathan.willimissbart.Enums.StyleEnum;
import com.example.jonathan.willimissbart.Dialogs.DeleteAlertDialog;
import com.example.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.example.jonathan.willimissbart.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class BartDataElemViewHolder {
    @Bind(R.id.day_of_week_layout) public LinearLayout dayOfWeekLayout;
    @Bind(R.id.bart_spinner) public Spinner bartSpinner;
    @Bind(R.id.direction_spinner) public Spinner directionSpinner;
    @Bind({R.id.sunday_box, R.id.monday_box,
            R.id.tuesday_box, R.id.wednesday_box,
            R.id.thursday_box, R.id.friday_box,
            R.id.saturday_box}) public TextView[] dayBoxes;
    @Bind(R.id.divider) View divider;

    private Context context;
    private DeleteAlertDialog.DeleteDataElemListener listener;
    private StyleEnum style = StyleEnum.BART_STYLE;

    private int index;
    private int colorSelected = -1;
    private int colorNotSelected = -1;

    public BartDataElemViewHolder(View v,
                                  Context context,
                                  DeleteAlertDialog.DeleteDataElemListener listener,
                                  int index) {
        ButterKnife.bind(this, v);
        this.context = context;
        this.listener = listener;
        this.index = index;
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

    @OnLongClick(R.id.bart_data_elem_parent)
    public boolean onLongClickParent() {
        new DeleteAlertDialog(context, listener, index).show();
        return true;
    }

    //ghetto builder pattern
    public BartDataElemViewHolder build(UserBartData data) {
        colorNotSelected = ContextCompat.getColor(context, android.R.color.transparent);
        if (style != StyleEnum.BART_STYLE) {
            colorSelected = ContextCompat.getColor(context, R.color.lightGrey);
            int black = ContextCompat.getColor(context, R.color.black);

            GradientDrawable drawable =
                    (GradientDrawable) ContextCompat.getDrawable(
                            context,
                            R.drawable.white_rounded_background
                    );
            drawable.setStroke(2, black);
            bartSpinner.setBackground(drawable);
            directionSpinner.setBackground(drawable);
            divider.setBackgroundColor(black);

            for (TextView dayBox : dayBoxes) {
                dayBox.setTextColor(black);
            }
        } else {
            colorSelected = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        }

        if (data != null) {
            bartSpinner.setSelection(data.getStationIndex());
            directionSpinner.setSelection(data.getDirectionIndex());
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

    public int getStationIndex() {
        return bartSpinner.getSelectedItemPosition();
    }

    public String getStationAbbr() {
        return ((Station) bartSpinner.getSelectedItem()).getAbbr();
    }

    public String getDirection() {
        return directionSpinner.getSelectedItem().toString();
    }

    public int getDirectionIndex() {
        return directionSpinner.getSelectedItemPosition();
    }

    //This would take 1 line of code with lambas...
    public boolean[] getDaysOfWeekOfInterest() {
        boolean[] daysOfWeek = new boolean[7];
        for (int i = 0; i < dayBoxes.length; ++i) {
            daysOfWeek[i] =
                    ((ColorDrawable) dayBoxes[i].getBackground()).getColor() == colorSelected;
        }
        return daysOfWeek;
    }

    public UserBartData getDataFromView() {
        Station selectedStation = (Station) bartSpinner.getSelectedItem();
        return new UserBartData()
                .setStation(selectedStation.getName())
                .setStationIndex(bartSpinner.getSelectedItemPosition())
                .setAbbr(selectedStation.getAbbr())
                .setDirection((String) directionSpinner.getSelectedItem())
                .setDirectionIndex(directionSpinner.getSelectedItemPosition())
                .setDays(getDaysOfWeekOfInterest());
    }

    public void decrementIndex() {
        --index;
    }
}
