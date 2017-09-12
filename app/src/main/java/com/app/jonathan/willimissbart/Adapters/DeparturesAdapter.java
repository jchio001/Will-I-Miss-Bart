package com.app.jonathan.willimissbart.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.jonathan.willimissbart.API.Models.DeparturesFeedModels.FlattenedEstimate;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.DeparturesCardViewHolder;

import java.util.List;

public class DeparturesAdapter extends Adapter<DeparturesCardViewHolder> {
    private List<FlattenedEstimate> estimates;

    public DeparturesAdapter(List<FlattenedEstimate> estimates) {
        this.estimates = estimates;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return estimates.size();
    }

    @Override
    public DeparturesCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.departures_card_layout, parent, false);
        return new DeparturesCardViewHolder(v, parent.getContext());
    }

    @Override
    public void onBindViewHolder(DeparturesCardViewHolder holder, int position) {
        holder.setUp(estimates.get(position));
    }

    public void refresh(List<FlattenedEstimate> newEstimates) {
        estimates.clear();
        estimates.addAll(newEstimates);
        notifyDataSetChanged();
    }
}
