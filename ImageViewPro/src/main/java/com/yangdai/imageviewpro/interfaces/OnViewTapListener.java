package com.yangdai.imageviewpro.interfaces;

import android.view.View;

/**
 * @author 30415
 */
public interface OnViewTapListener {

    /**
     * 用于接收用户在ImageView上点击的位置的回调。如果用户在视图的任何位置点击，将会收到回调，点击"空白处"将不会被忽略。
     *
     * @param view - 用户点击的视图。
     * @param x    - 用户从视图左侧点击的位置。
     * @param y    - 用户从视图顶部点击的位置。
     */
    void onViewTaped(View view, float x, float y);
}