package com.kaho.app.UI.CustomViews;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.kaho.app.Adapters.KahooListAdapter;
import com.kaho.app.Data.Models.KahooPostModel;
import com.kaho.app.R;
import com.kaho.app.ViewHolders.KahooViewHolders.KahooDobaraViewHolder;
import com.kaho.app.ViewHolders.KahooViewHolders.KahooLinkDobaraVH;
import com.kaho.app.ViewHolders.KahooViewHolders.KahooLinkTextVH;
import com.kaho.app.ViewHolders.KahooViewHolders.KahooViewHolder;
import com.kaho.app.ViewHolders.LocalMediaViewHolder;

import java.util.Objects;

public class ExoPlayerRecyclerView extends RecyclerView {

  private static final String TAG = "ExoPlayerRecyclerView";
  private static final String AppName = "Android ExoPlayer";
  /**
   * PlayerViewHolder UI component
   * Watch PlayerViewHolder class
   */
  private ImageView mediaCoverImage, volumeControl,videoControl;
  private ProgressBar progressBar;
  private View viewHolderParent;
  private FrameLayout mediaContainer;
  private PlayerView videoSurfaceView;
  private SimpleExoPlayer videoPlayer;
  /**
   * variable declaration
   */
  private int videoSurfaceDefaultHeight = 0;
  private int screenDefaultHeight = 0;
  private Context context;
  private int playPosition = -1;
  private boolean isVideoViewAdded;
  // controlling volume state
  private VolumeState volumeState;
  private PlayerState playerState;
  private OnClickListener toggleVolumeClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      toggleVolume();
    }
  };
  private OnClickListener videoViewClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      toggleVideoPlay();
    }
  };

  private void toggleVideoPlay() {
    if (videoPlayer!=null){
      if (videoPlayer.isPlaying()){
        videoPlayer.pause();
      }else {
        videoPlayer.play();
      }
    }
  }

  public ExoPlayerRecyclerView(@NonNull Context context) {
    super(context);
    init(context);
  }

  public ExoPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  private void init(Context context) {
    this.context = context.getApplicationContext();
    Display display = ((WindowManager) Objects.requireNonNull(
            getContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
    Point point = new Point();
    display.getSize(point);

    videoSurfaceDefaultHeight = point.x;
    screenDefaultHeight = point.y;

    videoSurfaceView = new PlayerView(this.context);
    videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    AdaptiveTrackSelection.Factory videoTrackSelectionFactory =
            new AdaptiveTrackSelection.Factory();
    TrackSelector trackSelector =
            new DefaultTrackSelector(videoTrackSelectionFactory);

    //Create the player using ExoPlayerFactory
    videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    // Disable Player Control
    videoSurfaceView.setUseController(false);
    // Bind the player to the view.
    videoSurfaceView.setPlayer(videoPlayer);
    // Turn on Volume
    setVolumeControl(VolumeState.ON);



    addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
      @Override
      public void onChildViewAttachedToWindow(@NonNull View view) {

      }

      @Override
      public void onChildViewDetachedFromWindow(@NonNull View view) {
        if (viewHolderParent != null && viewHolderParent.equals(view)) {
          resetVideoView();
        }
      }
    });

    videoPlayer.addListener(new Player.EventListener() {
      @Override
      public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

      }

      @Override
      public void onTracksChanged(TrackGroupArray trackGroups,
                                  TrackSelectionArray trackSelections) {

      }

      @Override
      public void onLoadingChanged(boolean isLoading) {

      }

      @Override
      public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {

          case Player.STATE_BUFFERING:
            Log.e(TAG, "onPlayerStateChanged: Buffering video.");
            if (progressBar != null) {
              progressBar.setVisibility(VISIBLE);
            }

            break;
          case Player.STATE_ENDED:
            Log.d(TAG, "onPlayerStateChanged: Video ended.");
            videoPlayer.seekTo(0);
            break;
          case Player.STATE_IDLE:

            break;
          case Player.STATE_READY:
            Log.e(TAG, "onPlayerStateChanged: Ready to play.");
            if (progressBar != null) {
              progressBar.setVisibility(GONE);
            }
            if (!isVideoViewAdded) {
              addVideoView();
            }
            break;
          default:
            break;
        }
      }

      @Override
      public void onRepeatModeChanged(int repeatMode) {

      }

      @Override
      public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

      }

      @Override
      public void onPlayerError(ExoPlaybackException error){

      }

      @Override
      public void onPositionDiscontinuity(int reason){

      }

      @Override
      public void onPlaybackParametersChanged(PlaybackParameters playbackParameters){

      }

      @Override
      public void onSeekProcessed(){

      }
    });
  }

  public void playVideoFromIndex(int index,String mediaFile){
    Log.d("dsfjsksfs", "holder is index " + index);
    View child = getChildAt(index);
    if (child == null) {
      Log.d("dsfjsksfs", "child is null " + child);
      return;
    }

    if (videoSurfaceView == null) {
      Log.d("dsfjsksfs", "video surface view is null");
      return;
    }
    Log.d("dsfjsksfs", "after video surface view: target position: " + index+" mediaFile "+mediaFile);

    // remove any old surface views from previously playing videos
    videoSurfaceView.setVisibility(INVISIBLE);
    resetVideoView();
    //removeVideoView(videoSurfaceView);

    LocalMediaViewHolder holder =null;
    KahooViewHolder kahoHolder=null;
    KahooDobaraViewHolder kahoDobaraHolder=null;

    if (getChildViewHolder(child) instanceof LocalMediaViewHolder){
      holder= (LocalMediaViewHolder)  getChildViewHolder(child);
    }else if (getChildViewHolder(child) instanceof KahooDobaraViewHolder){
      kahoDobaraHolder=(KahooDobaraViewHolder)  getChildViewHolder(child);
    } else {
      kahoHolder= (KahooViewHolder)  getChildViewHolder(child);
    }


    if (holder == null&&kahoHolder==null&&kahoDobaraHolder==null) {
      playPosition = -1;
      Log.d("dsfjsksfs", "holder is null " + child);
      return;
    }
    if (holder!=null){
      if (holder.isVideo()){
        String mediaUrl = holder.getMediaUrl();
        if (mediaUrl.contains(".mp4")||mediaUrl.contains("video")||mediaUrl.contains("kahooappSerQ38videos")){
          holder.setVideoPlaying();
          mediaCoverImage = holder.localSingleMediaLayoutBinding.ivMediaCoverImage;
          progressBar = holder.localSingleMediaLayoutBinding.progressBar;
          volumeControl = holder.localSingleMediaLayoutBinding.ivVolumeControl;
          videoControl=holder.localSingleMediaLayoutBinding.videoControle;
          viewHolderParent = holder.itemView;
          mediaContainer = holder.localSingleMediaLayoutBinding.mediaContainer;

          videoSurfaceView.setPlayer(videoPlayer);
          viewHolderParent.setOnClickListener(videoViewClickListener);
          volumeControl.setOnClickListener(toggleVolumeClicked);
          setVolumeControl(VolumeState.OFF);
          //viewHolderParent.setOnClickListener(videoViewClickListener);
        }
        playVideoFile(mediaUrl);
      }
    }
    if (kahoDobaraHolder!=null){
      Log.e("kdfksffds","playing video 280");
      if (kahoDobaraHolder.isVideo()){
        String mediaUrl = kahoDobaraHolder.getMediaUrl();
        if (mediaUrl.contains(".mp4")||mediaUrl.contains("kahooappSerQ38videos")){
          Log.e("kdfksffds","playing video 284");
          kahoDobaraHolder.setVideoPlaying();
          mediaCoverImage = kahoDobaraHolder.kahooDobaraBinding.kahoImgHolder;
          progressBar = kahoDobaraHolder.kahooDobaraBinding.progressBar;
          volumeControl = kahoDobaraHolder.kahooDobaraBinding.ivVolumeControl;
          videoControl=kahoDobaraHolder.kahooDobaraBinding.videoControle;
          viewHolderParent = kahoDobaraHolder.itemView;
          mediaContainer = kahoDobaraHolder.kahooDobaraBinding.mediaContainer;

          videoSurfaceView.setPlayer(videoPlayer);
          viewHolderParent.setOnClickListener(videoViewClickListener);
          volumeControl.setOnClickListener(toggleVolumeClicked);
          setVolumeControl(VolumeState.OFF);
          //viewHolderParent.setOnClickListener(videoViewClickListener);
        }
        playVideoFile(mediaUrl);
        Log.e("kdfksffds","playing video 299");
      }
    }else {
      if (kahoHolder!=null){
        Log.e("kdfksffds","playing video 302 "+kahoHolder.isVideo());
        Log.e("kdfksffds","playing 305 media url "+kahoHolder.getMediaUrl());
        if (kahoHolder.isVideo()){
          Log.e("kdfksffds","playing video 306");
          String mediaUrl = kahoHolder.getMediaUrl();
          Log.e("kdfksffds","playing video 308 "+mediaUrl);
          if (mediaUrl.contains(".mp4")||mediaUrl.contains("kahooappSerQ38videos")){
            Log.e("kdfksffds","playing video 309");
            kahoHolder.setVideoPlaying();
            mediaCoverImage = kahoHolder.kahooSingleLayoutViewBinding.kahoImgHolder;
            progressBar = kahoHolder.kahooSingleLayoutViewBinding.progressBar;
            volumeControl = kahoHolder.kahooSingleLayoutViewBinding.ivVolumeControl;
            videoControl=kahoHolder.kahooSingleLayoutViewBinding.videoControle;
            viewHolderParent = kahoHolder.itemView;
            mediaContainer = kahoHolder.kahooSingleLayoutViewBinding.mediaContainer;

            videoSurfaceView.setPlayer(videoPlayer);
            viewHolderParent.setOnClickListener(videoViewClickListener);
            volumeControl.setOnClickListener(toggleVolumeClicked);
            setVolumeControl(VolumeState.OFF);
            //viewHolderParent.setOnClickListener(videoViewClickListener);
          }
          playVideoFile(mediaUrl);
          Log.e("kdfksffds","playing video 322");
        }
      }
    }

  }

  private void playVideoFile(String mediaUrl) {
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
            Util.getUserAgent(context, AppName));

    if (mediaUrl != null) {
      MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
              .createMediaSource(Uri.parse(mediaUrl));
      videoPlayer.prepare(videoSource);
      videoPlayer.setPlayWhenReady(true);
      setPlayerListener();
    }
  }

  public void playVideo(boolean isEndOfList, int index, KahooListAdapter kahooListAdapter) {

    int targetPosition;

    if (!isEndOfList) {
      int startPosition = ((LinearLayoutManager) Objects.requireNonNull(
              getLayoutManager())).findFirstVisibleItemPosition();
      int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition();

      // if there is more than 2 list-items on the screen, set the difference to be 1
      if (endPosition - startPosition > 1) {
        endPosition = startPosition + 1;
      }

      Log.e("dsfjsksfs","start position "+startPosition+" endposition "+endPosition);

      // something is wrong. return.
      if (startPosition < 0 || endPosition < 0) {
        return;
      }

      // if there is more than 1 list-item on the screen
      if (startPosition != endPosition) {
        int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
        int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

        targetPosition =
                startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
      } else {
        targetPosition = startPosition;
      }
    } else {
      targetPosition = index;
      Log.d("dsfjsksfs", "inside else before playVideo: target position: " + targetPosition+" index position "+index);
    }
    Log.d("dsfjsksfs", "before playVideo: target position: " + targetPosition+" index position "+index);

    //targetPosition=((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    Log.d("dsfjsksfs", "after playVideo: target position: " + targetPosition+" playPosition "+playPosition);

    // video is already playing so return
    if (targetPosition == playPosition) {
      return;
    }

    // set the position of the list-item that is to be played
    playPosition = targetPosition;
    if (videoSurfaceView == null) {
      Log.d("dsfjsksfs", "video surface view is null");
      return;
    }
    Log.d("dsfjsksfs", "after video surface view: target position: " + targetPosition+" playPosition "+playPosition);

    // remove any old surface views from previously playing videos
    videoSurfaceView.setVisibility(INVISIBLE);
    resetVideoView();
    //removeVideoView(videoSurfaceView);

    int currentPosition =
            targetPosition - ((LinearLayoutManager) Objects.requireNonNull(
                    getLayoutManager())).findFirstVisibleItemPosition();
    //currentPosition=((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();


    Log.d("dsfjsksfs", "after playVideo : currentPosition : " + currentPosition);

    View child = getChildAt(currentPosition);

    if (child == null) {
      Log.d("dsfjsksfs", "child is null " + child);
      return;
    }

    LocalMediaViewHolder holder = (LocalMediaViewHolder) child.getTag();
    KahooViewHolder kahoHolder=null;
    KahooDobaraViewHolder kahoDobaraHolder=null;
    KahooLinkDobaraVH linkDobaraVH=null;
    KahooLinkTextVH linkTextVH=null;

    if (getChildViewHolder(child) instanceof LocalMediaViewHolder){
      Log.e("kdfksffds","LocalMediaViewHolder playing video 456 ");
      holder= (LocalMediaViewHolder)  getChildViewHolder(child);
    }else if (getChildViewHolder(child) instanceof KahooDobaraViewHolder){
      Log.e("kdfksffds","KahooDobaraViewHolder playing video 456 ");
      kahoDobaraHolder=(KahooDobaraViewHolder)  getChildViewHolder(child);
    } else if (getChildViewHolder(child) instanceof KahooLinkDobaraVH){
      Log.e("kdfksffds","KahooLinkDobaraVH playing video 456 ");
      linkDobaraVH=(KahooLinkDobaraVH)  getChildViewHolder(child);
    }else if (getChildViewHolder(child) instanceof KahooLinkTextVH){
      Log.e("kdfksffds","KahooLinkTextVH playing video 456 ");
      linkTextVH=(KahooLinkTextVH)  getChildViewHolder(child);
    } else {
      kahoHolder= (KahooViewHolder)  getChildViewHolder(child);
    }


    if (holder == null&&kahoHolder==null&&kahoDobaraHolder==null&&linkDobaraVH==null&&linkTextVH==null) {
      playPosition = -1;
      Log.d("dsfjsksfs", "holder is null " + child);
      return;
    }
    Log.e("kdfksffds","playing video 456 ");

    if (holder!=null){
      String mediaUrl = holder.getMediaUrl();
      if (mediaUrl.contains(".mp4")){
        holder.setVideoPlaying();
        mediaCoverImage = holder.localSingleMediaLayoutBinding.ivMediaCoverImage;
        progressBar = holder.localSingleMediaLayoutBinding.progressBar;
        volumeControl = holder.localSingleMediaLayoutBinding.ivVolumeControl;
        viewHolderParent = holder.itemView;
        mediaContainer = holder.localSingleMediaLayoutBinding.mediaContainer;
        videoControl=holder.localSingleMediaLayoutBinding.videoControle;

        videoSurfaceView.setPlayer(videoPlayer);
        viewHolderParent.setOnClickListener(videoViewClickListener);
        volumeControl.setOnClickListener(toggleVolumeClicked);
        setVolumeControl(VolumeState.OFF);
        //viewHolderParent.setOnClickListener(videoViewClickListener);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, AppName));

        if (mediaUrl != null) {
          MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                  .createMediaSource(Uri.parse(mediaUrl));
          videoPlayer.prepare(videoSource);
          videoPlayer.setPlayWhenReady(true);
          setPlayerListener();
        }
      }
    }

    if (linkTextVH!=null){
      if (linkTextVH.isVideo()){
        String mediaUrl = linkTextVH.getMediaUrl();
        if (mediaUrl.contains(".mp4")||mediaUrl.contains("kahooappSerQ38videos")){
          Log.e("kdfksffds","playing video 309");
          linkTextVH.setVideoPlaying();
          mediaCoverImage = linkTextVH.kahooSingleLayoutViewBinding.kahoImgHolder;
          progressBar = linkTextVH.kahooSingleLayoutViewBinding.progressBar;
          volumeControl = linkTextVH.kahooSingleLayoutViewBinding.ivVolumeControl;
          videoControl=linkTextVH.kahooSingleLayoutViewBinding.videoControle;
          viewHolderParent = linkTextVH.itemView;
          mediaContainer = linkTextVH.kahooSingleLayoutViewBinding.mediaContainer;

          videoSurfaceView.setPlayer(videoPlayer);
          viewHolderParent.setOnClickListener(videoViewClickListener);
          volumeControl.setOnClickListener(toggleVolumeClicked);
          setVolumeControl(VolumeState.OFF);
          //viewHolderParent.setOnClickListener(videoViewClickListener);
        }
        playVideoFile(mediaUrl);
        Log.e("kdfksffds","playing video 322");
      }
    }else if (linkDobaraVH!=null){
      if (linkDobaraVH.isVideo()){
        String mediaUrl = linkDobaraVH.getMediaUrl();
        if (mediaUrl.contains(".mp4")||mediaUrl.contains("kahooappSerQ38videos")){
          Log.e("kdfksffds","playing video 284");
          linkDobaraVH.setVideoPlaying();
          mediaCoverImage = linkDobaraVH.kahooDobaraBinding.kahoImgHolder;
          progressBar = linkDobaraVH.kahooDobaraBinding.progressBar;
          volumeControl = linkDobaraVH.kahooDobaraBinding.ivVolumeControl;
          videoControl=linkDobaraVH.kahooDobaraBinding.videoControle;
          viewHolderParent = linkDobaraVH.itemView;
          mediaContainer = linkDobaraVH.kahooDobaraBinding.mediaContainer;

          videoSurfaceView.setPlayer(videoPlayer);
          viewHolderParent.setOnClickListener(videoViewClickListener);
          volumeControl.setOnClickListener(toggleVolumeClicked);
          setVolumeControl(VolumeState.OFF);
          //viewHolderParent.setOnClickListener(videoViewClickListener);
        }
        playVideoFile(mediaUrl);
        Log.e("kdfksffds","playing video 299");
      }
    }else
    if (kahoDobaraHolder!=null){
      Log.e("kdfksffds","playing video 280");
      if (kahoDobaraHolder.isVideo()){
        String mediaUrl = kahoDobaraHolder.getMediaUrl();
        if (mediaUrl.contains(".mp4")||mediaUrl.contains("kahooappSerQ38videos")){
          Log.e("kdfksffds","playing video 284");
          kahoDobaraHolder.setVideoPlaying();
          mediaCoverImage = kahoDobaraHolder.kahooDobaraBinding.kahoImgHolder;
          progressBar = kahoDobaraHolder.kahooDobaraBinding.progressBar;
          volumeControl = kahoDobaraHolder.kahooDobaraBinding.ivVolumeControl;
          videoControl=kahoDobaraHolder.kahooDobaraBinding.videoControle;
          viewHolderParent = kahoDobaraHolder.itemView;
          mediaContainer = kahoDobaraHolder.kahooDobaraBinding.mediaContainer;

          videoSurfaceView.setPlayer(videoPlayer);
          viewHolderParent.setOnClickListener(videoViewClickListener);
          volumeControl.setOnClickListener(toggleVolumeClicked);
          setVolumeControl(VolumeState.OFF);
          //viewHolderParent.setOnClickListener(videoViewClickListener);
        }
        playVideoFile(mediaUrl);
        Log.e("kdfksffds","playing video 299");
      }
    }else {
      if (kahoHolder!=null){
        Log.e("kdfksffds","playing video 302 "+kahoHolder.isVideo());
        Log.e("kdfksffds","playing 305 media url "+kahoHolder.getMediaUrl());
        if (kahoHolder.isVideo()){
          Log.e("kdfksffds","playing video 306");
          String mediaUrl = kahoHolder.getMediaUrl();
          Log.e("kdfksffds","playing video 308 "+mediaUrl);
          if (mediaUrl.contains(".mp4")||mediaUrl.contains("kahooappSerQ38videos")){
            Log.e("kdfksffds","playing video 309");
            kahoHolder.setVideoPlaying();
            mediaCoverImage = kahoHolder.kahooSingleLayoutViewBinding.kahoImgHolder;
            progressBar = kahoHolder.kahooSingleLayoutViewBinding.progressBar;
            volumeControl = kahoHolder.kahooSingleLayoutViewBinding.ivVolumeControl;
            videoControl=kahoHolder.kahooSingleLayoutViewBinding.videoControle;
            viewHolderParent = kahoHolder.itemView;
            mediaContainer = kahoHolder.kahooSingleLayoutViewBinding.mediaContainer;

            videoSurfaceView.setPlayer(videoPlayer);
            viewHolderParent.setOnClickListener(videoViewClickListener);
            volumeControl.setOnClickListener(toggleVolumeClicked);
            setVolumeControl(VolumeState.OFF);
            //viewHolderParent.setOnClickListener(videoViewClickListener);
          }
          playVideoFile(mediaUrl);
          Log.e("kdfksffds","playing video 322");
        }
      }
    }

  }

  private void setPlayerListener() {
    videoPlayer.addListener(new Player.EventListener() {
      @Override
      public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

      }

      @Override
      public void onIsPlayingChanged(boolean isPlaying) {
        if (isPlaying){
          playerState=PlayerState.PLAY;
        }else {
          playerState=PlayerState.PAUSE;
        }
        animatePlayerControle();
        Log.e("kjhkdjffs","this is playing button "+isPlaying);
      }
    });
  }

  /**
   * Returns the visible region of the video surface on the screen.
   * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
   */
  private int getVisibleVideoSurfaceHeight(int playPosition) {
    int at = playPosition - ((LinearLayoutManager) Objects.requireNonNull(
            getLayoutManager())).findFirstVisibleItemPosition();
    Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at);

    View child = getChildAt(at);
    if (child == null) {
      return 0;
    }

    int[] location = new int[2];
    child.getLocationInWindow(location);

    if (location[1] < 0) {
      return location[1] + videoSurfaceDefaultHeight;
    } else {
      return screenDefaultHeight - location[1];
    }
  }

  // Remove the old player
  private void removeVideoView(PlayerView videoView) {
    ViewGroup parent = (ViewGroup) videoView.getParent();
    if (parent == null) {
      return;
    }

    int index = parent.indexOfChild(videoView);
    if (index >= 0) {
      parent.removeViewAt(index);
      isVideoViewAdded = false;
      viewHolderParent.setOnClickListener(null);
    }
  }


  private void addVideoView() {
    mediaContainer.addView(videoSurfaceView);
    isVideoViewAdded = true;
    videoSurfaceView.requestFocus();
    videoSurfaceView.setVisibility(VISIBLE);
    videoSurfaceView.setAlpha(1);
    mediaCoverImage.setVisibility(GONE);
  }

  private void resetVideoView() {
    if (isVideoViewAdded) {
      removeVideoView(videoSurfaceView);
      playPosition = -1;
      videoSurfaceView.setVisibility(INVISIBLE);
      mediaCoverImage.setVisibility(VISIBLE);
    }
  }

  public void releasePlayer() {

    if (videoPlayer != null) {
      videoPlayer.release();
      videoPlayer = null;
    }

    viewHolderParent = null;
  }

  public void onPausePlayer() {
    if (videoPlayer != null) {
      videoPlayer.stop(true);
    }
  }
  public void pauseVideo(){
    if (videoPlayer!=null){
      videoPlayer.pause();
    }
  }
  public void resumeVideo(){
    if (videoPlayer!=null){
      videoPlayer.play();
    }
  }

  private void toggleVolume() {
    if (videoPlayer != null) {
      if (volumeState == VolumeState.OFF) {
        Log.d(TAG, "togglePlaybackState: enabling volume.");
        setVolumeControl(VolumeState.ON);
      } else if (volumeState == VolumeState.ON) {
        Log.d(TAG, "togglePlaybackState: disabling volume.");
        setVolumeControl(VolumeState.OFF);
      }
    }
  }

  //public void onRestartPlayer() {
  //  if (videoPlayer != null) {
  //   playVideo(true);
  //  }
  //}

  private void setVolumeControl(VolumeState state) {
    volumeState = state;
    if (state == VolumeState.OFF) {
      videoPlayer.setVolume(0f);
      animateVolumeControl();
    } else if (state == VolumeState.ON) {
      videoPlayer.setVolume(1f);
      animateVolumeControl();
    }
  }

  private void animateVolumeControl() {
    if (volumeControl != null) {
      volumeControl.bringToFront();
      if (volumeState == VolumeState.OFF) {
        volumeControl.setImageDrawable(context.getDrawable(R.drawable.ic_volume_off));
      } else if (volumeState == VolumeState.ON) {
        volumeControl.setImageDrawable(context.getDrawable(R.drawable.ic_volume_on));
      }
      volumeControl.animate().cancel();

      volumeControl.setAlpha(1f);

      /*volumeControl.animate()
              .alpha(0f)
              .setDuration(1500).setStartDelay(2000);*/
    }
  }
  private void animatePlayerControle() {
    if (videoPlayer!=null){
      if (videoControl != null) {
        videoControl.bringToFront();
        if (playerState == PlayerState.PAUSE) {
         // videoPlayer.pause();
          videoControl.setImageDrawable(context.getDrawable(R.drawable.ic_play));
        } else if (playerState == PlayerState.PLAY) {
         // videoPlayer.play();
          videoControl.setImageDrawable(context.getDrawable(R.drawable.ic_pause));
        }
        videoControl.animate().cancel();

        videoControl.setAlpha(1f);

        videoControl.animate()
                .alpha(0f)
                .setDuration(1500).setStartDelay(2000);
      }
    }
  }

  private enum PlayerState{
    PLAY,PAUSE
  }
  /**
   * Volume ENUM
   */
  private enum VolumeState {
    ON, OFF
  }
}

