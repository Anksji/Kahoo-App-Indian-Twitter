package com.kaho.app.Data.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class FollowersArrayList implements Serializable {

    ArrayList<String> followersList=new ArrayList<>();

    public ArrayList<String> getFollowersList() {
        return followersList;
    }

    public void setFollowersList(ArrayList<String> followersList) {
        this.followersList = followersList;
    }
}
