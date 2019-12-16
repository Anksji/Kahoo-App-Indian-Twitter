package com.kaho.app.ViewHolders.KahooViewHolders;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaho.app.Adapters.KahooListAdapter;
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
import com.kaho.app.databinding.KahooDobaraSingleLayoutBinding;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.ponnamkarthik.richlinkpreview.ViewListener;

import static com.kaho.app.Tools.Constants.PROFILE_IMAGE_NAME;

public class KahooDobaraViewHolder extends RecyclerView.ViewHolder {

    public KahooDobaraSingleLayoutBinding kahooDobaraBinding;
    public View itemView;
    private SessionPrefManager sessionPrefManager;
    private boolean isMoreOptionOpen;
    private LinearLayoutManager mediaLinearLayoutMngr;
    private KahooListAdapter kahooListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<KahooPostModel> kahooList=new ArrayList<>();
    private HashTagHelper mTextHashTagHelper;
    private HashTagHelper headingTagHelper;
    private boolean isTagClicked=false;

    public KahooDobaraViewHolder(@NonNull KahooDobaraSingleLayoutBinding kahooDobaraBinding) {
        super(kahooDobaraBinding.getRoot());
        itemView=kahooDobaraBinding.getRoot();
        this.kahooDobaraBinding =kahooDobaraBinding;
    }
    public void setVideoPlaying(){
        kahooDobaraBinding.kahoImgHolder.setVisibility(View.GONE);
    }
    private KahooPostModel kahooPostModel;
    public void setUpKahooView(KahooDobaraViewHolder holder, Fragment mContext,
                               KahooPostModel dataKahoPost, int position, boolean isFromViewHolder)  {

        kahooPostModel=dataKahoPost;
        sessionPrefManager=new SessionPrefManager(mContext.getActivity());

        if (dataKahoPost.getOther_kaho_data()!=null&&(dataKahoPost.isHas_dobara_kaho()||dataKahoPost.isHas_reply_kaho())){
            kahooListAdapter = new KahooListAdapter(mContext,true,kahooList);
            linearLayoutManager=new LinearLayoutManager(mContext.getActivity(), LinearLayoutManager.HORIZONTAL, false);
            kahooDobaraBinding.otherKahooList.setLayoutManager(linearLayoutManager);
            kahooDobaraBinding.otherKahooList.setAdapter(kahooListAdapter);
            kahooDobaraBinding.otherKahooList.setNestedScrollingEnabled(false);
            ArrayList<KahooPostModel> list=new ArrayList<>();
            list.add(dataKahoPost.getOther_kaho_data());
            List<KahooPostModel> dataList=(List<KahooPostModel>)(Object) list;
            kahooListAdapter.updateKahooList(dataList);
            kahooListAdapter.notifyDataSetChanged();

            kahooDobaraBinding.replyText.setVisibility(View.VISIBLE);

            String content="";
            if (dataKahoPost.isHas_reply_kaho()){
                content="Reply to";
                //kahooDobaraBinding.replyText.setText("Reply to @"+dataKahoPost.getOther_kaho_data().getKaho_added_user_handel_id());
                content+=" <font color='#fe9e21'>@"+dataKahoPost.getOther_kaho_data().getKaho_added_user_handel_id()+"</font>";

            }

            if (position==0){
                Log.e("kjsdfksff","dobara kaho status "+dataKahoPost.isHas_dobara_kaho());
            }

            if (dataKahoPost.isHas_dobara_kaho()){
                content="Dobara Kaha";
                content+=" <font color='#fe9e21'>@"+dataKahoPost.getKaho_added_user_handel_id()+"</font>";

                //kahooDobaraBinding.replyText.setText("Dobara Kahoo @"+dataKahoPost.getOther_kaho_data().getKaho_added_user_handel_id());
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                kahooDobaraBinding.replyText.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
            } else {
                kahooDobaraBinding.replyText.setText(Html.fromHtml(content));
            }
        }

        /*if (dataKahoPost.getKaho_added_user_id().equalsIgnoreCase(sessionPrefManager.getUserID())){
            kahooDobaraBinding.moreOptionBtn.setVisibility(View.VISIBLE);
        }else {
            kahooDobaraBinding.moreOptionBtn.setVisibility(View.GONE);
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
                .into(kahooDobaraBinding.userImage);

        kahooDobaraBinding.mediaCount.setText("1/"+(dataKahoPost.getKaho_media_list().size()));

        kahooDobaraBinding.linkPreviewLayout.setVisibility(View.GONE);




        /*if (urlFromcont.length()>0){
            //string has url
            kahooDobaraBinding.loadWeb.setVisibility(View.VISIBLE);
            kahooDobaraBinding.webViewLayout.setVisibility(View.VISIBLE);
            kahooDobaraBinding.loadWeb.load(urlFromcont);
            String finalUrlFromcont = urlFromcont;
            kahooDobaraBinding.webClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("sdkjfskffs","clicking on view");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrlFromcont));
                    mContext.getActivity().startActivity(browserIntent);
                }
            });
        }else {
            //String doesn't has url
            kahooDobaraBinding.webViewLayout.setVisibility(View.GONE);
            kahooDobaraBinding.loadWeb.setVisibility(View.GONE);
        }*/


        Log.e("kjdhfksdf","then url is  "+urlFromcont);


        if (mContext instanceof DobaraKahooAurReplyFrag||isFromViewHolder){
            kahooDobaraBinding.likesReply.setVisibility(View.GONE);
            kahooDobaraBinding.divider.setVisibility(View.GONE);
            kahooDobaraBinding.moreOptionBtn.setVisibility(View.GONE);
        }else {
            kahooDobaraBinding.divider.setVisibility(View.VISIBLE);
            kahooDobaraBinding.likesReply.setVisibility(View.VISIBLE);
            kahooDobaraBinding.moreImgLayout.setVisibility(View.VISIBLE);
        }

        kahooDobaraBinding.dobaraKahoCount.setText(""+dataKahoPost.getDobara_kahoo_count());
        kahooDobaraBinding.likeCountLike.setText(""+dataKahoPost.getKaho_like_count());
        kahooDobaraBinding.replyCountReply.setText(""+dataKahoPost.getKahoo_reply_count());
        kahooDobaraBinding.userKahooContent.setText(dataKahoPost.getKahoo_text_content());
        kahooDobaraBinding.shareCount.setText(""+dataKahoPost.getKahoo_share_count());



        if (dataKahoPost.getLikedUserIdsList().contains(sessionPrefManager.getUserID())){
            kahooDobaraBinding.heartSymbol.setImageDrawable(mContext.getActivity().getResources().getDrawable(R.drawable.ic_heart_colored));
        }else {
            kahooDobaraBinding.heartSymbol.setImageDrawable(mContext.getActivity().getResources().getDrawable(R.drawable.ic_heart));
        }

        /*
        isMoreOptionOpen=false;
        kahooDobaraBinding.moreOptionLayout.setVisibility(View.INVISIBLE);
        kahooDobaraBinding.moreOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMoreOptionOpen){
                    isMoreOptionOpen=true;
                    kahooDobaraBinding.moreOptionLayout.setVisibility(View.VISIBLE);
                }else {
                    isMoreOptionOpen=false;
                    kahooDobaraBinding.moreOptionLayout.setVisibility(View.INVISIBLE);
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
            kahooDobaraBinding.userNameHandleTimeHead.setText(Html.fromHtml(titleHeadContent, Html.FROM_HTML_MODE_COMPACT));
        } else {
            kahooDobaraBinding.userNameHandleTimeHead.setText(Html.fromHtml(titleHeadContent));
        }

       /* Glide.with(mContext)
                .load(dataKahoPost.getKaho_added_user_image())
                .placeholder(R.color.dark_primary_500)
                .error(R.color.dark_primary_500)
                .into(kahooDobaraBinding.userImage);*/
        if (dataKahoPost.getKaho_media_list().size()>1){
            kahooDobaraBinding.moreImgGradient.setVisibility(View.VISIBLE);
            kahooDobaraBinding.moreImgLayout.setVisibility(View.VISIBLE);
        }else {
            kahooDobaraBinding.moreImgLayout.setVisibility(View.GONE);
            kahooDobaraBinding.moreImgGradient.setVisibility(View.GONE);
        }
        if (dataKahoPost.getKaho_media_list().size()>0){
            kahooDobaraBinding.kahoMediaContentHolder.setVisibility(View.VISIBLE);
            if (dataKahoPost.getKaho_media_list().get(0).contains(".mp4")||
                    dataKahoPost.getKaho_media_list().get(0).contains("kahooappSerQ38videos")){
                //video file
                kahooDobaraBinding.videoControle.setVisibility(View.VISIBLE);
                kahooDobaraBinding.mediaContainer.setVisibility(View.VISIBLE);
                kahooDobaraBinding.ivVolumeControl.setVisibility(View.VISIBLE);

            }else {
                //not video file
                kahooDobaraBinding.mediaContainer.setVisibility(View.GONE);
                kahooDobaraBinding.videoControle.setVisibility(View.GONE);
                kahooDobaraBinding.ivVolumeControl.setVisibility(View.GONE);
            }

            Log.e("sdkfskff","media list size "+dataKahoPost.getKaho_media_list().size());
            Log.e("sdkfskff","media 0 index content "+dataKahoPost.getKaho_media_list().get(0));
            String imgUrl=dataKahoPost.getKaho_media_list().get(0);
            Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(R.color.dark_primary_500)
                    .error(R.color.dark_primary_500)
                    .override(800, 560)
                    .into(kahooDobaraBinding.kahoImgHolder);

            /*Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(R.color.dark_primary_500)
                    .error(R.color.dark_primary_500)
                    .override(800, 560)
                    .into(kahooDobaraBinding.ivMediaCoverImage);*/

        } else {
            kahooDobaraBinding.kahoMediaContentHolder.setVisibility(View.GONE);
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

        mTextHashTagHelper.handle(kahooDobaraBinding.userKahooContent);
        Linkify.addLinks(kahooDobaraBinding.userKahooContent, Linkify.WEB_URLS);
        kahooDobaraBinding.userKahooContent.setMovementMethod(LinkMovementMethod.getInstance());


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

        headingTagHelper.handle(kahooDobaraBinding.userNameHandleTimeHead);

        kahooDobaraBinding.userKahooContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ksdfjhskfd","item clicked ");
                if (!isTagClicked){
                    Log.e("ksdfjhskfd","not inside if item clicked ");
                    onItemClicked(mContext,dataKahoPost);
                }
            }
        });

        kahooDobaraBinding.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ksdfjhskfd","item clicked ");
                onItemClicked(mContext,dataKahoPost);
            }
        });

        kahooDobaraBinding.userKahooContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ksdfjhskfd","item clicked ");
                if (!isTagClicked){
                    Log.e("ksdfjhskfd","not inside if item clicked ");
                    onItemClicked(mContext,dataKahoPost);
                }
            }
        });

        kahooDobaraBinding.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ksdfjhskfd","item clicked ");
                onItemClicked(mContext,dataKahoPost);
            }
        });


        kahooDobaraBinding.kahoMediaContentHolder.setOnClickListener(new View.OnClickListener() {
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


        kahooDobaraBinding.shareBtn.setOnClickListener(new View.OnClickListener() {
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
        kahooDobaraBinding.userImage.setOnClickListener(new View.OnClickListener() {
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
        /*kahooDobaraBinding.moreImgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof ViewKahooFrontFragment){
                    ((ViewKahooFrontFragment) mContext).openMediaFile(position,dataKahoPost);
                }else if (mContext instanceof SingleDetailKahooView){
                    ((SingleDetailKahooView) mContext).kahoMoreMediaClicked(dataKahoPost);
                }
            }
        });*/
        kahooDobaraBinding.likeBtn.setOnClickListener(new View.OnClickListener() {
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
        kahooDobaraBinding.dobaraKahoBtn.setOnClickListener(new View.OnClickListener() {
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
        kahooDobaraBinding.replyBtn.setOnClickListener(new View.OnClickListener() {
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

        /*kahooDobaraBinding.moreImgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof ViewKahooFrontFragment){
                    ((ViewKahooFrontFragment) mContext).openMediaFile(position,dataKahoPost);
                }else if (mContext instanceof SingleDetailKahooView){
                    ((SingleDetailKahooView) mContext).kahoMoreMediaClicked(dataKahoPost);
                }
            }
        });*/

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
                ((ViewKahooFrontFragment) mContext).showTagKahooDetail(hashTag,true);
            }else if (mContext instanceof SingleDetailKahooView){
                ((SingleDetailKahooView) mContext).showTagKahooDetail(hashTag,true);
            }else if (mContext instanceof SingleHashTagKahooView){
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
        return kahooPostModel.getKaho_media_list().get(0);
    }
}
