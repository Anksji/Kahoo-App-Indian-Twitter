package com.kaho.app.Tools.Utills.Animations;

import android.animation.Animator;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.RequiresApi;

public class CustmAnimation {


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void circularHide(final View view, int collapsePointX, int collapsePointY, int duration,
                                    float initialRadius, float finalRadius) {

        // Radius is whichever dimension is the longest on our screen


        Log.e("sdfsdkfdsf","cx "+collapsePointX+" cy "+collapsePointY+
                " initial radius "+initialRadius+" final radius "+finalRadius);
        // Start circular animation
        Animator animator = ViewAnimationUtils.createCircularReveal(view, collapsePointX, collapsePointY, initialRadius, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(duration);
        animator.start();

        // Listen for completion and hide view
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void circleRevelAnime(final View view, int expansionPointX, int expansionPointY, int duration,
                                        float initialRadius, float finalRadius){

        // Start circular animation
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(view,
                expansionPointX, expansionPointY,
                initialRadius, finalRadius);
        circularReveal.setDuration(duration);

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        circularReveal.start();
    }


}
