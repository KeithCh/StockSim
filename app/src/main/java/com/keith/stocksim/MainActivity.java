package com.keith.stocksim;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.keith.stocksim.repository.Quote;
import com.keith.stocksim.repository.StockQuery;
import com.keith.stocksim.support.IextradingInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;


public class MainActivity extends AppCompatActivity {
    ProgressDialog pd;
    double price = 0;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    public void updateList() {
        Map<String,?> keys = mPreferences.getAll();
        StringBuilder list = new StringBuilder();
        for (Map.Entry<String,?> entry : keys.entrySet()) {
            list.append(entry.getKey() + " ");
            getPrice(entry.getKey());
            list.append(Double.toString(price) + " ");
            list.append(entry.getValue() + "\n");
        }
        TextView sharedPreferences =(TextView) findViewById(R.id.portfolio);
        sharedPreferences.setText(list.toString());
    }

    public void addOrder(View view){
        mEditor = mPreferences.edit();
        EditText tickerEdit =(EditText) findViewById(R.id.tickerEdit);
        String tickerSymbol = tickerEdit.getText().toString().toUpperCase();
        if (tickerSymbol.equals("")) {
            return;
        }
        EditText sharesEdit = (EditText) findViewById(R.id.orderSizeEdit);
        Integer newShares = Integer.parseInt(sharesEdit.getText().toString());
        Integer oldShares = Integer.parseInt(mPreferences.getString(tickerSymbol, newShares.toString()));
        Integer totalShares = newShares + oldShares;
        mEditor.putString(tickerSymbol, totalShares.toString());
        mEditor.commit();
        updateList();
    }
    public void getPrice(final String ticker) {
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.iextrading.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            IextradingInterface service = retrofit.create(IextradingInterface.class);
            Call<StockQuery> theQuote = service.getQuote(ticker);

            @Override
            public void run() {
                try {
                    price = theQuote.execute().body().quote.latestPrice;
                } catch (IOException ie) {
                    System.out.println();
                }
                latch.countDown();
            }
        });
        thread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        updateList();
        System.out.println("hello");
        System.out.println(price);
    }
}


