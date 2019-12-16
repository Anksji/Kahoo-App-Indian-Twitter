package com.kaho.app.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kaho.app.Data.Models.KahooMediaModel;
import com.kaho.app.R;
import com.kaho.app.ViewHolders.LocalMediaViewHolder;
import com.kaho.app.databinding.LocalSingleMediaLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class LocalMediaListAdapters extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity activity;
    List<KahooMediaModel> itemList =new ArrayList<>();
    private Fragment mFragCtx;

    public LocalMediaListAdapters(List<KahooMediaModel> article, Activity activity, Fragment fragment){
        this.itemList = article;
        mFragCtx=fragment;
        this.activity=activity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder vh;

        return new LocalMediaViewHolder(LocalSingleMediaLayoutBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false));

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {
        final LocalMediaViewHolder holder;
        holder = (LocalMediaViewHolder) rholder;
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