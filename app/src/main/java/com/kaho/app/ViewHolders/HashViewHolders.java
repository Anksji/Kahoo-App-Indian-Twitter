package com.kaho.app.ViewHolders;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kaho.app.Data.Models.HashTagsModel;
import com.kaho.app.R;
import com.kaho.app.UI.KahooUi.Search.SearchFragment;
import com.kaho.app.UI.KahooUi.ViewingKahoo.ViewKahooFrontFragment;


public class HashViewHolders extends RecyclerView.ViewHolder{

    private RelativeLayout catBackground;
    private TextView categoryName;


    public HashViewHolders(final View itemView) {
        super(itemView);
        catBackground = (RelativeLayout)itemView.findViewById( R.id.cat_background );
        categoryName = (TextView)itemView.findViewById( R.id.category_name );
    }

    public void setupCard(final HashViewHolders holder, final Activity activity,
                          final HashTagsModel data, final int position, final Fragment fragmentCtx) {


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentCtx instanceof ViewKahooFrontFragment){
                    ((ViewKahooFrontFragment) fragmentCtx).trendingHashTagSelected(position,data);
                }else if (fragmentCtx instanceof SearchFragment){
                    ((SearchFragment) fragmentCtx).trendingHashTagSelected(position,data);
                }
            }
        });


        categoryName.setText((position+1)+" #"+data.getHashTag());

        if (data.isSelected()){
            catBackground.setBackgroundResource(R.drawable.category_back_active);
            categoryName.setTextColor(activity.getResources().getColor(R.color.white));
        }else {
            catBackground.setBackgroundResource(R.drawable.category_back_normal);
            categoryName.setTextColor(activity.getResources().getColor(R.color.white));
        }
    }

}
