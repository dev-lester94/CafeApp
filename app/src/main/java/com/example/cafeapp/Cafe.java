package com.example.cafeapp;

import com.google.android.gms.maps.model.LatLng;

public class Cafe {
    //Cafe has a name, the coordinates where its located latlng, the address which
    //is provided though vicinity, and the rating
    private String mName;
    private LatLng mLatLng;
    private String mVicinity;
    private double mRating;

    public Cafe(String name, LatLng latLng, String vicinity, double rating){
        this.mName = name;
        this.mLatLng = latLng;
        this.mVicinity = vicinity;
        this.mRating = rating;
    }

    public String getName(){
        return mName;
    }

    public LatLng getLatLng(){
        return mLatLng;
    }

    public String getVicinity() {
        return mVicinity;
    }

    public double getRating() {
        return mRating;
    }
}
