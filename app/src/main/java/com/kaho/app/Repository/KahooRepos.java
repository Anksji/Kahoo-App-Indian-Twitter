package com.kaho.app.Repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaho.app.Data.Models.KahooPostModel;
import com.kaho.app.LiveData.KahooListLiveData;
import com.kaho.app.ViewModels.KahooViewModel;

import java.util.HashMap;
import java.util.List;

import static com.kaho.app.Tools.Constants.KAHOO_FETCH_LIMIT;
import static com.kaho.app.Tools.FirebaseConstant.KAHOO_ADDED_TIME;
import static com.kaho.app.Tools.FirebaseConstant.KAHOO_MAIN_COLLECTION;
import static com.kaho.app.Tools.FirebaseConstant.KAHOO_TAG_LIST;

public class KahooRepos implements KahooViewModel.KahooListRepository,
        KahooListLiveData.OnLastVisibleProductCallback,
        KahooListLiveData.OnLastProductReachedCallback {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    /*private Query query = firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
            .orderBy(KAHOO_ADDED_TIME, Query.Direction.DESCENDING).limit(KAHOO_FETCH_LIMIT);*/
    private DocumentSnapshot lastVisibleProduct;
    private boolean isLastProductReached;

    private HashMap<String,Query> databaseQuery=new HashMap<>();


    private onFirestoreTaskComplete firestoreTaskComplete;
    String userAuthId= FirebaseAuth.getInstance().getUid();


    Query kahooFeedQuery=firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
            .orderBy("kahooAddedTimeStamp", Query.Direction.DESCENDING);

    public KahooRepos() {
        Log.e("sdkjfsfsdf","initializing the repo");
    }

    public KahooRepos(onFirestoreTaskComplete firestoreTaskComplete){
        this.firestoreTaskComplete=firestoreTaskComplete;
    }
    public void getReplyKahooReplyList(String currentUserId){
        Query replyKahooQuery=firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                .whereEqualTo("has_reply_kaho",true)
                .whereEqualTo("kaho_added_user_id",currentUserId)
                .orderBy("kahooAddedTimeStamp", Query.Direction.DESCENDING);
        replyKahooQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e("jdsffsfd","fetch data successreply ");
                    firestoreTaskComplete.kahooReplyListDataAdded(task.getResult().toObjects(KahooPostModel.class));
                }else {
                    firestoreTaskComplete.onError(task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("jdsffsfd","this is error "+e.getMessage());
                firestoreTaskComplete.onError(e);
            }
        });
    }
    public void getDobaraKahooReplyList(String currentUserId){
        Query dobaraKahooQuery=firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                .whereEqualTo("has_dobara_kaho",true)
                .whereEqualTo("kaho_added_user_id",currentUserId)
                .orderBy("kahooAddedTimeStamp", Query.Direction.DESCENDING);
        dobaraKahooQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e("jdsffsfd","fetch data successreply ");
                    firestoreTaskComplete.kahooDobaraListDataAdded(task.getResult().toObjects(KahooPostModel.class));
                }else {
                    firestoreTaskComplete.onError(task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("jdsffsfd","this is error "+e.getMessage());
                firestoreTaskComplete.onError(e);
            }
        });
    }

    public void getUserKahooList(String currentUserId){
        Query myKahooQuery=firebaseFirestore.collection(KAHOO_MAIN_COLLECTION).
                whereEqualTo("kaho_added_user_id",currentUserId)
                .orderBy("kahooAddedTimeStamp", Query.Direction.DESCENDING)
                .limit(20);
        myKahooQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e("jdsffsfd","fetch data successreply ");
                    firestoreTaskComplete.kahooUsrListDataAdded(task.getResult().toObjects(KahooPostModel.class));
                }else {
                    firestoreTaskComplete.onError(task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("jdsffsfd","this is error "+e.getMessage());
                firestoreTaskComplete.onError(e);
            }
        });
    }
    public void getKahooFeedList(){

        /*kahooFeedQuery.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    firestoreTaskComplete.onError(error);
                }else {
                    firestoreTaskComplete.kahooListDataAdded(snapshot.toObject(KahooPostModel.class));
                }
            }
        });
        kahooFeedQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    firestoreTaskComplete.onError(error);
                }else {
                    firestoreTaskComplete.kahooListDataAdded(value.getDocumentChanges().to);
                }
            }
        });*/
        kahooFeedQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    firestoreTaskComplete.kahooListDataAdded(task.getResult().toObjects(KahooPostModel.class));
                }else {
                    firestoreTaskComplete.onError(task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("jdsffsfd","this is error "+e.getMessage());
                firestoreTaskComplete.onError(e);
            }
        });
    }

    public void getLikedKahooList(String currentUserId){
        Query kahooLikedQuery=firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                .whereArrayContains("likedUserIdsList",currentUserId)
                .orderBy("kahooAddedTimeStamp", Query.Direction.DESCENDING);
        kahooLikedQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    firestoreTaskComplete.kahooLikedListDataAdded(task.getResult().toObjects(KahooPostModel.class));
                }else {
                    firestoreTaskComplete.onError(task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("jdsffsfd","this is error "+e.getMessage());
                firestoreTaskComplete.onError(e);
            }
        });
    }

    public void getSingleKahooThread(String kahooId) {
        Query kahooLikedQuery=firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                .whereEqualTo("parent_kahoo_id",kahooId)
                .orderBy("kahooAddedTimeStamp", Query.Direction.DESCENDING);
        kahooLikedQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    firestoreTaskComplete.kahooSingleThread(task.getResult().toObjects(KahooPostModel.class));
                }else {
                    firestoreTaskComplete.onError(task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("jdsffsfd","this is error "+e.getMessage());
                firestoreTaskComplete.onError(e);
            }
        });
    }

    @Override
    public void setLastVisibleProduct(DocumentSnapshot lastVisibleProduct) {
        this.lastVisibleProduct = lastVisibleProduct;
    }

    @Override
    public void setLastProductReached(boolean isLastProductReached) {
        this.isLastProductReached = isLastProductReached;
    }

    private String prevSelectedTag="";

    @Override
    public KahooListLiveData getKahooListLiveData(String selectedTag,boolean isReload) {
        Log.e("sdkjfsfsdf","calling live kahoo selectedTag "+selectedTag+" prevSelectedTag "+prevSelectedTag);


        Log.e("sdkjfsfsdf","not reached at end return values calling live kahoo data");
        if (prevSelectedTag.equalsIgnoreCase(selectedTag)&&!isReload) {
            if (isLastProductReached) {
                Log.e("sdkjfsfsdf","reached at end return null calling live kahoo data");
                return null;
            }
            if (lastVisibleProduct != null) {
                databaseQuery.put("getKahooListLiveData",databaseQuery
                        .get("getKahooListLiveData").startAfter(lastVisibleProduct));
               // query = query.startAfter(lastVisibleProduct);
            }else {
                databaseQuery.put("getKahooListLiveData",firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                        .whereArrayContains(KAHOO_TAG_LIST, selectedTag)
                        .orderBy(KAHOO_ADDED_TIME, Query.Direction.DESCENDING).limit(KAHOO_FETCH_LIMIT));
               // query=databaseQuery.get("getKahooListLiveData");
            }
        }else {
            databaseQuery.put("getKahooListLiveData",firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                    .whereArrayContains(KAHOO_TAG_LIST, selectedTag)
                    .orderBy(KAHOO_ADDED_TIME, Query.Direction.DESCENDING).limit(KAHOO_FETCH_LIMIT));
            //query=databaseQuery.get("getKahooListLiveData");
        }
        prevSelectedTag=selectedTag;
        return new KahooListLiveData(databaseQuery.get("getKahooListLiveData"),"getKahooListLiveData", this, this);
    }

    @Override
    public KahooListLiveData getSingleKahooThreadLiveData(String kahooId,boolean isReload) {
        if (isLastProductReached&&!isReload) {
            return null;
        }
        if (lastVisibleProduct != null&&!isReload) {
            databaseQuery.put("getSingleKahooThreadLiveData",databaseQuery
                    .get("getSingleKahooThreadLiveData").startAfter(lastVisibleProduct));
            //query = query.startAfter(lastVisibleProduct);
        }else {
            databaseQuery.put("getSingleKahooThreadLiveData",firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                    .whereEqualTo("parent_kahoo_id",kahooId)
                    .orderBy(KAHOO_ADDED_TIME, Query.Direction.DESCENDING).limit(KAHOO_FETCH_LIMIT));
           // query=;
        }
        return new KahooListLiveData(databaseQuery.get("getSingleKahooThreadLiveData"),"getSingleKahooThreadLiveData", this,
                this);

    }

    @Override
    public KahooListLiveData getKahooHashTagThreadLiveData(String hashTag,boolean isReload) {
        if (isLastProductReached&&!isReload) {
            return null;
        }
        if (lastVisibleProduct != null&&!isReload) {
            databaseQuery.put("getKahooHashTagThreadLiveData",databaseQuery
                    .get("getKahooHashTagThreadLiveData").startAfter(lastVisibleProduct));
            //query = query.startAfter(lastVisibleProduct);
        }else {
            databaseQuery.put("getKahooHashTagThreadLiveData",firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                    .whereArrayContains("hash_tag_list",hashTag)
                    .orderBy(KAHOO_ADDED_TIME, Query.Direction.DESCENDING).limit(KAHOO_FETCH_LIMIT));
        }
        return new KahooListLiveData(databaseQuery.get("getKahooHashTagThreadLiveData"),"getKahooHashTagThreadLiveData", this, this);
    }

    @Override
    public KahooListLiveData getDobaraKahooListLiveData(String userId,boolean isReload) {
        if (isLastProductReached&&!isReload) {
            return null;
        }
        if (lastVisibleProduct != null&&!isReload) {
            databaseQuery.put("getDobaraKahooListLiveData",databaseQuery
                    .get("getDobaraKahooListLiveData").startAfter(lastVisibleProduct));
            //query = query.startAfter(lastVisibleProduct);
        }else {
            databaseQuery.put("getDobaraKahooListLiveData",firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                    .whereEqualTo("has_dobara_kaho",true)
                    .whereEqualTo("kaho_added_user_id",userId)
                    .orderBy(KAHOO_ADDED_TIME, Query.Direction.DESCENDING).limit(KAHOO_FETCH_LIMIT));
            //query=;
        }
        return new KahooListLiveData(databaseQuery.get("getDobaraKahooListLiveData"), "getDobaraKahooListLiveData",this, this);
        //return null;
    }

    @Override
    public KahooListLiveData getReplyKahooListLiveData(String userId,boolean isReload) {
        if (isLastProductReached&&!isReload) {
            return null;
        }
        if (lastVisibleProduct != null&&!isReload) {
            databaseQuery.put("getReplyKahooListLiveData",databaseQuery
                    .get("getReplyKahooListLiveData").startAfter(lastVisibleProduct));
            //query = query.startAfter(lastVisibleProduct);
        }else {
            databaseQuery.put("getReplyKahooListLiveData",firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                    .whereEqualTo("has_reply_kaho",true)
                    .whereEqualTo("kaho_added_user_id",userId)
                    .orderBy(KAHOO_ADDED_TIME, Query.Direction.DESCENDING).limit(KAHOO_FETCH_LIMIT));
            //query=;
        }
        return new KahooListLiveData(databaseQuery.get("getReplyKahooListLiveData"),"getReplyKahooListLiveData", this, this);
        //return null;
    }

    @Override
    public KahooListLiveData getLikedKahooListLiveData(String userId,boolean isReload) {
        if (isLastProductReached&&!isReload) {
            return null;
        }
        if (lastVisibleProduct != null&&!isReload) {
            databaseQuery.put("getLikedKahooListLiveData",databaseQuery
                    .get("getLikedKahooListLiveData").startAfter(lastVisibleProduct));
            //query = query.startAfter(lastVisibleProduct);
        }else {
            databaseQuery.put("getLikedKahooListLiveData",firebaseFirestore.collection(KAHOO_MAIN_COLLECTION)
                    .whereArrayContains("likedUserIdsList",userId)
                    .orderBy(KAHOO_ADDED_TIME, Query.Direction.DESCENDING).limit(KAHOO_FETCH_LIMIT));
            //query=;
        }
        return new KahooListLiveData(databaseQuery.get("getLikedKahooListLiveData"),"getLikedKahooListLiveData", this, this);
    }

    @Override
    public KahooListLiveData getUsrKahooListLiveData(String userId,boolean isReload) {
        if (isLastProductReached&&!isReload) {
            return null;
        }
        if (lastVisibleProduct != null&&!isReload) {
            databaseQuery.put("getUsrKahooListLiveData",databaseQuery
                    .get("getUsrKahooListLiveData").startAfter(lastVisibleProduct));
            //query = query.startAfter(lastVisibleProduct);
        }else {
            databaseQuery.put("getUsrKahooListLiveData",firebaseFirestore.collection(KAHOO_MAIN_COLLECTION).
                    whereEqualTo("kaho_added_user_id",userId)
                    .orderBy(KAHOO_ADDED_TIME, Query.Direction.DESCENDING).limit(KAHOO_FETCH_LIMIT));
            /*query=firebaseFirestore.collection(KAHOO_MAIN_COLLECTION).
                    whereEqualTo("kaho_added_user_id",userId)
                    .orderBy(KAHOO_ADDED_TIME, Query.Direction.DESCENDING).limit(KAHOO_FETCH_LIMIT);*/
        }
        return new KahooListLiveData(databaseQuery.get("getUsrKahooListLiveData"),"getUsrKahooListLiveData", this, this);
    }



    public interface onFirestoreTaskComplete{
        void kahooSingleThread(List<KahooPostModel> kahooList);
        void kahooDobaraListDataAdded(List<KahooPostModel> kahooList);
        void kahooReplyListDataAdded(List<KahooPostModel> kahooList);
        void kahooLikedListDataAdded(List<KahooPostModel> kahooList);
        void kahooUsrListDataAdded(List<KahooPostModel> kahooList);
        void kahooListDataAdded(List<KahooPostModel> kahooList);
        void onError(Exception e);
    }
}
