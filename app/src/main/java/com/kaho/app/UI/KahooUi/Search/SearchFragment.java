package com.kaho.app.UI.KahooUi.Search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.kaho.app.Adapters.HashListAdpters;
import com.kaho.app.Data.Models.HashTagsModel;
import com.kaho.app.Data.Models.SearchListModel;
import com.kaho.app.Data.Models.SearchModel;
import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;
import com.kaho.app.UI.KahooUi.ViewingKahoo.ViewKahooFrontFragment;
import com.kaho.app.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;


public class SearchFragment extends Fragment {

    private NavController navController;
    FragmentSearchBinding searchBinding;
    private SessionPrefManager sessionPrefManager;
    private FirebaseFirestore mDatabase;
    private HashListAdpters hashListAdpters;
    private String currentHashTagSelected="all";
    private ArrayList<HashTagsModel> hashTagList=new ArrayList<>();
    private ArrayList<HashTagsModel>shortedHashTagList=new ArrayList<>();
    private FirebaseDatabase myDb;
    private SearchListModel listModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        searchBinding=FragmentSearchBinding.inflate(inflater,container,false);
        return searchBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        navController= Navigation.findNavController(view);
        myDb = FirebaseDatabase.getInstance();
        mDatabase=FirebaseFirestore.getInstance();
        sessionPrefManager=new SessionPrefManager(getActivity());

        searchBinding.hashList.setHasFixedSize(true);
        hashListAdpters = new HashListAdpters(shortedHashTagList,getActivity(), SearchFragment.this);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        LinearLayoutManager _LR_Layout_manager= new GridLayoutManager(getContext(), 2);
        searchBinding.hashList.setLayoutManager(staggeredGridLayoutManager);
        searchBinding.hashList.setAdapter(hashListAdpters);
        getTrendingHashTags();

        editTextListners();
        clickListeners();

    }

    private void editTextListners() {


        searchBinding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length()>0){
                    filterSearchUsersList(s.toString().trim());
                }
            }
        });
    }

    private void filterSearchUsersList(String searchWord) {
        /*SearchModel searchModel=new SearchModel();
        SearchListModel userListSearch=new SearchListModel();
        if (userListSearch.getUsersList().indexOf(searchModel)>0){

        }
        HashMap<String,Object> data=new HashMap<>();
        data.put("usersList",userListSearch.getUsersList());
        data.put("update_time_stamp", FieldValue.serverTimestamp());*/
    }

    private void clickListeners() {
        searchBinding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"This feature is coming soon, be patient.",Toast.LENGTH_SHORT).show();
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

        /*getTimeInPast(1).addOnCompleteListener(new OnCompleteListener<String>() {
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
        });*/


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
    }

}