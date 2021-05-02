package com.keith.stocksim;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.keith.stocksim.repository.StockQuery;
import com.keith.stocksim.support.AlphaVantageInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PortfolioFragment extends Fragment {
    private double price = 0;
    private ListView listView;
    private ArrayList<HashMap<String, String>> list;
    public static final String COMPANY_COLUMN = "company";
    public static final String SHARES_COLUMN = "shares";
    public static final String PRICE_COLUMN = "price";
    public static final String GAIN_LOSS_COLUMN = "gain_loss";
    private int interval = 60000;
    private Handler handler;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        handler = new Handler();
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        updateList();
        startRepeatingTask();
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable updateListRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                updateList();
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                handler.postDelayed(updateListRunnable, interval);
            }
        }
    };

    void startRepeatingTask() {
        updateListRunnable.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(updateListRunnable);
    }

    public String calculateReturn(double latestPrice, double startingVal, int numShares) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        String formatted = df.format((latestPrice - startingVal / numShares) / (startingVal / numShares) * 100);
        if (formatted.equals("0.00")) {
            return "0.00";
        } else if (formatted.substring(0, 1).equals("-")) {
            return formatted;
        }
        return "+" + formatted;
    }

    public void updateList() {
        list = new ArrayList<HashMap<String, String>>();
        final List<Stock> allStocks = ((MainActivity) getActivity()).db.getAllStocks();
        Thread thread = new Thread(new Runnable() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.alphavantage.co/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            AlphaVantageInterface service = retrofit.create(AlphaVantageInterface.class);

            @Override
            public void run() {
                try {
                    for (Stock s : allStocks) {
                        Call<StockQuery> theQuote = service.getQuote(s.getTicker(), ((MainActivity) getActivity()).apikey);
                        Response<StockQuery> response = theQuote.execute();
                        StockQuery sq = response.body();
                        HashMap<String, String> hashmap = new HashMap<String, String>();
                        if (sq != null && sq.quote != null) {
                            hashmap.put(COMPANY_COLUMN, s.getTicker());
                            hashmap.put(SHARES_COLUMN, String.valueOf(s.numShares));
                            hashmap.put(PRICE_COLUMN, String.format("$ %1.2f", Float.valueOf(sq.quote.latestPrice)));
                            hashmap.put(GAIN_LOSS_COLUMN, calculateReturn(Double.parseDouble(sq.quote.latestPrice), s.startValue, s.numShares) + "%");
                        } else {
                            hashmap.put(COMPANY_COLUMN, s.getTicker());
                            hashmap.put(SHARES_COLUMN, String.valueOf(s.numShares));
                            hashmap.put(PRICE_COLUMN, "N/A");
                            hashmap.put(GAIN_LOSS_COLUMN, "N/A");
                        }
                        list.add(hashmap);
                    }
                } catch (IOException ie) {
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ListView listView = (ListView) getView().findViewById(R.id.list_view);
                        PortfolioListViewAdapter adapter = new PortfolioListViewAdapter(getActivity(), list);
                        listView.setAdapter(adapter);
                    }
                });
            }
        });
        thread.start();
    }
}

