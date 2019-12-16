package com.kaho.app.UI.KahooUi.ViewingKahoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaho.app.Activities.YoutubePlayVideoScreen;
import com.kaho.app.Adapters.HashListAdpters;
import com.kaho.app.Adapters.KahooListAdapter;
import com.kaho.app.Data.Models.HashTagsModel;
import com.kaho.app.Data.Models.KahooPostModel;
import com.kaho.app.Data.Models.SingltonUserDataModel;
import com.kaho.app.Data.Models.UserModel;
import com.kaho.app.LiveData.KahooListLiveData;
import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;
import com.kaho.app.Tools.FirebaseConstant;
import com.kaho.app.Tools.PublicMethods;
import com.kaho.app.Tools.Utills.ScreenshotUtils;
import com.kaho.app.ViewModels.KahooViewModel;
import com.kaho.app.databinding.ViewKahooFragmentBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kaho.app.Tools.FirebaseConstant.KAHOO_MAIN_COLLECTION;
import static com.kaho.app.Tools.FirebaseConstant.KAHOO_REPORTS_COLLECTION;
import static com.kaho.app.Tools.PublicMethods.formatStringNumber;
import static com.kaho.app.Tools.PublicMethods.loadBitmapFromView;

public class ViewKahooFrontFragment extends Fragment {

    private NavController navController;
    private ViewKahooFragmentBinding kahooFragmentBinding;
    private KahooListAdapter kahooListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private KahooViewModel viewModelKahoo;
    private SessionPrefManager sessionPrefManager;
    private FirebaseFirestore mDatabase;
    private HashListAdpters hashListAdpters;
    private String currentHashTagSelected="all";
    private ArrayList<HashTagsModel>hashTagList=new ArrayList<>();
    private ArrayList<HashTagsModel>shortedHashTagList=new ArrayList<>();
    private FirebaseDatabase myDb;
    private boolean isReload=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        kahooFragmentBinding=ViewKahooFragmentBinding.inflate(inflater,container,false);
        return kahooFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        clickListeners();

        myDb = FirebaseDatabase.getInstance();
        mDatabase=FirebaseFirestore.getInstance();
        sessionPrefManager=new SessionPrefManager(getActivity());
        kahooListAdapter = new KahooListAdapter(ViewKahooFrontFragment.this,false,kahooList);
        linearLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        kahooFragmentBinding.kahooListRv.setLayoutManager(linearLayoutManager);
        kahooFragmentBinding.kahooListRv.setAdapter(kahooListAdapter);

        kahooFragmentBinding.hashList.setHasFixedSize(true);
        hashListAdpters = new HashListAdpters(shortedHashTagList,getActivity(), ViewKahooFrontFragment.this);
        kahooFragmentBinding.hashList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        kahooFragmentBinding.hashList.setAdapter(hashListAdpters);
        getTrendingHashTags();

        setupUserInfo();

        kahooFragmentBinding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        kahooFragmentBinding.skimmerView.shimmerViewContainer.startShimmer();
    }


    public void refreshList(){
        isReload=true;
        kahooList.clear();
        kahooListAdapter.notifyDataSetChanged();
        kahooFragmentBinding.skimmerView.shimmerViewContainer.setVisibility(View.VISIBLE);
        kahooFragmentBinding.skimmerView.shimmerViewContainer.startShimmer();
        getKahooList();
    }


    public void kahoMediaClicked(int index,KahooPostModel kahooPostModel){

        //playVideoClicked(kahooPostModel);
        /*
        Bundle bundle =new Bundle();
        bundle.putString("media_url",kahooPostModel.getKaho_media_list().get(0));
        navController.navigate(R.id.action_kahooMainFragment_to_playOnlineVideosFrag,bundle);
        */
            Log.e("kdsjfk","kaho clicke "+index+"Media list size "+kahooPostModel.getKaho_media_list().size());
            if (kahooPostModel.getKaho_media_list().size()>0&&isVideo(kahooPostModel.getKaho_media_list().get(0))){
                Log.e("kdsjfk","inside if kaho clicke "+index+"Media url "+kahooPostModel.getKaho_media_list().get(0));
                kahooFragmentBinding.kahooListRv.playVideoFromIndex(index,kahooPostModel.getKaho_media_list().get(0));
            }

    }

    private FirebaseFunctions mFunctions;
    private Task<String> getTimeInPast(int days) {
        mFunctions = FirebaseFunctions.getInstance();
        Log.e("sjdhkfskfjsf","calling time in future ");
        Map<String, Object> data = new HashMap<>();
        data.put("aheadInFuture",days);
        return mFunctions.getHttpsCallable("getPastTime")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        Log.e("sjdhkfskfjsf","this is function result "+result);
                        return result;
                    }
                });
    }

    private void getTrendingHashTags() {

        long time=System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date newDate = calendar.getTime();

        getTopTrendingHashTags(""+calendar.getTimeInMillis());
        Log.e("dskfhfdffsd","new date "+calendar.getTimeInMillis()+" prev tme "+time);

        getTimeInPast(1).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                        Log.e("sjdhkfskfjsf","exception this is task result "+details);
                    }
                    else{
                        //String  future_date=task.getResult().trim().toString();
                        String pastDate=task.getResult().trim().toString();
                        Log.e("sjdhkfskfjsf","past time  "+pastDate);
                        getTopTrendingHashTags(pastDate);
                    }
                }
            }
        });



    }

    private void getTopTrendingHashTags(String pastDate) {
        DatabaseReference dabaseRef=myDb.getReference("TrendingHashTags");

        Log.e("jkhksdasasd","thi sis past date "+pastDate);
        /*ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("jkhksdasasd", " onChildAdded : " + dataSnapshot.getKey());
                HashTagsModel tagsModel=dataSnapshot.getValue(HashTagsModel.class);
                if (hashTagList.contains(tagsModel)){
                    int index=hashTagList.indexOf(tagsModel);

                    Log.e("kjdhfdkfsdfd","this is current index "+index);
                }else {
                    hashTagList.add(0,tagsModel);
                }
                Log.e("jkhksdasasd", "this is list item hash tag "+tagsModel.getHashTag()+" timestamp "+tagsModel.getLast_used_time()+" total use time "+tagsModel.getUsed_no_of_times());
                shortHashList();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                Log.d("jkhksdasasd", "onChildChanged");
                HashTagsModel tagsModel=dataSnapshot.getValue(HashTagsModel.class);
                if (hashTagList.contains(tagsModel)){
                    Log.d("kjdfksafjsdkl", "item contain in list ");
                    int index=hashTagList.indexOf(tagsModel);
                    hashTagList.get(index).setUsed_no_of_times(tagsModel.getUsed_no_of_times());
                    Log.e("kjdhfdkfsdfd","this is current index "+index);
                }else {
                    Log.d("kjdfksafjsdkl", "item not contain in list ");
                    hashTagList.add(0,tagsModel);
                }
                Log.e("jkhksdasasd", "this is list item hash tag "+tagsModel.getHashTag()+" timestamp "+tagsModel.getLast_used_time()+" total use time "+tagsModel.getUsed_no_of_times());
                shortHashList();

            }@Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("jkhksdasasd", "onChildRemoved:" + dataSnapshot.getKey());
                String commentKey = dataSnapshot.getKey();

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("jkhksdasasd", "onChildMoved:" + dataSnapshot.getKey());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                 Log.d("jkhksdasasd", "postComments:onCancelled", databaseError.toException());
            }
        };
        double lastTime=Double.parseDouble(pastDate);
        dabaseRef.orderByChild("last_used_time").startAt(lastTime)
                .limitToLast(40).addChildEventListener(childEventListener);*/
        Log.d("jkhksdasasd", "last line");


        /*final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    HashTagsModel tagsModel=postSnapshot.getValue(HashTagsModel.class);
                    if (hashTagList.contains(tagsModel)){
                        Log.d("kjdfksafjsdkl", "item contain in list ");
                        int index=hashTagList.indexOf(tagsModel);
                        hashTagList.get(index).setUsed_no_of_times(tagsModel.getUsed_no_of_times());
                        Log.e("kjdhfdkfsdfd","this is current index "+index);
                    }else {
                        Log.d("kjdfksafjsdkl", "item not contain in list ");
                        hashTagList.add(0,tagsModel);
                    }
                    Log.e("kjfshhweds", "this is list item hash tag "+tagsModel.getHashTag()+" timestamp "+tagsModel.getLast_used_time()+" total use time "+tagsModel.getUsed_no_of_times());


                    tagsModel=postSnapshot.getValue(HashTagsModel.class);

                    if (!hashTagList.contains(tagsModel)) {
                        hashTagList.add(0,tagsModel);
                    }else {
                        HashTagsModel tagsModel2=postSnapshot.getValue(HashTagsModel.class);
                        int index=hashTagList.indexOf(tagsModel2);

                        Log.e("kjdhfdkfsdfd","this is current index "+index);
                    }
                    //shortedHashTagList;
                }
                shortHashList();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("kjfhksfdsd", "loadPost:onCancelled", databaseError.toException());
            }
        };
        double lastTime=Double.parseDouble(pastDate);
        dabaseRef.orderByChild("last_used_time").startAt(lastTime)
                .limitToLast(10).addValueEventListener(postListener);
        */

        myDb.getReference("TrendingHashTags").orderByChild("last_used_time")
                .limitToLast(20).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    HashTagsModel tagsModel=postSnapshot.getValue(HashTagsModel.class);
                    if (!hashTagList.contains(tagsModel)) {
                        hashTagList.add(0,tagsModel);
                    }
                }
                shortHashList();
                //dabaseRef.removeEventListener();
                Log.e("kjdfhjskdff","on data change is called list size "+hashTagList.size());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("kjdfhjskdff", "loadPost:onCancelled",
                        databaseError.toException());

            }
        });

    }

    private void shortHashList() {

        Collections.sort(hashTagList);
        for(HashTagsModel hash : hashTagList){
            Log.e("dsjfhsjdfksfs","hash tag "+hash.getHashTag()+" hash tag use number "+hash.getUsed_no_of_times());
        }

        shortedHashTagList.clear();

        int j=1;
        for (int i=hashTagList.size()-1;i>=0&&j<=10;i--){
            if (!hashTagList.get(i).getHashTag().equalsIgnoreCase("all")){
                j++;
                shortedHashTagList.add(hashTagList.get(i));
            }
        }

        Log.e("kjdhsffsd","sorted list size "+shortedHashTagList.size());
        hashListAdpters.notifyDataSetChanged();
    }

    public void trendingHashTagSelected(int position, HashTagsModel hashTag){
        Bundle bundle =new Bundle();
        bundle.putString("clicked_hash_tag",hashTag.getHashTag());
        navController.navigate(R.id.action_kahooMainFragment_to_singleHashTagKahooView,bundle);
       /* for (int i=0;i<shortedHashTagList.size();i++){
            shortedHashTagList.get(i).setSelected(false);
        }
        shortedHashTagList.get(position).setSelected(true);
        hashListAdpters.notifyDataSetChanged();

        currentHashTagSelected=hashTag.getHashTag();

        kahooList.clear();
        kahooListAdapter.notifyDataSetChanged();
        kahooFragmentBinding.skimmerView.shimmerViewContainer.setVisibility(View.VISIBLE);
        kahooFragmentBinding.skimmerView.shimmerViewContainer.startShimmer();
        getKahooList();*/

    }

    private String currentKahooUrl="";
    private String currentVideoId="";

    public void openYoutubeVideo(int position,KahooPostModel kahooPostModel,String videoUrl){
        Log.e("ksdfhssdf","this is playing youtube video "+videoUrl);

        currentVideoId=PublicMethods.getYoutubeVideoId(videoUrl);
        Intent intent=new Intent(getContext(), YoutubePlayVideoScreen.class);
        intent.putExtra("videoId",currentVideoId);
        intent.putExtra("video_url",videoUrl);
        getActivity().startActivity(intent);

    }

    private String videoUrl="";
    private ArrayList<String> imageMediaList=new ArrayList<>();
    private int currentMediaIndex=-1;

    public void openMediaFile(int position,KahooPostModel kahooPostModel){


        if (kahooPostModel.getKaho_media_list().size()>0){
            kahooFragmentBinding.mediaView.setVisibility(View.VISIBLE);
            kahooFragmentBinding.playerView.setVisibility(View.VISIBLE);
            imageMediaList=kahooPostModel.getKaho_media_list();
            currentMediaIndex=-1;
            openMedia(true);
        }

        openMediaAnimation();

        kahooFragmentBinding.leftMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kahooFragmentBinding.mediaImage.setVisibility(View.GONE);
                kahooFragmentBinding.idExoPlayerVIew.setVisibility(View.GONE);
                openMedia(false);
            }
        });
        kahooFragmentBinding.rightMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kahooFragmentBinding.mediaImage.setVisibility(View.GONE);
                kahooFragmentBinding.idExoPlayerVIew.setVisibility(View.GONE);
                openMedia(true);
            }
        });
        kahooFragmentBinding.closeMediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMedia();
            }
        });
        kahooFragmentBinding.playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMedia();
            }
        });
    }

    private void openMediaAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_anim);
        animation.setStartOffset(0);
        kahooFragmentBinding.centerView.startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_anim);
        animation.setStartOffset(0);
        kahooFragmentBinding.closeMediaView.startAnimation(animation2);
    }

    private void closeMedia() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down_anim);
        animation.setStartOffset(0);
        kahooFragmentBinding.centerView.startAnimation(animation);
        Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_anim);
        animation.setStartOffset(0);
        kahooFragmentBinding.closeMediaView.startAnimation(animation2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                kahooFragmentBinding.playerView.setVisibility(View.GONE);
            }
        },490);

    }

    private void openMedia(boolean isRight) {
        if (isRight){
            currentMediaIndex++;
        }else {
            currentMediaIndex--;
        }

        if (imageMediaList.size()>currentMediaIndex&&currentMediaIndex>=0){

        }else {
            currentMediaIndex=0;
        }

        kahooFragmentBinding.mediaIndex.setText(""+(1+currentMediaIndex)+"/"+(imageMediaList.size()));
        Log.e("kjdfhksd","calling open media isRight "+isRight+" current media index "+currentMediaIndex+" media list size "+imageMediaList.size());

        if (imageMediaList.size()>currentMediaIndex&&currentMediaIndex>=0){
            kahooFragmentBinding.playerView.setVisibility(View.VISIBLE);
            if (isVideo(imageMediaList.get(currentMediaIndex))) {
                Log.e("kdhfksfs","is video playing currentMediaIndex "+currentMediaIndex);
                MediaSource mediaSource = new ExtractorMediaSource(
                        Uri.parse(imageMediaList.get(currentMediaIndex)),
                        new DefaultDataSourceFactory(getContext(), "MyMedia"),
                        new DefaultExtractorsFactory(),
                        null,
                        null);
                player.prepare(mediaSource);
                player.setPlayWhenReady(true);

                kahooFragmentBinding.mediaImage.setVisibility(View.GONE);
                kahooFragmentBinding.idExoPlayerVIew.setVisibility(View.VISIBLE);
            } else {
                Log.e("kdhfksfs","is image playing currentMediaIndex "+currentMediaIndex);
                if (player.isPlaying()){
                    player.stop();
                }
                kahooFragmentBinding.idExoPlayerVIew.setVisibility(View.GONE);
                kahooFragmentBinding.mediaImage.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(imageMediaList.get(currentMediaIndex))
                        .placeholder(R.color.dark_primary_500)
                        .error(R.color.dark_primary_500)
                        .into(kahooFragmentBinding.mediaImage);
            }
        }else {
            currentMediaIndex=-1;
        }
    }

    /*

    public void openMediaFile(int position,KahooPostModel kahooPostModel){
        kahooFragmentBinding.playerView.setVisibility(View.VISIBLE);
        if (kahooPostModel.getKaho_media_list().size()>0&&
                isVideo(kahooPostModel.getKaho_media_list().get(0))){
            if (!videoUrlList.contains(kahooPostModel.getKaho_media_list().get(0))){
                videoUrlList.add(kahooPostModel.getKaho_media_list().get(0));
            }
        }
        *//*for (int i=0;i<videoUrlList.size();i++){
            if (!isVideoUrlExistInPlaylist(videoUrlList.get(i))){
                MediaSource mediaSource = new ExtractorMediaSource(
                        Uri.parse(videoUrlList.get(i)),
                        new DefaultDataSourceFactory(getContext(), "MyMedia"),
                        new DefaultExtractorsFactory(),
                        null,
                        null);
                concatenatingMediaSource.addMediaSource(mediaSource);
            }
        }*//*
        int mediaIndex=0;
        *//*for (int i=0;i<concatenatingMediaSource.getSize();i++){
            if (concatenatingMediaSource.getMediaSource(i).getMediaItem().mediaId.toString()
                    .trim().equalsIgnoreCase(kahooPostModel.getKaho_media_list().get(0))){
                mediaIndex=i;
            }
        }*//*
        String media=videoUrlList.get(0);
        MediaSource mediaSource = new ExtractorMediaSource(
                Uri.parse(media),
                new DefaultDataSourceFactory(getContext(), "MyMedia"),
                new DefaultExtractorsFactory(),
                null,
                null);
        Log.e("kldfsjsdfskd","this is media index "+mediaIndex);
        player.prepare(mediaSource);
        player.seekTo(mediaIndex, C.TIME_UNSET);
        player.setPlayWhenReady(true);
    }

    */

    private boolean isVideoUrlExistInPlaylist(String s) {
        boolean isExist=false;
        for (int i=0;i<concatenatingMediaSource.getSize();i++){
            Log.e("kfjdsffff","thi sis media source "+concatenatingMediaSource.getMediaSource(i).getMediaItem().mediaId);
            if (concatenatingMediaSource.getMediaSource(i).getMediaItem().mediaId.toString().trim().equals(s.trim())){
                isExist=true;
                break;
            }
        }
        return isExist;
    }

    private ArrayList<String> videoUrlList=new ArrayList<>();
    ConcatenatingMediaSource concatenatingMediaSource;
    SimpleExoPlayer player;
    private void initializePlayer() {
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Create the player using ExoPlayerFactory
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

        concatenatingMediaSource = new ConcatenatingMediaSource();

        //Ensure to populate the allFiles array.

        player.prepare(concatenatingMediaSource);

        kahooFragmentBinding.idExoPlayerVIew.setPlayer(player);

    }

    private void setupUserInfo() {

        kahooFragmentBinding.userName.setText(sessionPrefManager.getUserName());
        FirebaseStorage storage= FirebaseStorage.getInstance();
        StorageReference storageRef;
        storageRef= storage.getReference().child("profileImage").child(sessionPrefManager.getUserID()+"profile_image.jpg");
        Glide.with(getActivity())
                .load(storageRef)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(kahooFragmentBinding.userImg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (kahooFragmentBinding.kahooListRv!=null){
            kahooFragmentBinding.kahooListRv.releasePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("kjdhsfksfs","this is onpause");
        if (kahooFragmentBinding.kahooListRv!=null){
            kahooFragmentBinding.kahooListRv.pauseVideo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("kjdhsfksfs","this is onresume");
        if (kahooFragmentBinding.kahooListRv!=null){
            kahooFragmentBinding.kahooListRv.resumeVideo();
        }
    }



    public boolean isVideo(String media){
        boolean isVideo=false;
        if (media.contains(".mp4")||
                media.contains("kahooappSerQ38videos")){
            isVideo=true;
        }
        return isVideo;
    }

    public void kahoMoreMediaClicked(KahooPostModel kahooPostModel){

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelKahoo=new ViewModelProvider(ViewKahooFrontFragment.this).get(KahooViewModel.class);
        Log.e("sdkjfsfsdf", "initialing view model on activity created viewkahofrontfrag");
        initializePlayer();
        getKahooList();
        initRecyclerViewOnScrollListener();
    }

    private boolean isScrolling;
    private List<KahooPostModel> kahooList=new ArrayList<>();
    private void initRecyclerViewOnScrollListener() {
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    int index=linearLayoutManager.findFirstCompletelyVisibleItemPosition();

                    Log.e("dsfjsksfs","calling inside onscorlled state change "+index);
                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
                    /*if (!recyclerView.canScrollHorizontally(1)) {
                        addNewKahooBinding.mediaFileSelected.playVideoFromIndex(index);
                    } else {
                        addNewKahooBinding.mediaFileSelected.playVideoFromIndex(index);
                    }*/
                    if (!recyclerView.canScrollVertically(1)) {
                        kahooFragmentBinding.kahooListRv.playVideo(true,kahooListAdapter.getItemList().size()-1,kahooListAdapter);
                    } else {
                        kahooFragmentBinding.kahooListRv.playVideo(false,0,kahooListAdapter);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                if (layoutManager != null) {
                    int firstVisibleProductPosition = layoutManager.findFirstVisibleItemPosition();
                    int visibleProductCount = layoutManager.getChildCount();
                    int totalProductCount = layoutManager.getItemCount();

                    if (isScrolling && (firstVisibleProductPosition + visibleProductCount == totalProductCount)) {
                        isScrolling = false;
                        getKahooList();
                    }
                }
            }
        };
        kahooFragmentBinding.kahooListRv.addOnScrollListener(onScrollListener);
    }

    private void addProduct(KahooPostModel addedProduct) {
        boolean isExist=false;
        for (int i=0;i<kahooList.size();i++){
            if (kahooList.get(i).getKahooPostId().equalsIgnoreCase(addedProduct.getKahooPostId())){
                isExist=true;
                break;
            }
        }
        if (!isExist){
            kahooList.add(addedProduct);
        }

        if (addedProduct.getKaho_media_list().size()>0&&isVideo(addedProduct.getKaho_media_list().get(0))){
            if (!videoUrlList.contains(addedProduct.getKaho_media_list().get(0))){
                videoUrlList.add(addedProduct.getKaho_media_list().get(0));
            }
        }
    }



    private void modifyProduct(KahooPostModel modifiedProduct) {
        int i=0;
        for (i = 0; i < kahooList.size(); i=i) {
            KahooPostModel currentProduct = kahooList.get(i);
            if (currentProduct.getKahooPostId().equals(modifiedProduct.getKahooPostId())) {
                kahooList.remove(currentProduct);
                kahooList.add(i, modifiedProduct);
                break;
            }
            i++;
        }
        kahooListAdapter.notifyItemChanged(i,modifiedProduct);
        if (modifiedProduct.getKaho_media_list().size()>0&&isVideo(modifiedProduct.getKaho_media_list().get(0))){
            if (!videoUrlList.contains(modifiedProduct.getKaho_media_list().get(0))){
                videoUrlList.add(modifiedProduct.getKaho_media_list().get(0));
            }
        }

    }

    private void removeProduct(KahooPostModel removedProduct) {
        int i = 0;
        for (i = 0; i < kahooList.size(); i=i) {
            KahooPostModel currentProduct = kahooList.get(i);
            if (currentProduct.getKahooPostId().equals(removedProduct.getKahooPostId())){
                kahooList.remove(currentProduct);
                break;
            }
             i++;
        }
        kahooListAdapter.notifyItemRemoved(i);
        kahooListAdapter.notifyItemRangeChanged(i,kahooList.size());
    }

    private void getKahooList() {
        Log.e("khfdsfsfd","calling getkahoo list view font frag");
        KahooListLiveData productListLiveData = viewModelKahoo.getKahooFeedListLiveData(currentHashTagSelected,isReload);
        isReload=false;
        if (productListLiveData != null) {
            productListLiveData.observe(getViewLifecycleOwner(), operation -> {
                switch (operation.kahooType) {
                    case R.string.added:
                        KahooPostModel addedProduct = operation.kahooPostModel;
                        Log.e("dskfksfsdf","item added "+addedProduct.getKahooPostId());
                        addProduct(addedProduct);
                        break;

                    case R.string.modified:

                        KahooPostModel modifiedProduct = operation.kahooPostModel;
                        Log.e("dskfksfsdf","item modified "+modifiedProduct.getKahooPostId());
                        modifyProduct(modifiedProduct);
                        break;

                    case R.string.removed:
                        KahooPostModel removedProduct = operation.kahooPostModel;
                        removeProduct(removedProduct);
                }
                kahooListAdapter.notifyDataSetChanged();

                if (kahooFragmentBinding.swipeToRefresh.isRefreshing()){
                    kahooFragmentBinding.swipeToRefresh.setRefreshing(false);
                }

                if (kahooFragmentBinding.skimmerView.shimmerViewContainer.isShimmerVisible()){
                    kahooFragmentBinding.skimmerView.shimmerViewContainer.stopShimmer();
                    kahooFragmentBinding.skimmerView.shimmerViewContainer.setVisibility(View.GONE);
                }
            });
        }
    }


    private void clickListeners() {
        kahooFragmentBinding.menuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        kahooFragmentBinding.addNewKaho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_kahooMainFragment_to_addnewkahoofragment);
            }
        });
    }

    public void likeKahooClicked(KahooPostModel dataKahoPost, int position) {
        Log.e("djfskfssdf","likeKahooClicked position "+position);
        if (!dataKahoPost.getLikedUserIdsList().contains(sessionPrefManager.getUserID())){
            updateLikeCount(dataKahoPost,position,true);
        }else {
            updateLikeCount(dataKahoPost,position,false);
        }
    }

    private void updateLikeCount(KahooPostModel kahooPostModel,int position,boolean isLike) {
        HashMap<String,Object> kahoData=new HashMap<>();
        Log.e("djfskfssdf","updateLikeCount islike "+isLike+" position "+position);
        if (isLike){
            if (!kahooPostModel.getLikedUserIdsList().contains(sessionPrefManager.getUserID())){
                kahoData.put("likedUserIdsList", FieldValue.arrayUnion(sessionPrefManager.getUserID()));
                kahoData.put("kaho_like_count", FieldValue.increment(1));
                mDatabase.collection(KAHOO_MAIN_COLLECTION)
                        .document(kahooPostModel.getKahooPostId())
                        .update(kahoData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.e("djfskfssdf","task success ");
                                    if (!kahooList.get(position).getLikedUserIdsList().contains(sessionPrefManager.getUserID())){
                                        kahooList.get(position).getLikedUserIdsList().add(sessionPrefManager.getUserID());
                                        kahooListAdapter.notifyItemChanged(position,kahooList.get(position));
                                    }
                                    //kahooListAdapter.updateKahoAfterLike(sessionPrefManager.getUserID(),position);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("djfskfssdf","this is error "+e.getMessage());
                    }
                });
            }
        }else {
            if (kahooPostModel.getLikedUserIdsList().contains(sessionPrefManager.getUserID())){
                kahoData.put("likedUserIdsList", FieldValue.arrayRemove(sessionPrefManager.getUserID()));
                kahoData.put("kaho_like_count", FieldValue.increment(-1));
                mDatabase.collection(KAHOO_MAIN_COLLECTION)
                        .document(kahooPostModel.getKahooPostId())
                        .update(kahoData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.e("djfskfssdf","task success ");
                                    if (kahooList.get(position).getLikedUserIdsList().contains(sessionPrefManager.getUserID())){
                                        kahooList.get(position).getLikedUserIdsList().remove(sessionPrefManager.getUserID());
                                        kahooListAdapter.notifyItemChanged(position,kahooList.get(position));
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("djfskfssdf","this is error "+e.getMessage());
                    }
                });
            }
        }



    }

    public void dobaraKahooClicked(KahooPostModel dataKahoPost, int position) {
        Bundle bundle =new Bundle();
        bundle.putSerializable("kahoo_post",dataKahoPost);
        bundle.putBoolean("isdobarakaho",true);
        navController.navigate(R.id.action_kahooMainFragment_to_dobaraKahooAurReplyFrag,bundle);
    }

    public void replyKahooClicked(KahooPostModel dataKahoPost, int position) {
        Bundle bundle =new Bundle();
        bundle.putSerializable("kahoo_post",dataKahoPost);
        bundle.putBoolean("isdobarakaho",false);
        navController.navigate(R.id.action_kahooMainFragment_to_dobaraKahooAurReplyFrag,bundle);
    }

    public void editKahoo(KahooPostModel kahooPostModel, int position) {
        Bundle bundle =new Bundle();
        bundle.putSerializable("kahoo_post",kahooPostModel);
        navController.navigate(R.id.action_kahooMainFragment_to_editKahooFragment,bundle);
    }

    public void deleteKahoo(KahooPostModel kahooPostModel, int position) {
        if (kahooPostModel.getKaho_added_user_id().equalsIgnoreCase(sessionPrefManager.getUserID())){
            confirmDeleteKahoo(kahooPostModel,position);
        }
    }

    private void confirmDeleteKahoo(KahooPostModel kahooPostModel, int position) {
        Activity context=getActivity();
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View mView = context.getLayoutInflater().inflate(R.layout.on_delete_kahoo_confirm, null);

            TextView message;
            TextView cancel;
            TextView delete;

            message = (TextView)mView.findViewById( R.id.message );
            cancel = (TextView)mView.findViewById( R.id.cancle );
            delete = (TextView)mView.findViewById( R.id.delete );

            builder.setView(mView);
            final AlertDialog alertDialog = builder.create();

            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    mDatabase.collection(KAHOO_MAIN_COLLECTION)
                            .document(kahooPostModel.getKahooPostId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(context,context.getResources().getString(R.string.KAHOO_SUCCESS_FULLY_DELETED),Toast.LENGTH_SHORT).show();
                                kahooList.remove(position);
                                kahooListAdapter.notifyItemRemoved(position);
                                kahooListAdapter.notifyItemRangeChanged(position,kahooList.size());
                            }
                        }
                    });
                }
            });

            alertDialog.show();
        }catch (Exception e){
            Log.e("kjsdhfsdkf","this is error "+e.getMessage());
        }

    }

    public void shareKahooClicked(KahooPostModel kahooPostModel, int position, View parentView) {
        Bitmap image=loadBitmapFromView(parentView);
        if (image != null) {
            File saveFile = ScreenshotUtils.getMainDirectoryName(getActivity());
            File file = ScreenshotUtils.store(image, "screenshot" + ".jpg", saveFile);
            shareScreenshot(file,kahooPostModel);
        } else
            shareScreenshot(null,kahooPostModel);
    }

    /*  Share Screenshot  */
    private void shareScreenshot(File file,KahooPostModel kahooPostModel) {
        String appShareableLink;
        try {
            appShareableLink = "" + Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
        } catch (android.content.ActivityNotFoundException anfe) {
            appShareableLink = "" + Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Kahoo Bharat App - The Indian Tweet App");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Download Kahoo Bharat Indian Microblogging app from play store." +
                "\n\nJoin the Kahoo Bharat App and start contributing the indian voice all over the world\n" +
                "Hit the below link to download the app.\n"+appShareableLink);
        if (file!=null){
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getActivity(), getString(R.string.camera_file_provider_name),
                    file));
        }else {
            intent.setType("text/plain");
        }
        startActivity(Intent.createChooser(intent, "Share With"));

        HashMap<String,Object>kahooData=new HashMap<>();
        kahooData.put("kahoo_share_count",FieldValue.increment(1));
        mDatabase.collection(KAHOO_MAIN_COLLECTION).document(kahooPostModel.getKahooPostId())
                .update(kahooData);
    }

    public void showDetailKahoo(KahooPostModel dataKahoPost) {
        Bundle bundle =new Bundle();
        bundle.putSerializable("kahoo_post",dataKahoPost);
        navController.navigate(R.id.action_kahooMainFragment_to_singleDetailKahoo,bundle);
    }

    public void showUserDetail(KahooPostModel dataKahoPost) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_other_profile",true);
        bundle.putString("other_profile_id",dataKahoPost.getKaho_added_user_id());
        navController.navigate(R.id.action_kahooMainFragment_to_viewProfileFragment,bundle);
    }

    public void showTagKahooDetail(String tag,boolean isHashTag) {
        if (isHashTag){
            Bundle bundle = new Bundle();
            bundle.putString("clicked_hash_tag",tag);
            navController.navigate(R.id.action_kahooMainFragment_to_singleHashTagKahooView,bundle);
        }else {
            mDatabase.collection(FirebaseConstant.USER_UNIQUE_ID_COLLECTION).document(tag)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot=task.getResult();
                        if (documentSnapshot.exists()){
                            Bundle bundle =new Bundle();
                            bundle.putBoolean("is_other_profile",true);
                            bundle.putString("other_profile_id",documentSnapshot.getString("user_id"));
                            navController.navigate(R.id.action_kahooMainFragment_to_viewProfileFragment,bundle);
                        }else {
                            Toast.makeText(getContext(),getActivity().getResources().getString(R.string.USER_ID_NOT_ACTIVE),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void showReportKahooDialog(KahooPostModel kahooPostModel, int position) {
        Activity context=getActivity();
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View mView = context.getLayoutInflater().inflate(R.layout.show_report_kahoo_dialog, null);

            TextView message;
            TextView cancel;
            TextView reportKahoo;
            EditText kahooReportContent;

            message = (TextView)mView.findViewById( R.id.message );
            cancel = (TextView)mView.findViewById( R.id.cancle );
            reportKahoo = (TextView)mView.findViewById( R.id.report );
            kahooReportContent=mView.findViewById(R.id.kahoo_report_text);

            builder.setView(mView);
            final AlertDialog alertDialog = builder.create();

            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            reportKahoo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserModel userDataModel=com.kaho.app.Data.Models.SingltonUserDataModel.getInstance().getUserData();
                    Log.e("ksdjfhksfd","thi sis is user name "+userDataModel.getUser_name());
                    alertDialog.dismiss();
                    HashMap<String,Object> data=new HashMap<>();
                    data.put("kahoo_report_count",FieldValue.increment(1));
                    mDatabase.collection(KAHOO_MAIN_COLLECTION)
                            .document(kahooPostModel.getKahooPostId())
                            .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String reportId= PublicMethods.getRandomString(16).toUpperCase();
                            HashMap<String,Object> reportData=new HashMap<>();
                            reportData.put("reported_kahoo_id",kahooPostModel.getKahooPostId());
                            reportData.put("reported_kahoo_added_by_id",kahooPostModel.getKaho_added_user_id());
                            reportData.put("kahoo_reported_by",sessionPrefManager.getUserID());
                            reportData.put("kahoo_data",kahooPostModel);
                            reportData.put("reported_user_data",userDataModel);
                            mDatabase.collection(KAHOO_REPORTS_COLLECTION)
                                    .document(reportId)
                                    .set(reportData, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(context,context.getResources().getString(R.string.REPORT_SUBMITTED_SUCCESSFULLY),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
            });

            alertDialog.show();
        }catch (Exception e){
            Log.e("kjsdhfsdkf","this is error "+e.getMessage());
        }

    }

    public void reportKahoo(KahooPostModel kahooPostModel, int position) {
        showReportKahooDialog(kahooPostModel,position);
    }


}
