package com.kaho.app.LiveData;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaho.app.Data.Models.KahooPostModel;
import com.kaho.app.Operations.Operation;
import com.kaho.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kaho.app.Tools.Constants.KAHOO_FETCH_LIMIT;

@SuppressWarnings("ConstantConditions")
public class KahooListLiveData extends LiveData<Operation> implements EventListener<QuerySnapshot> {
    private Query query;
   // private static HashMap<String,ListenerRegistration>listenerRegistrationHashMap=new HashMap<>();
    private ListenerRegistration listenerRegistration;
    private OnLastVisibleProductCallback onLastVisibleProductCallback;
    private OnLastProductReachedCallback onLastProductReachedCallback;
    //private static  HashMap<String,Query> dataQuery=new HashMap<>();

    public KahooListLiveData(Query query,String queryName, OnLastVisibleProductCallback onLastVisibleProductCallback, OnLastProductReachedCallback onLastProductReachedCallback) {
        this.query = query;
        //dataQuery.put(queryName,query);
        this.onLastVisibleProductCallback = onLastVisibleProductCallback;
        this.onLastProductReachedCallback = onLastProductReachedCallback;
        this.onLastProductReachedCallback.setLastProductReached(false);
        this.onLastVisibleProductCallback.setLastVisibleProduct(null);

        Log.d("sdkjfsfsdf","calling live data");
    }

    @Override
    protected void onActive() {
        listenerRegistration = query.addSnapshotListener(this);
        /*Log.e("dkhfksfsssdf","onActive is called dataQuery size "+dataQuery.size());
        for (Map.Entry<String, Query> entry : dataQuery.entrySet()) {
            Query query = entry.getValue();
            // Do things with the list
            //
            listenerRegistrationHashMap.put(entry.getKey(),query.addSnapshotListener(this));
        }*/
    }

    @Override
    protected void onInactive() {
        //Log.e("dkhfksfsssdf","on inactive is called dataQuery size "+dataQuery.size());
        listenerRegistration.remove();
        /*for (Map.Entry<String, Query> entry : dataQuery.entrySet()) {
            //listenerRegistration = query.addSnapshotListener(this);
            listenerRegistrationHashMap.get(entry.getKey()).remove();
        }*/
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.e("kjhdsff","this is error "+e.getMessage());
            return;
        }

        for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
            switch (documentChange.getType()) {
                case ADDED:
                    KahooPostModel addedProduct = documentChange.getDocument().toObject(KahooPostModel.class);
                    Operation addOperation = new Operation(addedProduct, R.string.added);
                    setValue(addOperation);
                    break;

                case MODIFIED:
                    KahooPostModel modifiedProduct = documentChange.getDocument().toObject(KahooPostModel.class);
                    Operation modifyOperation = new Operation(modifiedProduct, R.string.modified);
                    setValue(modifyOperation);
                    break;

                case REMOVED:
                    KahooPostModel removedProduct = documentChange.getDocument().toObject(KahooPostModel.class);
                    Operation removeOperation = new Operation(removedProduct, R.string.removed);
                    setValue(removeOperation);
            }
        }

        int querySnapshotSize = querySnapshot.size();
        if (querySnapshotSize < KAHOO_FETCH_LIMIT) {
            onLastProductReachedCallback.setLastProductReached(true);
        } else {
            DocumentSnapshot lastVisibleProduct = querySnapshot.getDocuments().get(querySnapshotSize - 1);
            onLastVisibleProductCallback.setLastVisibleProduct(lastVisibleProduct);
        }
    }

    public interface OnLastVisibleProductCallback {
        void setLastVisibleProduct(DocumentSnapshot lastVisibleProduct);
    }

    public interface OnLastProductReachedCallback {
        void setLastProductReached(boolean isLastProductReached);
    }
}