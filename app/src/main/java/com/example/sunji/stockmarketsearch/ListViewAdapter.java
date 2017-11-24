package com.example.sunji.stockmarketsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> header = new ArrayList<>();
    private ArrayList<String> data = new ArrayList<>();

    public ListViewAdapter(@NonNull Context context, ArrayList<String> header, ArrayList<String> data) {
        this.context = context;
        this.header = header;
        this.data = data;
    }

//    public void addData(final String item) {
//        data.add(item);
//        notifyDataSetChanged();
//    }

//    public void addHeader(final String item) {
//        header.add(item);
//        notifyDataSetChanged();
//    }

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
        return view;
    }
}
