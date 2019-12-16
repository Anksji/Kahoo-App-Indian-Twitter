package com.kaho.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kaho.app.Data.Models.ShBackColorsModel;
import com.kaho.app.R;
import com.kaho.app.ViewHolders.ShBackColorViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SHAddBackColorAdapters extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Fragment context;
    List<ShBackColorsModel> itemList =new ArrayList<>();

    public SHAddBackColorAdapters(List<ShBackColorsModel> article, Fragment context){
        this.itemList = article;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder vh;

        View v =  LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.individual_sh_color_layout,
                        viewGroup, false);
        vh= new ShBackColorViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {
        final ShBackColorViewHolder holder;
        holder = (ShBackColorViewHolder) rholder;
        holder.setupCard(holder, context, itemList.get(i),i);

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
        return itemList.size();
    }

}