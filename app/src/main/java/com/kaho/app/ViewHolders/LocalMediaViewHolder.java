package com.kaho.app.ViewHolders;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.kaho.app.Data.Models.HashTagsModel;
import com.kaho.app.Data.Models.KahooMediaModel;
import com.kaho.app.R;
import com.kaho.app.UI.KahooUi.AddingKahoo.AddNewKahooFragment;
import com.kaho.app.UI.KahooUi.AddingKahoo.DobaraKahooAurReplyFrag;
import com.kaho.app.UI.KahooUi.AddingKahoo.EditKahooFragment;
import com.kaho.app.databinding.KahooSingleLayoutViewBinding;
import com.kaho.app.databinding.LocalSingleMediaLayoutBinding;

public class LocalMediaViewHolder extends RecyclerView.ViewHolder implements Player.EventListener{


    public LocalSingleMediaLayoutBinding localSingleMediaLayoutBinding;
    public View itemView;

    public LocalMediaViewHolder(final LocalSingleMediaLayoutBinding localSingleMediaLayoutBinding) {
        super(localSingleMediaLayoutBinding.getRoot());
        itemView=localSingleMediaLayoutBinding.getRoot();
        this.localSingleMediaLayoutBinding=localSingleMediaLayoutBinding;
    }

    public KahooMediaModel kahooMediaModel;

    public void setVideoPlaying(){
        localSingleMediaLayoutBinding.selectedImage.setVisibility(View.GONE);
    }

    public void setupCard(final LocalMediaViewHolder holder, final Activity activity,
                          final KahooMediaModel data, final int position, final Fragment fragmentCtx) {

        SimpleExoPlayer exoPlayer=new SimpleExoPlayer.Builder(activity).build();

        this.kahooMediaModel=data;

        if (data.isVideo()){
            Uri uri=Uri.parse(data.getLocalUrl());
            localSingleMediaLayoutBinding.mediaContainer.setVisibility(View.VISIBLE);
            localSingleMediaLayoutBinding.selectedImage.setVisibility(View.GONE);
            localSingleMediaLayoutBinding.videoControle.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragmentCtx instanceof AddNewKahooFragment){
                        localSingleMediaLayoutBinding.selectedImage.setVisibility(View.GONE);
                        ((AddNewKahooFragment) fragmentCtx).playVideoClicked(position,data);
                    }else if (fragmentCtx instanceof EditKahooFragment){
                        localSingleMediaLayoutBinding.selectedImage.setVisibility(View.GONE);
                        ((EditKahooFragment) fragmentCtx).playVideoClicked(position,data);
                    }else if (fragmentCtx instanceof DobaraKahooAurReplyFrag){
                        localSingleMediaLayoutBinding.selectedImage.setVisibility(View.GONE);
                        ((DobaraKahooAurReplyFrag) fragmentCtx).playVideoClicked(position,data);
                    }
                }
            });
        }else {
            localSingleMediaLayoutBinding.mediaContainer.setVisibility(View.GONE);
            localSingleMediaLayoutBinding.selectedImage.setVisibility(View.VISIBLE);
            localSingleMediaLayoutBinding.videoControle.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(data.getLocalUrl())
                    .into(localSingleMediaLayoutBinding.selectedImage);
        }

        localSingleMediaLayoutBinding.selectedImage.setVisibility(View.VISIBLE);
        Glide.with(activity)
                .load(data.getLocalUrl())
                .into(localSingleMediaLayoutBinding.selectedImage);
       /*
       localSingleMediaLayoutBinding.selectedVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                localSingleMediaLayoutBinding.videoControle.setImageDrawable(activity.getDrawable(R.drawable.exo_controls_play));
            }
        });
        */
        /*
        localSingleMediaLayoutBinding.videoControle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localSingleMediaLayoutBinding.selectedVideo!=null
                        &&!localSingleMediaLayoutBinding.selectedVideo.isPlaying()){
                    localSingleMediaLayoutBinding.selectedVideo.start();
                }
            }
        });
        */

        localSingleMediaLayoutBinding.clearSelectedMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("jkhsfsfsd","clear media is clicked above ");
                if (fragmentCtx instanceof AddNewKahooFragment){
                    ((AddNewKahooFragment) fragmentCtx).clearMediaFileIsClicked(data);
                }else if (fragmentCtx instanceof EditKahooFragment){
                    Log.e("jkhsfsfsd","edit media is clicked ");
                    ((EditKahooFragment) fragmentCtx).clearMediaFileIsClicked(data);
                }else if (fragmentCtx instanceof DobaraKahooAurReplyFrag){
                    Log.e("jkhsfsfsd","edit media is clicked ");
                    ((DobaraKahooAurReplyFrag) fragmentCtx).clearMediaFileIsClicked(data);
                }
            }
        });
    }

    public String getMediaUrl(){
        return kahooMediaModel.getLocalUrl();
    }
    public boolean isVideo(){
        return kahooMediaModel.isVideo();
    }

}
