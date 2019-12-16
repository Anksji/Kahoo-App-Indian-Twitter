package com.kaho.app.Data.Models;

public class FollowerModel {
    String follower_user_name,follower_user_img,follower_user_id,follower_user_about;
    String follower_unique_id;

    public FollowerModel() {
    }

    public String getFollower_unique_id() {
        return follower_unique_id;
    }

    public void setFollower_unique_id(String follower_unique_id) {
        this.follower_unique_id = follower_unique_id;
    }

    public String getFollower_user_name() {
        return follower_user_name;
    }

    public void setFollower_user_name(String follower_user_name) {
        this.follower_user_name = follower_user_name;
    }

    public String getFollower_user_img() {
        return follower_user_img;
    }

    public void setFollower_user_img(String follower_user_img) {
        this.follower_user_img = follower_user_img;
    }

    public String getFollower_user_id() {
        return follower_user_id;
    }

    public void setFollower_user_id(String follower_user_id) {
        this.follower_user_id = follower_user_id;
    }

    public String getFollower_user_about() {
        return follower_user_about;
    }

    public void setFollower_user_about(String follower_user_about) {
        this.follower_user_about = follower_user_about;
    }
}
