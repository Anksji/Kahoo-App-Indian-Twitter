package com.kaho.app.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kaho.app.Data.Models.TagSuggestionModel;
import com.kaho.app.R;
import com.kaho.app.UI.KahooUi.AddingKahoo.AddNewKahooFragment;
import com.kaho.app.UI.KahooUi.AddingKahoo.EditKahooFragment;


public class HashTagSuggestionViewHolder extends RecyclerView.ViewHolder{

    private TextView hashTag;


    public HashTagSuggestionViewHolder(final View itemView) {
        super(itemView);
        hashTag = (TextView)itemView.findViewById( R.id.hash_tag );
    }

    public void setupCard(final HashTagSuggestionViewHolder holder, final Fragment context,
                          final TagSuggestionModel data, final int position) {


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof AddNewKahooFragment){
                    if (data.getTagType().equals("@")){
                        ((AddNewKahooFragment) context).selectedItem(data.getUserUniqueAtRateId());
                    }else {
                        ((AddNewKahooFragment) context).selectedItem(data.getHashTag());
                    }

                }else if (context instanceof EditKahooFragment){
                   /* if (data.getTagType().equals("@")){
                        ((EditKahooFragment) context).selectedItem(data.getUserUniqueAtRateId());
                    }else {
                        ((EditKahooFragment) context).selectedItem(data.getHashTag());
                    }*/
                }
            }
        });




        if (data.getTagType().equals("@")){
            hashTag.setText(data.getTagType()+data.getUserUniqueAtRateId());
            hashTag.setTextColor(context.getResources().getColor(R.color.primary_600));
        }else {
            hashTag.setText(data.getTagType()+data.getHashTag());
            hashTag.setTextColor(context.getResources().getColor(R.color.kaho_red));
        }


    }

}
