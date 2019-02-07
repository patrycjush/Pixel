package com.ezajpat.pixel;

import java.util.ArrayList;
import java.util.TreeSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

class MyCustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    ArrayList<String> mItems = new ArrayList<String>();
    ArrayList<String> mAdditives = new ArrayList<String>();
    ArrayList<String> mPrices = new ArrayList<String>();
    TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    // View lookup cache
    private static class ViewHolder {
        TextView textItem;
        TextView textAdditive;
        TextView textPrice;
        // LinearLayout layoutToHide;
    }

    public MyCustomAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final String item, final String additive, final String price) {
    // public void addItem(final String item) {
        mItems.add(item);
        mAdditives.add(additive);
        mPrices.add(price);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        mItems.add(item);
        mAdditives.add("-");
        mPrices.add("-");
        sectionHeader.add(mItems.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public String getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.snippet_item1, null);
                    holder.textItem = (TextView) convertView.findViewById(R.id.text);
                    holder.textAdditive = (TextView) convertView.findViewById(R.id.additive);
                    holder.textPrice = (TextView) convertView.findViewById(R.id.price);
                    // holder.layoutToHide = (LinearLayout) convertView.findViewById(R.id.layout_to_hide);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.snippet_item2, null);
                    holder.textItem = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.textItem.setText(mItems.get(position));
        if(rowType == TYPE_ITEM) {
            //if(mAdditives.get(position).isEmpty()) {
            // holder.layoutToHide.setVisibility(View.GONE);
            //} else {
            //}
            holder.textAdditive.setText(mAdditives.get(position));
            holder.textPrice.setText(mPrices.get(position));
        }


        return convertView;
    }

}

