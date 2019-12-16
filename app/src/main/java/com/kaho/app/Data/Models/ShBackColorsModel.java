package com.kaho.app.Data.Models;

import androidx.annotation.Nullable;

public class ShBackColorsModel {

    private int colorCode;
    private String colorId;
    private boolean isWhiteTheme;
    private boolean is_selected;

    public ShBackColorsModel(String colorId){
        this.colorId=colorId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (colorId.equalsIgnoreCase(((ShBackColorsModel)obj).colorId)){
            return true;
        }else {
            return false;
        }
    }

    public ShBackColorsModel(int colorCode, String colorId, boolean isWhiteTheme, boolean is_selected) {
        this.colorCode = colorCode;
        this.colorId = colorId;
        this.isWhiteTheme = isWhiteTheme;
        this.is_selected = is_selected;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public boolean isWhiteTheme() {
        return isWhiteTheme;
    }

    public void setWhiteTheme(boolean whiteTheme) {
        isWhiteTheme = whiteTheme;
    }

    public boolean isIs_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }
}
