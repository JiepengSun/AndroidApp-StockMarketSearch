package com.example.sunji.stockmarketsearch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_STOCK_DETAILS_ACTIVITY = 100;

    private List<FavouriteList> favouriteLists;
    private static final String SHARED_PREFERENCE_KEY = "shared_preference_keys";

    private Spinner sortSpinner;
    private Spinner orderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////////////////////////////////////////////////////////////////////////////

        // Load Data //
        List<FavouriteList> savedFavouriteLists = SharedPreferences.read(this, SHARED_PREFERENCE_KEY, new TypeToken<List<FavouriteList>>(){});
        favouriteLists = savedFavouriteLists == null ? new ArrayList<FavouriteList>() : savedFavouriteLists;

        final ArrayList<String> symbolInListView = new ArrayList<>();
        final ArrayList<String> priceInListView = new ArrayList<>();
        final ArrayList<String> changeInListView = new ArrayList<>();

        for(int i = 0; i < favouriteLists.size(); i++) {
            symbolInListView.add(favouriteLists.get(i).symbol);
            priceInListView.add(favouriteLists.get(i).price);
            changeInListView.add(favouriteLists.get(i).change + " (" + favouriteLists.get(i).changePercent + "%) ");
        }



        // Favourite List
        final ListView favListView = (ListView) findViewById(R.id.favListView);
        favListView.setAdapter(new FavouriteListViewAdapter(this, symbolInListView, priceInListView, changeInListView));

        // Click Event
        favListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "Click: " + position, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, StockDetailsActivity.class);
                intent.putExtra("symbolTitle", favouriteLists.get(position).symbol);
                startActivityForResult(intent, REQ_CODE_STOCK_DETAILS_ACTIVITY);
            }
        });


        ///////////////////////////////////////////////////////////////////////////////

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

        // Set Spinner
        sortSpinner = (Spinner) findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> sortSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sortSpinner, android.R.layout.simple_spinner_item);
        sortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortSpinnerAdapter);
        sortSpinner.setOnItemSelectedListener(new MainActivity.sortSpinnerOnItemSelectedListener());

        orderSpinner = (Spinner) findViewById(R.id.orderSpinner);
        ArrayAdapter<CharSequence> orderSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.orderSpinner, android.R.layout.simple_spinner_item);
        orderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderSpinnerAdapter);
        orderSpinner.setOnItemSelectedListener(new MainActivity.orderSpinnerOnItemSelectedListener());

        // Set Spinners
        if(favouriteLists.size() == 0) {
            sortSpinner.setEnabled(false);
            orderSpinner.setEnabled(false);
        }


    }

    /**
     *      Spinner Listener
     */

    private boolean isDefault;
    private boolean isAscending = true;
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

            if(isDefault) {
                return;
            }
            //Toast.makeText(MainActivity.this, parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
            orderSelected = parent.getItemAtPosition(pos).toString().equals("Ascending") ? 1 : -1;
            sortList(typeSelected, orderSelected);
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public void sortList(String indicator, int order) {
        // Sort
        Map<Float, Integer> mapFloat = new HashMap<>();
        Map<String, Integer> mapString = new HashMap<>();
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

        final ArrayList<String> symbolInListView = new ArrayList<>();
        final ArrayList<String> priceInListView = new ArrayList<>();
        final ArrayList<String> changeInListView = new ArrayList<>();

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
                    symbolInListView.add(favouriteLists.get(mapString.get(arrayToSortString[position])).symbol);
                    priceInListView.add(favouriteLists.get(mapString.get(arrayToSortString[position])).price);
                    changeInListView.add(favouriteLists.get(mapString.get(arrayToSortString[position])).change + " (" + favouriteLists.get(mapString.get(arrayToSortString[position])).changePercent + "%) ");
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
                    symbolInListView.add(favouriteLists.get(mapFloat.get(arrayToSortFloat[position])).symbol);
                    priceInListView.add(favouriteLists.get(mapFloat.get(arrayToSortFloat[position])).price);
                    changeInListView.add(favouriteLists.get(mapFloat.get(arrayToSortFloat[position])).change + " (" + favouriteLists.get(mapFloat.get(arrayToSortFloat[position])).changePercent + "%) ");
                }
                break;

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView favListView = (ListView) MainActivity.this.findViewById(R.id.favListView);
                favListView.setAdapter(new FavouriteListViewAdapter(MainActivity.this, symbolInListView, priceInListView, changeInListView));
            }
        });
    }


}
