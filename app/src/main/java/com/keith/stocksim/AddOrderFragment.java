package com.keith.stocksim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.keith.stocksim.repository.StockQuery;
import com.keith.stocksim.support.IextradingInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddOrderFragment extends Fragment {
    DatabaseHandler db;
    void displayToast(final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, msg, duration);
                toast.show();
            }
        });
    }
    public void addOrder(){
        db =  ((MainActivity) getActivity()).db;
        final SharedPreferences sharedPreferences = ((MainActivity) getActivity()).sharedPreferences;
        EditText tickerEdit =(EditText) getView().findViewById(R.id.tickerEdit);
        final String tickerSymbol = tickerEdit.getText().toString().toUpperCase();
        if (tickerSymbol.equals("")) {
            return;
        }
        Thread thread = new Thread(new Runnable() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.iextrading.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            IextradingInterface service = retrofit.create(IextradingInterface.class);


            @Override
            public void run() {
                try {
                    Call<StockQuery> theQuote = service.getQuote(tickerSymbol);
                    Response<StockQuery> response = theQuote.execute();
                    Integer oldShares = (db.getStock(tickerSymbol) != null) ? db.getStock(tickerSymbol).numShares : 0;
                    // ticker is valid
                    if (response.body() != null) {
                        EditText sharesEdit = (EditText) getView().findViewById(R.id.orderSizeEdit);
                        if (sharesEdit.getText().toString().equals("")) return;
                        Integer newShares = Integer.parseInt(sharesEdit.getText().toString());
                        if (newShares == 0) return;
                        if (response.body().quote.latestPrice * newShares > sharedPreferences.getFloat("cashBalance", 0)) {
                            displayToast("You do not have enough money");
                        }
                        else if(oldShares + newShares < 0){
                            displayToast("You Do Not Have Enough Shares");
                        }
                        else{
                            Integer totalShares = newShares + oldShares;
                            if (totalShares == 0) {
                                db.deleteStock(new Stock(tickerSymbol, 0, 0));
                            }
                            else if (db.getStock(tickerSymbol) == null)
                                db.addStock(new Stock(tickerSymbol, totalShares, 0));
                            else db.updateStock(new Stock(tickerSymbol, totalShares, 0));
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putFloat("cashBalance", sharedPreferences.getFloat("cashBalance", 0) - newShares * (float)response.body().quote.latestPrice);
                            editor.commit();
                            displayToast("Order Added Successfully");
                        }
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Context context = getContext();
                                CharSequence text = tickerSymbol + " is not a valid ticker";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        });
                    }
                } catch (IOException ie) {}
            }
        });
        thread.start();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_order, container, false);
    }
}
