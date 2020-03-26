package com.example.cafeapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



public class Listing extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ListingListener listener;


    public interface ListingListener{
        void onListingListenerSent(CharSequence input);
    }

    public Listing() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_listing, container, false);
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

    public void onListingListenerReceive(CharSequence input){
        Log.i("MainActivitytoListing",input.toString());
    }
}
