package com.kaho.app.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kaho.app.Data.Models.TagSuggestionModel;
import com.kaho.app.R;
import com.kaho.app.ViewHolders.HashTagSuggestionViewHolder;

import java.util.ArrayList;

public class HashTagSuggestionAdapters extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<TagSuggestionModel> _itemList;
    private Fragment mContext;


    public HashTagSuggestionAdapters(Fragment context, ArrayList<TagSuggestionModel> itemsList){
        this._itemList = itemsList;
        this.mContext = context;
    }

    public void updateList(ArrayList<TagSuggestionModel> temp) {
        _itemList.clear();
        this._itemList = temp;
        Log.e("hsdfsfsdfd"," activating tamp hash tag "+temp.size());
        Log.e("hsdfsfsdfd"," activating hash tag "+_itemList.size());
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.hash_tag_suggestion_individual_view, viewGroup, false);

        vh = new HashTagSuggestionViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {

        if (rholder instanceof HashTagSuggestionViewHolder) {
            HashTagSuggestionViewHolder holder;
            holder = (HashTagSuggestionViewHolder) rholder;
            holder.setupCard(holder, mContext, _itemList.get(i),i);
        }
    }


    @Override
    public int getItemCount() {
        return (null != _itemList ? _itemList.size() : 0);
    }


}