/*
 * File: UiUtil.java
 * Purpose: Apply global backgrounds and small UI helpers.
 */

package com.nmims.madproj.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public final class UiUtil {

    private UiUtil() {}

    public static void applyAmbientBackground(Activity activity) {
        try {
            View content = activity.findViewById(android.R.id.content);
            if (content instanceof ViewGroup) {
                ViewGroup root = (ViewGroup) content;
                int resId = activity.getResources().getIdentifier("Aurora_Mac", "drawable", activity.getPackageName());
                if (resId != 0) {
                    root.setBackgroundResource(resId);
                } else {
                    root.setBackgroundResource(com.nmims.madproj.R.drawable.bg_gradient);
                }
            }
        } catch (Throwable t) {
            // Fallback if OOM or resource error
            View content = activity.findViewById(android.R.id.content);
            if (content instanceof ViewGroup) {
                ((ViewGroup) content).setBackgroundResource(com.nmims.madproj.R.drawable.bg_gradient);
            }
        }
    }
}


