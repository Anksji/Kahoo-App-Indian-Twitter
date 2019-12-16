package com.kaho.app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaho.app.Data.Models.SingltonUserDataModel;
import com.kaho.app.Data.Models.UserModel;
import com.kaho.app.Session.SessionPrefManager;
import com.kaho.app.UI.Profile.EditUserProfileFragment;
import com.kaho.app.databinding.ActivityMainBinding;

import static com.kaho.app.Tools.FirebaseConstant.USER_MAIN_COLLECTION;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore mDatabase;
    SessionPrefManager sessionPrefManager;
    Fragment currentFragCtx;
    ActivityMainBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        sessionPrefManager=new SessionPrefManager(MainActivity.this);
        mDatabase=FirebaseFirestore.getInstance();

        if (sessionPrefManager.isLoggedIn()){
            mDatabase.collection(USER_MAIN_COLLECTION)
                    .document(sessionPrefManager.getUserID())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot=task.getResult();
                        if (documentSnapshot.exists()){
                            SingltonUserDataModel userDataModel=com.kaho.app.Data.Models.SingltonUserDataModel.getInstance();

                            UserModel data =(UserModel)documentSnapshot.toObject(UserModel.class);
                            Log.e("dsfjdfkddf","user name "+data.getUser_name());
                            userDataModel.setUserData(data);

                            Log.e("dsfjdfkddf","from singlton user name "+userDataModel.getUserData().getUser_name());
                        }
                    }
                }
            });
        }
    }
    public void initilizeFragment(Fragment fragment){
        currentFragCtx=fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentFragCtx instanceof EditUserProfileFragment){
            currentFragCtx.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void uniqueHandelClicked(String uniqueHandel){

    }
    public void hashTagClicked(String hashTag){

    }
}