package com.example.sunji.stockmarketsearch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    }
}
