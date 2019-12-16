package com.kaho.app.Data.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class UserModel implements Serializable {

    String user_id,user_name,user_email,user_about,user_dob,
            user_phone_number,user_profile_pic_path,unique_user_id,user_kahoo_handel;

    long follwersCount,followingCount,kahooCount;
    ArrayList<String> followersList=new ArrayList<>();
    ArrayList<String> followingList=new ArrayList<>();
    String user_unique_id_array_ref;
    long user_unique_id_index;

    public UserModel() {
    }

    public String getUser_unique_id_array_ref() {
        return user_unique_id_array_ref;
    }

    public void setUser_unique_id_array_ref(String user_unique_id_array_ref) {
        this.user_unique_id_array_ref = user_unique_id_array_ref;
    }

    public long getUser_unique_id_index() {
        return user_unique_id_index;
    }

    public void setUser_unique_id_index(long user_unique_id_index) {
        this.user_unique_id_index = user_unique_id_index;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_about() {
        return user_about;
    }

    public void setUser_about(String user_about) {
        this.user_about = user_about;
    }

    public String getUser_dob() {
        return user_dob;
    }

    public void setUser_dob(String user_dob) {
        this.user_dob = user_dob;
    }

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public void setUser_phone_number(String user_phone_number) {
        this.user_phone_number = user_phone_number;
    }

    public String getUser_profile_pic_path() {
        return user_profile_pic_path;
    }

    public void setUser_profile_pic_path(String user_profile_pic_path) {
        this.user_profile_pic_path = user_profile_pic_path;
    }

    public String getUnique_user_id() {
        return unique_user_id;
    }

    public void setUnique_user_id(String unique_user_id) {
        this.unique_user_id = unique_user_id;
    }

    public String getUser_kahoo_handel() {
        return user_kahoo_handel;
    }

    public void setUser_kahoo_handel(String user_kahoo_handel) {
        this.user_kahoo_handel = user_kahoo_handel;
    }

    public long getFollwersCount() {
        return follwersCount;
    }

    public void setFollwersCount(long follwersCount) {
        this.follwersCount = follwersCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(long followingCount) {
        this.followingCount = followingCount;
    }

    public long getKahooCount() {
        return kahooCount;
    }

    public void setKahooCount(long kahooCount) {
        this.kahooCount = kahooCount;
    }

    public ArrayList<String> getFollowersList() {
        return followersList;
    }

    public void setFollowersList(ArrayList<String> followersList) {
        this.followersList = followersList;
    }

    public ArrayList<String> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(ArrayList<String> followingList) {
        this.followingList = followingList;
    }
}