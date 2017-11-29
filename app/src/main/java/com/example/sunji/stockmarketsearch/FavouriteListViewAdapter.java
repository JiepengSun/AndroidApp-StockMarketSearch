package com.example.sunji.stockmarketsearch;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by sunji on 2017/11/26.
 */

public class FavouriteListViewAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> symbol = new ArrayList<>();
    private ArrayList<String> price = new ArrayList<>();
    private ArrayList<String> change = new ArrayList<>();

    public FavouriteListViewAdapter(@NonNull Context context, ArrayList<String> symbol, ArrayList<String> price, ArrayList<String> change) {
        this.context = context;
        this.symbol = symbol;
        this.price = price;
        this.change = change;
    }

    @Override
    public int getCount() {
        return symbol.size();
    }

    @Override
    public Object getItem(int position) {
        return symbol.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fav_list_iem, null);
        ((TextView) view.findViewById(R.id.favSymbol)).setText(symbol.get(position));
        ((TextView) view.findViewById(R.id.favPrice)).setText(price.get(position));
        ((TextView) view.findViewById(R.id.favChange)).setText(change.get(position));
        if(change.get(position).contains("-")) {
            ((TextView) view.findViewById(R.id.favChange)).setTextColor(Color.parseColor("#b60006"));
        } else {
            ((TextView) view.findViewById(R.id.favChange)).setTextColor(Color.parseColor("#1e8000"));
        }
        return view;
    }
}
