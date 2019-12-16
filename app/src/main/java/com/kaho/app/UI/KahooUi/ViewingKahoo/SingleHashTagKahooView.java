package com.kaho.app.UI.KahooUi.ViewingKahoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.kaho.app.Data.Models.UserModel;
import com.kaho.app.LiveData.KahooListLiveData;
import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;
import com.kaho.app.Tools.FirebaseConstant;
import com.kaho.app.Tools.PublicMethods;
import com.kaho.app.Tools.Utills.ScreenshotUtils;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.ReplyKahooListFrag;
import com.kaho.app.ViewModels.KahooViewModel;
import com.kaho.app.databinding.FragmentReplyKahooListBinding;
import com.kaho.app.databinding.FragmentSingleHashTagKahooViewBinding;
import com.kaho.app.databinding.ViewKahooFragmentBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kaho.app.Tools.Constants.EMPTY_ANIM_SHOW_TIME;
import static com.kaho.app.Tools.FirebaseConstant.KAHOO_MAIN_COLLECTION;
import static com.kaho.app.Tools.FirebaseConstant.KAHOO_REPORTS_COLLECTION;
import static com.kaho.app.Tools.PublicMethods.loadBitmapFromView;

public class SingleHashTagKahooView extends Fragment {

    FragmentSingleHashTagKahooViewBinding hashTagKahooViewBinding;

    private SessionPrefManager sessionPrefManager;
    private NavController navController;
    private FirebaseFirestore mDatabase;
    private KahooListAdapter kahooListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private KahooViewModel viewModelKahoo;
    private List<KahooPostModel> kahooList=new ArrayList<>();
    String hashTagClicked;
    private boolean isReload=false;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        hashTagKahooViewBinding = FragmentSingleHashTagKahooViewBinding.inflate(inflater,container,false);
        
        return hashTagKahooViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        mDatabase= FirebaseFirestore.getInstance();
        sessionPrefManager=new SessionPrefManager(getActivity());
        kahooListAdapter = new KahooListAdapter(SingleHashTagKahooView.this,false,kahooList);
        linearLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        hashTagKahooViewBinding.kahooListRv.setLayoutManager(linearLayoutManager);
        hashTagKahooViewBinding.kahooListRv.setAdapter(kahooListAdapter);

        clickListener();

        hashTagKahooViewBinding.skimmerView.shimmerViewContainer.startShimmer();

        Bundle bundle=getArguments();
        hashTagClicked=bundle.getString("clicked_hash_tag");

        hashTagKahooViewBinding.tagTitle.setText("#"+hashTagClicked);
        if (kahooList.size()>0){
            hashTagKahooViewBinding.skimmerView.shimmerViewContainer.setVisibility(View.GONE);
            hashTagKahooViewBinding.skimmerView.shimmerViewContainer.stopShimmer();
        }else {
            hashTagKahooViewBinding.skimmerView.shimmerViewContainer.startShimmer();
            hashTagKahooViewBinding.skimmerView.shimmerViewContainer.setVisibility(View.VISIBLE);
        }

        hashTagKahooViewBinding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

    }

    private void clickListener() {
        hashTagKahooViewBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void refreshList(){
        hashTagKahooViewBinding.emptyAnimation.setVisibility(View.GONE);
        isReload=true;
        kahooList.clear();
        kahooListAdapter.notifyDataSetChanged();
        hashTagKahooViewBinding.skimmerView.shimmerViewContainer.setVisibility(View.VISIBLE);
        hashTagKahooViewBinding.skimmerView.shimmerViewContainer.startShimmer();
        Log.e("dskjfhkjdfsf","skimmer is started");
        getKahooList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (hashTagKahooViewBinding.kahooListRv!=null){
            hashTagKahooViewBinding.kahooListRv.releasePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (hashTagKahooViewBinding.kahooListRv!=null){
            hashTagKahooViewBinding.kahooListRv.pauseVideo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hashTagKahooViewBinding.kahooListRv!=null){
            hashTagKahooViewBinding.kahooListRv.resumeVideo();
        }
    }


    private String currentKahooUrl="";
    private String currentVideoId="";

    public void openYoutubeVideo(int position,KahooPostModel kahooPostModel,String videoUrl){
        Log.e("ksdfhssdf","this is playing youtube video "+videoUrl);

        currentVideoId= PublicMethods.getYoutubeVideoId(videoUrl);
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
            hashTagKahooViewBinding.mediaView.setVisibility(View.VISIBLE);
            hashTagKahooViewBinding.playerView.setVisibility(View.VISIBLE);
            imageMediaList=kahooPostModel.getKaho_media_list();
            currentMediaIndex=-1;
            openMedia(true);
        }

        openMediaAnimation();

        hashTagKahooViewBinding.leftMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashTagKahooViewBinding.mediaImage.setVisibility(View.GONE);
                hashTagKahooViewBinding.idExoPlayerVIew.setVisibility(View.GONE);
                openMedia(false);
            }
        });
        hashTagKahooViewBinding.rightMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashTagKahooViewBinding.mediaImage.setVisibility(View.GONE);
                hashTagKahooViewBinding.idExoPlayerVIew.setVisibility(View.GONE);
                openMedia(true);
            }
        });
        hashTagKahooViewBinding.closeMediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMedia();
            }
        });
        hashTagKahooViewBinding.playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMedia();
            }
        });
    }

    private void openMediaAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_anim);
        animation.setStartOffset(0);
        hashTagKahooViewBinding.centerView.startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_anim);
        animation.setStartOffset(0);
        hashTagKahooViewBinding.closeMediaView.startAnimation(animation2);
    }

    private void closeMedia() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down_anim);
        animation.setStartOffset(0);
        hashTagKahooViewBinding.centerView.startAnimation(animation);
        Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_anim);
        animation.setStartOffset(0);
        hashTagKahooViewBinding.closeMediaView.startAnimation(animation2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hashTagKahooViewBinding.playerView.setVisibility(View.GONE);
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

        hashTagKahooViewBinding.mediaIndex.setText(""+(1+currentMediaIndex)+"/"+(imageMediaList.size()));
        Log.e("kjdfhksd","calling open media isRight "+isRight+" current media index "+currentMediaIndex+" media list size "+imageMediaList.size());

        if (imageMediaList.size()>currentMediaIndex&&currentMediaIndex>=0){
            hashTagKahooViewBinding.playerView.setVisibility(View.VISIBLE);
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

                hashTagKahooViewBinding.mediaImage.setVisibility(View.GONE);
                hashTagKahooViewBinding.idExoPlayerVIew.setVisibility(View.VISIBLE);
            } else {
                Log.e("kdhfksfs","is image playing currentMediaIndex "+currentMediaIndex);
                if (player.isPlaying()){
                    player.stop();
                }
                hashTagKahooViewBinding.idExoPlayerVIew.setVisibility(View.GONE);
                hashTagKahooViewBinding.mediaImage.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(imageMediaList.get(currentMediaIndex))
                        .placeholder(R.color.dark_primary_500)
                        .error(R.color.dark_primary_500)
                        .into(hashTagKahooViewBinding.mediaImage);
            }
        }else {
            currentMediaIndex=-1;
        }
    }


    public void kahoMediaClicked(int index,KahooPostModel kahooPostModel){
        Log.e("kdsjfk","kaho clicke "+index);
        if (kahooPostModel.getKaho_media_list().size()>0&&isVideo(kahooPostModel.getKaho_media_list().get(0))){
            hashTagKahooViewBinding.kahooListRv.playVideoFromIndex(index,kahooPostModel.getKaho_media_list().get(0));
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

        hashTagKahooViewBinding.idExoPlayerVIew.setPlayer(player);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelKahoo=new ViewModelProvider(SingleHashTagKahooView.this).get(KahooViewModel.class);
        Log.e("sdkjfsfsdf", "initialing view model on activity created");
        initializePlayer();
        getKahooList();
        initRecyclerViewOnScrollListener();
    }
    private void updateOnNoData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (kahooList.size()==0){
                    hashTagKahooViewBinding.emptyAnimation.setVisibility(View.VISIBLE);
                    if (hashTagKahooViewBinding.skimmerView.shimmerViewContainer.isShimmerVisible()){
                        hashTagKahooViewBinding.skimmerView.shimmerViewContainer.stopShimmer();
                        hashTagKahooViewBinding.skimmerView.shimmerViewContainer.setVisibility(View.GONE);
                    }
                    if (hashTagKahooViewBinding.swipeToRefresh.isRefreshing()){
                        hashTagKahooViewBinding.swipeToRefresh.setRefreshing(false);
                    }
                }
            }
        },EMPTY_ANIM_SHOW_TIME);
    }


    private void getKahooList() {
        updateOnNoData();
        KahooListLiveData productListLiveData = viewModelKahoo.getHashTagThreadListLiveData(hashTagClicked,isReload);
        isReload=false;
        if (productListLiveData != null) {
            productListLiveData.observe(getViewLifecycleOwner(), operation -> {
                switch (operation.kahooType) {
                    case R.string.added:
                        KahooPostModel addedProduct = operation.kahooPostModel;
                        addProduct(addedProduct);
                        break;

                    case R.string.modified:
                        KahooPostModel modifiedProduct = operation.kahooPostModel;
                        modifyProduct(modifiedProduct);
                        break;

                    case R.string.removed:
                        KahooPostModel removedProduct = operation.kahooPostModel;
                        removeProduct(removedProduct);
                }
                kahooListAdapter.notifyDataSetChanged();


                if (hashTagKahooViewBinding.swipeToRefresh.isRefreshing()){
                    hashTagKahooViewBinding.swipeToRefresh.setRefreshing(false);
                }

                Log.e("dskjfhkjdfsf","skimmer is gone");
                if (hashTagKahooViewBinding.skimmerView.shimmerViewContainer.isShimmerVisible()){
                    hashTagKahooViewBinding.skimmerView.shimmerViewContainer.stopShimmer();
                    hashTagKahooViewBinding.skimmerView.shimmerViewContainer.setVisibility(View.GONE);
                }
            });
        }
    }

    private boolean isScrolling;
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
                        hashTagKahooViewBinding.kahooListRv.playVideo(true,kahooListAdapter.getItemList().size()-1,kahooListAdapter);
                    } else {
                        hashTagKahooViewBinding.kahooListRv.playVideo(false,0,kahooListAdapter);
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
        hashTagKahooViewBinding.kahooListRv.addOnScrollListener(onScrollListener);
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
        if (kahooList.size()>0){
            hashTagKahooViewBinding.emptyAnimation.setVisibility(View.GONE);
        }
    }


    private void modifyProduct(KahooPostModel modifiedProduct) {
        for (int i = 0; i < kahooList.size(); i++) {
            KahooPostModel currentProduct = kahooList.get(i);
            if (currentProduct.getKahooPostId().equals(modifiedProduct.getKahooPostId())) {
                kahooList.remove(currentProduct);
                kahooList.add(i, modifiedProduct);
            }
        }
        if (modifiedProduct.getKaho_media_list().size()>0&&isVideo(modifiedProduct.getKaho_media_list().get(0))){
            if (!videoUrlList.contains(modifiedProduct.getKaho_media_list().get(0))){
                videoUrlList.add(modifiedProduct.getKaho_media_list().get(0));
            }
        }
    }

    private void removeProduct(KahooPostModel removedProduct) {
        for (int i = 0; i < kahooList.size(); i++) {
            KahooPostModel currentProduct = kahooList.get(i);
            if (currentProduct.getKahooPostId().equals(removedProduct.getKahooPostId())) {
                kahooList.remove(currentProduct);
            }
        }
    }

    public void likeKahooClicked(KahooPostModel dataKahoPost, int position) {
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
        navController.navigate(R.id.action_singleHashTagKahooView_to_dobaraKahooAurReplyFrag,bundle);
    }

    public void replyKahooClicked(KahooPostModel dataKahoPost, int position) {
        Bundle bundle =new Bundle();
        bundle.putSerializable("kahoo_post",dataKahoPost);
        bundle.putBoolean("isdobarakaho",false);
        navController.navigate(R.id.action_singleHashTagKahooView_to_dobaraKahooAurReplyFrag,bundle);
    }

    public void editKahoo(KahooPostModel kahooPostModel, int position) {
        Bundle bundle =new Bundle();
        bundle.putSerializable("kahoo_post",kahooPostModel);
        navController.navigate(R.id.action_singleHashTagKahooView_to_editUserProfileFragment,bundle);
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
        navController.navigate(R.id.action_singleHashTagKahooView_to_singleDetailKahoo,bundle);
    }

    public void showUserDetail(KahooPostModel dataKahoPost) {
        Bundle bundle =new Bundle();
        bundle.putBoolean("is_other_profile",true);
        bundle.putString("other_profile_id",dataKahoPost.getKaho_added_user_id());
        navController.navigate(R.id.action_singleHashTagKahooView_to_viewProfileFragment,bundle);
    }

    public void showTagKahooDetail(String tag,boolean isHashTag) {
        if (isHashTag){
            if (!tag.equalsIgnoreCase(hashTagClicked)){
                Bundle bundle =new Bundle();
                bundle.putString("clicked_hash_tag",tag);
                navController.navigate(R.id.action_singleHashTagKahooView_self2,bundle);
            }
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
                            navController.navigate(R.id.action_singleHashTagKahooView_to_viewProfileFragment,bundle);
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