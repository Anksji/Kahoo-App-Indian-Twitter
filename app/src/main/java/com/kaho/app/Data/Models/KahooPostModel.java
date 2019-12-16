package com.kaho.app.Data.Models;

import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class KahooPostModel implements Serializable {


    String kahooPostId,kahoo_text_content,kaho_added_user_id,kaho_added_user_name,
            kaho_added_user_handel_id,kaho_added_user_image,
            kahoo_status_message,parent_kahoo_id;
    boolean kaho_isWhiteTheme,has_dobara_kaho,has_reply_kaho;

    boolean isAdsType;

    long dobara_kahoo_count,kahoo_reply_count,kaho_like_count,kahoo_share_count,kahoo_report_count;
    private Date kahooAddedTimeStamp,kahooUpdatedTimeStamp;

    KahooPostModel other_kaho_data;

    private ArrayList<String> hash_tag_list=new ArrayList<>();
    private ArrayList<String> likedUserIdsList=new ArrayList<>();
    private ArrayList<String>kahoo_tag_people_list=new ArrayList<>();
    private ArrayList<String>kaho_media_list=new ArrayList<>();
    private ArrayList<String>kahoo_owner_id=new ArrayList<>();

    private ArrayList<String>dobaraKahooUidsList=new ArrayList<>();
    private ArrayList<String>repliedKahooUidsList=new ArrayList<>();


    public KahooPostModel() {
    }

    public boolean isAdsType() {
        return isAdsType;
    }

    public void setAdsType(boolean adsType) {
        isAdsType = adsType;
    }

    public long getKahoo_report_count() {
        return kahoo_report_count;
    }

    public void setKahoo_report_count(long kahoo_report_count) {
        this.kahoo_report_count = kahoo_report_count;
    }

    public boolean isKaho_isWhiteTheme() {
        return kaho_isWhiteTheme;
    }

    public void setKaho_isWhiteTheme(boolean kaho_isWhiteTheme) {
        this.kaho_isWhiteTheme = kaho_isWhiteTheme;
    }

    public long getKahoo_share_count() {
        return kahoo_share_count;
    }

    public void setKahoo_share_count(long kahoo_share_count) {
        this.kahoo_share_count = kahoo_share_count;
    }

    public boolean isHas_reply_kaho() {
        return has_reply_kaho;
    }

    public void setHas_reply_kaho(boolean has_reply_kaho) {
        this.has_reply_kaho = has_reply_kaho;
    }

    public String getParent_kahoo_id() {
        return parent_kahoo_id;
    }

    public void setParent_kahoo_id(String parent_kahoo_id) {
        this.parent_kahoo_id = parent_kahoo_id;
    }

    public boolean isHas_dobara_kaho() {
        return has_dobara_kaho;
    }

    public void setHas_dobara_kaho(boolean has_dobara_kaho) {
        this.has_dobara_kaho = has_dobara_kaho;
    }

    public KahooPostModel getOther_kaho_data() {
        return other_kaho_data;
    }

    public void setOther_kaho_data(KahooPostModel other_kaho_data) {
        this.other_kaho_data = other_kaho_data;
    }

    public String getKahooPostId() {
        return kahooPostId;
    }

    public void setKahooPostId(String kahooPostId) {
        this.kahooPostId = kahooPostId;
    }


    public String getKahoo_text_content() {
        return kahoo_text_content;
    }

    public void setKahoo_text_content(String kahoo_text_content) {
        this.kahoo_text_content = kahoo_text_content;
    }

    public String getKaho_added_user_id() {
        return kaho_added_user_id;
    }

    public void setKaho_added_user_id(String kaho_added_user_id) {
        this.kaho_added_user_id = kaho_added_user_id;
    }

    public String getKaho_added_user_name() {
        return kaho_added_user_name;
    }

    public void setKaho_added_user_name(String kaho_added_user_name) {
        this.kaho_added_user_name = kaho_added_user_name;
    }

    public String getKaho_added_user_handel_id() {
        return kaho_added_user_handel_id;
    }

    public void setKaho_added_user_handel_id(String kaho_added_user_handel_id) {
        this.kaho_added_user_handel_id = kaho_added_user_handel_id;
    }

    public String getKaho_added_user_image() {
        return kaho_added_user_image;
    }

    public void setKaho_added_user_image(String kaho_added_user_image) {
        this.kaho_added_user_image = kaho_added_user_image;
    }

    public String getKahoo_status_message() {
        return kahoo_status_message;
    }

    public void setKahoo_status_message(String kahoo_status_message) {
        this.kahoo_status_message = kahoo_status_message;
    }

    public long getKaho_like_count() {
        return kaho_like_count;
    }

    public void setKaho_like_count(long kaho_like_count) {
        this.kaho_like_count = kaho_like_count;
    }

    public long getDobara_kahoo_count() {
        return dobara_kahoo_count;
    }

    public void setDobara_kahoo_count(long dobara_kahoo_count) {
        this.dobara_kahoo_count = dobara_kahoo_count;
    }

    public long getKahoo_reply_count() {
        return kahoo_reply_count;
    }

    public void setKahoo_reply_count(long kahoo_reply_count) {
        this.kahoo_reply_count = kahoo_reply_count;
    }

    public Date getKahooAddedTimeStamp() {
        return kahooAddedTimeStamp;
    }

    public void setKahooAddedTimeStamp(Date kahooAddedTimeStamp) {
        this.kahooAddedTimeStamp = kahooAddedTimeStamp;
    }

    public Date getKahooUpdatedTimeStamp() {
        return kahooUpdatedTimeStamp;
    }

    public void setKahooUpdatedTimeStamp(Date kahooUpdatedTimeStamp) {
        this.kahooUpdatedTimeStamp = kahooUpdatedTimeStamp;
    }

    public ArrayList<String> getHash_tag_list() {
        return hash_tag_list;
    }

    public void setHash_tag_list(ArrayList<String> hash_tag_list) {
        this.hash_tag_list = hash_tag_list;
    }

    public ArrayList<String> getLikedUserIdsList() {
        return likedUserIdsList;
    }

    public void setLikedUserIdsList(ArrayList<String> likedUserIdsList) {
        this.likedUserIdsList = likedUserIdsList;
    }

    public ArrayList<String> getDobaraKahooUidsList() {
        return dobaraKahooUidsList;
    }

    public void setDobaraKahooUidsList(ArrayList<String> dobaraKahooUidsList) {
        this.dobaraKahooUidsList = dobaraKahooUidsList;
    }

    public ArrayList<String> getRepliedKahooUidsList() {
        return repliedKahooUidsList;
    }

    public void setRepliedKahooUidsList(ArrayList<String> repliedKahooUidsList) {
        this.repliedKahooUidsList = repliedKahooUidsList;
    }

    public ArrayList<String> getKahoo_tag_people_list() {
        return kahoo_tag_people_list;
    }

    public void setKahoo_tag_people_list(ArrayList<String> kahoo_tag_people_list) {
        this.kahoo_tag_people_list = kahoo_tag_people_list;
    }

    public ArrayList<String> getKaho_media_list() {
        return kaho_media_list;
    }

    public void setKaho_media_list(ArrayList<String> kaho_media_list) {
        this.kaho_media_list = kaho_media_list;
    }

    public ArrayList<String> getKahoo_owner_id() {
        return kahoo_owner_id;
    }

    public void setKahoo_owner_id(ArrayList<String> kahoo_owner_id) {
        this.kahoo_owner_id = kahoo_owner_id;
    }
}
