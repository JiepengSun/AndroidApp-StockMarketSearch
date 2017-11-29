package com.example.sunji.stockmarketsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

import com.example.sunji.stockmarketsearch.model.FavouriteList;
import com.example.sunji.stockmarketsearch.util.SharedPreferences;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.facebook.FacebookSdk;

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

    String priceInFavList;
    String changeInFavList;
    String percentInFavList;

    boolean isReadyPrice = false;
    boolean isReadySMA = false;
    boolean isReadyEMA = false;
    boolean isReadySTOCH = false;
    boolean isReadyRSI = false;
    boolean isReadyADX = false;
    boolean isReadyCCI = false;
    boolean isReadyBBANDS = false;
    boolean isReadyMACD = false;

    /**
     *      JavaScript Interface
     */
    private class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void getStockDetailsData(String[] data, String change, String percent) {
            dataFromJS = data;
            priceInFavList = dataFromJS[1];
            changeInFavList = change;
            percentInFavList = percent;
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

        @JavascriptInterface
        public void priceIsReady() {
            isReadyPrice = true;
        }

        @JavascriptInterface
        public void smaIsReady() {
            isReadySMA = true;
        }

        @JavascriptInterface
        public void emaIsReady() {
            isReadyEMA = true;
        }

        @JavascriptInterface
        public void stochIsReady() {
            isReadySTOCH = true;
        }

        @JavascriptInterface
        public void rsiIsReady() {
            isReadyRSI = true;
        }

        @JavascriptInterface
        public void adxIsReady() {
            isReadyADX = true;
        }

        @JavascriptInterface
        public void cciIsReady() {
            isReadyCCI = true;
        }

        @JavascriptInterface
        public void bbandsIsReady() {
            isReadyBBANDS = true;
        }

        @JavascriptInterface
        public void macdIsReady() {
            isReadyMACD = true;
        }

        @JavascriptInterface
        public void showErrorMessage() {
            displayErrorMessage();
        }

    }

    public void displayErrorMessage() {
        //Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ProgressBar) getActivity().findViewById(R.id.progressCurrentList)).setVisibility(View.GONE);
                ((ProgressBar) getActivity().findViewById(R.id.progressCurrentChart)).setVisibility(View.GONE);
                ((ProgressBar) getActivity().findViewById(R.id.progressHistoricalChart)).setVisibility(View.GONE);
                ((ProgressBar) getActivity().findViewById(R.id.progressNews)).setVisibility(View.GONE);
                ((WebView) getActivity().findViewById(R.id.detailsChartWebView)).setVisibility(View.GONE);
                ((WebView) getActivity().findViewById(R.id.historicalChartWebView)).setVisibility(View.GONE);
                ((TextView) getActivity().findViewById(R.id.errMsgDetails)).setVisibility(View.VISIBLE);
                ((TextView) getActivity().findViewById(R.id.errMsgChart)).setVisibility(View.VISIBLE);
                ((TextView) getActivity().findViewById(R.id.errMsgHistorical)).setVisibility(View.VISIBLE);
                ((TextView) getActivity().findViewById(R.id.errMsgNews)).setVisibility(View.VISIBLE);
            }
        });
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

        // Active Spinner
        final Spinner spinner = (Spinner) getActivity().findViewById(R.id.indicatorSpinner);

        // Active Buttons
        final ImageView facebook = (ImageView) getActivity().findViewById(R.id.imageFacebook);
        final ImageView favourite = (ImageView) getActivity().findViewById(R.id.imageFavourite);

        // Update UI
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stockListView.setVisibility(View.VISIBLE);
                stockListView.setAdapter(new StockDetailsListViewAdapter(getActivity(), headerInListView, dataInListView));
                webView.setVisibility(View.VISIBLE);
                progressCurrentList.setVisibility(View.GONE);
                progressCurrentChart.setVisibility(View.GONE);
                spinner.setEnabled(true);
                facebook.getDrawable().setAlpha(255);
                favourite.getDrawable().setAlpha(255);
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
        linkInListView.addAll(Arrays.asList(linkFromJS).subList(0, linkFromJS.length));

        // Update UI
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newsListView.setVisibility(View.VISIBLE);
                newsListView.setAdapter(new NewsListViewAdapter(getActivity(), titleInListView, authorInListView, dateInListView));
                progressNews.setVisibility(View.GONE);
            }
        });

        // Click List and Go To Browser
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkInListView.get(position)));
                startActivity(browserIntent);
            }
        });
    }


    /**
     *      Current Page
     */
    private List<FavouriteList> favouriteLists;
    private static final String SHARED_PREFERENCE_KEY = "shared_preference_keys";

    // CURRENT PAGE //
    public View setCurrentView(View currentView) {

        symbol = StockDetailsActivity.getSymbol();

        // Load Shared Preferences
        List<FavouriteList> savedFavouriteLists = SharedPreferences.read(getContext(), SHARED_PREFERENCE_KEY, new TypeToken<List<FavouriteList>>(){});
        favouriteLists = savedFavouriteLists == null ? new ArrayList<FavouriteList>() : savedFavouriteLists;

        final Set<String> favSymbol = new HashSet<String>();
        for(int i = 0; i < favouriteLists.size(); i++) {
            favSymbol.add(favouriteLists.get(i).symbol);
        }

        // Init Current View
        if(favSymbol.contains(symbol)) {
            ((ImageView) currentView.findViewById(R.id.imageFavourite)).setImageResource(R.drawable.ic_star_black_24px);
        }
        if(!activeChange) {
            ((TextView) currentView.findViewById(R.id.change)).setTextColor(Color.parseColor("#a8a8a8"));
        }

        // Disable Button When Loading
        ((ImageView) currentView.findViewById(R.id.imageFacebook)).getDrawable().setAlpha(64);
        ((ImageView) currentView.findViewById(R.id.imageFavourite)).getDrawable().setAlpha(64);

        // Hide Error Message
        ((TextView) currentView.findViewById(R.id.errMsgDetails)).setVisibility(View.GONE);
        ((TextView) currentView.findViewById(R.id.errMsgChart)).setVisibility(View.GONE);

        // Favourite List Button
        final ImageView imageFav = (ImageView) currentView.findViewById(R.id.imageFavourite);
        imageFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isReadyPrice) {
                    return;
                }
                if(!favSymbol.contains(symbol)) {
                    imageFav.setImageResource(R.drawable.ic_star_black_24px);

                    // Add to FavList
                    favSymbol.add(symbol);
                    FavouriteList currentList = new FavouriteList();
                    currentList.symbol = symbol;
                    currentList.price = priceInFavList;
                    currentList.change = changeInFavList;
                    currentList.changePercent = percentInFavList;
                    favouriteLists.add(currentList);
                    SharedPreferences.save(getContext(), SHARED_PREFERENCE_KEY, favouriteLists);
                } else {
                    imageFav.setImageResource(R.drawable.ic_star_border_black_24px);

                    // Delete From FavList
                    favSymbol.remove(symbol);
                    for(int i = 0; i < favouriteLists.size(); i++) {
                        if(favouriteLists.get(i).symbol.equals(symbol)) {
                            favouriteLists.remove(i);
                            SharedPreferences.save(getContext(), SHARED_PREFERENCE_KEY, favouriteLists);
                        }
                    }
                }
            }
        });

        // Facebook button
        ImageView imageFacebook = (ImageView) currentView.findViewById(R.id.imageFacebook);
        imageFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                //webView.loadUrl("javascript:getNewsFeed('" + symbol + "')");
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
        spinner.setEnabled(false);

        // Change Button
        final TextView change = (TextView) currentView.findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeChange) {
                    // Set Color
                    change.setTextColor(Color.parseColor("#a8a8a8"));
                    activeChange = false;

                    // Call JavaScript Functions
                    String spinnerText = spinner.getSelectedItem().toString();
                    String url = "javascript:getPrice('" + symbol + "')";
                    switch (spinnerText) {
                        case "Price":
                            url = !isReadyPrice ? "javascript:getPrice('" + symbol + "')" : "javascript:drawPriceChart()";
                            break;
                        case "SMA":
                            url = !isReadySMA ? "javascript:getSMA()" : "javascript:drawSMAChart()";
                            break;
                        case "EMA":
                            url = !isReadyEMA ? "javascript:getEMA()" : "javascript:drawEMAChart()";
                            break;
                        case "STOCH":
                            url = !isReadySTOCH ? "javascript:getSTOCH()" : "javascript:drawSTOCHChart()";
                            break;
                        case "RSI":
                            url = !isReadyRSI ? "javascript:getRSI()" : "javascript:drawRSIChart()";
                            break;
                        case "ADX":
                            url = !isReadyADX ? "javascript:getADX()" : "javascript:drawADXChart()";
                            break;
                        case "CCI":
                            url = !isReadyCCI ? "javascript:getCCI()" : "javascript:drawCCIChart()";
                            break;
                        case "BBANDS":
                            url = !isReadyBBANDS ? "javascript:getBBANDS()" : "javascript:drawBBANDSChart()";
                            break;
                        case "MACD":
                            url = !isReadyMACD ? "javascript:getMACD()" : "javascript:drawMACDChart()";
                            break;
                    }
                    webView.loadUrl(url);
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

        // Hide Error Message
        ((TextView) historicalView.findViewById(R.id.errMsgHistorical)).setVisibility(View.GONE);

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

        // Hide Error Message
        ((TextView) newsView.findViewById(R.id.errMsgNews)).setVisibility(View.GONE);

        return newsView;
    }

}
