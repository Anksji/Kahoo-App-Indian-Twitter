package com.kaho.app.UI.KahooUi.AddingKahoo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaho.app.Adapters.HashTagSuggestionAdapters;
import com.kaho.app.Adapters.KahooListAdapter;
import com.kaho.app.Adapters.LocalMediaListAdapters;
import com.kaho.app.Adapters.SHAddBackColorAdapters;
import com.kaho.app.Data.Models.HashTagsModel;
import com.kaho.app.Data.Models.KahooMediaModel;
import com.kaho.app.Data.Models.KahooPostModel;
import com.kaho.app.Data.Models.ShBackColorsModel;
import com.kaho.app.Data.Models.TagSuggestionModel;
import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;
import com.kaho.app.Tools.CropImage.CropImgActivity;
import com.kaho.app.Tools.PublicMethods;
import com.kaho.app.Tools.Utills.Animations.CustmAnimation;
import com.kaho.app.databinding.FragmentDobaraKahooAurReplyBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFtask;

import static android.app.Activity.RESULT_OK;
import static android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION;
import static com.kaho.app.Tools.FirebaseConstant.KAHOO_MAIN_COLLECTION;
import static com.kaho.app.Tools.FirebaseConstant.USER_MAIN_COLLECTION;
import static com.kaho.app.Tools.FirebaseConstant.USER_SHOUT_POST_UPDATE_TIME;
import static com.kaho.app.Tools.Constants.SHOUT_STATUS_PUBLISHED;
import static com.kaho.app.Tools.Constants.VIDEO_ALLOWED_TIME_IN_SEC;
import static com.kaho.app.Tools.HelpMethod.hideKeyboard;


public class DobaraKahooAurReplyFrag extends Fragment {



    private static final int REQUEST_CHECK_SETTINGS = 2542;
    private final int MAX_CHARACTER_LIMIT=500;
    public static final int TAKE_PHOTO_CODE=1353;
    public static final int PHOTO_FROM_GALLERY=1235;
    public static final int PERMISSIONS_REQUEST_TOKEN=2345;
    public static final int REQUEST_TAKE_GALLERY_VIDEO=23422;
    private AlertDialog mProgressBar;
    private SessionPrefManager sessionPrefManager;
    private FirebaseFirestore mDatabase;
    FirebaseDatabase myDb = FirebaseDatabase.getInstance();

    private SHAddBackColorAdapters shAddBackColorAdapters;
    private ArrayList<ShBackColorsModel> shBackColorsList=new ArrayList<>();

    private ShBackColorsModel currentSelectedTheme;

    private String userImageSelected;
    private int permissionAskingFor=-1;
    private String kahooPostId;

    private HashTagSuggestionAdapters tagSuggestionAdapters;
    private ArrayList<String> tagList = new ArrayList<>();
    private ArrayList<String> atRateUniqueIdList=new ArrayList<>();

    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL =  2* 1000;  /* 2 secs */
    private final static long FASTEST_INTERVAL = 1000 ; /* 1 sec */
    private double LastKnownLatitude = 0, LastKnownLongitude = 0;
    private NavController navController;
    private boolean isMenuOptionOpen=false;

    private FragmentDobaraKahooAurReplyBinding dobaraKahooAurReplyBinding;

    private boolean isImagePostAdded=false;
    private boolean isImagePostUploaded=false;

    private List<KahooPostModel> kahooList=new ArrayList<>();
    private ArrayList<String> mediaDownloadUrlList=new ArrayList<>();
    /*
    private ArrayList<String> localMediaList=new ArrayList<>();*/
    private LocalMediaListAdapters localMediaListAdapters;
    private ArrayList<KahooMediaModel> kahooMediaList=new ArrayList<>();
    private File videoFolder;
    private String videoFile;
    private String Orientation="";
    private int ProgressCounter=0;
    private boolean isVideoAdded=false;
    private boolean isVideoRemoved=false;
    private boolean isVideoUploaded=false;
    private boolean createPostClicked=false;
    private String downloadVideoUrl="";

    private LinearLayoutManager mediaLinearLayoutMngr;

    private KahooListAdapter kahooListAdapter;
    private LinearLayoutManager linearLayoutManager;


    private Looper myLooper;
    private LocationCallback mLocationCallback = new LocationCallback() {


        @Override
        public void onLocationResult(LocationResult locationResult) {

            Log.d("sdfdfsfsgs", "onLocationResult: got location result.");

            final Location location = locationResult.getLastLocation();


            Log.e("sdfdfsfsgs", "location is not stopped ");
            LastKnownLatitude = location.getLatitude();
            LastKnownLongitude = location.getLongitude();

            if (mFusedLocationClient != null&&LastKnownLatitude>0) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
            //saveUserLocation(location.getLatitude(), location.getLongitude());
        }
    };


    private void getLocation() {

        if (LastKnownLatitude>0&&LastKnownLongitude>0){
            if (mFusedLocationClient != null&&LastKnownLatitude>0) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
            return;
        }

        final LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);
        myLooper = Looper.myLooper();


        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("jsklsdsdf", "getLocation: stopping the location service. return");
            //stopSelf();
            return;
        }
        Log.d("jsklsdsdf", "getLocation: getting location information. fetching");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, mLocationCallback, myLooper);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    LastKnownLatitude = location.getLatitude();
                    LastKnownLongitude = location.getLongitude();
                    //saveUserLocation(location.getLatitude(), location.getLongitude());
                    Log.e("jsklsdsdf","location fetch success locatin is "+location.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("jsklsdsdf","location fetch error "+e.getMessage());
            }
        });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                onBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dobaraKahooAurReplyBinding = FragmentDobaraKahooAurReplyBinding.inflate(inflater,container,false);
        return dobaraKahooAurReplyBinding.getRoot();
    }

    private KahooPostModel kahooPostModel;
    private boolean isKahooDobara=false;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        sessionPrefManager=new SessionPrefManager(getActivity());
        mDatabase=FirebaseFirestore.getInstance();
        navController= Navigation.findNavController(view);
        addBackGroundColorList();

        Bundle bundle=getArguments();
        kahooPostModel=(KahooPostModel) bundle.getSerializable("kahoo_post");
        isKahooDobara=bundle.getBoolean("isdobarakaho");

        kahooPostId=sessionPrefManager.getUserID()+"_"+PublicMethods.getRandomString(8);


        kahooListAdapter = new KahooListAdapter(DobaraKahooAurReplyFrag.this,false,kahooList);
        linearLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        dobaraKahooAurReplyBinding.kahooListRv.setLayoutManager(linearLayoutManager);
        dobaraKahooAurReplyBinding.kahooListRv.setAdapter(kahooListAdapter);
        initilizeList();


        dobaraKahooAurReplyBinding.hashTagSuggestion.setHasFixedSize(true);
        tagSuggestionAdapters = new HashTagSuggestionAdapters(DobaraKahooAurReplyFrag.this,new ArrayList<>());
        dobaraKahooAurReplyBinding.hashTagSuggestion.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        dobaraKahooAurReplyBinding.hashTagSuggestion.setAdapter(tagSuggestionAdapters);


        mediaLinearLayoutMngr=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        localMediaListAdapters = new LocalMediaListAdapters(kahooMediaList,getActivity(),DobaraKahooAurReplyFrag.this);
        dobaraKahooAurReplyBinding.mediaFileSelected.setLayoutManager(mediaLinearLayoutMngr);
        dobaraKahooAurReplyBinding.mediaFileSelected.setAdapter(localMediaListAdapters);


        createVideoFolder();
        createVideoFile();

        dobaraKahooAurReplyBinding.mediaFileSelected.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    int index=mediaLinearLayoutMngr.findFirstCompletelyVisibleItemPosition();

                    Log.e("dsfjsksfs","calling inside onscorlled state change "+index);
                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
                   /*if (!recyclerView.canScrollHorizontally(1)) {
                        addNewKahooBinding.mediaFileSelected.playVideoFromIndex(index);
                    } else {
                        addNewKahooBinding.mediaFileSelected.playVideoFromIndex(index);
                    }
                    */
                    if (!recyclerView.canScrollHorizontally(1)) {
                        dobaraKahooAurReplyBinding.mediaFileSelected.playVideo(true,kahooMediaList.size()-1,null);
                    } else {
                        dobaraKahooAurReplyBinding.mediaFileSelected.playVideo(false,0,null);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });



        /*addNewKahooBinding.backColorPalletList.setHasFixedSize(true);
        shAddBackColorAdapters = new SHAddBackColorAdapters(shBackColorsList,EditKahooFragment.this);
        addNewKahooBinding.backColorPalletList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        addNewKahooBinding.backColorPalletList.setAdapter(shAddBackColorAdapters);
*/
        //addNewKahooBinding.postContent.setSelection(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (!GPSstatusCheck()) {
            settingsrequest();
        }else {
            getLocation();
        }

        //filter("");
        dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.GONE);

        editTextListeners();
        clickListeners();
        showKeyboard(getActivity());


    }

    private void initilizeList() {
        ArrayList<KahooPostModel> list=new ArrayList<>();
        list.add(kahooPostModel);
        List<KahooPostModel> dataList=(List<KahooPostModel>)(Object) list;
        kahooListAdapter.updateKahooList(dataList);
        kahooListAdapter.notifyDataSetChanged();
    }

    public void kahoMediaClicked(int index,KahooPostModel kahooPostModel){
        Log.e("kdsjfk","kaho clicke "+index);
        if (kahooPostModel.getKaho_media_list().size()>0&&isVideo(kahooPostModel.getKaho_media_list().get(0))){
            dobaraKahooAurReplyBinding.kahooListRv.playVideoFromIndex(index,kahooPostModel.getKaho_media_list().get(0));
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



    public void clearMediaFileIsClicked(KahooMediaModel data){
        int index=kahooMediaList.indexOf(new KahooMediaModel(data.getLocalUrl()));
        if (index>=0&&index<kahooMediaList.size()){
            kahooMediaList.remove(index);
            if (data.isVideo()){
                isVideoRemoved=true;
            }
        }
        Log.e("jkhsfsfsd","this is the index "+index+" media download url list "+mediaDownloadUrlList.size());
        if (data.isVideo()){
            for (int i=0;i<mediaDownloadUrlList.size();i++) {
                if (mediaDownloadUrlList.get(i).contains(".mp4")||mediaDownloadUrlList.contains("kahooappSerQ38videos")) {
                    mediaDownloadUrlList.remove(i);
                    isVideoAdded=false;
                    break;
                }
            }
        } else {
            for (int i=0;i<mediaDownloadUrlList.size();i++){
                if (mediaDownloadUrlList.get(i).contains(data.getLocalImageName())){
                    mediaDownloadUrlList.remove(i);
                    break;
                }
            }
        }
        Log.e("jkhsfsfsd","after removing media download url list "+mediaDownloadUrlList.size());
        localMediaListAdapters.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dobaraKahooAurReplyBinding.kahooListRv!=null){
            dobaraKahooAurReplyBinding.kahooListRv.releasePlayer();
        }
        if (dobaraKahooAurReplyBinding.mediaFileSelected!=null){
            dobaraKahooAurReplyBinding.mediaFileSelected.releasePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dobaraKahooAurReplyBinding.kahooListRv!=null){
            dobaraKahooAurReplyBinding.kahooListRv.pauseVideo();
        }
        if (dobaraKahooAurReplyBinding.mediaFileSelected!=null){
            dobaraKahooAurReplyBinding.mediaFileSelected.pauseVideo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dobaraKahooAurReplyBinding.kahooListRv!=null){
            dobaraKahooAurReplyBinding.kahooListRv.resumeVideo();
        }
        if (dobaraKahooAurReplyBinding.mediaFileSelected!=null){
            dobaraKahooAurReplyBinding.mediaFileSelected.resumeVideo();
        }
    }


    public void playVideoClicked(int index,KahooMediaModel kahooMediaModel){
        dobaraKahooAurReplyBinding.mediaFileSelected.playVideoFromIndex(index,"");
    }


    private void openMediaOptions() {

        if (isMenuOptionOpen){
            return;
        }

        isMenuOptionOpen=true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            /*int devWidth=sessionManager.getUserDeviceWidth();
            int Inpix= Math.round(PublicMethods.convertDpToPixel(FirstScreen.this,24.0f));*/
            int UserImgaeXTouch= Math.round(dobaraKahooAurReplyBinding.mediaOptions.getX());
            int UserImageYTouch= (Math.round(dobaraKahooAurReplyBinding.mediaOptions.getY()));

            float finalRadius = (float) Math.hypot((dobaraKahooAurReplyBinding.mediaOptions.getWidth()+ dobaraKahooAurReplyBinding.mediaOptions.getX()),
                    (dobaraKahooAurReplyBinding.mediaOptions.getWidth()+ dobaraKahooAurReplyBinding.mediaOptions.getY()));

            Log.e("sdfsdkfdsf"," cx "+UserImgaeXTouch+" cy "+UserImageYTouch+
                    " initial radius "+0f+" final radius "+finalRadius);
            CustmAnimation.circleRevelAnime(dobaraKahooAurReplyBinding.mediaOptions,UserImgaeXTouch,UserImageYTouch,
                    500,0f,finalRadius);


        } else {
            // set the view to visible without a circular reveal animation below Lollipop
            dobaraKahooAurReplyBinding.mediaOptions.setVisibility(View.VISIBLE);
        }


    }
    private void closeMediaOptions(){

        Log.e("dkfsjfsfd","closing user profile ");

        isMenuOptionOpen=false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = dobaraKahooAurReplyBinding.mediaOptions.getWidth();
            int cy = dobaraKahooAurReplyBinding.mediaOptions.getHeight();

            int UserImgaeXTouch= Math.round(dobaraKahooAurReplyBinding.mediaOptions.getX());
            int UserImageYTouch= (Math.round(dobaraKahooAurReplyBinding.mediaOptions.getY()));

            float initialRadius = (float) Math.hypot((dobaraKahooAurReplyBinding.mediaOptions.getWidth()+ dobaraKahooAurReplyBinding.mediaOptions.getX()),
                    (dobaraKahooAurReplyBinding.mediaOptions.getWidth()+ dobaraKahooAurReplyBinding.mediaOptions.getY()));

            CustmAnimation.circularHide(dobaraKahooAurReplyBinding.mediaOptions,UserImgaeXTouch,UserImageYTouch,
                    500,initialRadius,0f);

        } else {
            // set the view to visible without a circular reveal animation below Lollipop
            dobaraKahooAurReplyBinding.mediaOptions.setVisibility(View.GONE);
        }
    }


    public void onBackPressed() {
        Log.e("dkfjkdffsd","onback press is clicked");
        if (isMenuOptionOpen){
            closeMediaOptions();
        }else {
            backPressConfirm(getActivity());
            hideKeyboard(getActivity());
        }
    }


    private void backPressConfirm(Activity context) {

        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View mView = context.getLayoutInflater().inflate(R.layout.onback_press_dialog, null);

            TextView message;
            TextView EXISTbTUN;
            TextView keep_writing;

            message = (TextView)mView.findViewById( R.id.message );
            EXISTbTUN = (TextView)mView.findViewById( R.id.cancle );
            keep_writing = (TextView)mView.findViewById( R.id.delete );

            builder.setView(mView);
            final AlertDialog alertDialog = builder.create();

            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            EXISTbTUN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFusedLocationClient != null) {
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    }
                    alertDialog.dismiss();
                    navController.navigate(R.id.action_dobaraKahooAurReplyFrag_to_mainFragment);
                }
            });

            keep_writing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        }catch (Exception e){
            Log.e("kjsdhfsdkf","this is error "+e.getMessage());
        }

    }

    private String presentDate,pastDate;
    Spannable mspanable;
    int hashTagIsComing = 0;
    int atRateIsComing = 0;
    private FirebaseFunctions mFunctions;
    private String newHashTag="";
    private String tagType="";
    private void editTextListeners() {

        mFunctions= FirebaseFunctions.getInstance();
        mspanable = dobaraKahooAurReplyBinding.postContent.getText();

        dobaraKahooAurReplyBinding.postContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String startChar = null;
                try{
                    startChar = Character.toString(s.charAt(start));
                    Log.i(getClass().getSimpleName(), " CHARACTER OF NEW WORD : " + startChar);


                }catch(Exception ex){
                    startChar = "";
                }

                if (startChar.equals("#")&&atRateIsComing==0){
                    dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.VISIBLE);
                    changeTheColor(s.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                    Log.e("gghfgfgdf"," inside hashtag comming 369 ");
                    tagType="#";
                    filter("#");
                }

                if (startChar.equals("@")){
                    dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.VISIBLE);
                    changeAtTheRateColor(s.toString().substring(start), start, start + count);
                    atRateIsComing++;
                    Log.e("gghfgfgdf","inside hashtag comming 369 ");
                    tagType="@";
                    filter("@");
                }


                if(startChar.equals(" ")){
                    atRateIsComing = 0;
                    hashTagIsComing = 0;
                    if (newHashTag.trim().length()>0
                            && !usedHashList.contains(newHashTag.trim().toLowerCase())
                            && tagType.equals("#")) {
                        //usedHashList.add(newHashTag.toLowerCase());
                        Log.e("gghfgfgdf"," setting has tag to zero 380 "+newHashTag);
                    }
                    newHashTag="";
                    tagType="";
                    dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.GONE);
                }


                if (atRateIsComing!=0&&hashTagIsComing==0){
                    changeAtTheRateColor(s.toString().substring(start), start, start + count);
                    atRateIsComing++;

                }

                if(hashTagIsComing != 0 && atRateIsComing==0) {
                    changeTheColor(s.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                    Log.e("gghfgfgdf"," setting has tag to not zero 381 ");
                }

                if (newHashTag.length()>0){
                    filter(newHashTag);
                }

                if(dobaraKahooAurReplyBinding.postContent.getText().toString().trim().length()==0){
                    hashTagIsComing=0;
                    atRateIsComing=0;
                    tagType="";
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after){
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (dobaraKahooAurReplyBinding.postContent.getText().toString().length()>0){
                    if (dobaraKahooAurReplyBinding.postContent.getText().toString().length()<MAX_CHARACTER_LIMIT) {
                        dobaraKahooAurReplyBinding.textLimit.setText((dobaraKahooAurReplyBinding.postContent.getText().toString().length()+1)+"/"+MAX_CHARACTER_LIMIT);
                    }
                }
                if (isMenuOptionOpen){
                    closeMediaOptions();
                }
            }
        });

        getTimeInPresent().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                        Log.e("adrw3esefsfdgd","this is task result "+details);
                    }
                    else{
                        presentDate=task.getResult().trim().toString();
                        Log.e("adrw3esefsfdgd","present date "+presentDate);
                    }
                }
            }
        });

        getTopTrendingHashTags();
        getTimeInPast(1).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                        Log.e("sjdhkfskfjsf","this is task result "+details);
                    }
                    else{
                        //String  future_date=task.getResult().trim().toString();
                        pastDate=task.getResult().trim().toString();
                        getTopTrendingHashTags();
                        //updateSubscription(mCurrentSubscribedSku,""+todayDate,future_date, finalSelectedSku);
                        Log.e("sadaddkjeweq","past date "+pastDate);
                    }
                }
            }
        });

    }

    private void changeAtTheRateColor(String s, int start, int end) {
        try {
            if (s.length() == 0) {
                if (newHashTag.length()>0){
                    newHashTag=newHashTag.substring(0,newHashTag.length()-1);
                }
            }else {
                if (isHashTagCharacter(s)) {
                    newHashTag+=""+s;
                    dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.VISIBLE);
                }
            }

            if (newHashTag.length()==0) {
                dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.GONE);
                atRateIsComing = 0;
            }

            Log.e("gghfgfgsdsdfdf"," startChar inside hashtag comming "+newHashTag);
            Log.e("gghfgfgsdsdfdf"," startChar inside hashtag comming "+s);
            if ((s.length()>0&&isHashTagCharacter(s))||(s.equals("@"))){
                mspanable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_600)), start, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mspanable.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.grey_200)), start, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }catch (Exception e){
            Log.e("kdshfsfsd","this is error "+e.getMessage());
        }
    }

    void filter(String text){
        Log.e("dskhjfksd","calling filtration "+tagType);
        String originalText=text;
        if (text.equals("#")||text.equals("@")) {
            text="";
        }
        ArrayList<TagSuggestionModel> temp = new ArrayList();

        if (tagType.equals("#")){
            for(String d: tagList){
                if(d.toLowerCase().contains(text.toLowerCase())){
                    temp.add(new TagSuggestionModel(d,"",tagType));
                }
            }
        }else if (tagType.equals("@")){
            for(String d: atRateUniqueIdList){
                if(d.toLowerCase().contains(text.toLowerCase())){
                    temp.add(new TagSuggestionModel("",d,tagType));
                }
            }
        }
        Log.e("dskhjfksd"," calling taglist size "+tagList.size());
        Log.e("dskhjfksd"," calling atRateUniqueIdList size "+atRateUniqueIdList.size());
        Log.e("dskhjfksd"," calling filtration temp list size "+temp.size());
        if (dobaraKahooAurReplyBinding.postContent.getText().toString().length()<=0){
            dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.GONE);
        }else if (temp.size()>0&&text.length()>0){
            dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.VISIBLE);
        }else {
            if (originalText.equals("#")||originalText.equals("@")){
                dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.VISIBLE);
            }else {
                dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.GONE);
            }
        }
        tagSuggestionAdapters.updateList(temp);
    }

    public void selectedItem(String item) {
        if ( newHashTag.length()>0||tagType.length()>0 ) {
            /*if (tagType.equals("#")){
                usedHashList.add(item);
            }*/
            dobaraKahooAurReplyBinding.postContent.getText().replace(dobaraKahooAurReplyBinding.postContent.getText().toString().length()-newHashTag.length(),
                    dobaraKahooAurReplyBinding.postContent.getText().toString().length(),item);
            newHashTag="";
            hashTagIsComing = 0;
            atRateIsComing=0;
            tagType="";
            dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.GONE);
        }
        Log.e("kjhfksfdsf","this is selected item "+item+" new hash tag "+newHashTag);
    }

    private ArrayList<HashTagsModel>hashTagList=new ArrayList<>();
    private void getTopTrendingHashTags() {

        getUniqueIdList();

        DatabaseReference dabaseRef=myDb.getReference("TrendingHashTags");

        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    HashTagsModel tagsModel=postSnapshot.getValue(HashTagsModel.class);
                    if (!hashTagList.contains(tagsModel)) {
                        hashTagList.add(0,tagsModel);
                    }
                }

                for (int i=0;i<hashTagList.size();i++){
                    tagList.add(""+hashTagList.get(i).getHashTag());
                    Log.e("dsffferwg","current tag "+hashTagList.get(i).getHashTag());
                }
                Log.e("kjdfhjskdff","tag list size "+tagList.size());
                tagSuggestionAdapters.notifyDataSetChanged();

                Log.e("kjdfhjskdff","on data change is called list size "+hashTagList.size());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        dabaseRef.orderByChild("last_used_time")
                .limitToLast(50).addValueEventListener(postListener);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (postListener!=null){
                    dabaseRef.removeEventListener(postListener);
                }
            }
        },4000);

    }

    private void getUniqueIdList() {
        mDatabase.collection("UserUniqueIds")
                .orderBy("update_time_stamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (DocumentSnapshot documentSnapshot : task.getResult()){
                            JSONArray uniqueIdsArray = new JSONArray(documentSnapshot.getString("unique_array_list"));
                            for (int i=0;i<uniqueIdsArray.length();i++){
                                JSONObject object=uniqueIdsArray.getJSONObject(i);
                                if (!atRateUniqueIdList.contains(object.getString("user_at_rate_unq_id"))){
                                    atRateUniqueIdList.add(object.getString("user_at_rate_unq_id"));
                                }
                            }
                        }
                    }catch (Exception e){
                        Log.e("kjhfswegs","this is error "+e.getMessage());
                    }
                }
            }
        });
    }

    private Task<String> getTimeInPresent() {
        Log.e("sjdhkfskfjsf","calling time in future ");
        return mFunctions.getHttpsCallable("getPresentTime")
                .call()
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

    private Task<String> getTimeInPast(int days) {
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

    private void changeTheColor(String s, int start, int end) {
        try {

            if (s.length() == 0) {
                if (newHashTag.length()>0){
                    newHashTag=newHashTag.substring(0,newHashTag.length()-1);
                }
            }else {
                if (isHashTagCharacter(s)){
                    newHashTag+=""+s;
                    dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.VISIBLE);
                }
            }

            if (newHashTag.length()==0 ) {
                hashTagIsComing = 0;
                dobaraKahooAurReplyBinding.hashTagSuggestion.setVisibility(View.GONE);
            }

            Log.e("gghfgfgsdsdfdf"," startChar inside hashtag comming "+newHashTag);
            Log.e("gghfgfgsdsdfdf"," startChar inside hashtag comming "+s);
            if ((s.length() > 0&&isHashTagCharacter(s))||s.equals("#")){
                mspanable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.kaho_red)), start, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mspanable.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.grey_200)), start, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }catch (Exception e){
            Log.e("kdshfsfsd","this is error "+e.getMessage());
        }

    }

    private boolean isHashTagCharacter(String substring) {
        if (Pattern.matches("[a-zA-Z0-9_]+",substring)) {
            // Do something
            System.out.println("Yes, string contains letters only");
            return true;
        }else{
            return false;
        }
    }

    private void addBackGroundColorList() {

        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_0,"#EEEEEE",false,true));
        //shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_1,"#ff7171",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_2,"#4CAF50",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_3,"#FDB49D",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_4,"#FF8BB3",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_5,"#9C27B0",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_6,"#FFB907",false,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_7,"#feceab",false,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_8,"#7fdbda",false,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_9,"#6f4a8e",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_10,"#96bb7c",true,false));


        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_12,"#450125",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_13,"#2D0460",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_14,"#0D3730",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_15,"#0D4712",true,false));

        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_16,"#006064",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_17,"#3C391B",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_18,"#4E2914",true,false));
        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_19,"#11283A",true,false));

        shBackColorsList.add(new ShBackColorsModel(R.color.kaho_back_color_11,"#000000",true,false));
        currentSelectedTheme=shBackColorsList.get(0);

    }

    public void colorSlectedClicked(int position, ShBackColorsModel data) {
        for (int i=0;i<shBackColorsList.size();i++){
            shBackColorsList.get(i).setIs_selected(false);
        }

        dobaraKahooAurReplyBinding.editBackImg.setColorFilter(ContextCompat.getColor(getActivity(), data.getColorCode()),
                android.graphics.PorterDuff.Mode.SRC_IN);
        //addNewKahooBinding.postContent.setBackgroundColor(shBackColorsList.get(position).getColorCode());
        if (data.isWhiteTheme()){
            dobaraKahooAurReplyBinding.postContent.setTextColor(getResources().getColor(R.color.white));
            dobaraKahooAurReplyBinding.postContent.setHintTextColor(getResources().getColor(R.color.grey_400));
        }else {
            dobaraKahooAurReplyBinding.postContent.setTextColor(getResources().getColor(R.color.grey_700));
            dobaraKahooAurReplyBinding.postContent.setHintTextColor(getResources().getColor(R.color.grey_400));
        }
        currentSelectedTheme=data;
        shBackColorsList.get(position).setIs_selected(true);
        shAddBackColorAdapters.notifyDataSetChanged();
    }

    private static void showKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideBottomSheet() {
        Animation animFadeIn, comesUp;
        dobaraKahooAurReplyBinding.backgroundOverlay.clearAnimation();
        dobaraKahooAurReplyBinding.bottomSheetLayout.clearAnimation();
        animFadeIn= AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_anim);
        comesUp=AnimationUtils.loadAnimation(getActivity(), R.anim.item_anim_push_down);

        dobaraKahooAurReplyBinding.backgroundOverlay.setAnimation(animFadeIn);
        dobaraKahooAurReplyBinding.bottomSheetLayout.setAnimation(comesUp);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dobaraKahooAurReplyBinding.shareScreen.setVisibility(View.GONE);
            }
        },400);
    }

    private void showBottomSheet() {
        Animation animFadeIn, comesUp;
        animFadeIn= AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_anim);
        comesUp=AnimationUtils.loadAnimation(getActivity(), R.anim.item_anim_comes_up);

        dobaraKahooAurReplyBinding.backgroundOverlay.setAnimation(animFadeIn);
        dobaraKahooAurReplyBinding.bottomSheetLayout.setAnimation(comesUp);
    }

    private int askTime=0;
    private boolean isNormalPostSet=false;


    private void clickListeners() {

        dobaraKahooAurReplyBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dobaraKahooAurReplyBinding.cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dobaraKahooAurReplyBinding.backgroundOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomSheet();
            }
        });

        dobaraKahooAurReplyBinding.shareNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()){
                    isNormalPostSet=false;
                    if (LastKnownLatitude==0||LastKnownLongitude==0){
                        if (!GPSstatusCheck()) {
                            settingsrequest();
                        }else {
                            getLocation();
                        }
                        mProgressBar= PublicMethods.showprogressLoading(getActivity());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                shareFinalPost();
                            }
                        },1000*10);
                    }else {
                        mProgressBar=PublicMethods.showprogressLoading(getActivity());
                        shareFinalPost();
                    }
                }else {
                    askForLocationPermission();
                }
            }
        });

        dobaraKahooAurReplyBinding.normalPublicPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNormalPostSet=true;
                mProgressBar=PublicMethods.showprogressLoading(getActivity());
                shareFinalPost();
            }
        });

        dobaraKahooAurReplyBinding.createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImgUri==null&& dobaraKahooAurReplyBinding.postContent.getText().toString().length()<5){
                    if (isKahooDobara){
                        hideKeyboard(getActivity());
                        mProgressBar=PublicMethods.showprogressLoading(getActivity());
                        shareFinalPost();
                    }else {
                        Toast.makeText(getActivity(),getString(R.string.PLEASE_ADD_DATA),Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else {
                    hideKeyboard(getActivity());
                    if (sessionPrefManager.isLoggedIn()) {
                        if (isVideoAdded){
                            if (isVideoUploaded){
                                mProgressBar=PublicMethods.showprogressLoading(getActivity());
                                shareFinalPost();
                            }else {
                                mProgressBar=PublicMethods.showprogressLoading(getActivity());
                                createPostClicked=true;
                            }
                        }else  if (isImagePostAdded){
                            if (isImagePostUploaded){
                                mProgressBar=PublicMethods.showprogressLoading(getActivity());
                                shareFinalPost();
                            }else {
                                mProgressBar=PublicMethods.showprogressLoading(getActivity());
                                createPostClicked=true;
                            }
                        }else {
                            mProgressBar=PublicMethods.showprogressLoading(getActivity());
                            shareFinalPost();
                        }

                        /*
                        askTime++;
                        Log.e("kdhjfksf","outside this is click on post button askTime "+askTime);
                        if (checkLocationPermission()||askTime>2){
                            Log.e("kdhjfksf","inside this is click on post button LastKnownLatitude "+LastKnownLatitude);
                            Log.e("kdhjfksf","inside this is click on post button LastKnownLongitude "+LastKnownLongitude);
                            askPermAndAddNewPost();

                        }else {
                            permissionAskingFor=3;
                            askForLocationPermission();
                        }*/
                    }else {
                        openSignInScreen();
                    }
                }
            }
        });

       /* addNewKahooBinding.clearSelectedVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewKahooBinding.selectedVideoLayout.setVisibility(View.GONE);
                downloadVideoUrl="";
                isVideoUploaded=false;
                isVideoAdded=false;
            }
        });

        addNewKahooBinding.clearImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewKahooBinding.selectedImageLayout.setVisibility(View.GONE);
                selectedImgUri=null;
                isImagePostUploaded=false;
                isImagePostAdded=false;
                userImageSelected="";
            }
        });*/

        dobaraKahooAurReplyBinding.closeMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMediaOptions();
            }
        });

        dobaraKahooAurReplyBinding.addImageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMenuOptionOpen){
                    closeMediaOptions();
                }else {
                    openMediaOptions();
                }

                /*hideKeyboard(getActivity());
                if (checkCameraPermission()){
                    openCamera();
                }else {
                    permissionAskingFor=1;
                    askForPermission();
                }*/
            }
        });

        dobaraKahooAurReplyBinding.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVideoAdded){
                    if (checkCameraPermission()){
                        closeMediaOptions();
                        Intent intent = new Intent();
                        intent.setType("video/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
                    }else {
                        permissionAskingFor=1;
                        askForPermission();
                    }
                }else {
                    if (isVideoRemoved){
                        Toast.makeText(getActivity(),getResources().getString(R.string.VIDEO_REMOVE_IS_IN_PROGRESS),Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(),getResources().getString(R.string.ONLY_ONE_VIDEO_IN_ONE_POST),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dobaraKahooAurReplyBinding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(getActivity());
                if (checkCameraPermission()){
                    closeMediaOptions();
                    openCamera();
                }else {
                    permissionAskingFor=1;
                    askForPermission();
                }
            }
        });

        dobaraKahooAurReplyBinding.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMediaOptions();
                hideKeyboard(getActivity());
                if (checkCameraPermission()){
                    openCropImage();
                }else {
                    permissionAskingFor=2;
                    askForPermission();
                }
            }
        });

        dobaraKahooAurReplyBinding.addImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void openSignInScreen() {
        navController.navigate(R.id.action_landingFragment_to_signInFragment);
    }
    /*

        private void askPermAndAddNewPost(){
            if (askTime>2){
                if (selectedImgUri!=null){
                    mProgressBar=PublicMethods.showprogressLoading(getActivity());
                    uploadImage();
                }else{
                    userImageSelected="";
                    finalSavePost("");
                }
            }else if (checkLocationPermission()){
                if (LastKnownLatitude==0||LastKnownLongitude==0){
                    if (!GPSstatusCheck()) {
                        settingsrequest();
                    }else {
                        getLocation();
                    }
                    getLocation();
                    mProgressBar=PublicMethods.showprogressLoading(getActivity());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (selectedImgUri!=null){
                                uploadImage();
                            }else{
                                userImageSelected="";
                                finalSavePost("");
                            }
                        }
                    },1000*15);
                }else {
                    if (selectedImgUri!=null){
                        mProgressBar=PublicMethods.showprogressLoading(getActivity());
                        uploadImage();
                    }else{
                        userImageSelected="";
                        finalSavePost("");
                    }
                }
            }else {
                if (selectedImgUri!=null){
                    mProgressBar=PublicMethods.showprogressLoading(getActivity());
                    uploadImage();
                }else{
                    userImageSelected="";
                    finalSavePost("");
                }
            }
        }

        */
    private boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat
                    .checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                return false;
            }else {
                return true;
            }
        }else {
            return true;
        }
    }

    public void askForLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat
                    .checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

                    Snackbar snackbar = Snackbar
                            .make(getActivity().findViewById(android.R.id.content), getString(R.string.PERMISSION_SNACK_BAR_MSG), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary_800));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.setAction(R.string.ENABLE, new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View view) {
                            requestPermissions(
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSIONS_REQUEST_TOKEN);
                        }
                    });
                    snackbar.show();
                }else {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_TOKEN);
                }
            }
        }else {
            if (!GPSstatusCheck()) {
                settingsrequest();
            }
        }
    }


    public boolean GPSstatusCheck() {
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            String allowedLocationProviders =
                    Settings.System.getString(getActivity().getContentResolver(),
                            Settings.System.LOCATION_PROVIDERS_ALLOWED);

            if (allowedLocationProviders == null) {
                allowedLocationProviders = "";
            }

            gps_enabled= allowedLocationProviders.contains(LocationManager.GPS_PROVIDER);
            // gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        /*try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}
*/
        Log.e("dsfhsjfdds","gps status "+gps_enabled);

        if(!gps_enabled) {
            return false;
        }else {
            return true;
        }

    }


    private String TAG="deliverymainfksfdsf";

    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    public void settingsrequest() {

        mSettingsClient = LocationServices.getSettingsClient(getActivity());
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Log.e("sdfjdskfs","mSettingsClient");
                        //noinspection MissingPermission
                        getLocation();
                        //mProgressBar=PublicMethods.showprogressLoading(getActivity());
                       /* new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (selectedImgUri!=null){
                                    uploadImage();
                                }else{
                                    userImageSelected="";
                                    finalSavePost("");
                                }
                            }
                        },1000*15);*/
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    public static boolean isNewPost=false;
    private ArrayList<String> usedHashList=new ArrayList<>();

    private void shareFinalPost() {

        usedHashList.clear();
        String fullText=dobaraKahooAurReplyBinding.postContent.getText().toString();

        ArrayList<String> userTagList=new ArrayList<>();

        String[] textArray=fullText.split("\\s+" );

        for (int i=0;i<textArray.length;i++){
            if (textArray[i].contains("#")){
                String[] hashTag=textArray[i].split("#" );
                for (int j=1;j<hashTag.length;j++){
                    Log.e("ksdjfhjskf","after tag split string is "+hashTag[j]);
                    if (hashTag[j].trim().length()>0&&hashTag[j].trim().length()<33){
                        usedHashList.add(hashTag[j].toLowerCase());
                    }
                }
            }else if (textArray[i].contains("@")){
                String[] peopleTag=textArray[i].split("@" );
                for (int j=1;j<peopleTag.length;j++){
                    Log.e("ksdjfhjskf","after tag split string is "+peopleTag[j]);
                    if (peopleTag[j].trim().length()>0){
                        userTagList.add(peopleTag[j]);
                    }
                }
            }
        }

        for (int i=0;i<kahooPostModel.getKahoo_tag_people_list().size();i++){
            if (!userTagList.contains(kahooPostModel.getKahoo_tag_people_list().get(i))){
                userTagList.add(kahooPostModel.getKahoo_tag_people_list().get(i));
            }
        }
        for (int i=0;i<kahooPostModel.getHash_tag_list().size();i++){
            if (!usedHashList.contains(kahooPostModel.getHash_tag_list().get(i))){
                usedHashList.add(kahooPostModel.getHash_tag_list().get(i));
            }
        }
        kahooPostModel.setHash_tag_list(new ArrayList<>());
        kahooPostModel.setKahoo_tag_people_list(new ArrayList<>());
        kahooPostModel.setLikedUserIdsList(new ArrayList<>());
        kahooPostModel.setRepliedKahooUidsList(new ArrayList<>());
        kahooPostModel.setDobaraKahooUidsList(new ArrayList<>());

        Log.e("kjdfhsskf","adding data to server");

        kahooPostModel.setOther_kaho_data(null);
        HashMap<String,Object> kahoData=new HashMap<>();
        String content= dobaraKahooAurReplyBinding.postContent.getText().toString();

        if (isKahooDobara){
            if (content.length()==0){
                String extra="";
                if (kahooPostModel.getKahoo_text_content().trim().endsWith("ne dobara kaha")){

                }else {
                    extra=kahooPostModel.getKahoo_text_content();
                }
                content=sessionPrefManager.getUserName().toUpperCase()+ " ne dobara kaha "+extra;
            }
        }

        
        kahoData.put("kahooPostId",kahooPostId);
        kahoData.put("has_dobara_kaho",isKahooDobara);
        kahoData.put("has_reply_kaho",!isKahooDobara);
        kahoData.put("parent_kahoo_id",kahooPostModel.getKahooPostId());
        kahoData.put("other_kaho_data",kahooPostModel);
        //shoutData.put("kahooPostImgUri",userImageSelected);
        kahoData.put("kahoo_text_content",content);
        //shoutData.put("shWordBkgroundColor",currentSelectedTheme.getColorId().trim());
        kahoData.put("kaho_isWhiteTheme",currentSelectedTheme.isWhiteTheme());
        kahoData.put("kahooAddedTimeStamp", FieldValue.serverTimestamp());
        if (!isKahooDobara){
            kahoData.put("replyAddedTimeStamp", FieldValue.serverTimestamp());
        }

        kahoData.put("kaho_added_user_id",sessionPrefManager.getUserID());
        kahoData.put("kaho_added_user_name",sessionPrefManager.getUserName());
        kahoData.put("kaho_added_user_handel_id",sessionPrefManager.getAtTheRateUserKahoHandel());
        kahoData.put("kaho_added_user_image",sessionPrefManager.getUserProfilePic());

        kahoData.put("kaho_media_list",mediaDownloadUrlList);
        kahoData.put("dobara_kahoo_count",0);
        kahoData.put("kahoo_reply_count",0);
        kahoData.put("kaho_like_count",0);
        kahoData.put("kahooUpdatedTimeStamp", FieldValue.serverTimestamp());
        kahoData.put("kahoo_status_message","Your voice submitted successfully, It will be live soon.");
        kahoData.put("kahoo_status",SHOUT_STATUS_PUBLISHED);
        kahoData.put("likedUserIdsList",new ArrayList<>());
        kahoData.put("dobaraKahooUidsList",new ArrayList<>());
        kahoData.put("repliedKahooUidsList",new ArrayList<>());
        ArrayList<String>shoutOwner=new ArrayList<>();
        shoutOwner.add(sessionPrefManager.getUserID());
        kahoData.put("kahoo_owner_id",shoutOwner);
        usedHashList.add("all");
        kahoData.put("hash_tag_list",usedHashList);
        kahoData.put("kahoo_tag_people_list",userTagList);

        /*if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mFusedLocationClient=null;
        }*/

        mDatabase.collection(KAHOO_MAIN_COLLECTION)
                .document(kahooPostId)
                .set(kahoData, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.e("kjdfhsskf","data added success data to server");
                            Log.e("kdjhfskf","Task is successfull");
                            subSubscribeToShoutPostNotification(kahooPostId);
                            HashMap<String,Object> dataUpdate=new HashMap<>();
                            dataUpdate.put(USER_SHOUT_POST_UPDATE_TIME,FieldValue.serverTimestamp());
                            dataUpdate.put("kahooCount",FieldValue.increment(1));
                            mDatabase.collection(USER_MAIN_COLLECTION).document(sessionPrefManager.getUserID())
                                    .update(dataUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                HashMap<String,Object> kahoData=new HashMap<>();

                                                if (isKahooDobara){
                                                    kahoData.put("dobara_kahoo_count",FieldValue.increment(1));
                                                }else {
                                                    kahoData.put("kahoo_reply_count",FieldValue.increment(1));
                                                }
                                                mDatabase.collection(KAHOO_MAIN_COLLECTION)
                                                        .document(kahooPostModel.getKahooPostId())
                                                        .update(kahoData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    if (usedHashList.size()>0){
                                                                        updateHashTags();
                                                                    }else {
                                                                        Log.e("kdjhfskf","inner else Task is successfull");
                                                                        if (mProgressBar!=null){
                                                                            PublicMethods.dismissLoadingScreen(getActivity(),mProgressBar);
                                                                        }
                                                                        isNewPost=true;
                                                                        Toast.makeText(getActivity(),getString(R.string.YOUR_KAHOO_SEND),Toast.LENGTH_SHORT).show();
                                                                        navController.navigate(R.id.action_addnewkahoofragment_to_mainFragment);
                                                                    }
                                                                }
                                                            }
                                                        });

                                                Log.e("kdjhfskf","inner Task is successfull hash list size "+usedHashList.size());
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("kdjhfskf","this is error "+e.getMessage());
                                }
                            });

                        }
                    }
                });

    }
/*

    private void shareFinalPost() {

        usedHashList.clear();
        String fullText= dobaraKahooAurReplyBinding.postContent.getText().toString();

        ArrayList<String> userTagList=new ArrayList<>();

        String[] textArray=fullText.split("\\s+");

        for (int i=0;i<textArray.length;i++){
            if (textArray[i].contains("#")){
                String[] hashTag=textArray[i].split("#");
                for (int j=1;j<hashTag.length;j++){
                    Log.e("ksdjfhjskf","after tag split string is "+hashTag[j]);
                    if (hashTag[j].trim().length()>0&&hashTag[j].trim().length()<33){
                        usedHashList.add(hashTag[j].toLowerCase());
                    }
                }
            }else if (textArray[i].contains("@")){
                String[] peopleTag=textArray[i].split("@");
                for (int j=1;j<peopleTag.length;j++){
                    Log.e("ksdjfhjskf","after tag split string is "+peopleTag[j]);
                    if (peopleTag[j].trim().length()>0){
                        userTagList.add(peopleTag[j]);
                    }
                }
            }
        }

        Log.e("kjdfhsskf","adding data to server");


        HashMap<String,Object> kahoData=new HashMap<>();

        kahoData.put("kahooPostId", kahooPostId);
        //shoutData.put("kahooPostImgUri",userImageSelected);
        kahoData.put("kahoo_text_content", dobaraKahooAurReplyBinding.postContent.getText().toString());
        kahoData.put("kaho_added_user_name",sessionPrefManager.getUserName());
        kahoData.put("kaho_added_user_handel_id",sessionPrefManager.getAtTheRateUserKahoHandel());
        kahoData.put("kaho_added_user_image",sessionPrefManager.getUserProfilePic());

        kahoData.put("kaho_media_list",mediaDownloadUrlList);
        kahoData.put("kahooUpdatedTimeStamp", FieldValue.serverTimestamp());
        kahoData.put("kahoo_status_message","Your voice submitted successfully, It will live soon.");
        kahoData.put("kahoo_status",SHOUT_STATUS_PUBLISHED);
        usedHashList.add("all");
        kahoData.put("hash_tag_list",usedHashList);
        kahoData.put("kahoo_tag_people_list",userTagList);



        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mFusedLocationClient=null;
        }

        mDatabase.collection(SHOUT_A_LOUD_COLLECTION)
                .document(kahooPostId)
                .set(kahoData, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.e("kjdfhsskf","data added success data to server");
                            Log.e("kdjhfskf","Task is successfull");

                            HashMap<String,Object> dataUpdate=new HashMap<>();
                            dataUpdate.put(USER_SHOUT_POST_UPDATE_TIME,FieldValue.serverTimestamp());
                            mDatabase.collection(USER_MAIN_COLLECTION).document(sessionPrefManager.getUserID())
                                    .set(dataUpdate,SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Log.e("kdjhfskf","inner Task is successfull hash list size "+usedHashList.size());
                                                if (usedHashList.size()>0){
                                                    updateHashTags();
                                                }else {
                                                    Log.e("kdjhfskf","inner else Task is successfull");
                                                    if (mProgressBar!=null){
                                                        PublicMethods.dismissLoadingScreen(getActivity(),mProgressBar);
                                                    }
                                                    isNewPost=true;
                                                    Toast.makeText(getActivity(),getString(R.string.YOUR_KAHOO_SEND),Toast.LENGTH_SHORT).show();
                                                    navController.navigate(R.id.action_dobaraKahooAurReplyFrag_to_mainFragment);
                                                }
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("kdjhfskf","this is error "+e.getMessage());
                                }
                            });

                        }
                    }
                });

    }
*/

    private void updateHashTags() {

        presentDate=""+System.currentTimeMillis();


        for (int i=0;i<usedHashList.size();i++){
            Log.e("kdshfkds","inside loop this is present date "+presentDate+
                    " hash list size "+usedHashList.size()+" i value "+i);

            int finalI = i;
            int finalI1 = i;
            myDb.getReference("TrendingHashTags")
                    .child(usedHashList.get(i))
                    .runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Log.e("kdshfkds","inside transaction 1375");

                            HashTagsModel dataValue = mutableData.getValue(HashTagsModel.class);
                            if (dataValue == null) {
                                HashMap<String,Object> objHash=new HashMap<>();
                                objHash.put("hashTag",usedHashList.get(finalI));
                                objHash.put("last_used_time", ServerValue.TIMESTAMP);
                                objHash.put("used_no_of_times",1);
                                mutableData.setValue(objHash);
                                Log.e("kdshfkds","inside transaction 1384 success return");

                                return Transaction.success(mutableData);
                            }

                            Log.e("kdshfkds","success retrn inside transaction 1389");
                            // Set value and report transaction success
                            HashMap<String,Object> objHash=new HashMap<>();
                            objHash.put("hashTag",usedHashList.get(finalI));
                            long diff=PublicMethods.getTimeDiffInHours(Long.parseLong(presentDate),dataValue.getLast_used_time());
                            Log.e("kjdfhkffdsdf","difference is "+diff);
                            if (diff>24){
                                objHash.put("used_no_of_times",1);
                            }else {
                                objHash.put("used_no_of_times",(dataValue.getUsed_no_of_times()+1));
                            }
                            objHash.put("last_used_time", ServerValue.TIMESTAMP);

                            mutableData.setValue(objHash);
                            Log.e("kdshfkds","success retrn inside transaction 1402");

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed,
                                               DataSnapshot currentData) {
                            // Transaction completed
                            if (databaseError!=null){
                                Log.e("sdfsdfskfdsfd", "postTransaction:onComplete:" + databaseError.getMessage());
                            }
                            //Log.d("sdfsdfskfdsfd", "postTransaction:onComplete:" + databaseError);
                            Log.d("sdfsdfskfdsfd", " hash list size " + usedHashList.size()+" final "+finalI1);
                            if (finalI1 == usedHashList.size()-1){
                                if (mProgressBar!=null){
                                    PublicMethods.dismissLoadingScreen(getActivity(),mProgressBar);
                                }
                                isNewPost=true;
                                Toast.makeText(getActivity(),getString(R.string.YOUR_KAHOO_SEND),Toast.LENGTH_SHORT).show();
                                navController.navigate(R.id.action_dobaraKahooAurReplyFrag_to_mainFragment);
                            }
                        }
                    });
        }
    }

    private void subSubscribeToShoutPostNotification(String id){
        Log.e("fsdkfjsf"," calling to subscribe admin");
        FirebaseMessaging.getInstance().subscribeToTopic(id)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "subscribed successfully";
                        if (!task.isSuccessful()) {
                            msg = "subscribing failed";
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fsdkfjsf"," topic is subscribe failed "+e.getMessage());
            }
        });
    }


    private void uploadImage() {
        try {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            final StorageReference imageRef = storageRef.child("kahooALoud/kahooAppSerQJ92Img/"+selectedImgUri.getLastPathSegment());

            String fileName = new Compressor(getActivity())
                    .setMaxWidth(800)
                    .setMaxHeight(800)
                    .setQuality(70)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(new File(selectedImgUri.getPath())).getAbsolutePath();
            Log.e("jdfksffsdff","this is file name "+fileName);
            final Uri imageUri = Uri.fromFile(new File(fileName));
            Bitmap compressedImg = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

            KahooMediaModel mediaModel=new KahooMediaModel();
            mediaModel.setLocalUrl(imageUri.toString());
            mediaModel.setLocalImageName(imageUri.getLastPathSegment());
            kahooMediaList.add(mediaModel);
            localMediaListAdapters.notifyDataSetChanged();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImg.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            UploadTask uploadTask = imageRef.putBytes(baos.toByteArray());

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e("kjhsdfskf","this is error "+exception.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){

                }
            });

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e("sdfhskjfsdf","this is download uri "+downloadUri);
                        selectedImgUri=downloadUri;
                        userImageSelected=downloadVideoUrl.toString();
                        mediaDownloadUrlList.add(downloadUri.toString());
                        if (mediaDownloadUrlList.size()==kahooMediaList.size()){
                            isImagePostAdded=true;
                            isImagePostUploaded=true;
                            if (createPostClicked){
                                shareFinalPost();
                            }
                        }

                        Log.e("kjhsdfskf","download uri "+downloadUri.toString());

                    } else {
                        Toast.makeText(getActivity(), "image upload error please try again ",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            Log.e("dfhksjfsdf","this is error "+e.getMessage());
        }

    }

    private void finalSavePost(String shoutImgUri) {
        userImageSelected=shoutImgUri;

        if (!sessionPrefManager.getNearByPostPermission()){
            if (mProgressBar!=null){
                PublicMethods.dismissLoadingScreen(getActivity(),mProgressBar);
            }
            dobaraKahooAurReplyBinding.shareScreen.setVisibility(View.VISIBLE);
            showBottomSheet();
        }else {
            if (mProgressBar==null){
                mProgressBar = PublicMethods.showprogressLoading(getActivity());
            }
            shareFinalPost();
        }

        //showBottomSheet();
    }

    private void openCropImage() {
        Intent intent=new Intent(getActivity(), CropImgActivity.class);
        startActivityForResult(intent,PHOTO_FROM_GALLERY);
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) +  ContextCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)+
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    public void askForPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) +  ContextCompat
                    .checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)+ContextCompat
                    .checkSelfPermission(getActivity(),
                            Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (getActivity(), Manifest.permission.CAMERA)) {

                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.PERMISSION_SNACK_BAR_MSG,
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(
                                            new String[]{Manifest.permission
                                                    .READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.CAMERA},
                                            PERMISSIONS_REQUEST_TOKEN);
                                }
                            }).show();

                }else {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_TOKEN);
                }

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_TOKEN:
                if (grantResults.length> 0) {
                    if (checkCameraPermission()&&permissionAskingFor<3){
                        if (permissionAskingFor==1){
                            openCamera();
                        }else if (permissionAskingFor==2){
                            openCropImage();
                        }
                    }else if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
                            &&permissionAskingFor==3){
                        settingsrequest();
                    }else {
                        Toast.makeText(getActivity(),"Please give permissions",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private Uri selectedImgUri;

    private void openCamera(){
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        if (!newdir.exists()){
            newdir.mkdirs();
        }


        String file = dir + PublicMethods.getRandomString(6) +".jpg";
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            Log.e("dsfhksdjdsf","this is exception "+e.getMessage());
        }

        selectedImgUri = Uri.fromFile(newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(getActivity(), getString(R.string.camera_file_provider_name),
                        newfile));
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO&&resultCode==RESULT_OK) {
            Uri selectedImageUri = data.getData();

            // OI FILE Manager
            String filemanagerstring = selectedImageUri.getPath();

            // MEDIA GALLERY
            String selectedImagePath = getPath(selectedImageUri);


            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getActivity(),selectedImageUri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInSec=(Long.parseLong(time)/1000);
            if (timeInSec<=VIDEO_ALLOWED_TIME_IN_SEC){
                /*addNewKahooBinding.selectedVideoLayout.setVisibility(View.VISIBLE);
                addNewKahooBinding.selectedVideo.setVideoURI(data.getData());
                addNewKahooBinding.selectedVideo.start();*/

                String metaRotation = retriever.extractMetadata(METADATA_KEY_VIDEO_ROTATION);
                int rotation = metaRotation == null ? 0 : Integer.parseInt(metaRotation);

                int MediaWidth,MediaHeight;
                if (rotation==0){
                    MediaWidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                    MediaHeight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                }else {
                    MediaWidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    MediaHeight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                }


                kahooMediaList.add(new KahooMediaModel(selectedImageUri.toString(),true));
                localMediaListAdapters.notifyDataSetChanged();
                processVideoNext(MediaWidth,MediaHeight,selectedImageUri);

            }else {
                /*addNewKahooBinding.selectedVideoLayout.setVisibility(View.GONE);
                if (addNewKahooBinding.selectedVideo.isPlaying()){
                    addNewKahooBinding.selectedVideo.stopPlayback();
                }*/
                Toast.makeText(getActivity(),getResources().getString(R.string.ONLY_SORT_VIDEO_LESS_TEN30_SEC),Toast.LENGTH_SHORT).show();
            }

            Log.e("jhsdfkjdsffs","video length in sec "+timeInSec);

            File file = new File(filemanagerstring);
            Log.e("jhsdfkjdsffs","this is file "+file.toString());

            Log.e("jhsdfkjdsffs","this is data uri "+data.getData());
            Log.e("jhsdfkjdsffs","this is file manager uri "+filemanagerstring);
            Log.e("jhsdfkjdsffs","this is selectedImagePath "+selectedImagePath);

            /*if (selectedImagePath != null) {

                Intent intent = new Intent(HomeActivity.this,
                        VideoplayAvtivity.class);
                intent.putExtra("path", selectedImagePath);
                startActivity(intent);
            }*/
        }else if (requestCode==TAKE_PHOTO_CODE){

            Log.e("kjdfhsdkdsf","this is img uri on add new shout "+selectedImgUri);


            File file=new File(selectedImgUri.getPath());
            int fileSize=Integer.parseInt(String.valueOf(file.length()));

            Log.e("kjdfhksdsd","this is fileSize"+fileSize);
          /* if (fileSize>0){
               addNewKahooBinding.selectedImageLayout.setVisibility(View.VISIBLE);
               Glide.with(getActivity())
                       .load(selectedImgUri)
                       .into(addNewKahooBinding.selectedImage);
               isImagePostAdded=true;
               isImagePostUploaded=false;
               uploadImage();
           }*/
            Intent intent=new Intent(getActivity(), CropImgActivity.class);
            intent.putExtra("selected_uri",selectedImgUri.toString());
            System.out.println(intent.getExtras());
            startActivityForResult(intent,PHOTO_FROM_GALLERY);


        }else if (requestCode==PHOTO_FROM_GALLERY){
            if (data!=null&&data.getExtras()!=null){
                Log.e("djhfkfssf","inside shout user data = "+data.getExtras());
                String img=data.getExtras().getString("user_profile_img");
                Log.e("djhfkfssf","inside shout user selected image "+img);
                selectedImgUri=Uri.fromFile(new File(img));
                isImagePostAdded=true;
                uploadImage();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private File createVideoFile() {
        try {
            String prepend = "VIDS_";
            File imageFile = new File(videoFolder+"/kahoo_video.mp4");
            //File.createTempFile(prepend, ".mp4", videoFolder);

            videoFile = imageFile.getAbsolutePath();
            return imageFile;
        }catch (Exception e){
            Log.e("kjskdfsdfsf","this is error "+e.getMessage());
            return null;
        }

    }
    private String createVideoFolder(){
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        videoFolder = new File(imageFile, "kahoo/data");
        if(!videoFolder.exists()) {
            videoFolder.mkdirs();
        }
        return videoFolder.toString();
    }

    private void processVideoNext(int mediaWidth, int mediaHeight, Uri selectedImageUri) {
        Bitmap bitmap;
        bitmap = createThumbnailFromBitmap(selectedImageUri.getPath());
        isVideoAdded=true;
        isVideoUploaded=false;
        String commandStr;
        String fileUri=PublicMethods.getPathFromUri(getActivity(),selectedImageUri);
        //fileUri=fileUri.replaceAll(" ","%20");
        Log.e("kjsdfsfsd","this is file uri "+fileUri);

        if (Orientation.equalsIgnoreCase("landscape")){
            commandStr="-y -i \""+fileUri+"\" -s 352x240 -c:v libx264 -preset ultrafast -crf 22 -c:a copy "+ videoFile;
            Log.e("skjdfsfsffs","orientation is landscape ");
        }else {
            Log.e("skjdfsfsffs","orientation is portrait ");
            commandStr="-y -i \""+fileUri+"\" -s 240x352 -c:v libx264 -preset ultrafast -crf 22 -c:a copy "+ videoFile;

        }

        List<String> commandList = new LinkedList<>();
        commandList.add("-y");
        commandList.add("-i");
        commandList.add(fileUri);
        commandList.add("-s");
        if (Orientation.equalsIgnoreCase("landscape")){
            commandList.add("352x240");
        }else {
            commandList.add("240x352");
        }
        commandList.add("-c:v");
        commandList.add("libx264");
        commandList.add("-preset");
        commandList.add("ultrafast");
        commandList.add("-crf");
        commandList.add("22");
        commandList.add("-c:a");
        commandList.add("copy");
        commandList.add(videoFile);

        String[] cropCommand  = commandList.toArray(new String[commandList.size()]);

        executeCmd(cropCommand,getActivity());
    }

    private void executeCmd(final String[] command,Context context) {
        Log.e("fgjdlkdgdg","Executing commands");
        try {

            final FFtask task = FFmpeg.getInstance(getActivity()).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                    Log.e("kjjkdsffd","this is onstart");
                }
                @Override
                public void onFinish() {
                    Log.e("kjhsdfsfd","onfinish is called");
                }

                @Override
                public void onSuccess(String message) {
                    Log.e("kjsdhfksdfsf","this is onsuccess "+message);
                    uploadVideoFile();
                }

                @Override
                public void onProgress(String message) {
                    Log.e("kjsdhfksdfsf","this is onprogress "+message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e("kjsdhfksdfsf","this is message "+message);
                }
            });


            ProgressCounter=0;

        } catch (Exception e) {
            //There is a command already running
            Log.e("kjsdhfksdfsf"," this is command already running exception "+e.getMessage());
        }
    }

    private void uploadVideoFile() {

        Uri videoUri=Uri.fromFile(new File(videoFile));
        Log.e("jshdfjksss","this is video uri file name "+videoUri);
        String randomName=PublicMethods.getRandomString(8);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imageRef = storageRef.child("videoFile/kahooappSerQ38videos/"+kahooPostId.substring(0,8)+randomName+"_"+videoUri.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(videoUri);



        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("kjhsdfskf","this is error "+exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.e("jkhsfsfsd","initially inside video upload media download url list "+mediaDownloadUrlList.size()+" isvideo removed"+isVideoRemoved);
                    //Toast.makeText(getActivity(),"video upload success ",Toast.LENGTH_SHORT).show();

                    if (!isVideoRemoved){
                        mediaDownloadUrlList.add(downloadUri.toString());
                        if (mediaDownloadUrlList.size()==kahooMediaList.size()){
                            isVideoAdded=true;
                            isVideoUploaded=true;
                        }
                    }else {
                        Toast.makeText(getActivity(),getResources().getString(R.string.VIDEO_REMOVED_SUCCESS),Toast.LENGTH_SHORT).show();
                        isVideoAdded=false;
                        isVideoRemoved=false;
                    }

                    if (mediaDownloadUrlList.size()==kahooMediaList.size()){
                        if (createPostClicked){
                            shareFinalPost();
                        }
                    }
                    Log.e("jkhsfsfsd","inside video upload media download url list "+mediaDownloadUrlList.size()+" isvideo removed"+isVideoRemoved);

                    Log.e("kjhsdfskf","download uri "+downloadUri);
                } else {
                    Toast.makeText(getActivity(), "image upload error please try again ",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private Bitmap createThumbnailFromBitmap(String filePath){
        File videoFile = new File(filePath);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        return thumb;
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

}