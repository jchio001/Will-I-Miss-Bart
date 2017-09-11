package com.app.jonathan.willimissbart.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.jonathan.willimissbart.API.Models.Generic.SimpleListItem;
import com.app.jonathan.willimissbart.Enums.StyleEnum;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.SimpleLargeListItemViewHolder;

import java.util.List;

public class SimpleLargeTextListAdapter<T extends SimpleListItem> extends BaseAdapter {
    private Context context;
    private List<T> items;
    private StyleEnum style = StyleEnum.BART_STYLE;

    public SimpleLargeTextListAdapter(Context context, List<T> items) {
        this.context = context;
        this.items = items;
    }

    public SimpleLargeTextListAdapter setStyle(StyleEnum style) {
        this.style = style;
        return this;
    }

    public void refresh(List<T> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleLargeListItemViewHolder holder;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.simple_larger_list_item, parent, false);
            holder = new SimpleLargeListItemViewHolder(convertView)
                    .setContext(context)
                    .setUpStyle(style);
            convertView.setTag(holder);
        } else {
            holder = (SimpleLargeListItemViewHolder) convertView.getTag();
        }

        holder.textView.setText(items.get(position).getTag());

        return convertView;
    }
}
