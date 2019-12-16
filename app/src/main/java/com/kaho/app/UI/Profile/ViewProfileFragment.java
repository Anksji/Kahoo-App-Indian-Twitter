package com.kaho.app.UI.Profile;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaho.app.Adapters.FragmentAdapters.ScreenSlidePagerAdapter;
import com.kaho.app.Data.Models.KahooPostModel;
import com.kaho.app.Data.Models.SingltonUserDataModel;
import com.kaho.app.Data.Models.UserModel;
import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;
import com.kaho.app.Tools.Utills.ScreenshotUtils;
import com.kaho.app.Tools.Utills.ZoomOutPageViewPager;
import com.kaho.app.UI.KahooUi.AddingKahoo.AddNewKahooFragmentDirections;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.DobaraKahooList;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.LikedKahooList;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.MyKahooListFrag;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.ReplyKahooListFrag;
import com.kaho.app.databinding.FragmentViewProfileBinding;

import java.io.File;
import java.util.HashMap;

import static com.kaho.app.Tools.FirebaseConstant.FOLLOWER_COLLECTION;
import static com.kaho.app.Tools.FirebaseConstant.FOLLOWING_COLLECTION;
import static com.kaho.app.Tools.FirebaseConstant.KAHOO_MAIN_COLLECTION;
import static com.kaho.app.Tools.FirebaseConstant.USER_MAIN_COLLECTION;
import static com.kaho.app.Tools.PublicMethods.loadBitmapFromView;


public class ViewProfileFragment extends Fragment {

    FragmentViewProfileBinding viewProfileBinding;

    private NavController navController;
    private ScreenSlidePagerAdapter adapter;
    private SessionPrefManager sessionPrefManager;
    private String otherUserId;
    private boolean isOtherProfile;
    private FirebaseFirestore mDatabase;
    private UserModel currentUserData;
    private Menu menuContext;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewProfileBinding= FragmentViewProfileBinding.inflate(inflater,container,false);
        return viewProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        viewProfileBinding.viewPager.setPageTransformer(true,new ZoomOutPageViewPager());
        adapter=new ScreenSlidePagerAdapter(getChildFragmentManager());
        viewProfileBinding.viewPager.setAdapter(adapter);
        viewProfileBinding.viewPager.setSaveEnabled(false);

        mDatabase=FirebaseFirestore.getInstance();
        sessionPrefManager=new SessionPrefManager(getActivity());

        otherUserId=sessionPrefManager.getUserID();
        Bundle bundle=getArguments();

           if (bundle!=null&&bundle.containsKey("is_other_profile")){
            isOtherProfile=bundle.getBoolean("is_other_profile",false);
            otherUserId = bundle.getString("other_profile_id");
        }

           Log.e("sdfkjdfsf","this is other user id "+otherUserId+" session user id "+sessionPrefManager.getUserID());

        MyKahooListFrag myKahoo=new MyKahooListFrag(otherUserId,isOtherProfile);
        DobaraKahooList dobaraKaho=new DobaraKahooList(otherUserId,isOtherProfile);
        ReplyKahooListFrag replyKaho=new ReplyKahooListFrag(otherUserId,isOtherProfile);
        LikedKahooList kahooLikes=new LikedKahooList(otherUserId,isOtherProfile);

        adapter.addFrag(myKahoo,"Kahoo");
        adapter.addFrag(dobaraKaho,"Re-Kahoo");
        adapter.addFrag(replyKaho,"Replies");
        adapter.addFrag(kahooLikes,"Liked");
        adapter.notifyDataSetChanged();

        viewProfileBinding.tabs.setupWithViewPager(viewProfileBinding.viewPager);

        ((AppCompatActivity)getActivity()).setSupportActionBar(viewProfileBinding.toolbar);

        viewProfileBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        if (isOtherProfile){
            getOtherProfileData();
        }else {
            setUpProfileData();
        }

        if (otherUserId.equalsIgnoreCase(sessionPrefManager.getUserID())) {
            //my profile only
            viewProfileBinding.editUserImg.setVisibility(View.VISIBLE);
        }else {
            viewProfileBinding.editUserImg.setVisibility(View.GONE);
        }

        if (sessionPrefManager.getUserID().equalsIgnoreCase(otherUserId)){
            viewProfileBinding.followBtn.setVisibility(View.GONE);
            viewProfileBinding.messageBtn.setVisibility(View.GONE);
        }

        clickListeners();
        setHasOptionsMenu(true);
    }

    private void clickListeners() {
        viewProfileBinding.editUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOtherProfile){
                    navController.navigate(R.id.action_viewProfileFragment_to_editUserProfileFragment);
                }else {
                    navController.navigate(R.id.action_kahooMainFragment_to_editUserProfileFragment);
                }

            }
        });
        viewProfileBinding.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUnfollowUser();
            }
        });
        viewProfileBinding.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMessagingUser();
            }
        });

    }

    private void startMessagingUser() {

    }

    public void followUnfollowUser() {

        Log.e("kjdshfkjs","clicking follow unfollow data ");
        if (currentUserData.getFollowersList().contains(sessionPrefManager.getUserID())) {
            //make unfollow user
            unfollowUser();
        }else {
            //make follow user
            followUser();
        }

    }

    private void unfollowUser() {

        HashMap<String,Object> data=new HashMap<>();
        data.put("followersList",FieldValue.arrayRemove(sessionPrefManager.getUserID()));
        data.put("follwersCount",FieldValue.increment(-1));
        mDatabase.collection(USER_MAIN_COLLECTION)
                .document(currentUserData.getUser_id())
                .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                HashMap<String,Object> data=new HashMap<>();
                data.put("followingList",FieldValue.arrayRemove(currentUserData.getUser_id()));
                data.put("followingCount",FieldValue.increment(-1));
                mDatabase.collection(USER_MAIN_COLLECTION)
                        .document(sessionPrefManager.getUserID())
                        .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            currentUserData.getFollowersList().remove(sessionPrefManager.getUserID());
                        }
                    }
                });
            }
        });

        HashMap<String,Object> data2=new HashMap<>();
        data.put("follower_data",userDataModel.getUserData());
        data.put("follower_added_timestamp",FieldValue.serverTimestamp());
        mDatabase.collection(USER_MAIN_COLLECTION)
                .document(currentUserData.getUser_id())
                .collection(FOLLOWER_COLLECTION)
                .document(sessionPrefManager.getUserID())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mDatabase.collection(USER_MAIN_COLLECTION)
                            .document(sessionPrefManager.getUserID())
                            .collection(FOLLOWING_COLLECTION)
                            .document(currentUserData.getUser_id()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                viewProfileBinding.followBtn.setText("| Follow");
                                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.NOW_UNFOLLOWED_SUCCESS),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void followUser() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("followersList",FieldValue.arrayUnion(sessionPrefManager.getUserID()));
        data.put("follwersCount",FieldValue.increment(1));
        mDatabase.collection(USER_MAIN_COLLECTION)
                .document(currentUserData.getUser_id())
                .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                HashMap<String,Object> data=new HashMap<>();
                data.put("followingList",FieldValue.arrayUnion(currentUserData.getUser_id()));
                data.put("followingCount",FieldValue.increment(1));
                mDatabase.collection(USER_MAIN_COLLECTION)
                        .document(sessionPrefManager.getUserID())
                        .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });

        HashMap<String,Object> data2=new HashMap<>();
        data2.put("follower_data",userDataModel.getUserData());
        data2.put("follower_added_timestamp",FieldValue.serverTimestamp());
        mDatabase.collection(USER_MAIN_COLLECTION)
                .document(currentUserData.getUser_id())
                .collection(FOLLOWER_COLLECTION)
                .document(sessionPrefManager.getUserID())
                .set(data2, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                HashMap<String,Object> data=new HashMap<>();
                data.put("following_data",currentUserData);
                data.put("following_started_timestamp",FieldValue.serverTimestamp());
                mDatabase.collection(USER_MAIN_COLLECTION)
                        .document(sessionPrefManager.getUserID())
                        .collection(FOLLOWING_COLLECTION)
                        .document(currentUserData.getUser_id())
                        .set(data,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            currentUserData.getFollowersList().add(sessionPrefManager.getUserID());
                            viewProfileBinding.followBtn.setText("| Unfollow");
                            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.STARTED_FOLLOWING_SUCCESS),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void getOtherProfileData() {
            mDatabase.collection(USER_MAIN_COLLECTION)
                    .document(otherUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()){
                            updateUserProfile(task.getResult().toObject(UserModel.class));
                        }
                    }
                }
            });
    }




    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (sessionPrefManager.getUserID().equalsIgnoreCase(otherUserId)){
            inflater.inflate(R.menu.profile_menu_options,menu);
        }else {
            inflater.inflate(R.menu.other_prof_menu_options,menu);
        }
        this.menuContext = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.kahoo_at:
                Log.e("ksdjfklsfjslk","this is user kahoo handel "+currentUserData.getUser_kahoo_handel());
                Bundle bundle =new Bundle();
                bundle.putString("unique_handel",currentUserData.getUnique_user_id());
                navController.navigate(R.id.action_viewProfileFragment_to_addnewkahoofragment,bundle);
                break;

            case R.id.mute_at:

                break;

            case R.id.block_at:

                break;

            case R.id.report_at:

                break;

            case R.id.edit_profile :
                if (isOtherProfile){
                    navController.navigate(R.id.action_viewProfileFragment_to_editUserProfileFragment);
                }else {
                    navController.navigate(R.id.action_kahooMainFragment_to_editUserProfileFragment);
                }

                Log.e("kjhdskfsf","this edit profile ");
                break;
            case R.id.share_profile :
                Log.e("kjhdskfsf","this is edit profile ");
                shareUserProfile();
                break;
            case R.id.report :
                Log.e("kjhdskfsf","this is logout ");
                break;
            case R.id.logout :
                Log.e("kjhdskfsf","this is logout ");
                logoutConfirmDialog(getActivity());
                break;
            default:
                return true;
        }
        return true;
    }




    private void shareUserProfile() {
        Bitmap image=loadBitmapFromView(viewProfileBinding.parentView);
        if (image != null) {
            File saveFile = ScreenshotUtils.getMainDirectoryName(getActivity());
            File file = ScreenshotUtils.store(image, "screenshot" + ".jpg", saveFile);
            shareScreenshot(file,currentUserData);
        } else
            shareScreenshot(null,currentUserData);
    }

    private void shareScreenshot(File file,UserModel userModel) {
        String appShareableLink;
        try {
            appShareableLink = "" + Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
        } catch (android.content.ActivityNotFoundException anfe) {
            appShareableLink = "" + Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Kahoo Bharat App - The Indian Tweet App");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Download Kahoo Bharat Indian Micro-blogging app from play store." +
                "\n\nJoin the Kahoo Bharat App and start contributing the indian voice all over the world\n\n" +
                "Connect " +userModel.getUser_kahoo_handel()+" only on Kahoo Bharat Indian App\n\n"+
                "Hit the below link to download the app.\n"+appShareableLink);
        if (file!=null){
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getActivity(), getString(R.string.camera_file_provider_name),
                    file));
        }else {
            intent.setType("text/plain");
        }
        startActivity(Intent.createChooser(intent, "Share With"));

    }


    private void updateUserProfile(UserModel dataUser) {
        userDataModel=com.kaho.app.Data.Models.SingltonUserDataModel.getInstance();

        Log.e("sdflkfsffss","current user session id "+sessionPrefManager.getUserID()
                +" database user id "+dataUser.getUser_id());
        if (!sessionPrefManager.getUserID().equalsIgnoreCase(dataUser.getUser_id())){
            menuContext.findItem(R.id.kahoo_at).setTitle("Kahoo "+dataUser.getUser_kahoo_handel());
            menuContext.findItem(R.id.mute_at).setTitle("Mute "+dataUser.getUser_kahoo_handel());
            menuContext.findItem(R.id.block_at).setTitle("Block "+dataUser.getUser_kahoo_handel());
            menuContext.findItem(R.id.report_at).setTitle("Report "+dataUser.getUser_kahoo_handel());

            viewProfileBinding.followBtn.setVisibility(View.VISIBLE);
            viewProfileBinding.messageBtn.setVisibility(View.VISIBLE);

            if (dataUser.getFollowersList().contains(sessionPrefManager.getUserID())){
                viewProfileBinding.followBtn.setText("| Unfollow");
            }else {
                viewProfileBinding.followBtn.setText("| Follow");
            }

        }else {
            viewProfileBinding.followBtn.setVisibility(View.GONE);
            viewProfileBinding.messageBtn.setVisibility(View.GONE);
        }


        currentUserData=dataUser;
        viewProfileBinding.userAbout.setText(dataUser.getUser_about());
        viewProfileBinding.tvUserName.setText(dataUser.getUser_name());
        viewProfileBinding.followerCount.setText(""+dataUser.getFollwersCount());
        viewProfileBinding.followingCount.setText(""+dataUser.getFollowingCount());
        viewProfileBinding.kahooCount.setText(""+dataUser.getKahooCount());
        viewProfileBinding.userKahooHandel.setText(""+dataUser.getUser_kahoo_handel());


        Log.e("sdfkjdfsf","setting profile "+dataUser.getUser_id());

        FirebaseStorage storage= FirebaseStorage.getInstance();
        StorageReference storageRef;
        storageRef= storage.getReference().child("profileImage").child(dataUser.getUser_id()+"profile_image.jpg");
        Glide.with(getActivity())
                .load(storageRef)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(viewProfileBinding.userProfileImg);

        storageRef= storage.getReference().child("profileImage").child(dataUser.getUser_id()+"cover_image.jpg");
        Glide.with(getActivity())
                .load(storageRef)
                .placeholder(R.drawable.background_half_rect)
                .error(R.drawable.background_half_rect)
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(viewProfileBinding.coverImage);
    }
    SingltonUserDataModel userDataModel;
    private void setUpProfileData() {

        userDataModel=com.kaho.app.Data.Models.SingltonUserDataModel.getInstance();
        if (userDataModel.getUserData()!=null){
            currentUserData=userDataModel.getUserData();
            Log.e("dsfjdfkddf","inside fragment user name "+userDataModel.getUserData().getUser_name());
            viewProfileBinding.userAbout.setText(userDataModel.getUserData().getUser_about());
            viewProfileBinding.tvUserName.setText(userDataModel.getUserData().getUser_name());
            viewProfileBinding.followerCount.setText(""+userDataModel.getUserData().getFollwersCount());
            viewProfileBinding.followingCount.setText(""+userDataModel.getUserData().getFollowingCount());
            viewProfileBinding.kahooCount.setText(""+userDataModel.getUserData().getKahooCount());
            viewProfileBinding.userKahooHandel.setText(""+userDataModel.getUserData().getUser_kahoo_handel());


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
                    .into(viewProfileBinding.userProfileImg);

            storageRef= storage.getReference().child("profileImage").child(sessionPrefManager.getUserID()+"cover_image.jpg");
            Glide.with(getActivity())
                    .load(storageRef)
                    .placeholder(R.drawable.background_half_rect)
                    .error(R.drawable.background_half_rect)
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(viewProfileBinding.coverImage);
        }else {

            mDatabase.collection(USER_MAIN_COLLECTION)
                    .document(sessionPrefManager.getUserID())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot=task.getResult();
                        if (documentSnapshot.exists()){
                            SingltonUserDataModel userDataModel=SingltonUserDataModel.getInstance();

                            UserModel data =(UserModel)documentSnapshot.toObject(UserModel.class);
                            Log.e("dsfjdfkddf","user name "+data.getUser_name());
                            updateUserProfile(data);
                            Log.e("dsfjdfkddf","from singlton user name "+userDataModel.getUserData().getUser_name());
                        }
                    }
                }
            });
        }
    }


    private void logoutConfirmDialog(Activity context) {

        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View mView = context.getLayoutInflater().inflate(R.layout.onback_press_dialog, null);

            TextView message;
            TextView EXISTbTUN;
            TextView keep_writing;

            message = (TextView)mView.findViewById( R.id.message );
            EXISTbTUN = (TextView)mView.findViewById( R.id.cancle );
            keep_writing = (TextView)mView.findViewById( R.id.delete );

            message.setText(getResources().getString(R.string.ARE_YOU_SURE_TO_LOGOUT));
            EXISTbTUN.setText(getResources().getString(R.string.LOGOUT));
            keep_writing.setText(getResources().getString(R.string.CANCEL_CAPS));
            builder.setView(mView);
            final AlertDialog alertDialog = builder.create();

            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            EXISTbTUN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    sessionPrefManager.EraseAllData(context);
                    if (isOtherProfile){
                        navController.navigate(R.id.action_viewProfileFragment_to_landingFragment);
                    }else {
                        navController.navigate(R.id.action_kahooMainFragment_to_landingFragment);
                    }
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


}