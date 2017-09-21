package com.app.jonathan.willimissbart.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.jonathan.willimissbart.Enums.StyleEnum;
import com.app.jonathan.willimissbart.R;
import com.app.jonathan.willimissbart.ViewHolders.SimpleLargeListItemViewHolder;

import java.util.List;

public class StringAdapter extends BaseAdapter {
    private Context context;
    private List<String> stringList;
    private StyleEnum style = StyleEnum.BART_STYLE;

    public StringAdapter(Context context, List<String> stringList) {
        this.context = context;
        this.stringList = stringList;
    }

    public StringAdapter setStyle(StyleEnum style) {
        this.style = style;
        return this;
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public String getItem(int position) {
        return stringList.get(position);
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

        holder.textView.setText(stringList.get(position));

        return convertView;
    }
}
