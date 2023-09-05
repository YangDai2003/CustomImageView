package com.yangdai.imageviewpro.interfaces;

import android.graphics.RectF;

/**
 * 当内部矩阵发生变化时，用于回调的接口定义。
 * @author 30415
 */
public interface OnMatrixChangeListener {

    /**
     * 当显示Drawable的矩阵发生变化时的回调。这可能是因为View的边界发生了变化，或者用户进行了缩放操作。
     *
     * @param rect - 显示Drawable新边界的矩形。
     */
    void onMatrixChanged(RectF rect);
}