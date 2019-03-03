package com.keith.stocksim;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddOrderFragment extends Fragment {
    SharedPreferences mPreferences;
    public void addOrder(){
        SharedPreferences.Editor mEditor = mPreferences.edit();
        EditText tickerEdit =(EditText) getView().findViewById(R.id.tickerEdit);
        String tickerSymbol = tickerEdit.getText().toString().toUpperCase();
        if (tickerSymbol.equals("")) {
            return;
        }
        EditText sharesEdit = (EditText) getView().findViewById(R.id.orderSizeEdit);
        Integer newShares = Integer.parseInt(sharesEdit.getText().toString());
        Integer oldShares = Integer.parseInt(mPreferences.getString(tickerSymbol, newShares.toString()));
        Integer totalShares = newShares + oldShares;
        mEditor.putString(tickerSymbol, totalShares.toString());
        if (totalShares == 0) {
            mEditor.remove(tickerSymbol);
        }
        mEditor.commit();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_order, container, false);
    }
}
