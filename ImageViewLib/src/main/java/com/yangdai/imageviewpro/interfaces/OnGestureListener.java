package com.yangdai.imageviewpro.interfaces;

/**
 * 手势监听器接口，用于监听手势操作的回调。
 *
 * @author 30415
 */
public interface OnGestureListener {

    /**
     * 当发生拖动操作时调用的回调方法。
     *
     * @param dx x轴上的拖动距离
     * @param dy y轴上的拖动距离
     */
    void onDrag(float dx, float dy);

    /**
     * 当发生快速滑动操作时调用的回调方法。
     *
     * @param startX    滑动起始点的x坐标
     * @param startY    滑动起始点的y坐标
     * @param velocityX x轴上的滑动速度
     * @param velocityY y轴上的滑动速度
     */
    void onFling(float startX, float startY, float velocityX, float velocityY);

    /**
     * 当发生缩放操作时调用的回调方法。
     *
     * @param scaleFactor 缩放因子
     * @param focusX      缩放焦点的x坐标
     * @param focusY      缩放焦点的y坐标
     */
    void onScale(float scaleFactor, float focusX, float focusY);

}