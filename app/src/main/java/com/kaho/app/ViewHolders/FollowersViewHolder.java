package com.kaho.app.ViewHolders;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaho.app.Data.Models.FollowerModel;
import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;


public class FollowersViewHolder extends RecyclerView.ViewHolder{

    private ImageView userImage;
    private TextView userName;

   // private CardView mainCardLayout;
    private SessionPrefManager sessionPrefManager;

    public FollowersViewHolder(final View itemView) {
        super(itemView);
        userImage = (ImageView)itemView.findViewById(R.id.user_image);
        userName = (TextView)itemView.findViewById(R.id.user_name);
        //mainCardLayout=itemView.findViewById(R.id.main_card_layout);
    }

    public void setupCard(final FollowersViewHolder holder, final Activity activity,
                          final FollowerModel data, final int position,
                          final Fragment fragment) {

        sessionPrefManager=new SessionPrefManager(activity);
        int width=(sessionPrefManager.getUserDeviceWidth())/2;
        //width=width-15;

      /*  holder.mainCardLayout.getLayoutParams().width = width - PublicMethods.getInDp(10,sessionPrefManager.getDeviceDensity());
        holder.mainCardLayout.getLayoutParams().height = width + PublicMethods.getInDp(30,sessionPrefManager.getDeviceDensity());
*/

        userName.setText(data.getFollower_user_name());
        FirebaseStorage storage;
        storage= FirebaseStorage.getInstance();
        StorageReference storageRef;
        String imgName;
        if (data.getFollower_user_img()==null||data.getFollower_user_img().trim().length()<5){
            imgName="placeholder.jpg";
        }else {
            imgName=data.getFollower_user_img();
        }
        storageRef= storage.getReference().child("usersProfile/images/").child(imgName);

        //storageRef= storage.getReference().child("usersProfile/images/").child(data.getSh_added_user_image());

        Glide.with(activity).load(storageRef)
                .placeholder(R.drawable.user_icon)
                .error(R.drawable.user_icon)
                .override(400, 400) // resizes the image to these dimensions (in pixel)
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(userImage);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (fragment instanceof MyFollowersFragment){
                    try {
                        ((MyFollowersFragment) fragment).followerItemClicked(data);
                    }catch (Exception e) {
                        Log.e("kdsjfhsdfd", "this si error " + e.getMessage());
                    }
                }*/
            }
        });
        /*if (data.getShoutWordPost().length()>5){

        }else {
            holder.shoutTextPostContent.setVisibility(View.GONE);
        }*/

    }

}
