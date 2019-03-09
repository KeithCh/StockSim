package com.keith.stocksim;

public class Stock {
    String ticker;
    int numShares;
    double startValue;

    public Stock() {}
    public Stock(String ticker, int numShares, double startValue) {
        this.ticker = ticker;
        this.numShares = numShares;
        this.startValue = startValue;
    }
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    public String getTicker() {
        return this.ticker;
    }
    public int getNumShares(){
        return this.numShares;
    }
    public void setNumShares(int numShares){
        this.numShares = numShares;
    }
    public double getStartValue(){
        return this.startValue;
    }
    public void setStartValue(double startValue){
        this.startValue = startValue;
    }
}
