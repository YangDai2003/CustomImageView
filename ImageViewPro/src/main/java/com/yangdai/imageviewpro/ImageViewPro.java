package com.yangdai.imageviewpro;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

import com.yangdai.imageviewpro.interfaces.OnMatrixChangeListener;
import com.yangdai.imageviewpro.interfaces.OnOutsidePhotoTapListener;
import com.yangdai.imageviewpro.interfaces.OnPhotoTapListener;
import com.yangdai.imageviewpro.interfaces.OnScaleChangeListener;
import com.yangdai.imageviewpro.interfaces.OnSingleFlingListener;
import com.yangdai.imageviewpro.interfaces.OnViewDragListener;
import com.yangdai.imageviewpro.interfaces.OnViewTapListener;

public class ImageViewPro extends AppCompatImageView {

    private ImageViewProController controller;
    private ScaleType pendingScaleType;

    public ImageViewPro(Context context) {
        this(context, null);
    }

    public ImageViewPro(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public ImageViewPro(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init();
    }

    private void init() {
        controller = new ImageViewProController(this);
        // 总是使用 Matrix 缩放类型，尽管可以通过 controller 更改为其他缩放类型
        super.setScaleType(ScaleType.MATRIX);
        // 应用之前应用的缩放类型
        if (pendingScaleType != null) {
            setScaleType(pendingScaleType);
            pendingScaleType = null;
        }
    }

    /**
     * 获取当前视图的{@link ImageViewProController}。注意不要持有对该 controller的引用，因为它引用了该视图，如果引用放置在错误的位置，可能会导致内存泄漏。
     *
     * @return controller
     */
    public ImageViewProController getController() {
        return controller;
    }

    @Override
    public ScaleType getScaleType() {
        return controller.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return controller.getImageMatrix();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        controller.setOnLongClickListener(l);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        controller.setOnClickListener(l);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (controller == null) {
            pendingScaleType = scaleType;
        } else {
            controller.setScaleType(scaleType);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap调用此方法
        if (controller != null) {
            controller.update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (controller != null) {
            controller.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (controller != null) {
            controller.update();
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            controller.update();
        }
        return changed;
    }

    public void setRotationTo(float rotationDegree) {
        controller.setRotationTo(rotationDegree);
    }

    public void setRotationBy(float rotationDegree) {
        controller.setRotationBy(rotationDegree);
    }

    @Deprecated
    public boolean isZoomEnabled() {
        return controller.isZoomEnabled();
    }

    public boolean isZoomable() {
        return controller.isZoomable();
    }

    public void setZoomable(boolean zoomable) {
        controller.setZoomable(zoomable);
    }

    public RectF getDisplayRect() {
        return controller.getDisplayRect();
    }

    public void getDisplayMatrix(Matrix matrix) {
        controller.getDisplayMatrix(matrix);
    }

    public boolean setDisplayMatrix(Matrix finalRectangle) {
        return controller.setDisplayMatrix(finalRectangle);
    }

    public void getSuppMatrix(Matrix matrix) {
        controller.getSuppMatrix(matrix);
    }

    public boolean setSuppMatrix(Matrix matrix) {
        return controller.setDisplayMatrix(matrix);
    }

    public float getMinimumScale() {
        return controller.getMinimumScale();
    }

    public float getMediumScale() {
        return controller.getMediumScale();
    }

    public float getMaximumScale() {
        return controller.getMaximumScale();
    }

    public float getScale() {
        return controller.getScale();
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        controller.setAllowParentInterceptOnEdge(allow);
    }

    public void setMinimumScale(float minimumScale) {
        controller.setMinimumScale(minimumScale);
    }

    public void setMediumScale(float mediumScale) {
        controller.setMediumScale(mediumScale);
    }

    public void setMaximumScale(float maximumScale) {
        controller.setMaximumScale(maximumScale);
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        controller.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    public void setOnMatrixChangeListener(OnMatrixChangeListener listener) {
        controller.setOnMatrixChangeListener(listener);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        controller.setOnPhotoTapListener(listener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
        controller.setOnOutsidePhotoTapListener(listener);
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        controller.setOnViewTapListener(listener);
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        controller.setOnViewDragListener(listener);
    }

    public void setScale(float scale) {
        controller.setScale(scale);
    }

    public void setScale(float scale, boolean animate) {
        controller.setScale(scale, animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        controller.setScale(scale, focalX, focalY, animate);
    }

    public void setZoomTransitionDuration(int milliseconds) {
        controller.setZoomTransitionDuration(milliseconds);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        controller.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener) {
        controller.setOnScaleChangeListener(onScaleChangeListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        controller.setOnSingleFlingListener(onSingleFlingListener);
    }
}
