package com.example.sunji.stockmarketsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sunji on 2017/11/25.
 */

public class NewsListViewAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> author = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();

    public NewsListViewAdapter(@NonNull Context context, ArrayList<String> title, ArrayList<String> author, ArrayList<String> date) {
        this.context = context;
        this.title = title;
        this.author = author;
        this.date = date;
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int position) {
        return title.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_list_view_item, null);
        ((TextView) view.findViewById(R.id.newsItemTitle)).setText(title.get(position));
        ((TextView) view.findViewById(R.id.newsItemAuthor)).setText(author.get(position));
        ((TextView) view.findViewById(R.id.newsItemDate)).setText(date.get(position));
        return view;
    }
}
