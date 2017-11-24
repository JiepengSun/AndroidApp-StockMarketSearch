package com.example.sunji.stockmarketsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 1:
                rootView = inflater.inflate(R.layout.fragment_current, container, false);
                rootView = setCurrentView(rootView);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_historial, container, false);
                break;
            default:
                rootView = inflater.inflate(R.layout.fragment_news, container, false);
                break;
//          default:
//              rootView = inflater.inflate(R.layout.fragment_stock_details, container, false);
//              TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//              textView.setText("Hello World");
        }
        return rootView;
    }

    boolean addToFavList = false;

    // CURRENT PAGE //
    public View setCurrentView(View currentView) {

        // Add to Favourite List
        final ImageView imageFav = (ImageView) currentView.findViewById(R.id.imageFavourite);
        imageFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addToFavList) {
                    imageFav.setImageResource(R.drawable.ic_star_black_24px);
                    addToFavList = true;
                    String actionbarTitle = getActivity().getIntent().getStringExtra("symbolTitle");
                    //Toast.makeText(getActivity(), "The Inout is: " + actionbarTitle, Toast.LENGTH_LONG).show();
                } else {
                    imageFav.setImageResource(R.drawable.ic_star_border_black_24px);
                    addToFavList = false;
                }
            }
        });

        // Stock Details List View
        ListView stockListView = (ListView) currentView.findViewById(R.id.stockListView);

        // Set Header
        ArrayList<String> header = new ArrayList<>();
        header.add("Stock Symbol");
        header.add("Last Price");
        header.add("Change");
        header.add("Timestamp");
        header.add("Open");
        header.add("Close");
        header.add("Day's Range");
        header.add("Volume");

        // Set Data
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            data.add("123");
        }

        stockListView.setAdapter(new ListViewAdapter(getActivity(), header, data));

        return currentView;
    }
}
