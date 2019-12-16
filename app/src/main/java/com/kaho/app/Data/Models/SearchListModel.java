package com.kaho.app.Data.Models;

import java.util.List;

public class SearchListModel {

    public List<SearchModel>usersList;

    public SearchListModel() {
    }


    public List<SearchModel> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<SearchModel> usersList) {
        this.usersList = usersList;
    }
}
