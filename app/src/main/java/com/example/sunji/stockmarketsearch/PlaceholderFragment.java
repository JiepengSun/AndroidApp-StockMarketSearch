package com.example.sunji.stockmarketsearch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView;
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 0:
                rootView = inflater.inflate(R.layout.fragment_current, container, false);
                rootView = setCurrentView(rootView);
                break;
            case 1:
                rootView = inflater.inflate(R.layout.fragment_historial, container, false);
                rootView = setHistoricalView(rootView);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_news, container, false);
                rootView = setNewsView(rootView);
                break;
            default:
                rootView = inflater.inflate(R.layout.fragment_current, container, false);
                break;
        }
        return rootView;
    }


    /**
     *      Variables
     */
    boolean addToFavList = false;
    boolean activeChange = false;

    String prevIndicator = "Price";
    String curIndicator;
    String symbol;

    String[] headers = {"Stock Symbol", "Last Price", "Change", "Timestamp", "Open", "Close", "Day's Range", "Volume"};
    String[] dataFromJS;
    String[] titleFromJS;
    String[] authorFromJS;
    String[] dateFromJS;
    String[] linkFromJS;


    /**
     *      JavaScript Interface
     */
    private class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void getStockDetailsData(String[] data) {
            dataFromJS = data;
            updateStockDetailsView();
        }

        @JavascriptInterface
        public void getHistoricalChart() {
            updateHistoricalView();
        }

        @JavascriptInterface
        public void getNewsFeed(String[] title, String[] author, String[] date, String[] link) {
            titleFromJS = title;
            authorFromJS = author;
            dateFromJS = date;
            linkFromJS = link;
            updateNewsFeed();
        }
    }


    /**
     *      Update UI
     */
    public void updateStockDetailsView() {

        final ListView stockListView = (ListView) getActivity().findViewById(R.id.stockListView);
        final WebView webView = (WebView) getActivity().findViewById(R.id.detailsChartWebView);
        final ProgressBar progressCurrentList = (ProgressBar) getActivity().findViewById(R.id.progressCurrentList);
        final ProgressBar progressCurrentChart = (ProgressBar) getActivity().findViewById(R.id.progressCurrentChart);

        // Set Header & Data
        final ArrayList<String> headerInListView = new ArrayList<>();
        final ArrayList<String> dataInListView = new ArrayList<>();
        headerInListView.addAll(Arrays.asList(headers).subList(0, 8));
        dataInListView.addAll(Arrays.asList(dataFromJS).subList(0, 8));

        // Update UI
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stockListView.setVisibility(View.VISIBLE);
                stockListView.setAdapter(new StockDetailsListViewAdapter(getActivity(), headerInListView, dataInListView));
                webView.setVisibility(View.VISIBLE);
                progressCurrentList.setVisibility(View.GONE);
                progressCurrentChart.setVisibility(View.GONE);
            }
        });
    }

    public void updateHistoricalView() {

        final WebView webView = (WebView) getActivity().findViewById(R.id.historicalChartWebView);
        final ProgressBar progressHistorical = (ProgressBar) getActivity().findViewById(R.id.progressHistoricalChart);

        //Update UI
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.setVisibility(View.VISIBLE);
                progressHistorical.setVisibility(View.GONE);
            }
        });
    }

    public void updateNewsFeed() {

        final ListView newsListView = (ListView) getActivity().findViewById(R.id.newsListView);
        final ProgressBar progressNews = (ProgressBar) getActivity().findViewById(R.id.progressNews);

        // Set Title & Author & Date
        final ArrayList<String> titleInListView = new ArrayList<>();
        final ArrayList<String> authorInListView = new ArrayList<>();
        final ArrayList<String> dateInListView = new ArrayList<>();
        final ArrayList<String> linkInListView = new ArrayList<>();
        titleInListView.addAll(Arrays.asList(titleFromJS).subList(0, titleFromJS.length));
        authorInListView.addAll(Arrays.asList(authorFromJS).subList(0, authorFromJS.length));
        dateInListView.addAll(Arrays.asList(dateFromJS).subList(0, dateFromJS.length));

        // Update UI
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newsListView.setVisibility(View.VISIBLE);
                newsListView.setAdapter(new NewsListViewAdapter(getActivity(), titleInListView, authorInListView, dateInListView));
                progressNews.setVisibility(View.GONE);
            }
        });
    }


    /**
     *      Current Page
     */
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

        // Set Stock Details List View Invisible
        ListView stockListView = (ListView) currentView.findViewById(R.id.stockListView);
        stockListView.setVisibility(View.GONE);

        // Show Details Chart Using Web View
        final WebView webView = (WebView) currentView.findViewById(R.id.detailsChartWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");

        String url = "file:///android_asset/www/getStockDetailsChart.html";
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished (WebView view, String url) {
                symbol = getActivity().getIntent().getStringExtra("symbolTitle");
                webView.loadUrl("javascript:getPrice('" + symbol + "')");
                webView.loadUrl("javascript:getNewsFeed('" + symbol + "')");
            }
        });

        // Set Web View Invisible
        webView.setVisibility(View.GONE);

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

    /**
     *      Historical Page
     */
    // HISTORICAL PAGE //
    public View setHistoricalView(View historicalView) {

        // Show Details Chart Using Web View
        final WebView webView = (WebView) historicalView.findViewById(R.id.historicalChartWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");

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

        // Set Web View Invisible
        webView.setVisibility(View.GONE);

        return historicalView;
    }

    /**
     *      News Page
     */
    // NEWS PAGE //
    public View setNewsView(View newsView) {

        // News List View
        ListView newsListView = (ListView) newsView.findViewById(R.id.newsListView);
        newsListView.setVisibility(View.GONE);

        return newsView;
    }

}
