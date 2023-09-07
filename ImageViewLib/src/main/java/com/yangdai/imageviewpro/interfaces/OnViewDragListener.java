package com.yangdai.imageviewpro.interfaces;

/**
 * 照片拖动监听器接口，用于在照片发生拖动事件时的回调。
 *
 * @author 30415
 */
public interface OnViewDragListener {

    /**
     * 当照片发生拖动事件时调用的回调方法。当用户进行缩放操作时，此回调不会被调用。
     *
     * @param dx x方向上的坐标变化量
     * @param dy y方向上的坐标变化量
     */
    void onDragged(float dx, float dy);
}