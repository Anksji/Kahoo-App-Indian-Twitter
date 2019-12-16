package com.kaho.app.Data.Models;

import java.util.ArrayList;

public class SingltonUserDataModel {

    private static SingltonUserDataModel userInstance = new SingltonUserDataModel();

    private void SingltonUserDataModel() {};

    public static synchronized SingltonUserDataModel getInstance() {
        if (userInstance == null) {
            userInstance = new SingltonUserDataModel();
        }
        return(userInstance);
    }

    UserModel userData;

    public UserModel getUserData() {
        return userData;
    }

    public void setUserData(UserModel userData) {
        this.userData = userData;
    }
}