package com.app.jonathan.willimissbart.Adapters;


import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.jonathan.willimissbart.API.Models.Routes.Leg;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.LegViewHolder;

import java.util.List;

public class LegsAdapter extends BaseAdapter {
    private List<Leg> legs;

    public LegsAdapter(List<Leg> legs) {
        this.legs = legs;
    }

    @Override
    public int getCount() {
        return legs.size();
    }

    @Override
    public Leg getItem(int position) {
        return legs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LegViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leg_elem, parent, false);
            viewHolder = new LegViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LegViewHolder) convertView.getTag();
        }

        viewHolder.setUp(legs.get(position));

        return convertView;
    }
}
