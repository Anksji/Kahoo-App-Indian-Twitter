package com.kaho.app.Repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaho.app.Data.Models.KahooPostModel;
import com.kaho.app.Data.Models.SingltonUserDataModel;
import com.kaho.app.Data.Models.UserModel;

import java.util.List;

import static com.kaho.app.Tools.FirebaseConstant.USER_MAIN_COLLECTION;

public class UserRepos {

    private onSingleUserFetch singleUserFetch;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    public UserRepos(onSingleUserFetch onSingleUserFetch){
        this.singleUserFetch=onSingleUserFetch;
    }

    public void getUserDetail(String userId){
        firebaseFirestore.collection(USER_MAIN_COLLECTION)
                .document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        singleUserFetch.onSingleUserSuccess(task.getResult().toObject(UserModel.class));
                    }else {
                        singleUserFetch.onFetchError(task.getException());
                    }
                }else {
                    singleUserFetch.onFetchError(task.getException());
                }
            }
        });
    }

    public interface onSingleUserFetch{
        void onUserSuccess(List<UserModel> userList);
        void onSingleUserSuccess(UserModel userList);
        void onFetchError(Exception e);
    }
}
