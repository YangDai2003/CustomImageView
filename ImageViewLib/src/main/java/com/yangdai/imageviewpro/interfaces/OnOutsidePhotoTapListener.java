package com.yangdai.imageviewpro.interfaces;

import android.widget.ImageView;

/**
 * 照片外部点击监听器接口，用于监听照片外部点击事件的回调。
 *
 * @author 30415
 */
public interface OnOutsidePhotoTapListener {

    /**
     * 当照片外部被点击时调用的回调方法。
     *
     * @param imageView 被点击的ImageView
     */
    void onOutsidePhotoTaped(ImageView imageView);
}