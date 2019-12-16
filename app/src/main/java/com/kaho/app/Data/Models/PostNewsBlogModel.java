package com.kaho.app.Data.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class PostNewsBlogModel implements Serializable {

    String postId,postTitle,postBannerImgUrl,postContent,postSource,postSourceUrl;
    long p_like_count,p_dislike_count,p_comment_count,post_current_rank;
    String postContent_2,postContent_3;

    Date postUpdateTimeStamp;

    boolean isSuggestion;

    String postType;
    String postLanguage;

    boolean isAds;

    ArrayList<String> postTags=new ArrayList<>();
    ArrayList<String>likedUserList=new ArrayList<>();
    ArrayList<String>dislikedUserList=new ArrayList<>();


    public boolean isSuggestion() {
        return isSuggestion;
    }

    public void setSuggestion(boolean suggestion) {
        isSuggestion = suggestion;
    }

    public PostNewsBlogModel(boolean isAds) {
        this.isAds = isAds;
    }

    public PostNewsBlogModel() {
    }

    public boolean isAds() {
        return isAds;
    }

    public String getPostContent_2() {
        return postContent_2;
    }

    public void setPostContent_2(String postContent_2) {
        this.postContent_2 = postContent_2;
    }

    public String getPostContent_3() {
        return postContent_3;
    }

    public void setPostContent_3(String postContent_3) {
        this.postContent_3 = postContent_3;
    }

    public void setAds(boolean ads) {
        isAds = ads;
    }

    public Date getPostUpdateTimeStamp() {
        return postUpdateTimeStamp;
    }

    public void setPostUpdateTimeStamp(Date postUpdateTimeStamp) {
        this.postUpdateTimeStamp = postUpdateTimeStamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostBannerImgUrl() {
        return postBannerImgUrl;
    }

    public void setPostBannerImgUrl(String postBannerImgUrl) {
        this.postBannerImgUrl = postBannerImgUrl;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostSource() {
        return postSource;
    }

    public void setPostSource(String postSource) {
        this.postSource = postSource;
    }

    public String getPostSourceUrl() {
        return postSourceUrl;
    }

    public void setPostSourceUrl(String postSourceUrl) {
        this.postSourceUrl = postSourceUrl;
    }

    public long getP_like_count() {
        return p_like_count;
    }

    public void setP_like_count(long p_like_count) {
        this.p_like_count = p_like_count;
    }

    public long getP_dislike_count() {
        return p_dislike_count;
    }

    public void setP_dislike_count(long p_dislike_count) {
        this.p_dislike_count = p_dislike_count;
    }

    public long getP_comment_count() {
        return p_comment_count;
    }

    public void setP_comment_count(long p_comment_count) {
        this.p_comment_count = p_comment_count;
    }

    public long getPost_current_rank() {
        return post_current_rank;
    }

    public void setPost_current_rank(long post_current_rank) {
        this.post_current_rank = post_current_rank;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostLanguage() {
        return postLanguage;
    }

    public void setPostLanguage(String postLanguage) {
        this.postLanguage = postLanguage;
    }

    public ArrayList<String> getPostTags() {
        return postTags;
    }

    public void setPostTags(ArrayList<String> postTags) {
        this.postTags = postTags;
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
