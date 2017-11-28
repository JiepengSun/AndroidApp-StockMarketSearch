package com.example.sunji.stockmarketsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunji.stockmarketsearch.model.FavouriteList;
import com.example.sunji.stockmarketsearch.util.SharedPreferences;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_STOCK_DETAILS_ACTIVITY = 100;

    private List<FavouriteList> favouriteLists;
    private static final String SHARED_PREFERENCE_KEY = "shared_preference_keys";

    private Timer timer;

    String[] dataRefresh;
    String priceRefresh;
    String changeRefresh;
    String percentRefresh;

    private boolean isAutoRefresh = false;

    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };

    private class jsInterface {
        Context mContext;

        jsInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void getRefreshData(String[] data, String change, String percent) {
            dataRefresh = data;
            priceRefresh = dataRefresh[1];
            changeRefresh = change;
            percentRefresh = percent;
            refreshFavList(dataRefresh[0], priceRefresh, changeRefresh, percentRefresh);
        }

        @JavascriptInterface
        public void showDebugMessage() {
            Toast.makeText(MainActivity.this, "Test", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshFavList(String symbol, String price, String change, String percent) {
        Toast.makeText(MainActivity.this, symbol + " " + price + " " + change + " " + percent, Toast.LENGTH_LONG).show();
        for (int i = 0; i < favouriteLists.size(); i++) {
            if (favouriteLists.get(i).symbol.equals(symbol)) {
                favouriteLists.get(i).price = price;
                favouriteLists.get(i).change = change;
                favouriteLists.get(i).changePercent = percent;
            }
        }
        updateFavList();
        SharedPreferences.save(this, SHARED_PREFERENCE_KEY, favouriteLists);
    }

    public void updateFavList() {

        final ArrayList<String> symbolInListView = new ArrayList<>();
        final ArrayList<String> priceInListView = new ArrayList<>();
        final ArrayList<String> changeInListView = new ArrayList<>();

        for(int i = 0; i < favouriteLists.size(); i++) {
            symbolInListView.add(favouriteLists.get(i).symbol);
            priceInListView.add(favouriteLists.get(i).price);
            changeInListView.add(favouriteLists.get(i).change + " (" + favouriteLists.get(i).changePercent + "%) ");
        }

        final ListView favListView = (ListView) findViewById(R.id.favListView);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                favListView.setAdapter(new FavouriteListViewAdapter(MainActivity.this, symbolInListView, priceInListView, changeInListView));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load Data //
        List<FavouriteList> savedFavouriteLists = SharedPreferences.read(this, SHARED_PREFERENCE_KEY, new TypeToken<List<FavouriteList>>(){});
        favouriteLists = savedFavouriteLists == null ? new ArrayList<FavouriteList>() : savedFavouriteLists;

        // Favourite List
        ListView favListView = (ListView) findViewById(R.id.favListView);
        registerForContextMenu(favListView);
        updateFavList();

        // Click Event
        favListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, StockDetailsActivity.class);
                intent.putExtra("symbolTitle", favouriteLists.get(position).symbol);
                startActivityForResult(intent, REQ_CODE_STOCK_DETAILS_ACTIVITY);
            }
        });

        // Click Get Quote Button
        TextView getQuote = (TextView) findViewById(R.id.getQuote);
        getQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText) findViewById(R.id.stockQuoteInput);
                String input = text.getText().toString();
                if(!input.isEmpty()) {
                    // Toast.makeText(MainActivity.this, "The Inout is: " + input, Toast.LENGTH_LONG).show();
                    // Active Stock Details Activity
                    Intent intent = new Intent(MainActivity.this, StockDetailsActivity.class);
                    intent.putExtra("symbolTitle", input);
                    startActivityForResult(intent, REQ_CODE_STOCK_DETAILS_ACTIVITY);
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter a Stock Name or Symbol", Toast.LENGTH_LONG).show();
                }

            }
        });

        // Click Clear Button
        TextView clear = (TextView) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //Toast.makeText(MainActivity.this, "Click the Clear Button", Toast.LENGTH_LONG).show();
                 String newString = "";
                 ((TextView) findViewById(R.id.stockQuoteInput)).setText(newString);
             }
         });

       //String[] COUNTRIES = { "Belgium", "France", "Italy", "Germany", "Spain" }
        // Set Autocomplete Menu
        EditText edit = (EditText) findViewById(R.id.stockQuoteInput);
        ArrayAdapter<String> autocompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(autocompleteAdapter);

        // Set Spinner
        Spinner sortSpinner = (Spinner) findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> sortSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sortSpinner, android.R.layout.simple_spinner_item);
        sortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortSpinnerAdapter);
        sortSpinner.setOnItemSelectedListener(new MainActivity.sortSpinnerOnItemSelectedListener());

        Spinner orderSpinner = (Spinner) findViewById(R.id.orderSpinner);
        ArrayAdapter<CharSequence> orderSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.orderSpinner, android.R.layout.simple_spinner_item);
        orderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderSpinnerAdapter);
        orderSpinner.setOnItemSelectedListener(new MainActivity.orderSpinnerOnItemSelectedListener());
        if(favouriteLists.size() == 0) {
            sortSpinner.setEnabled(false);
            orderSpinner.setEnabled(false);
        }

        // Fake Web View
        WebView webView = (WebView) findViewById(R.id.mainWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new jsInterface(this), "Android");
        String url = "file:///android_asset/www/refresh.html";
        webView.loadUrl(url);

        // Auto Refresh
        Switch autoRefresh = (Switch) findViewById(R.id.autoRefreshSwitch);
        autoRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAutoRefresh) {
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    jsRefreshFavList();
                                }
                            });
                        }
                    }, 0, 10000);
                    isAutoRefresh = true;
                } else {
                    timer.cancel();
                    isAutoRefresh = false;
                }
            }
        });

        // Refresh
        ImageView imageRefresh = (ImageView) findViewById(R.id.imageRefresh);
        imageRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsRefreshFavList();
            }
        });
    }


    public void jsRefreshFavList() {
        WebView webView = (WebView) findViewById(R.id.mainWebView);
        for (int i = 0; i < favouriteLists.size(); i++) {
            webView.loadUrl("javascript:refreshFavList('" + favouriteLists.get(i).symbol + "')");
        }
    }




    /**
     *      List Menu
     */

    // Create Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.favListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    // Click Menu To Delete
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.menuTitle:
                return true;
            case R.id.menuNo:
                Toast.makeText(MainActivity.this, info.position + "", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menuYes:
                favouriteLists.remove(info.position);
                SharedPreferences.save(this, SHARED_PREFERENCE_KEY, favouriteLists);
                updateFavList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    /**
     *      Spinner Listener
     */

    private boolean isDefault;
    private String typeSelected = "Default";
    private int orderSelected = 1;
    // Sort
    public class sortSpinnerOnItemSelectedListener extends Activity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            isDefault = parent.getItemAtPosition(pos).toString().equals("Default");
            if(isDefault) {
                return;
            }
            typeSelected = parent.getItemAtPosition(pos).toString();
            sortList(typeSelected, orderSelected);
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    // Order
    public class orderSpinnerOnItemSelectedListener extends Activity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            orderSelected = parent.getItemAtPosition(pos).toString().equals("Ascending") ? 1 : -1;
            if(!isDefault) {
                sortList(typeSelected, orderSelected);
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private Map<Float, Integer> mapFloat = new HashMap<>();
    private Map<String, Integer> mapString = new HashMap<>();

    public void sortList(String indicator, int order) {
        // Sort
        Float[] arrayToSortFloat = new Float[favouriteLists.size()];
        String[] arrayToSortString = new String[favouriteLists.size()];

        switch (indicator) {
            case "Symbol":
                for (int position = 0; position < favouriteLists.size(); position++) {
                    mapString.put(favouriteLists.get(position).symbol, position);
                    arrayToSortString[position] = favouriteLists.get(position).symbol;
                }
                break;
            case "Price":
                for (int position = 0; position < favouriteLists.size(); position++) {
                    mapFloat.put(Float.parseFloat(favouriteLists.get(position).price), position);
                    arrayToSortFloat[position] = Float.parseFloat(favouriteLists.get(position).price);
                }
                break;
            case "Change":
                for (int position = 0; position < favouriteLists.size(); position++) {
                    mapFloat.put(Float.parseFloat(favouriteLists.get(position).change), position);
                    arrayToSortFloat[position] = Float.parseFloat(favouriteLists.get(position).change);
                }
                break;
            case "Change Percent":
                for (int position = 0; position < favouriteLists.size(); position++) {
                    mapFloat.put(Float.parseFloat(favouriteLists.get(position).changePercent), position);
                    arrayToSortFloat[position] = Float.parseFloat(favouriteLists.get(position).changePercent);
                }
                break;
        }

        List<FavouriteList> currentLists = new ArrayList<>();

        switch (indicator) {
            case "Symbol" :
                if (order > 0) {
                    Arrays.sort(arrayToSortString);
                } else {
                    Arrays.sort(arrayToSortString);
                    List<String> tempList = Arrays.asList(arrayToSortString);
                    Collections.reverse(tempList);
                    arrayToSortString = tempList.toArray(arrayToSortString);
                }
                for (int position = 0; position < favouriteLists.size(); position++) {
                    FavouriteList tempItem = new FavouriteList();
                    tempItem.symbol = favouriteLists.get(mapString.get(arrayToSortString[position])).symbol;
                    tempItem.price = favouriteLists.get(mapString.get(arrayToSortString[position])).price;
                    tempItem.change = favouriteLists.get(mapString.get(arrayToSortString[position])).change;
                    tempItem.changePercent = favouriteLists.get(mapString.get(arrayToSortString[position])).changePercent;;
                    currentLists.add(tempItem);
                }
                break;
            case "Price":
            case "Change":
            case "Change Percent":
                if (order > 0) {
                    Arrays.sort(arrayToSortFloat);
                } else {
                    Arrays.sort(arrayToSortFloat);
                    List<Float> tempList = Arrays.asList(arrayToSortFloat);
                    Collections.reverse(tempList);
                    arrayToSortFloat = tempList.toArray(arrayToSortFloat);
                }
                for (int position = 0; position < favouriteLists.size(); position++) {
                    FavouriteList tempItem = new FavouriteList();
                    tempItem.symbol = favouriteLists.get(mapFloat.get(arrayToSortFloat[position])).symbol;
                    tempItem.price = favouriteLists.get(mapFloat.get(arrayToSortFloat[position])).price;
                    tempItem.change = favouriteLists.get(mapFloat.get(arrayToSortFloat[position])).change;
                    tempItem.changePercent = favouriteLists.get(mapFloat.get(arrayToSortFloat[position])).changePercent;
                    currentLists.add(tempItem);
                }
                break;
        }

        favouriteLists = currentLists;
        updateFavList();
        SharedPreferences.save(this, SHARED_PREFERENCE_KEY, favouriteLists);
    }
}
