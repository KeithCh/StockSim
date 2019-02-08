package com.keith.stocksim.support;

import com.keith.stocksim.repository.StockQuery;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IextradingInterface {
    @GET("1.0/stock/{ticker}/book")
    Call<StockQuery> getQuote(@Path("ticker") String ticker);
}
