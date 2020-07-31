package com.example.cafeapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.text.NoCopySpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class Map extends Fragment implements OnMapReadyCallback {

    //LListner to send data from the map to mainactivity
    private MapListener listener;

    //reference to the google map to be drawn with markers of the cafe and its nearby cafes
    private GoogleMap gMap;

    //Used to set the location of the user on the google map
    private Location mCurrentLocation;

    //Used to get the place that was searched in the searchbar
    private Place mSearchPlace;


    private SupportMapFragment supportMapFragment;

    //Searchbar autocompletefragment
    private AutocompleteSupportFragment autocompleteFragment;


    public interface MapListener{
        void mapToMainActivitySent(ArrayList<Cafe> cafeList);
    }

    public Map() {
        // Required empty public constructor
    }


    //Make sure the places api is initalize
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), getString(R.string.map_key), Locale.US);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        
        return v;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Map is ready set the location and listeners for when a user
        //searches a place or cafe in the search bar
        gMap = googleMap;

        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));

        autocompleteFragment= (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //Log.i("Place", "Place: " + place.getName() + ", " + place.getLatLng());
                mSearchPlace = place;

                //Query the cafe and its nearby places
                queryCafe(place);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });



    }

    public void queryCafe(Place place) {

        //Create a url to be used to request info for the cafe and its nearby cafes
        String latLngStr = place.getLatLng().latitude + "," + place.getLatLng().longitude;
        Log.i("latLng", latLngStr);

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location="+latLngStr+"&radius=1500&type=restaurant&keyword=cafe&" +
                "key=AIzaSyAuxkV5sQWDJiE3c9BtJEMESCe2KMva_Qk";


        Toast.makeText(getActivity(),"Query for " + mSearchPlace.getName() + ": " +
                "In Progress", Toast.LENGTH_SHORT).show();


        new QueryCafeTask().execute(url);
    }

    private class QueryCafeTask extends AsyncTask<String,String,ArrayList<Cafe>>{

        @Override
        protected ArrayList<Cafe> doInBackground(String... strings) {
            //Background thread to fetch the cafe data
            ArrayList<Cafe> cafes = QueryUtils.fetchCafeData(strings[0]);

            return cafes;

        }

        @Override
        protected void onPostExecute(ArrayList<Cafe> cafes) {
            Toast.makeText(getActivity(),"Query for " + mSearchPlace.getName() + ": " +"Completed", Toast.LENGTH_SHORT).show();

            //Get back an arraylist of cafes and draw them as markers on the map
            gMap.clear();

            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSearchPlace.getLatLng(),16));

            for(Cafe cafe: cafes){
                //Log.i("cafe", cafe.getName());
                //Log.i("mSearchPlace", mSearchPlace.getName().split(",")[0]);
                //Log.i("cafeName", cafe.getName());
                if(!cafe.getName().equals(mSearchPlace.getName().split(",")[0])) {
                    //Log.i("cafe", cafe.getName());
                    gMap.addMarker(new MarkerOptions().position(cafe.getLatLng()).
                            title(cafe.getName()));
                    //Log.i("rating",String.valueOf(cafe.getRating()));
                }
            }

            gMap.addMarker(new MarkerOptions()
                    .position(mSearchPlace.getLatLng())
                    .title(mSearchPlace.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            //Send the cafe info to mainactivity which would be later sent to listing
            //fragment to display info about the cafes
            listener.mapToMainActivitySent(cafes);

            super.onPostExecute(cafes);


        }
    }

    public void listingCafeItemClick(LatLng latLng){
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
    }






    //Attach and detach the listener to interface with MainActivity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MapListener){
            listener = (MapListener) context;
        }else{
            throw new RuntimeException(context.toString()
                    + "must implement MapListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void MainActivityToMapSent(Location currentLocation){
        //Start and get the map ready once the location is recieved
        mCurrentLocation = currentLocation;
        Log.i("mCurrentLocation", mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());

        supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        supportMapFragment.getMapAsync(Map.this);
    }

}
