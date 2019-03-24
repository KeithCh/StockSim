package com.keith.stocksim;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.keith.stocksim.repository.StockQuery;
import com.keith.stocksim.support.IextradingInterface;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PortfolioFragment extends Fragment {
    private double price = 0;
    private ListView listView;
    private ArrayList<HashMap<String, String>> list;
    public static final String COMPANY_COLUMN="company";
    public static final String SHARES_COLUMN="shares";
    public static final String PRICE_COLUMN="price";
    public static final String GAIN_LOSS_COLUMN="gain_loss";
  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);
        updateList();
    }
    public void updateList() {
        list=new ArrayList<HashMap<String,String>>();
        final List<Stock> allStocks = ((MainActivity) getActivity()).db.getAllStocks();
        HashMap<String,String> headerRow=new HashMap<String, String>();
        headerRow.put(COMPANY_COLUMN, "Company");
        headerRow.put(SHARES_COLUMN, "Shares");
        headerRow.put(PRICE_COLUMN, "Price");
        headerRow.put(GAIN_LOSS_COLUMN, "+/-");
        list.add(headerRow);
        Thread thread = new Thread(new Runnable() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.iextrading.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            IextradingInterface service = retrofit.create(IextradingInterface.class);

            @Override
            public void run() {
                try {
                    for (Stock s: allStocks) {
                        Call<StockQuery> theQuote = service.getQuote(s.getTicker());
                        Response<StockQuery> response = theQuote.execute();
                        if (response.body() != null) {
                            HashMap<String, String> hashmap = new HashMap<String, String>();
                            hashmap.put(COMPANY_COLUMN, s.getTicker());
                            hashmap.put(SHARES_COLUMN, String.valueOf(s.numShares));
                            hashmap.put(PRICE_COLUMN, String.valueOf(response.body().quote.latestPrice));
                            hashmap.put(GAIN_LOSS_COLUMN, "ph");
                            list.add(hashmap);
                        }
                    }
                } catch (IOException ie) {}
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ListView listView=(ListView)getView().findViewById(R.id.list_view);
                        PortfolioListViewAdapter adapter=new PortfolioListViewAdapter(getActivity(), list);
                        listView.setAdapter(adapter);
                    }
                });
            }
        });
        thread.start();
    }
}

