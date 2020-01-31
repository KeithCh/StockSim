package com.keith.stocksim;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PortfolioListViewAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    public static final String COMPANY_COLUMN="company";
    public static final String SHARES_COLUMN="shares";
    public static final String PRICE_COLUMN="price";
    public static final String GAIN_LOSS_COLUMN="gain_loss";

    public PortfolioListViewAdapter(Activity activity,ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        TextView txtCompany;
        TextView txtShares;
        TextView txtPrice;
        TextView txtGainLoss;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){

            convertView=inflater.inflate(R.layout.portfolio_column, null);
            holder=new ViewHolder();

            holder.txtCompany=(TextView) convertView.findViewById(R.id.company_column);
            holder.txtShares=(TextView) convertView.findViewById(R.id.shares_column);
            holder.txtPrice=(TextView) convertView.findViewById(R.id.price_column);
            holder.txtGainLoss=(TextView) convertView.findViewById(R.id.gain_loss_column);
            convertView.setTag(holder);
        }else{

            holder=(ViewHolder) convertView.getTag();
        }
        HashMap<String, String> map=list.get(position);
        holder.txtCompany.setText(map.get(COMPANY_COLUMN));
        holder.txtShares.setText(map.get(SHARES_COLUMN));
        holder.txtPrice.setText(map.get(PRICE_COLUMN));
        holder.txtGainLoss.setText(map.get(GAIN_LOSS_COLUMN));
        int textColour = Color.parseColor("#dcdcdc");
        holder.txtCompany.setTextColor(textColour);
        holder.txtShares.setTextColor(textColour);
        holder.txtPrice.setTextColor(textColour);
        String gainLossStr = map.get(GAIN_LOSS_COLUMN);
        holder.txtGainLoss.setTextColor(textColour);
        if (gainLossStr.charAt(0) == '-') {
            holder.txtGainLoss.setTextColor(Color.RED);
        }
        else if (gainLossStr.charAt(0) == '+') {
            holder.txtGainLoss.setTextColor(Color.GREEN);
        }
        return convertView;
    }

}
