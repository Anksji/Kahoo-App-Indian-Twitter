package com.kaho.app.Data.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ShoutPostModel implements Serializable {

    private String shoutPostId,shoutPostImgUri,shoutWordPost;
    private String shWordBkgroundColor;
    private boolean sh_isWhiteTheme;
    private String sh_added_user_id,sh_added_user_name,sh_added_user_image;
    private Date shoutAddedTimeStamp;
    private long shout_status;
    private String shout_status_message;
    private long sh_like_count,sh_dislike_count,sh_comment_count;

    private long number_of_reports;
    private String sh_video_url;
    private boolean sh_has_video,sh_has_image;

    private ArrayList<String>hash_tag_list=new ArrayList<>();
    private ArrayList<String> likedUserList=new ArrayList<>();
    private ArrayList<String>dislikedUserList=new ArrayList<>();
    private boolean isAds;
    private String sh_added_unique_user_id;

    public boolean isAds() {
        return isAds;
    }

    public void setAds(boolean ads) {
        isAds = ads;
    }

    public ShoutPostModel(boolean isAds) {
        this.isAds = isAds;
    }

    public boolean isSh_has_video() {
        return sh_has_video;
    }


    public boolean isSh_has_image() {
        return sh_has_image;
    }

    public ShoutPostModel() {
    }

    public ArrayList<String> getHash_tag_list() {
        return hash_tag_list;
    }

    public void setHash_tag_list(ArrayList<String> hash_tag_list) {
        this.hash_tag_list = hash_tag_list;
    }

    public String getShout_status_message() {
        return shout_status_message;
    }

    public void setShout_status_message(String shout_status_message) {
        this.shout_status_message = shout_status_message;
    }

    public long getNumber_of_reports() {
        return number_of_reports;
    }

    public void setNumber_of_reports(long number_of_reports) {
        this.number_of_reports = number_of_reports;
    }

    public long getShout_status() {
        return shout_status;
    }

    public void setShout_status(long shout_status) {
        this.shout_status = shout_status;
    }

    public String getSh_added_unique_user_id() {
        return sh_added_unique_user_id;
    }


    public String getShoutPostId() {
        return shoutPostId;
    }

    public void setShoutPostId(String shoutPostId) {
        this.shoutPostId = shoutPostId;
    }

    public String getShoutPostImgUri() {
        return shoutPostImgUri;
    }

    public void setShoutPostImgUri(String shoutPostImgUri) {
        this.shoutPostImgUri = shoutPostImgUri;
    }

    public boolean isSh_isWhiteTheme() {
        return sh_isWhiteTheme;
    }


    public String getShoutWordPost() {
        return shoutWordPost;
    }

    public void setShoutWordPost(String shoutWordPost) {
        this.shoutWordPost = shoutWordPost;
    }

    public String getShWordBkgroundColor() {
        return shWordBkgroundColor;
    }

    public void setShWordBkgroundColor(String shWordBkgroundColor) {
        this.shWordBkgroundColor = shWordBkgroundColor;
    }


    public String getSh_added_user_id() {
        return sh_added_user_id;
    }


    public Date getShoutAddedTimeStamp() {
        return shoutAddedTimeStamp;
    }

    public void setShoutAddedTimeStamp(Date shoutAddedTimeStamp) {
        this.shoutAddedTimeStamp = shoutAddedTimeStamp;
    }

    public long getSh_like_count() {
        return sh_like_count;
    }

    public long getSh_dislike_count() {
        return sh_dislike_count;
    }


    public long getSh_comment_count() {
        return sh_comment_count;
    }

    public ArrayList<String> getLikedUserList() {
        return likedUserList;
    }

    public void setLikedUserList(ArrayList<String> likedUserList) {
        this.likedUserList = likedUserList;
    }

    public ArrayList<String> getDislikedUserList() {
        return dislikedUserList;
    }

    public void setDislikedUserList(ArrayList<String> dislikedUserList) {
        this.dislikedUserList = dislikedUserList;
    }
}
