package com.kaho.app.Data.Models;

import java.io.Serializable;
import java.util.Objects;

public class HashTagsModel implements Serializable, Comparable<HashTagsModel> {

    String hashTag;
    long last_used_time;
    long used_no_of_times;
    boolean isSelected;

    public HashTagsModel() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashTagsModel tagsModel = (HashTagsModel) o;
        return Objects.equals(hashTag, tagsModel.hashTag);
    }

    @Override
    public int compareTo(HashTagsModel f) {

        if (used_no_of_times > f.used_no_of_times) {
            return 1;
        }
        else if (used_no_of_times <  f.used_no_of_times) {
            return -1;
        }
        else {
            return 0;
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(hashTag, last_used_time, used_no_of_times, isSelected);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public long getLast_used_time() {
        return last_used_time;
    }

    public void setLast_used_time(long last_used_time) {
        this.last_used_time = last_used_time;
    }


    public long getUsed_no_of_times() {
        return used_no_of_times;
    }

    public void setUsed_no_of_times(long used_no_of_times) {
        this.used_no_of_times = used_no_of_times;
    }

}
