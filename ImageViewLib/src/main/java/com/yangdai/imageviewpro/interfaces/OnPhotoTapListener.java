package com.yangdai.imageviewpro.interfaces;

import android.widget.ImageView;

/**
 * 照片内部点击监听器接口，用于监听照片点击事件的回调。
 * @author 30415
 */
public interface OnPhotoTapListener {

    /**
     * 用于接收用户在照片上点击的位置的回调。只有当用户点击实际的照片时，才会收到回调，点击"空白处"将被忽略。
     *
     * @param view ImageView 用户点击的视图。
     * @param x    用户从Drawable的左侧点击的位置，作为Drawable宽度的百分比。
     * @param y    用户从Drawable的顶部点击的位置，作为Drawable高度的百分比。
     */
    void onPhotoTaped(ImageView view, float x, float y);
}