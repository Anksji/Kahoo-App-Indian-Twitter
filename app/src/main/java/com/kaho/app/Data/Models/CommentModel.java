package com.kaho.app.Data.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class CommentModel implements Serializable {

    private String parentId,cmtType;

    private String  user_name,user_profile_image,user_id,user_comment,comment_id;
    private Object comment_added_timestamp;

    private String replyToUserName,replyToUserId;
    private ArrayList<String> likedUserList=new ArrayList<>();
    private ArrayList<String>dislikedUserList=new ArrayList<>();

    private long c_like_count,c_dislike_count,c_reply_count;

    public CommentModel() {

    }

    public String getReplyToUserName() {
        return replyToUserName;
    }

    public void setReplyToUserName(String replyToUserName) {
        this.replyToUserName = replyToUserName;
    }

    public String getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(String replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCmtType() {
        return cmtType;
    }

    public void setCmtType(String cmtType) {
        this.cmtType = cmtType;
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
    public long getC_like_count() {
        return c_like_count;
    }

    public void setC_like_count(long c_like_count) {
        this.c_like_count = c_like_count;
    }

    public long getC_dislike_count() {
        return c_dislike_count;
    }

    public void setC_dislike_count(long c_dislike_count) {
        this.c_dislike_count = c_dislike_count;
    }

    public long getC_reply_count() {
        return c_reply_count;
    }

    public void setC_reply_count(long c_reply_count) {
        this.c_reply_count = c_reply_count;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profile_image() {
        return user_profile_image;
    }

    public void setUser_profile_image(String user_profile_image) {
        this.user_profile_image = user_profile_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_comment() {
        return user_comment;
    }

    public void setUser_comment(String user_comment) {
        this.user_comment = user_comment;
    }

    public Object getComment_added_timestamp() {
        return comment_added_timestamp;
    }

    public void setComment_added_timestamp(Date comment_added_timestamp) {
        this.comment_added_timestamp = comment_added_timestamp;
    }


}
