package com.yangdai.imageviewpro.interfaces;

/**
 * 缩放变化监听器接口，用于监听附加的ImageView缩放发生变化时的回调。
 * @author 30415
 */
public interface OnScaleChangeListener {

    /**
     * 当缩放发生变化时调用的回调方法。
     *
     * @param scaleFactor 缩放因子（小于1表示缩小，大于1表示放大）
     * @param focusX      焦点的X坐标位置
     * @param focusY      焦点的Y坐标位置
     */
    void onScaleChanged(float scaleFactor, float focusX, float focusY);
}