package com.keith.stocksim;

public class Portfolio {
    String ticker;
    int numShares;
    double startValue;

    public Portfolio() {}
    public Portfolio(String ticker, int numShares, double startValue) {
        this.ticker = ticker;
        this.numShares = numShares;
        this.startValue = startValue;
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
    public void setNumShares(double startValue){
        this.startValue = startValue;
    }
}
