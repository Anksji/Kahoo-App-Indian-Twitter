package com.kaho.app.ViewHolders.KahooViewHolders;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaho.app.Data.Models.KahooPostModel;
import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;
import com.kaho.app.Tools.PublicMethods;
import com.kaho.app.Tools.Utills.GetTimeAgo;
import com.kaho.app.Tools.Utills.HashTagAndAtRate.HashTagHelper;
import com.kaho.app.UI.KahooUi.AddingKahoo.DobaraKahooAurReplyFrag;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.DobaraKahooList;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.LikedKahooList;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.MyKahooListFrag;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.ReplyKahooListFrag;
import com.kaho.app.UI.KahooUi.ViewingKahoo.SingleDetailKahooView;
import com.kaho.app.UI.KahooUi.ViewingKahoo.SingleHashTagKahooView;
import com.kaho.app.UI.KahooUi.ViewingKahoo.ViewKahooFrontFragment;
import com.kaho.app.databinding.KahooSingleLayoutViewBinding;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.kaho.app.Tools.Constants.PROFILE_IMAGE_NAME;

public class KahooViewHolder extends RecyclerView.ViewHolder {

    public KahooSingleLayoutViewBinding kahooSingleLayoutViewBinding;
    public View itemView;
    private SessionPrefManager sessionPrefManager;
    private boolean isMoreOptionOpen;
    private HashTagHelper mTextHashTagHelper;
    private HashTagHelper headingTagHelper;
    private boolean isTagClicked=false;

    public KahooViewHolder(@NonNull KahooSingleLayoutViewBinding kahooSingleLayoutViewBinding) {
        super(kahooSingleLayoutViewBinding.getRoot());
        itemView=kahooSingleLayoutViewBinding.getRoot();
        this.kahooSingleLayoutViewBinding=kahooSingleLayoutViewBinding;
    }
    public void setVideoPlaying(){
        kahooSingleLayoutViewBinding.kahoImgHolder.setVisibility(View.GONE);
    }
    private KahooPostModel kahooPostModel;
    public void setUpKahooView(KahooViewHolder holder, Fragment mContext,
                               KahooPostModel dataKahoPost, int position, boolean isFromViewHolder) {

        kahooPostModel=dataKahoPost;
        sessionPrefManager=new SessionPrefManager(mContext.getActivity());

        if (dataKahoPost.getKaho_media_list().size()>0){
            Log.e("kjdfsfdsf","Position is "+position+" this is media url "+dataKahoPost.getKaho_media_list().get(0));
        }

        /*if (dataKahoPost.getKaho_added_user_id().equalsIgnoreCase(sessionPrefManager.getUserID())){
            kahooSingleLayoutViewBinding.moreOptionBtn.setVisibility(View.VISIBLE);
        }else {
            kahooSingleLayoutViewBinding.moreOptionBtn.setVisibility(View.GONE);
        }*/

        String urlFromcont=extractURL(dataKahoPost.getKahoo_text_content());
        String[] url=urlFromcont.split(" ");
        urlFromcont=url[0];

        FirebaseStorage storage= FirebaseStorage.getInstance();
        StorageReference storageRef;
        storageRef= storage.getReference().child("profileImage").child(dataKahoPost.getKaho_added_user_id()+PROFILE_IMAGE_NAME);
        Glide.with(mContext.getActivity())
                .load(storageRef)
                .placeholder(R.color.dark_primary_500)
                .error(R.color.dark_primary_500)
                .override(400, 400)
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(kahooSingleLayoutViewBinding.userImage);


        kahooSingleLayoutViewBinding.linkPreviewLayout.setVisibility(View.GONE);
        kahooSingleLayoutViewBinding.mediaCount.setText("1/"+(dataKahoPost.getKaho_media_list().size()));




        /*if (urlFromcont.length()>0){
            //string has url
            kahooSingleLayoutViewBinding.loadWeb.setVisibility(View.VISIBLE);
            kahooSingleLayoutViewBinding.webViewLayout.setVisibility(View.VISIBLE);
            kahooSingleLayoutViewBinding.loadWeb.load(urlFromcont);
            String finalUrlFromcont = urlFromcont;
            kahooSingleLayoutViewBinding.webClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("sdkjfskffs","clicking on view");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrlFromcont));
                    mContext.getActivity().startActivity(browserIntent);
                }
            });
        }else {
            //String doesn't has url
            kahooSingleLayoutViewBinding.webViewLayout.setVisibility(View.GONE);
            kahooSingleLayoutViewBinding.loadWeb.setVisibility(View.GONE);
        }*/


        Log.e("kjdhfksdf","then url is  "+urlFromcont);


        if (mContext instanceof DobaraKahooAurReplyFrag||isFromViewHolder){
            kahooSingleLayoutViewBinding.likesReply.setVisibility(View.GONE);
            kahooSingleLayoutViewBinding.divider.setVisibility(View.GONE);
            kahooSingleLayoutViewBinding.moreOptionBtn.setVisibility(View.GONE);
        }else {
            kahooSingleLayoutViewBinding.divider.setVisibility(View.VISIBLE);
            kahooSingleLayoutViewBinding.likesReply.setVisibility(View.VISIBLE);
            kahooSingleLayoutViewBinding.moreImgLayout.setVisibility(View.VISIBLE);
        }

        kahooSingleLayoutViewBinding.dobaraKahoCount.setText(""+dataKahoPost.getDobara_kahoo_count());
        kahooSingleLayoutViewBinding.likeCountLike.setText(""+dataKahoPost.getKaho_like_count());
        kahooSingleLayoutViewBinding.replyCountReply.setText(""+dataKahoPost.getKahoo_reply_count());
        kahooSingleLayoutViewBinding.userKahooContent.setText(dataKahoPost.getKahoo_text_content());
        kahooSingleLayoutViewBinding.shareCount.setText(""+dataKahoPost.getKahoo_share_count());



        if (dataKahoPost.getLikedUserIdsList().contains(sessionPrefManager.getUserID())){
            Log.d("djfskfssdf","user liked");
            kahooSingleLayoutViewBinding.heartSymbol.setImageDrawable(mContext.getActivity().getResources().getDrawable(R.drawable.ic_heart_colored));
        }else {
            Log.d("djfskfssdf","user not liked");
            kahooSingleLayoutViewBinding.heartSymbol.setImageDrawable(mContext.getActivity().getResources().getDrawable(R.drawable.ic_heart));
        }


        /*
        isMoreOptionOpen=false;
        kahooSingleLayoutViewBinding.moreOptionLayout.setVisibility(View.INVISIBLE);
        kahooSingleLayoutViewBinding.moreOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMoreOptionOpen){
                    isMoreOptionOpen=true;
                    kahooSingleLayoutViewBinding.moreOptionLayout.setVisibility(View.VISIBLE);
                }else {
                    isMoreOptionOpen=false;
                    kahooSingleLayoutViewBinding.moreOptionLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        */

        GetTimeAgo getTimeAgo=new GetTimeAgo();
        String kahooTime="";
        if (dataKahoPost.getKahooAddedTimeStamp()!=null){
            kahooTime=getTimeAgo.getTimeAgo(dataKahoPost.getKahooAddedTimeStamp().getTime(),mContext.getActivity());
            if (kahooTime==null){
                kahooTime=" 1 m";
            }else{
                kahooTime=kahooTime;
            }
        }else {
            kahooTime=" 1 m";
        }


        String titleHeadContent;
        titleHeadContent="<b>"+dataKahoPost.getKaho_added_user_name()+"</b>   ";
        titleHeadContent+=" <font color='#fe9e21'>@"+dataKahoPost.getKaho_added_user_handel_id()+"</font>  <font color='#cdd5e5'> •   "+kahooTime+" </font>";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            kahooSingleLayoutViewBinding.userNameHandleTimeHead.setText(Html.fromHtml(titleHeadContent, Html.FROM_HTML_MODE_COMPACT));
        } else {
            kahooSingleLayoutViewBinding.userNameHandleTimeHead.setText(Html.fromHtml(titleHeadContent));
        }

       /* Glide.with(mContext)
                .load(dataKahoPost.getKaho_added_user_image())
                .placeholder(R.color.dark_primary_500)
                .error(R.color.dark_primary_500)
                .into(kahooSingleLayoutViewBinding.userImage);*/
        if (dataKahoPost.getKaho_media_list().size()>1){
            kahooSingleLayoutViewBinding.moreImgGradient.setVisibility(View.VISIBLE);
            kahooSingleLayoutViewBinding.moreImgLayout.setVisibility(View.VISIBLE);
        }else {
            kahooSingleLayoutViewBinding.moreImgLayout.setVisibility(View.GONE);
            kahooSingleLayoutViewBinding.moreImgGradient.setVisibility(View.GONE);
        }
        if (dataKahoPost.getKaho_media_list().size()>0){
            kahooSingleLayoutViewBinding.kahoMediaContentHolder.setVisibility(View.VISIBLE);
            if (dataKahoPost.getKaho_media_list().get(0).contains(".mp4")||
            dataKahoPost.getKaho_media_list().get(0).contains("kahooappSerQ38videos")){
                //video file
                kahooSingleLayoutViewBinding.videoControle.setVisibility(View.VISIBLE);
                kahooSingleLayoutViewBinding.mediaContainer.setVisibility(View.VISIBLE);
                kahooSingleLayoutViewBinding.ivVolumeControl.setVisibility(View.VISIBLE);

            }else {
                //not video file
                kahooSingleLayoutViewBinding.mediaContainer.setVisibility(View.GONE);
                kahooSingleLayoutViewBinding.videoControle.setVisibility(View.GONE);
                kahooSingleLayoutViewBinding.ivVolumeControl.setVisibility(View.GONE);
            }

            Log.e("sdkfskff","media list size "+dataKahoPost.getKaho_media_list().size());
            Log.e("sdkfskff","media 0 index content "+dataKahoPost.getKaho_media_list().get(0));
            String imgUrl=dataKahoPost.getKaho_media_list().get(0);
            Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(R.color.dark_primary_500)
                    .error(R.color.dark_primary_500)
                    .override(800, 560)
                    .into(kahooSingleLayoutViewBinding.kahoImgHolder);

            /*Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(R.color.dark_primary_500)
                    .error(R.color.dark_primary_500)
                    .override(800, 560)
                    .into(kahooSingleLayoutViewBinding.ivMediaCoverImage);*/

        } else {
            kahooSingleLayoutViewBinding.kahoMediaContentHolder.setVisibility(View.GONE);
        }

        isTagClicked=false;



        mTextHashTagHelper = HashTagHelper.Creator.create(mContext.
                        getResources().getColor(R.color.primary_200),mContext.
                        getResources().getColor(R.color.primary_500),
                new HashTagHelper.OnHashTagClickListener() {
                    @Override
                    public void onHashTagClicked(String tagType,String hashTag) {
                        try {
                            isTagClicked=true;
                            onTagClicked(tagType,hashTag,mContext,dataKahoPost);
                        }catch (Exception e){
                            Log.e("kjdhskfssd","this is error "+e.getMessage());
                        }
                        Log.e("ksdfjhskfd","1 this hash tag is clicked "+hashTag);
                    }
                });

        mTextHashTagHelper.handle(kahooSingleLayoutViewBinding.userKahooContent);
        Linkify.addLinks(kahooSingleLayoutViewBinding.userKahooContent, Linkify.WEB_URLS);
        kahooSingleLayoutViewBinding.userKahooContent.setMovementMethod(LinkMovementMethod.getInstance());


        headingTagHelper = HashTagHelper.Creator.create(mContext.
                        getResources().getColor(R.color.primary_200),mContext.
                        getResources().getColor(R.color.primary_500),
                new HashTagHelper.OnHashTagClickListener() {
                    @Override
                    public void onHashTagClicked(String tagType,String hashTag) {
                        try {
                            isTagClicked=true;
                            onTagClicked(tagType,hashTag,mContext,dataKahoPost);
                        }catch (Exception e){
                            Log.e("kjdhskfssd","this is error "+e.getMessage());
                        }
                        Log.e("ksdfjhskfd","1 this hash tag is clicked "+hashTag);
                    }
                });

        headingTagHelper.handle(kahooSingleLayoutViewBinding.userNameHandleTimeHead);

        kahooSingleLayoutViewBinding.userKahooContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ksdfjhskfd","item clicked ");
                if (!isTagClicked){
                    Log.e("ksdfjhskfd","not inside if item clicked ");
                    onItemClicked(mContext,dataKahoPost);
                }
            }
        });

        kahooSingleLayoutViewBinding.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ksdfjhskfd","item clicked ");
                onItemClicked(mContext,dataKahoPost);
            }
        });


        kahooSingleLayoutViewBinding.kahoMediaContentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mContext instanceof ViewKahooFrontFragment){
                    ((ViewKahooFrontFragment) mContext).openMediaFile(position,dataKahoPost);
                }else if (mContext instanceof SingleDetailKahooView){
                    ((SingleDetailKahooView) mContext).openMediaFile(position,dataKahoPost);
                }else if (mContext instanceof SingleHashTagKahooView){
                    ((SingleHashTagKahooView) mContext).openMediaFile(position,dataKahoPost);
                }else if (mContext instanceof DobaraKahooList){
                    ((DobaraKahooList) mContext).openMediaFile(position,dataKahoPost);
                }else if (mContext instanceof LikedKahooList){
                    ((LikedKahooList) mContext).openMediaFile(position,dataKahoPost);
                }else if (mContext instanceof MyKahooListFrag){
                    ((MyKahooListFrag) mContext).openMediaFile(position,dataKahoPost);
                }else if (mContext instanceof ReplyKahooListFrag){
                    ((ReplyKahooListFrag) mContext).openMediaFile(position,dataKahoPost);
                }

                Log.e("kdfsfsdf","clicked on media holder ");
            }
        });


        kahooSingleLayoutViewBinding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof ViewKahooFrontFragment){
                    ((ViewKahooFrontFragment) mContext).shareKahooClicked(dataKahoPost,position,holder.itemView);
                }else if (mContext instanceof SingleDetailKahooView){
                    ((SingleDetailKahooView) mContext).shareKahooClicked(dataKahoPost,position,holder.itemView);
                }else if (mContext instanceof SingleHashTagKahooView){
                    ((SingleHashTagKahooView) mContext).shareKahooClicked(dataKahoPost,position,holder.itemView);
                }else if (mContext instanceof DobaraKahooList){
                    ((DobaraKahooList) mContext).shareKahooClicked(dataKahoPost,position,holder.itemView);
                }else if (mContext instanceof LikedKahooList){
                    ((LikedKahooList) mContext).shareKahooClicked(dataKahoPost,position,holder.itemView);
                }else if (mContext instanceof MyKahooListFrag){
                    ((MyKahooListFrag) mContext).shareKahooClicked(dataKahoPost,position,holder.itemView);
                }else if (mContext instanceof ReplyKahooListFrag){
                    ((ReplyKahooListFrag) mContext).shareKahooClicked(dataKahoPost,position,holder.itemView);
                }
            }
        });
        kahooSingleLayoutViewBinding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof ViewKahooFrontFragment){
                    ((ViewKahooFrontFragment) mContext).showUserDetail(dataKahoPost);
                }else if (mContext instanceof SingleDetailKahooView){
                    ((SingleDetailKahooView) mContext).showUserDetail(dataKahoPost);
                }else if (mContext instanceof SingleHashTagKahooView){
                    ((SingleHashTagKahooView) mContext).showUserDetail(dataKahoPost);
                }else if (mContext instanceof DobaraKahooList){
                    ((DobaraKahooList) mContext).showUserDetail(dataKahoPost);
                }else if (mContext instanceof LikedKahooList){
                    ((LikedKahooList) mContext).showUserDetail(dataKahoPost);
                }else if (mContext instanceof MyKahooListFrag){
                    ((MyKahooListFrag) mContext).showUserDetail(dataKahoPost);
                }else if (mContext instanceof ReplyKahooListFrag){
                    ((ReplyKahooListFrag) mContext).showUserDetail(dataKahoPost);
                }
            }
        });
        /*kahooSingleLayoutViewBinding.moreImgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof ViewKahooFrontFragment){
                    ((ViewKahooFrontFragment) mContext).openMediaFile(position,dataKahoPost);
                }else if (mContext instanceof SingleDetailKahooView){
                    ((SingleDetailKahooView) mContext).kahoMoreMediaClicked(dataKahoPost);
                }
            }
        });*/
        kahooSingleLayoutViewBinding.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof ViewKahooFrontFragment){
                    ((ViewKahooFrontFragment) mContext).likeKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof SingleDetailKahooView){
                    ((SingleDetailKahooView) mContext).likeKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof SingleHashTagKahooView){
                    ((SingleHashTagKahooView) mContext).likeKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof DobaraKahooList){
                    ((DobaraKahooList) mContext).likeKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof LikedKahooList){
                    ((LikedKahooList) mContext).likeKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof MyKahooListFrag){
                    ((MyKahooListFrag) mContext).likeKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof ReplyKahooListFrag){
                    ((ReplyKahooListFrag) mContext).likeKahooClicked(dataKahoPost,position);
                }
            }
        });
        kahooSingleLayoutViewBinding.dobaraKahoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mContext instanceof ViewKahooFrontFragment){
                    ((ViewKahooFrontFragment) mContext).dobaraKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof SingleDetailKahooView){
                    ((SingleDetailKahooView) mContext).dobaraKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof SingleHashTagKahooView){
                    ((SingleHashTagKahooView) mContext).dobaraKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof DobaraKahooList){
                    ((DobaraKahooList) mContext).dobaraKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof LikedKahooList){
                    ((LikedKahooList) mContext).dobaraKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof MyKahooListFrag){
                    ((MyKahooListFrag) mContext).dobaraKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof ReplyKahooListFrag){
                    ((ReplyKahooListFrag) mContext).dobaraKahooClicked(dataKahoPost,position);
                }
            }
        });
        kahooSingleLayoutViewBinding.replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof ViewKahooFrontFragment){
                    ((ViewKahooFrontFragment) mContext).replyKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof SingleDetailKahooView){
                    ((SingleDetailKahooView) mContext).replyKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof SingleHashTagKahooView){
                    ((SingleHashTagKahooView) mContext).replyKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof DobaraKahooList){
                    ((DobaraKahooList) mContext).replyKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof LikedKahooList){
                    ((LikedKahooList) mContext).replyKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof MyKahooListFrag){
                    ((MyKahooListFrag) mContext).replyKahooClicked(dataKahoPost,position);
                }else if (mContext instanceof ReplyKahooListFrag){
                    ((ReplyKahooListFrag) mContext).replyKahooClicked(dataKahoPost,position);
                }
            }
        });
    }

    private void onItemClicked(Fragment mContext,KahooPostModel dataKahoPost) {
        Log.e("kjhfksfsfre","this is item clicked ");
        if (mContext instanceof ViewKahooFrontFragment){
            ((ViewKahooFrontFragment) mContext).showDetailKahoo(dataKahoPost);
        }else if (mContext instanceof SingleDetailKahooView){
            ((SingleDetailKahooView) mContext).showDetailKahoo(dataKahoPost);
        }else if (mContext instanceof SingleHashTagKahooView){
            ((SingleHashTagKahooView) mContext).showDetailKahoo(dataKahoPost);
        }else if (mContext instanceof DobaraKahooList){
            ((DobaraKahooList) mContext).showDetailKahoo(dataKahoPost);
        }else if (mContext instanceof LikedKahooList){
            ((LikedKahooList) mContext).showDetailKahoo(dataKahoPost);
        }else if (mContext instanceof MyKahooListFrag){
            ((MyKahooListFrag) mContext).showDetailKahoo(dataKahoPost);
        }else if (mContext instanceof ReplyKahooListFrag){
            ((ReplyKahooListFrag) mContext).showDetailKahoo(dataKahoPost);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isTagClicked=false;
            }
        },200);
    }

    private void onTagClicked(String tagType,String hashTag,Fragment mContext,KahooPostModel kahooPostModel) {

        Log.e("kjhfksfsfre","this is tagtype "+tagType);
        Log.e("kjhfksfsfre","this is tagtype "+hashTag);
        if (mContext==null){
            Log.e("kjhfksfsfre","context is null ");
        }else {
            Log.e("kjhfksfsfre","context is not null ");
        }
        if (tagType.equalsIgnoreCase("@")){
            //at the rate unique handel type

            if (mContext instanceof ViewKahooFrontFragment){
                ((ViewKahooFrontFragment) mContext).showTagKahooDetail(hashTag,false);
            }else if (mContext instanceof SingleDetailKahooView){
                ((SingleDetailKahooView) mContext).showTagKahooDetail(hashTag,false);
            }else if (mContext instanceof SingleHashTagKahooView){
                ((SingleHashTagKahooView) mContext).showTagKahooDetail(hashTag,false);
            }else if (mContext instanceof DobaraKahooList){
                ((DobaraKahooList) mContext).showTagKahooDetail(hashTag,false);
            }else if (mContext instanceof LikedKahooList){
                ((LikedKahooList) mContext).showTagKahooDetail(hashTag,false);
            }else if (mContext instanceof MyKahooListFrag){
                ((MyKahooListFrag) mContext).showTagKahooDetail(hashTag,false);
            }else if (mContext instanceof ReplyKahooListFrag){
                ((ReplyKahooListFrag) mContext).showTagKahooDetail(hashTag,false);
            }

        }else if(tagType.equalsIgnoreCase("#")){
            //hash tag type
            if (mContext instanceof ViewKahooFrontFragment){
                Log.e("kjhfksfsfre","ViewKahooFrontFragment");
                ((ViewKahooFrontFragment) mContext).showTagKahooDetail(hashTag,true);
            }else if (mContext instanceof SingleDetailKahooView){
                Log.e("kjhfksfsfre","SingleDetailKahooView");
                ((SingleDetailKahooView) mContext).showTagKahooDetail(hashTag,true);
            }else if (mContext instanceof SingleHashTagKahooView){
                Log.e("kjhfksfsfre","SingleHashTagKahooView");
                ((SingleHashTagKahooView) mContext).showTagKahooDetail(hashTag,true);
            }else if (mContext instanceof DobaraKahooList){
                ((DobaraKahooList) mContext).showTagKahooDetail(hashTag,true);
            }else if (mContext instanceof LikedKahooList){
                ((LikedKahooList) mContext).showTagKahooDetail(hashTag,true);
            }else if (mContext instanceof MyKahooListFrag){
                ((MyKahooListFrag) mContext).showTagKahooDetail(hashTag,true);
            }else if (mContext instanceof ReplyKahooListFrag){
                ((ReplyKahooListFrag) mContext).showTagKahooDetail(hashTag,true);
            }
        }else {
            onItemClicked(mContext,kahooPostModel);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isTagClicked=false;
            }
        },200);
    }

    public boolean checkIsVideo(String media){
        boolean isVideo=false;
        if (media.contains(".mp4")||media.contains("kahooappSerQ38videos")){
            isVideo=true;
        }
        return isVideo;
    }

    public boolean isVideo(){
        boolean isVideo=false;
        if (kahooPostModel.getKaho_media_list().size()>0) {
            if (kahooPostModel.getKaho_media_list().get(0).contains(".mp4")||
                    kahooPostModel.getKaho_media_list().get(0).contains("kahooappSerQ38videos")){
                isVideo=true;
            }
        }
        return isVideo;
    }


    public String  extractURL(String str) {
        String finalUrl="";
        // Creating an empty ArrayList
        List<String> list = new ArrayList<>();

        String regex
                = "\\b((?:https?|ftp|file):"
                + "//[-a-zA-Z0-9+&@#/%?="
                + "~_|!:, .;]*[-a-zA-Z0-9+"
                + "&@#/%=~_|])";

        Pattern p = Pattern.compile(
                regex,
                Pattern.CASE_INSENSITIVE);

        // Find the match between string
        // and the regular expression
        Matcher m = p.matcher(str);

        // Find the next subsequence of
        // the input subsequence that
        // find the pattern
        while (m.find()) {

            // Find the substring from the
            // first index of match result
            // to the last index of match
            // result and add in the list
            list.add(str.substring(
                    m.start(0), m.end(0)));
        }

        // IF there no URL present
        if (list.size() == 0) {
            System.out.println("-1");
            return finalUrl;
        }

        // Print all the URLs stored

        for (String url : list) {
            finalUrl+=url;
        }
        return finalUrl;
    }

    public String getMediaUrl() {
        if (kahooPostModel.getKaho_media_list().size()>0){
            return kahooPostModel.getKaho_media_list().get(0);
        }else {
            return "no media file";
        }
    }
}
