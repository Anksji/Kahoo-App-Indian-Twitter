package com.kaho.app.Data.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ReplyModel implements Serializable {

    private String parentId,parentCmtType;
    private String parentCommentId;
    private String parentCommentUserName;
    private String parentCommentUserPic;
    private String parentUserComment;

    private String  user_name,user_profile_image,user_id,user_reply,reply_id;
    private Date reply_added_timestamp;
    private String replyToUserName,replyToUserId;

    private ArrayList<String> likedUserList=new ArrayList<>();
    private ArrayList<String>dislikedUserList=new ArrayList<>();

    private long r_like_count,r_dislike_count;

    public ReplyModel() {

    }

    public String getParentUserComment() {
        return parentUserComment;
    }

    public void setParentUserComment(String parentUserComment) {
        this.parentUserComment = parentUserComment;
    }

    public String getParentCmtType() {
        return parentCmtType;
    }

    public void setParentCmtType(String parentCmtType) {
        this.parentCmtType = parentCmtType;
    }

    public String getReplyToUserName() {
        return replyToUserName;
    }

    public void setReplyToUserName(String replyToUserName) {
        this.replyToUserName = replyToUserName;
    }

    public String getParentCommentUserName() {
        return parentCommentUserName;
    }

    public void setParentCommentUserName(String parentCommentUserName) {
        this.parentCommentUserName = parentCommentUserName;
    }

    public String getParentCommentUserPic() {
        return parentCommentUserPic;
    }

    public void setParentCommentUserPic(String parentCommentUserPic) {
        this.parentCommentUserPic = parentCommentUserPic;
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

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
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

    public String getUser_reply() {
        return user_reply;
    }

    public void setUser_reply(String user_reply) {
        this.user_reply = user_reply;
    }

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public Date getReply_added_timestamp() {
        return reply_added_timestamp;
    }

    public void setReply_added_timestamp(Date reply_added_timestamp) {
        this.reply_added_timestamp = reply_added_timestamp;
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

    public long getR_like_count() {
        return r_like_count;
    }

    public void setR_like_count(long r_like_count) {
        this.r_like_count = r_like_count;
    }

    public long getR_dislike_count() {
        return r_dislike_count;
    }

    public void setR_dislike_count(long r_dislike_count) {
        this.r_dislike_count = r_dislike_count;
    }

}
