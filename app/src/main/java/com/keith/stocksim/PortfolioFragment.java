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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PortfolioFragment extends Fragment {
    private double price = 0;
    private ListView listView;
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


            @Override
            public void run() {
                try {
                    final List<String> your_array_list = new ArrayList<String>();
                    List<Stock> allStocks = ((MainActivity) getActivity()).db.getAllStocks();
                    for (Stock s: allStocks) {
                        Call<StockQuery> theQuote = service.getQuote(s.getTicker());
                        Response<StockQuery> response = theQuote.execute();
                        if (response.body() != null) {
                            price = response.body().quote.latestPrice;
                            your_array_list.add(s.ticker + "           " + Integer.toString(s.numShares) + "             " + price );
                        }
                    }

//                    final TextView portfolio =(TextView) getView().findViewById(R.id.portfolio);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    your_array_list );

                            listView.setAdapter(arrayAdapter);
//                            setListText(portfolio, list.toString());
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
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.list_view);
        List<String> your_array_list = new ArrayList<String>();
        List<Stock> allStocks = ((MainActivity) getActivity()).db.getAllStocks();
        for (Stock s: allStocks) {
            your_array_list.add(s.ticker + "           " + Integer.toString(s.numShares));
        }

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                your_array_list );

        listView.setAdapter(arrayAdapter);
    }
}
