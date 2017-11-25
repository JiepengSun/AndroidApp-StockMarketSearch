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

    View prevCurrentView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current, container, false);;
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 1:
//                if(!alreadyGetCurrent) {
//                    rootView = inflater.inflate(R.layout.fragment_current, container, false);
//                    rootView = setCurrentView(rootView);
//                    prevCurrentView = rootView;
//                    alreadyGetCurrent = true;
//                } else {
//                    rootView = setCurrentView(prevCurrentView);
//                    prevCurrentView = rootView;
//                }
                rootView = inflater.inflate(R.layout.fragment_current, container, false);
                rootView = setCurrentView(rootView);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_historial, container, false);
                rootView = setHistoricalView(rootView);
                break;
            case 3:
                rootView = inflater.inflate(R.layout.fragment_news, container, false);
                break;
        }
        return rootView;
    }

    // Global Variables //
    boolean addToFavList = false;
    boolean activeChange = false;
    String prevIndicator = "Price";
    String curIndicator;
    String symbol;

    boolean alreadyGetCurrent = false;

    // CURRENT PAGE //
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
        final WebView webView = (WebView) currentView.findViewById(R.id.detailsChartWebView);
        webView.getSettings().setJavaScriptEnabled(true);

        String url = "file:///android_asset/www/getStockDetailsChart.html";
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished (WebView view, String url) {
                symbol = getActivity().getIntent().getStringExtra("symbolTitle");
                webView.loadUrl("javascript:getPrice('" + symbol + "')");
                //Toast.makeText(getActivity(), "The symbol is: " + symbol, Toast.LENGTH_LONG).show();
            }
        });

//        if(!alreadyGetCurrent) {
//            String url = "file:///android_asset/www/getStockDetailsChart.html";
//            webView.loadUrl(url);
//            webView.setWebViewClient(new WebViewClient() {
//                @Override
//                public void onPageFinished (WebView view, String url) {
//                    symbol = getActivity().getIntent().getStringExtra("symbolTitle");
//                    webView.loadUrl("javascript:getPrice('" + symbol + "')");
//                    //Toast.makeText(getActivity(), "The symbol is: " + symbol, Toast.LENGTH_LONG).show();
//                }
//            });
//            alreadyGetCurrent = true;
//        } else {
//            webView.loadUrl("javascript:drawChart()");
//        }

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
                    // Set Color
                    change.setTextColor(Color.parseColor("#868686"));
                    activeChange = false;

                    // Call JavaScript Functions
                    String spinnerText = spinner.getSelectedItem().toString();
                    switch (spinnerText) {
                        case "Price":
                            webView.loadUrl("javascript:getPrice('" + symbol + "')");
                            break;
                        case "SMA":
                            webView.loadUrl("javascript:getSMA('" + symbol + "')");
                            break;
                        case "EMA":
                            webView.loadUrl("javascript:getEMA('" + symbol + "')");
                            break;
                        case "STOCH":
                            webView.loadUrl("javascript:getSTOCH('" + symbol + "')");
                            break;
                        case "RSI":
                            webView.loadUrl("javascript:getRSI('" + symbol + "')");
                            break;
                        case "ADX":
                            webView.loadUrl("javascript:getADX('" + symbol + "')");
                            break;
                        case "CCI":
                            webView.loadUrl("javascript:getCCI('" + symbol + "')");
                            break;
                        case "BBANDS":
                            webView.loadUrl("javascript:getBBANDS('" + symbol + "')");
                            break;
                        case "MACD":
                            webView.loadUrl("javascript:getMACD('" + symbol + "')");
                            break;
                    }



                }
            }
        });

        return currentView;
    }

    // HISTORICAL PAGE //
    public View setHistoricalView(View historicalView) {

        // Show Details Chart Using Web View
        final WebView webView = (WebView) historicalView.findViewById(R.id.historicalChartWebView);
        webView.getSettings().setJavaScriptEnabled(true);

        String url = "file:///android_asset/www/getHistoricalChart.html";
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished (WebView view, String url) {
                symbol = getActivity().getIntent().getStringExtra("symbolTitle");
                webView.loadUrl("javascript:getHistoricalChart('" + symbol + "')");
                //Toast.makeText(getActivity(), "The symbol is: " + symbol, Toast.LENGTH_LONG).show();
            }
        });

        return historicalView;
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
