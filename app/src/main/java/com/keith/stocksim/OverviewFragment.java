package com.keith.stocksim;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.keith.stocksim.repository.StockQuery;
import com.keith.stocksim.support.AlphaVantageInterface;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OverviewFragment extends Fragment{
    TextView portfolioValueText;
    TextView stockValueText;
    TextView cashBalanceText;
    double stockValue;
    private int interval = 30000;
    private Handler handler;
    public void updateOverview() {
        portfolioValueText = (TextView) getView().findViewById(R.id.portfolio_value);
        stockValueText = (TextView) getView().findViewById(R.id.stock_value);
        cashBalanceText = (TextView) getView().findViewById(R.id.cash_balance);
        Thread thread = new Thread(new Runnable() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.alphavantage.co/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            AlphaVantageInterface service = retrofit.create(AlphaVantageInterface.class);

            @Override
            public void run() {
                try {
                    stockValue = 0;
                    double cashBalance = (double)((MainActivity) getActivity()).sharedPreferences.getFloat("cashBalance", 100000);
                    List<Stock> allStocks = ((MainActivity) getActivity()).db.getAllStocks();
                    for (Stock s: allStocks) {
                        Call<StockQuery> theQuote = service.getQuote(s.getTicker(), ((MainActivity) getActivity()).apikey);
                        Response<StockQuery> response = theQuote.execute();
                        if (response.body() != null) {
                            stockValue += Double.parseDouble(response.body().quote.latestPrice) * s.numShares;
                        }
                    }
                    stockValueText.setText(String.format("$ %1.2f", stockValue));
                    portfolioValueText.setText(String.format("$ %1.2f", stockValue + cashBalance));
                    cashBalanceText.setText(String.format("$ %1.2f", cashBalance));

                } catch (IOException ie) {}
            }
        });
        thread.start();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        handler = new Handler();
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
//        updateOverview();
        startRepeatingTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable updateOverviewRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                updateOverview();
            } finally {
                handler.postDelayed(updateOverviewRunnable, interval);
            }
        }
    };

    void startRepeatingTask() {
        updateOverviewRunnable.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(updateOverviewRunnable);
    }
}
