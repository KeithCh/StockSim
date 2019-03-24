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
import com.keith.stocksim.support.IextradingInterface;

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
    public void updateOverview(View view) {
        portfolioValueText = (TextView) view.findViewById(R.id.portfolio_value);
        stockValueText = (TextView) view.findViewById(R.id.stock_value);
        cashBalanceText = (TextView) view.findViewById(R.id.cash_balance);
        Thread thread = new Thread(new Runnable() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.iextrading.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            IextradingInterface service = retrofit.create(IextradingInterface.class);

            @Override
            public void run() {
                try {
                    stockValue = 0;
                    double cashBalance = (double)((MainActivity) getActivity()).sharedPreferences.getFloat("cashBalance", 100000);
                    List<Stock> allStocks = ((MainActivity) getActivity()).db.getAllStocks();
                    for (Stock s: allStocks) {
                        Call<StockQuery> theQuote = service.getQuote(s.getTicker());
                        Response<StockQuery> response = theQuote.execute();
                        if (response.body() != null) {
                            stockValue += response.body().quote.latestPrice * s.numShares;
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
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        updateOverview(view);
    }
}
