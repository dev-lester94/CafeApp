package com.example.cafeapp;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;


public class Listing extends Fragment {

    //RecyclerView is used to display the cafes info
    //the CafeAdapter is used to hold the cafe info and knows how to display
    //cafe item in the recycler view
    private RecyclerView mRecyclerView;
    private CafeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView mSearchImageView;
    private TextView mtextImageView;

    ArrayList<Cafe> mCafeList;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ListingListener listener;


    public interface ListingListener{
        void listingToMainActivitySent(LatLng latLng);
    }

    public Listing() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCafeList = new ArrayList<>();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_listing, container, false);

        mSearchImageView = v.findViewById(R.id.searchImageView);
        mtextImageView = v.findViewById(R.id.textImageView);

        //Set up the recycler view with the cafeAdpater
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new CafeAdapter(mCafeList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //If a user did not search up a cafe, display the empty view (searchimageView and textView)
        //to tell user to go back to map fragment
        if (mCafeList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mSearchImageView.setVisibility(View.VISIBLE);
            mtextImageView.setVisibility(View.VISIBLE);
        }
        else {
            //Set the empty view to be gone when a user does search up a cafe
            mRecyclerView.setVisibility(View.VISIBLE);
            mSearchImageView.setVisibility(View.GONE);
            mtextImageView.setVisibility(View.GONE);
        }

        //Send the latlng coordiantes of a cafe when it is clicked back to the map
        mAdapter.setOnItemClickListener(new CafeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i("itemClick", mCafeList.get(position).getName());
                listener.listingToMainActivitySent(mCafeList.get(position).getLatLng());
            }
        });

        return v;
    }

    //Attach and detach the listener to interface with MainActivity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ListingListener){
            listener = (ListingListener) context;
        }else{
            throw new RuntimeException(context.toString()
                    + "must implement ListingListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void listingToMainActivitySent(CharSequence input){
        Log.i("MainActivitytoListing",input.toString());
    }

    public void MainActivitytoListingSent(ArrayList<Cafe> cafeList){

        //Clear the current cafelist with new cafe information
        //Check to see if their new cafe information


        mCafeList.clear();
        mCafeList.addAll(cafeList);

        if (mCafeList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mSearchImageView.setVisibility(View.VISIBLE);
            mtextImageView.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mSearchImageView.setVisibility(View.GONE);
            mtextImageView.setVisibility(View.GONE);
        }

        //Notify the adpter that new cafe data is done
        mAdapter.notifyDataSetChanged();





        for(Cafe cafe: cafeList){
            Log.i("cafe", cafe.getName() + " " + cafe.getVicinity() + " " + cafe.getRating());
        }

    }
}
