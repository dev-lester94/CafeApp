package com.example.cafeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity implements com.example.cafeapp.Map.MapListener ,com.example.cafeapp.Listing.ListingListener{

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private Map map;
    private Listing listing;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        map = new Map();
        listing = new Listing();

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),0);
        viewPagerAdapter.addFragment(map,"Map");
        viewPagerAdapter.addFragment(listing,"Listing");
        viewPager.setAdapter(viewPagerAdapter);



    }

    //Interface between Listing and MainActivity
    @Override
    public void onMapListenerSent(CharSequence input){
        Log.i("MaptoMainactivity",input.toString());
        Listing fragmentObj= (Listing) viewPagerAdapter.getItem(1);
        fragmentObj.onListingListenerReceive("helloFromMain");

    }

    //Interface between Listing and MainActivity
    @Override
    public void onListingListenerSent(CharSequence input){
        Log.i("mainactivity",input.toString());

    }



}
