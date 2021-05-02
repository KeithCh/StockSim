package com.keith.stocksim.support;

import com.keith.stocksim.MainActivity;
import com.keith.stocksim.R;
import com.keith.stocksim.repository.StockQuery;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static android.provider.Settings.Secure.getString;

public interface AlphaVantageInterface {
    @GET("query?function=GLOBAL_QUOTE")
    Call<StockQuery> getQuote(@Query("symbol") String ticker, @Query("apikey") String apikey);
}
