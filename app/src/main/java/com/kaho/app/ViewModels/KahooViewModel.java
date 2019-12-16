package com.kaho.app.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kaho.app.Data.Models.KahooPostModel;
import com.kaho.app.LiveData.KahooListLiveData;
import com.kaho.app.Repository.KahooRepos;

import java.util.List;


@SuppressWarnings("WeakerAccess")
public class KahooViewModel extends ViewModel {

    KahooListRepository kahooRepos= new KahooRepos();

     MutableLiveData<List<KahooPostModel>> kahooListModelData=new MutableLiveData<>();
     MutableLiveData<List<KahooPostModel>> kahooDobaraListModelData=new MutableLiveData<>();
     MutableLiveData<List<KahooPostModel>> kahooReplyListModelData=new MutableLiveData<>();
     MutableLiveData<List<KahooPostModel>> kahooLikedListModelData=new MutableLiveData<>();
     MutableLiveData<List<KahooPostModel>> kahooUsrListModelData=new MutableLiveData<>();
     MutableLiveData<List<KahooPostModel>> kahooSingleThreadData=new MutableLiveData<>();

    /*public LiveData<List<KahooPostModel>> getKahooSingleThreadData() {
        return kahooSingleThreadData;
    }
    public LiveData<List<KahooPostModel>> getKahooDobaraListModelData() {
        return kahooDobaraListModelData;
    }
    public LiveData<List<KahooPostModel>> getKahooReplyListModelData() {
        return kahooReplyListModelData;
    }
    public LiveData<List<KahooPostModel>> getKahooLikedListModelData() {
        return kahooLikedListModelData;
    }
    public LiveData<List<KahooPostModel>> getKahooUsrListModelData() {
        return kahooUsrListModelData;
    }

    public LiveData<List<KahooPostModel>> getKahooListModelData() {
        return kahooListModelData;
    }
*/
   /* public void getSingleKahooThread(String kahooId){
        //kahooRepos.getSingleKahooThread(kahooId);
    }
    public void getFeedKahooList(){
        //kahooRepos.getKahooFeedList();
    }
    public void getDobaraKahoFullList(String currentUserId){
        //kahooRepos.getDobaraKahooReplyList(currentUserId);
    }
    public void getReplyKahoFullList(String currentUserId){
        //kahooRepos.getReplyKahooReplyList(currentUserId);
    }

    public void getLikedKahooList(String currentUserId){
        //kahooRepos.getLikedKahooList(currentUserId);
    }

    public void getUserKahooList(String currentUserId){
        //kahooRepos.getUserKahooList(currentUserId);
    }*/

    public KahooViewModel(){

    }

    /*@Override
    public void kahooSingleThread(List<KahooPostModel> kahooList) {
        kahooSingleThreadData.setValue(kahooList);
    }

    @Override
    public void kahooDobaraListDataAdded(List<KahooPostModel> kahooList) {
        kahooDobaraListModelData.setValue(kahooList);
    }

    @Override
    public void kahooReplyListDataAdded(List<KahooPostModel> kahooList) {
        kahooReplyListModelData.setValue(kahooList);
    }

    @Override
    public void kahooLikedListDataAdded(List<KahooPostModel> kahooList) {
        kahooLikedListModelData.setValue(kahooList);
    }

    @Override
    public void kahooUsrListDataAdded(List<KahooPostModel> kahooList) {
        kahooUsrListModelData.setValue(kahooList);
    }

    @Override
    public void kahooListDataAdded(List<KahooPostModel> kahooList) {
    kahooListModelData.setValue(kahooList);
    }


    @Override
    public void onError(Exception e) {

    }*/

    public KahooListLiveData getKahooFeedListLiveData(String selectedTags,boolean isreload) {
        return kahooRepos.getKahooListLiveData(selectedTags, isreload);
    }

    public KahooListLiveData getDobaraKahooFeedListLiveData(String userId,boolean isReload) {
        return kahooRepos.getDobaraKahooListLiveData(userId,isReload);
    }
    public KahooListLiveData getReplyKahooFeedListLiveData(String userId,boolean isReload) {
        return kahooRepos.getReplyKahooListLiveData(userId,isReload);
    }
    public KahooListLiveData getLikedKahooFeedListLiveData(String userId,boolean isReload) {
        return kahooRepos.getLikedKahooListLiveData(userId,isReload);
    }
    public KahooListLiveData getUsrKahooFeedListLiveData(String userId,boolean isReload) {
        return kahooRepos.getUsrKahooListLiveData(userId,isReload);
    }
    public KahooListLiveData getSingleKahooThreadListLiveData(String kahooId,boolean isReload) {
        return kahooRepos.getSingleKahooThreadLiveData(kahooId,isReload);
    }
    public KahooListLiveData getHashTagThreadListLiveData(String hashTag,boolean isReload) {
        return kahooRepos.getKahooHashTagThreadLiveData(hashTag,isReload);
    }

   public interface KahooListRepository {
        KahooListLiveData getKahooListLiveData(String selectedTag,boolean isReload);
       KahooListLiveData getDobaraKahooListLiveData(String userId,boolean isReload);
       KahooListLiveData getReplyKahooListLiveData(String userId,boolean isReload);
       KahooListLiveData getLikedKahooListLiveData(String userId,boolean isReload);
       KahooListLiveData getUsrKahooListLiveData(String userId,boolean isReload);
       KahooListLiveData getSingleKahooThreadLiveData(String kahooId,boolean isReload);
       KahooListLiveData getKahooHashTagThreadLiveData(String kahooId,boolean isReload);
    }
}
