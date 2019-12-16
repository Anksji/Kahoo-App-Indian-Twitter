package com.kaho.app.Data.Models;

import java.util.Objects;

public class SearchModel {

    String userName,userId,userHandleId;

    public SearchModel(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        if(((SearchModel) o).userName.contains(userName)||
                ((SearchModel) o).userName.contains(userHandleId)||
                ((SearchModel) o).userName.equals(userId)){
            return true;
        }
        return false;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserHandleId() {
        return userHandleId;
    }

    public void setUserHandleId(String userHandleId) {
        this.userHandleId = userHandleId;
    }
}
