package com.keith.stocksim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "portfolioManager";
    private static final String TABLE_STOCKS = "stocks";
    private static final String KEY_TICKER = "ticker";
    private static final String KEY_NUM_SHARES = "numShares";
    private static final String KEY_START_VALUE = "startValue";
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STOCKS_TABLE = "CREATE TABLE " + TABLE_STOCKS + "("
                + KEY_TICKER + " TEXT PRIMARY KEY," + KEY_NUM_SHARES + " INTEGER,"
                + KEY_START_VALUE + " REAL" + ")";
        db.execSQL(CREATE_STOCKS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKS);

        // Create tables again
        onCreate(db);
    }

    void addStock(Stock stock) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TICKER, stock.getTicker());
        values.put(KEY_NUM_SHARES, stock.getNumShares());
        values.put(KEY_START_VALUE, stock.getStartValue());
        // Inserting Row
        db.insert(TABLE_STOCKS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }


    Stock getStock(String ticker) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STOCKS, new String[] { KEY_TICKER,
                        KEY_NUM_SHARES, KEY_START_VALUE }, KEY_TICKER + "=?",
                new String[] { ticker }, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();

            Stock stock = new Stock(cursor.getString(0),
                    cursor.getInt(1), cursor.getDouble(2));
            return stock;
        }
        return null;
    }

    // code to get all stocks in a list view
    public List<Stock> getAllStocks() {
        List<Stock> stockList = new ArrayList<Stock>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STOCKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Stock stock = new Stock();
                stock.setTicker(cursor.getString(0));
                stock.setNumShares(Integer.parseInt(cursor.getString(1)));
                stock.setStartValue(Double.parseDouble(cursor.getString(2)));
                // Adding stock to list
                stockList.add(stock);
            } while (cursor.moveToNext());
        }

        // return stock list
        return stockList;
    }


    public int updateStock(Stock stock) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NUM_SHARES, stock.getNumShares());
        values.put(KEY_START_VALUE, stock.getStartValue());

        // updating row
        return db.update(TABLE_STOCKS, values, KEY_TICKER + " = ?",
                new String[] { stock.getTicker() });
    }


    public void deleteStock(Stock stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (stock == null) db.execSQL("delete from "+ TABLE_STOCKS);
        else {
            db.delete(TABLE_STOCKS, KEY_TICKER + " = ?",
            new String[] { stock.getTicker() });
        }
        db.close();
    }

    public int getStocksCount() {
        String countQuery = "SELECT  * FROM " + TABLE_STOCKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
