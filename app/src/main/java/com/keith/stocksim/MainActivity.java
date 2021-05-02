package com.keith.stocksim;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends FragmentActivity {
    SharedPreferences sharedPreferences;
    private ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    MenuItem prevMenuItem;
    PortfolioFragment portfolioFragment;
    AddOrderFragment addOrderFragment;
    OverviewFragment overviewFragment;
    Button addOrderButton;
    DatabaseHandler db;
    String apikey;
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        portfolioFragment = new PortfolioFragment();
        addOrderFragment = new AddOrderFragment();
        overviewFragment = new OverviewFragment();
        adapter.addFragment(overviewFragment);
        adapter.addFragment(portfolioFragment);
        adapter.addFragment(addOrderFragment);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);
    }

    public void addOrder(View v) {
        addOrderFragment.addOrder();
    }

    public void resetPortfolio() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("cashBalance", 100000);
        db.deleteStock(null);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        db = new DatabaseHandler(this);
        apikey = getString(R.string.alpha_vantage_api_key);
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_overview:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_portfolio:
                                viewPager.setCurrentItem(1);
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
                if (position == 0) {
//                    overviewFragment.updateOverview();
                } else if (position == 1) {
//                    portfolioFragment.updateList();
                }
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        resetPortfolio();
        setupViewPager(viewPager);
    }
}


