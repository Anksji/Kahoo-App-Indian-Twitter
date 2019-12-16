package com.kaho.app.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kaho.app.Data.Models.UserModel;
import com.kaho.app.Repository.UserRepos;

import java.util.List;


public class UserViewModel extends ViewModel implements UserRepos.onSingleUserFetch {

    UserRepos userRepos=new UserRepos(this);

    MutableLiveData<List<UserModel>> userLiveData=new MutableLiveData<>();


    @Override
    public void onUserSuccess(List<UserModel> userList) {

    }

    @Override
    public void onSingleUserSuccess(UserModel userList) {

    }

    @Override
    public void onFetchError(Exception e) {

    }
}
