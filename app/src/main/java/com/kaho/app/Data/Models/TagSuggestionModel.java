package com.kaho.app.Data.Models;

public class TagSuggestionModel {

    String hashTag;
    String userUniqueAtRateId;
    String tagType;

    public TagSuggestionModel() {

    }

    public TagSuggestionModel(String hashTag, String userUniqueAtRateId, String tagType) {
        this.hashTag = hashTag;
        this.userUniqueAtRateId = userUniqueAtRateId;
        this.tagType = tagType;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public String getUserUniqueAtRateId() {
        return userUniqueAtRateId;
    }

    public void setUserUniqueAtRateId(String userUniqueAtRateId) {
        this.userUniqueAtRateId = userUniqueAtRateId;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }
}
