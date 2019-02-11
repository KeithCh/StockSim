package com.keith.stocksim;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keith.stocksim.repository.Quote;
import com.keith.stocksim.repository.StockQuery;
import com.keith.stocksim.support.IextradingInterface;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends Navbar {
    private double price = 0;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public void updateList() {
//        final ProgressBar pbar = (ProgressBar) findViewById(R.id.pBar);
//        pbar.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.iextrading.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            IextradingInterface service = retrofit.create(IextradingInterface.class);


            public void setListText(TextView sharedPreferences, String list) {
                sharedPreferences.setText(list);
            }
            @Override
            public void run() {
                try {
                    Map<String,?> keys = mPreferences.getAll();
                    final StringBuilder list = new StringBuilder();
                    for (Map.Entry<String,?> entry : keys.entrySet()) {
                        list.append(entry.getKey() + " ");
                        Call<StockQuery> theQuote = service.getQuote(entry.getKey());
                        price = theQuote.execute().body().quote.latestPrice;
                        list.append(Double.toString(price) + " ");
                        list.append(entry.getValue() + "\n");
                    }
//                    final TextView sharedPreferences =(TextView) findViewById(R.id.portfolio);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
//                            setListText(sharedPreferences, list.toString());
//                            pbar.setVisibility(View.INVISIBLE);
                        }
                    });
                } catch (IOException ie) {}
            }
        });

        thread.start();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AddOrderFragment aof = new AddOrderFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, aof)
                .commit();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        updateList();
    }
}


