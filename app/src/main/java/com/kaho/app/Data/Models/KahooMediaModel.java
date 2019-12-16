package com.kaho.app.Data.Models;

import java.util.Objects;

public class KahooMediaModel {

    String localUrl;
    String localImageName;
    boolean isVideo=false;
    boolean isUploaded=false;

    public KahooMediaModel() {
    }

    public KahooMediaModel(String localUrl,String localImageName, boolean isVideo) {
        this.localUrl = localUrl;
        this.isVideo = isVideo;
        this.localImageName=localImageName;
        isUploaded=true;
    }

    public KahooMediaModel(String localUrl) {
        this.localUrl = localUrl;
    }
    public KahooMediaModel(String localUrl,boolean isVideo) {
        this.localUrl = localUrl;
        this.isVideo=isVideo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KahooMediaModel localMedia = (KahooMediaModel) o;
        return Objects.equals(localUrl, localMedia.localUrl);
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public String getLocalImageName() {
        return localImageName;
    }

    public void setLocalImageName(String localImageName) {
        this.localImageName = localImageName;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}
