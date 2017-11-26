package com.example.sunji.stockmarketsearch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_STOCK_DETAILS_ACTIVITY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // List View Fake Data
        ArrayList<String> symbolInListView = new ArrayList<>();
        ArrayList<String> priceInListView = new ArrayList<>();
        ArrayList<String> changeInListView = new ArrayList<>();

        symbolInListView.add("AAA");
        priceInListView.add("100");
        changeInListView.add("123123");

        // Favourite List
        ListView favListView = (ListView) findViewById(R.id.favListView);
        favListView.setAdapter(new FavouriteListViewAdapter(this, symbolInListView, priceInListView, changeInListView));
    }

    /**
     *      Spinner Listener
     */
    // Sort
    public class sortSpinnerOnItemSelectedListener extends Activity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(MainActivity.this, parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    // Order
    public class orderSpinnerOnItemSelectedListener extends Activity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(MainActivity.this, parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
