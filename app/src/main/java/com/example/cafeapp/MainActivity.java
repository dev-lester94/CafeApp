package com.example.cafeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements com.example.cafeapp.Map.MapListener, com.example.cafeapp.Listing.ListingListener {

    //Two fragments map and listings
    //Map has a searchbar where users can look up a cafe and its nearby cafe to be marked on the map
    //Listing shows more information about the cafe and its nearby cafe
    //View Pager shows which fragment is currently viewed by the viewpageadpater
    //that knows the references of each fragment
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private Map map;
    private Listing listing;

    //Use of the fusedlocationproviderclient to get the last location of the user
    private Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(findViewById(R.id.android_tablet_layout) != null){
            mTwoPane = true;

            //Create the two fragments
            map = new Map();
            listing = new Listing();

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.map_container, map);
            transaction.replace(R.id.listing_container, listing).commit();

        }else {


            //Set up the tabs and viewpager
            viewPager = findViewById(R.id.view_pager);
            tabLayout = findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(viewPager);

            //Create the two fragments
            map = new Map();
            listing = new Listing();

            //Add the fragments to the viewPagerAdapter and set the viewPager
            //to the adapter
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
            viewPagerAdapter.addFragment(map, "Map");
            viewPagerAdapter.addFragment(listing, "Listing");
            viewPager.setAdapter(viewPagerAdapter);
        }

        //Fetch the last location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

    }

    private void fetchLastLocation() {

        //Check if the user provided the permission to access location services
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            return;
        }

        //Log.i("permissionpass", "permissionpass");

        //Get the last location and send the location to the map fragment to zoom in and start
        //the google map
        Task task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                //Log.i("success","success");
                if(location != null){
                    //Log.i("locationisnotnull", "locationisnotnull");
                    currentLocation = location;
                    Toast.makeText(MainActivity.this, currentLocation.getLatitude() + ", " +
                            currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                    map.MainActivityToMapSent(currentLocation);


                }
            }


        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Log.i("accessgranted","accessgranted");

        switch(requestCode){
            case REQUEST_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Log.i("accessgranted","accessgranted");
                    fetchLastLocation();
                    //Log.i("currentLocation", currentLocation.getLatitude() + " " + currentLocation.getLongitude());

                    //map.MainActivityToMapSent(currentLocation);
                }
                break;

        }
    }





    //Interface between Listing and MainActivity
    @Override
    public void listingToMainActivitySent(LatLng latLng){
        //Log.i("mainactivity",input.toString());

        if(mTwoPane == false) {
            viewPager.setCurrentItem(0);
        }
        map.listingCafeItemClick(latLng);

    }


    @Override
    public void mapToMainActivitySent(ArrayList<Cafe> cafeList) {
        listing.MainActivitytoListingSent(cafeList);

        //Log.i("maptoMainActivitySent",input.toString());
        //map.MainActivityToMapSent(currentLocation);
    }

}
