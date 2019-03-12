package com.keith.stocksim;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keith.stocksim.repository.Quote;
import com.keith.stocksim.repository.StockQuery;
import com.keith.stocksim.support.IextradingInterface;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends FragmentActivity {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    MenuItem prevMenuItem;
    PortfolioFragment portfolioFragment;
    AddOrderFragment addOrderFragment;
    OverviewFragment overviewFragment;
    Button addOrderButton;
    DatabaseHandler db;
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        portfolioFragment = new PortfolioFragment();
        addOrderFragment = new AddOrderFragment();
        overviewFragment = new OverviewFragment();
//        addOrderFragment.mPreferences = mPreferences;
        adapter.addFragment(overviewFragment);
        adapter.addFragment(portfolioFragment);
        adapter.addFragment(addOrderFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        db = new DatabaseHandler(this);
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_overview:
                                viewPager.setCurrentItem(0);
                                portfolioFragment.updateList();
                                break;
                            case R.id.action_portfolio:
                                viewPager.setCurrentItem(1);
                                portfolioFragment.updateList();
                                break;
                            case R.id.action_addorder:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    portfolioFragment.updateList();
                }
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
//        db.deleteStock(null);
    }
    public void addOrder(View v) {
        addOrderFragment.addOrder();
    }
}


