package com.keith.stocksim.repository;

import com.google.gson.annotations.SerializedName;

public class Quote {
    @SerializedName("01. symbol")
    public String companyName;
    @SerializedName("05. price")
    public String latestPrice;
}
