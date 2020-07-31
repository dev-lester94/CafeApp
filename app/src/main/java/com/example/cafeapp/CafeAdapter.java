package com.example.cafeapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CafeAdapter extends RecyclerView.Adapter<CafeAdapter.CafeViewHolder> {

    private ArrayList<Cafe> mCafeList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    //Set up a listener when an item is clicked
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class CafeViewHolder extends RecyclerView.ViewHolder{

        //Set up the views from the layout and the onitemclick lisetner
        public TextView mCafeNameView;
        public TextView mCafeAddressView;
        public TextView mRatingView;

        public CafeViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mCafeNameView = itemView.findViewById(R.id.cafeNameView);
            mCafeAddressView = itemView.findViewById(R.id.cafeAddressView);
            mRatingView = itemView.findViewById(R.id.cafeRatingView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public CafeAdapter(ArrayList<Cafe> cafeList){
        mCafeList = cafeList;
    }

    @NonNull
    @Override
    public CafeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cafe_item,parent,false);
        CafeViewHolder cvh = new CafeViewHolder(v, mListener);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CafeViewHolder holder, int position) {
        //Display the info into the textviews
        Cafe currentItem = mCafeList.get(position);

        holder.mCafeNameView.setText(currentItem.getName());
        holder.mCafeAddressView.setText(currentItem.getVicinity());


        holder.mRatingView.setText(Double.toString(currentItem.getRating()));

        //Set the rating with different colors from green as best to red as worst
        double rating = currentItem.getRating();
        if(rating >= 4.5){
            holder.mRatingView.setTextColor(Color.parseColor("#056820"));
        }else if(rating >= 4.0 ){
            holder.mRatingView.setTextColor(Color.parseColor("#0fb83d"));
        }else if(rating >=3.5){
            holder.mRatingView.setTextColor(Color.parseColor("#adad06"));
        }else if(rating >=3.0){
            holder.mRatingView.setTextColor(Color.parseColor("#f0f00a"));
        }else if(rating>=2.5){
            holder.mRatingView.setTextColor(Color.parseColor("#ad8508"));
        }else if(rating>=2.0){
            holder.mRatingView.setTextColor(Color.parseColor("#f0b90e"));
        }else{
            holder.mRatingView.setTextColor(Color.parseColor("#f0410e"));
        }



    }

    @Override
    public int getItemCount() {
        return mCafeList.size();
    }



}
