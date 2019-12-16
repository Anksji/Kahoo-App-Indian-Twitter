package com.kaho.app.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.kaho.app.Data.Models.HashTagsModel;
import com.kaho.app.R;
import com.kaho.app.ViewHolders.HashViewHolders;

import java.util.ArrayList;
import java.util.List;

public class HashListAdpters extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity activity;
    List<HashTagsModel> itemList =new ArrayList<>();
    private Fragment mFragCtx;

    public HashListAdpters(List<HashTagsModel> article, Activity activity, Fragment fragment){
        this.itemList = article;
        mFragCtx=fragment;
        this.activity=activity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder vh;

        View v =  LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.individual_category_list_layout,
                        viewGroup, false);
        vh= new HashViewHolders(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {
        final HashViewHolders holder;
        holder = (HashViewHolders) rholder;
        holder.setupCard(holder, activity, itemList.get(i),i,mFragCtx);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

   /* @Override
    public int getItemViewType(int position) {
        return ITEM_VIEW_TYPE;
    }*/

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }

}