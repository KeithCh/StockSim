package com.keith.stocksim;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keith.stocksim.repository.StockQuery;
import com.keith.stocksim.support.IextradingInterface;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PortfolioFragment extends Fragment {
    private double price = 0;
    SharedPreferences mPreferences;
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
                    final TextView portfolio =(TextView) getView().findViewById(R.id.portfolio);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            setListText(portfolio, list.toString());
//                            pbar.setVisibility(View.INVISIBLE);
                        }
                    });
                } catch (IOException ie) {}
            }
        });

        thread.start();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateList();
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }
}
