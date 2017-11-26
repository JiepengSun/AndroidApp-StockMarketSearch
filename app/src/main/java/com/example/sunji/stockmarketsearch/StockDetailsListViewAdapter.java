package com.example.sunji.stockmarketsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class StockDetailsListViewAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> header = new ArrayList<>();
    private ArrayList<String> data = new ArrayList<>();

    public StockDetailsListViewAdapter(@NonNull Context context, ArrayList<String> header, ArrayList<String> data) {
        this.context = context;
        this.header = header;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.stock_list_view_item, null);
        ((TextView) view.findViewById(R.id.stockItemHeader)).setText(header.get(position));
        ((TextView) view.findViewById(R.id.stockItemContent)).setText(data.get(position));
        //((ImageView) view.findViewById(R.id.stockItemArrow)).setImageResource(R.mipmap.down_icon);
        if(position == 2) {
            if(!data.get(position).contains("-")) {
                ((ImageView) view.findViewById(R.id.stockItemArrow)).setImageResource(R.mipmap.up_icon);
            } else {
                ((ImageView) view.findViewById(R.id.stockItemArrow)).setImageResource(R.mipmap.down_icon);
            }
        }
        return view;
    }
}
