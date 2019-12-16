package com.kaho.app.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaho.app.R;
import com.kaho.app.databinding.ActivityMainBinding;
import com.kaho.app.databinding.ActivityYoutubePlayVideoScreenBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePlayVideoScreen extends YouTubeBaseActivity implements
        YouTubePlayer.PlaybackEventListener{

    YouTubePlayer.OnInitializedListener onInitializedListener=null;
    YouTubePlayer mYouTubePlayer=null;
    ActivityYoutubePlayVideoScreenBinding youtubePlayVideoScreenBinding;

    private String videosList="";
    private boolean isFromMain;
    private boolean isTermsDialog=false;
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        youtubePlayVideoScreenBinding= ActivityYoutubePlayVideoScreenBinding.inflate(getLayoutInflater());
        setContentView(youtubePlayVideoScreenBinding.getRoot());

        this.setFinishOnTouchOutside(false);


        isFromMain=getIntent().getBooleanExtra("from_main",false);


        clickListeners();
        videoId=getIntent().getStringExtra("videoId");

        startYoutubePlayer();
    }



    private void startYoutubePlayer() {
        //YouTubePlayer youTubePlayer
        if (mYouTubePlayer==null&&onInitializedListener==null){
            onInitializedListener=new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                    YouTubePlayer youTubePlayer, boolean b) {
                    mYouTubePlayer=youTubePlayer;
                    youTubePlayer.loadVideo(videoId);
                    //progressBar.setVisibility(View.GONE);
                    mYouTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onLoaded(String s) {

                        }

                        @Override
                        public void onAdStarted() {

                        }

                        @Override
                        public void onVideoStarted() {

                        }

                        @Override
                        public void onVideoEnded() {

                        }

                        @Override
                        public void onError(YouTubePlayer.ErrorReason errorReason) {

                        }
                    });
                }
                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            };
            youtubePlayVideoScreenBinding.youtubePlayer.initialize(getResources().getString(R.string.YOUTUBE_API_KEY),onInitializedListener);

        }else {
            mYouTubePlayer.loadVideo(videoId);
        }
    }


    private String getVideoId(String videolink) {
        String vidId="";
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(videolink); //url is youtube url for which you want to extract the id.
        if (matcher.find()) {
            vidId=matcher.group();
            return vidId;
        }
        return vidId;
    }





    protected void onPause() {
        super.onPause();
    }


    private void clickListeners() {
        youtubePlayVideoScreenBinding.closeMediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.nothing,R.anim.scale_down_anim);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onPlaying() {
        Log.e("kdfsdlfdsfds","youtube video playing ");
    }

    @Override
    public void onPaused() {
        Log.e("kdfsdlfdsfds","youtube video onPaused ");
    }

    @Override
    public void onStopped() {
        Log.e("kdfsdlfdsfds","youtube video onStopped ");
    }

    @Override
    public void onBuffering(boolean b) {
        Log.e("kdfsdlfdsfds","youtube video onBuffering ");
    }

    @Override
    public void onSeekTo(int i) {
        Log.e("kdfsdlfdsfds","youtube video onSeekTo ");
    }






}