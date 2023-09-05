package com.yangdai.imageviewpro.interfaces;

import android.view.MotionEvent;

/**
 * 单次快速滑动监听器接口，用于在ImageView被滑动时的回调。
 * @author 30415
 */
public interface OnSingleFlingListener {

    /**
     * 当用户在ImageView上进行单次快速滑动时调用的回调方法。如果用户在视图的任何位置进行滑动，将会收到回调。
     *
     * @param e1        用户首次触摸的MotionEvent。
     * @param e2        用户最后一次触摸的MotionEvent。
     * @param velocityX 用户水平快速滑动的距离。
     * @param velocityY 用户垂直快速滑动的距离。
     * @return 是否消费了快速滑动事件。
     */
    boolean onFlung(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
}