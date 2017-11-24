package com.example.sunji.stockmarketsearch;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
        View rootView = inflater.inflate(R.layout.fragment_current, container, false);;
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 1:
                rootView = inflater.inflate(R.layout.fragment_current, container, false);
                rootView = setCurrentView(rootView);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_historial, container, false);
                break;
            case 3:
                rootView = inflater.inflate(R.layout.fragment_news, container, false);
                break;
        }
        return rootView;
    }

    // CURRENT PAGE //
    boolean addToFavList = false;
    boolean activeChange = false;
    String prevIndicator = "Price";
    String curIndicator;

    public View setCurrentView(View currentView) {

        // Init Current View
        if(addToFavList) {
            ((ImageView) currentView.findViewById(R.id.imageFavourite)).setImageResource(R.drawable.ic_star_black_24px);
        }
        if(!activeChange) {
            ((TextView) currentView.findViewById(R.id.change)).setTextColor(Color.parseColor("#868686"));
        }


        // Favourite List Button
        final ImageView imageFav = (ImageView) currentView.findViewById(R.id.imageFavourite);
        imageFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addToFavList) {
                    imageFav.setImageResource(R.drawable.ic_star_black_24px);
                    addToFavList = true;
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


        // Show Details Chart Using Web View
        //String url = "https://www.google.com";
        String url = "file:///android_asset/www/index.html";
        //String url = "http://stockmarketsearch-env.us-west-1.elasticbeanstalk.com/getStockQuote?symbol=AAPL";
        final WebView webView = (WebView) currentView.findViewById(R.id.detailsChartWebView);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished (WebView view, String url) {
                //Toast.makeText(getActivity(), "!!!!!!" , Toast.LENGTH_LONG).show();
                webView.loadUrl("javascript:callFromActivity()");
            }
        });





        // Indicator Spinner
        final Spinner spinner = (Spinner) currentView.findViewById(R.id.indicatorSpinner);
        // Spinner Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.indicatorSpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // Spinner Selected Listener
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());


        // Change Button
        final TextView change = (TextView) currentView.findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeChange) {
                    String text = spinner.getSelectedItem().toString();
                    Toast.makeText(getActivity(), "Indicator is: " + text, Toast.LENGTH_LONG).show();
                    change.setTextColor(Color.parseColor("#868686"));
                    activeChange = false;
                }
            }
        });

        return currentView;
    }

    // Spinner Item Selected Listener
    public class CustomOnItemSelectedListener extends Activity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            curIndicator = parent.getItemAtPosition(pos).toString();
            if(!curIndicator.equals(prevIndicator)) {
                activeChange = true;
                ((TextView) getActivity().findViewById(R.id.change)).setTextColor(Color.parseColor("#000000"));
                prevIndicator = curIndicator;
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

}
