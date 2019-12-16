package com.kaho.app.Tools.Utills.HashTagAndAtRate;

import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;

public class ClickableForegroundColorSpan extends ClickableSpan {

    private OnHashTagClickListener mOnHashTagClickListener;

    public interface OnHashTagClickListener {
        void onHashTagClicked(String tagType,String hashTag);
    }

    private final int mColor;

    public ClickableForegroundColorSpan(@ColorInt int color, OnHashTagClickListener listener) {
        mColor = color;
        mOnHashTagClickListener = listener;

        if (mOnHashTagClickListener == null) {
            throw new RuntimeException("constructor, click listener not specified. Are you sure you need to use this class?");
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mColor);
    }

    @Override
    public void onClick(View widget) {
        CharSequence text = ((TextView) widget).getText();

        Log.e("erdsvfygsd","this is clicked text "+text);

        Spanned s = (Spanned) text;
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);

        Log.e("erdsvfygsd","this is spanded text "+s);
        Log.e("erdsvfygsd","this is start "+start+" this is end "+end);

        mOnHashTagClickListener.onHashTagClicked(text.subSequence(start, start+1).toString(),
                text.subSequence(start + 1/*skip "#" sign*/, end).toString());
    }
}