package com.kaho.app.ViewHolders;

import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kaho.app.Data.Models.ShBackColorsModel;
import com.kaho.app.R;


public class ShBackColorViewHolder extends RecyclerView.ViewHolder{

    private ImageView mainColor;
    private ImageView overLay;
    private ImageView selectedTick;


    public ShBackColorViewHolder(final View itemView) {
        super(itemView);
        mainColor = (ImageView)itemView.findViewById( R.id.main_color );
        overLay = (ImageView)itemView.findViewById( R.id.over_lay );
        selectedTick = (ImageView)itemView.findViewById( R.id.selected_tick );
    }

    public void setupCard(final ShBackColorViewHolder holder, final Fragment context,
                          final ShBackColorsModel data, final int position) {

        mainColor.setColorFilter(ContextCompat.getColor(context.getContext(), data.getColorCode()),
                android.graphics.PorterDuff.Mode.SRC_IN);

        if (data.isIs_selected()){
            overLay.setVisibility(View.VISIBLE);
            selectedTick.setVisibility(View.VISIBLE);
        }else {
            overLay.setVisibility(View.GONE);
            selectedTick.setVisibility(View.GONE);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if (context instanceof AddNewShoutVoiceActivity){
                    ((AddNewShoutVoiceActivity) context).colorSlectedClicked(position,data);
                }else if (context instanceof EditShoutVoiceActivity){
                    ((EditShoutVoiceActivity) context).colorSlectedClicked(position,data);
                }*/
            }
        });



    }

}
