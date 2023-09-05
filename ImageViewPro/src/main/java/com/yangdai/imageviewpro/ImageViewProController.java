package com.yangdai.imageviewpro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView.ScaleType;
import android.widget.OverScroller;

import androidx.annotation.NonNull;

import com.yangdai.imageviewpro.interfaces.OnGestureListener;
import com.yangdai.imageviewpro.interfaces.OnMatrixChangeListener;
import com.yangdai.imageviewpro.interfaces.OnOutsidePhotoTapListener;
import com.yangdai.imageviewpro.interfaces.OnPhotoTapListener;
import com.yangdai.imageviewpro.interfaces.OnScaleChangeListener;
import com.yangdai.imageviewpro.interfaces.OnSingleFlingListener;
import com.yangdai.imageviewpro.interfaces.OnViewDragListener;
import com.yangdai.imageviewpro.interfaces.OnViewTapListener;

/**
 * {@link ImageViewPro}的组件，负责实现缩放、缩放、平移等功能。
 * 如果您需要继承除了 AppCompatImageView 之外的其他类，并且仍然希望获得{@link ImageViewPro}提供的功能，
 * 则可以将其设置为 public。
 */
public class ImageViewProController implements View.OnTouchListener,
        View.OnLayoutChangeListener {

    private static final float DEFAULT_MAX_SCALE = 3.0f;
    private static final float DEFAULT_MID_SCALE = 1.75f;
    private static final float DEFAULT_MIN_SCALE = 1.0f;
    private static final int DEFAULT_ZOOM_DURATION = 200;

    private static final int EDGE_NONE = -1;
    private static final int EDGE_LEFT = 0;
    private static final int EDGE_RIGHT = 1;
    private static final int EDGE_BOTH = 2;
    private static final int SINGLE_TOUCH = 1;

    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private int mZoomDuration = DEFAULT_ZOOM_DURATION;
    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;

    private boolean mAllowParentInterceptOnEdge = true;
    private boolean mBlockParentIntercept = false;

    private final android.widget.ImageView mImageView;

    // 手势检测器
    private GestureDetector mGestureDetector;
    private CustomGestureDetector mScaleDragDetector;

    // 这些变量用于避免重复分配内存
    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();
    private final RectF mDisplayRect = new RectF();
    private final float[] mMatrixValues = new float[9];

    // 监听器
    private OnMatrixChangeListener mMatrixChangeListener;
    private OnPhotoTapListener mPhotoTapListener;
    private OnOutsidePhotoTapListener mOutsidePhotoTapListener;
    private OnViewTapListener mViewTapListener;
    private View.OnClickListener mOnClickListener;
    private OnLongClickListener mLongClickListener;
    private OnScaleChangeListener mScaleChangeListener;
    private OnSingleFlingListener mSingleFlingListener;
    private OnViewDragListener mOnViewDragListener;

    private FlingRunnable mCurrentFlingRunnable;
    private int mScrollEdge = EDGE_BOTH;
    private float mBaseRotation;

    private boolean mZoomEnabled = true;
    private ScaleType mScaleType = ScaleType.FIT_CENTER;

    private final OnGestureListener onGestureListener = new OnGestureListener() {
        @Override
        public void onDrag(float dx, float dy) {
            // 如果正在缩放，则不进行拖动
            if (mScaleDragDetector.isScaling()) {
                return;
            }

            if (mOnViewDragListener != null) {
                mOnViewDragListener.onDragged(dx, dy);
            }
            mSuppMatrix.postTranslate(dx, dy);
            checkAndDisplayMatrix();

            /*
             * 决定是否允许ImageView的父视图接管触摸事件。
             *
             * 首先检查是否启用了此功能。如果正在缩放，永远不希望父视图接管。
             * 然后检查边缘，以及滚动的方向（即如果正在对抗边缘进行滚动，即“过度滚动”，则让父视图接管）。
             */
            ViewParent parent = mImageView.getParent();
            if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling() && !mBlockParentIntercept) {
                if (mScrollEdge == EDGE_BOTH
                        || (mScrollEdge == EDGE_LEFT && dx >= 1f)
                        || (mScrollEdge == EDGE_RIGHT && dx <= -1f)) {
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    }
                }
            } else {
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
        }

        @Override
        public void onFling(float startX, float startY, float velocityX, float velocityY) {
            mCurrentFlingRunnable = new FlingRunnable(mImageView.getContext());
            mCurrentFlingRunnable.fling(getImageViewWidth(mImageView),
                    getImageViewHeight(mImageView), (int) velocityX, (int) velocityY);
            mImageView.post(mCurrentFlingRunnable);
        }

        @Override
        public void onScale(float scaleFactor, float focusX, float focusY) {
            if ((getScale() < mMaxScale || scaleFactor < 1f) && (getScale() > mMinScale || scaleFactor > 1f)) {
                if (mScaleChangeListener != null) {
                    mScaleChangeListener.onScaleChanged(scaleFactor, focusX, focusY);
                }
                mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
                checkAndDisplayMatrix();
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public ImageViewProController(android.widget.ImageView imageView) {
        mImageView = imageView;
        imageView.setOnTouchListener(this);
        imageView.addOnLayoutChangeListener(this);

        if (imageView.isInEditMode()) {
            return;
        }

        mBaseRotation = 0.0f;

        // 创建手势检测器
        mScaleDragDetector = new CustomGestureDetector(imageView.getContext(), onGestureListener);

        mGestureDetector = new GestureDetector(imageView.getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                if (mLongClickListener != null) {
                    mLongClickListener.onLongClick(mImageView);
                }
            }

            @Override
            public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2,
                                   float velocityX, float velocityY) {
                if (mSingleFlingListener != null) {
                    if (getScale() > DEFAULT_MIN_SCALE) {
                        return false;
                    }

                    if (e1.getPointerCount() > SINGLE_TOUCH
                            || e1.getPointerCount() > SINGLE_TOUCH) {
                        return false;
                    }

                    return mSingleFlingListener.onFlung(e1, e2, velocityX, velocityY);
                }
                return false;
            }
        });

        mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(mImageView);
                }
                final RectF displayRect = getDisplayRect();

                final float x = e.getX(), y = e.getY();

                if (mViewTapListener != null) {
                    mViewTapListener.onViewTaped(mImageView, x, y);
                }

                if (displayRect != null) {

                    // 检查用户是否点击了照片
                    if (displayRect.contains(x, y)) {

                        float xResult = (x - displayRect.left)
                                / displayRect.width();
                        float yResult = (y - displayRect.top)
                                / displayRect.height();

                        if (mPhotoTapListener != null) {
                            mPhotoTapListener.onPhotoTaped(mImageView, xResult, yResult);
                        }
                        return true;
                    } else {
                        if (mOutsidePhotoTapListener != null) {
                            mOutsidePhotoTapListener.onOutsidePhotoTaped(mImageView);
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent ev) {
                try {
                    float scale = getScale();
                    float x = ev.getX();
                    float y = ev.getY();

                    if (scale < getMediumScale()) {
                        setScale(getMediumScale(), x, y, true);
                    } else if (scale >= getMediumScale() && scale < getMaximumScale()) {
                        setScale(getMaximumScale(), x, y, true);
                    } else {
                        setScale(getMinimumScale(), x, y, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // 当调用getX()和getY()时，有时可能会发生异常
                }

                return true;
            }

            @Override
            public boolean onDoubleTapEvent(@NonNull MotionEvent e) {
                // 等待确认的双击事件
                return false;
            }
        });
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener newOnDoubleTapListener) {
        this.mGestureDetector.setOnDoubleTapListener(newOnDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener) {
        this.mScaleChangeListener = onScaleChangeListener;
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.mSingleFlingListener = onSingleFlingListener;
    }

    @Deprecated
    public boolean isZoomEnabled() {
        return mZoomEnabled;
    }

    public RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    public boolean setDisplayMatrix(Matrix finalMatrix) {
        if (finalMatrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }

        if (mImageView.getDrawable() == null) {
            return false;
        }

        mSuppMatrix.set(finalMatrix);
        checkAndDisplayMatrix();

        return true;
    }

    public void setBaseRotation(final float degrees) {
        mBaseRotation = degrees % 360;
        update();
        setRotationBy(mBaseRotation);
        checkAndDisplayMatrix();
    }

    public void setRotationTo(float degrees) {
        mSuppMatrix.setRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    public void setRotationBy(float degrees) {
        mSuppMatrix.postRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    public float getMinimumScale() {
        return mMinScale;
    }

    public float getMediumScale() {
        return mMidScale;
    }

    public float getMaximumScale() {
        return mMaxScale;
    }

    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(mSuppMatrix, Matrix.MSKEW_Y), 2));
    }

    public ScaleType getScaleType() {
        return mScaleType;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        // 更新基础矩阵，因为边界发生了变化
        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
            updateBaseMatrix(mImageView.getDrawable());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        boolean handled = false;

        if (mZoomEnabled && Util.hasDrawable((android.widget.ImageView) v)) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN -> {
                    ViewParent parent = v.getParent();
                    // 首先禁用父视图拦截触摸事件
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }

                    // 如果正在进行fling操作，并且用户按下了屏幕，则取消fling操作
                    cancelFling();
                }
                case MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    // 如果用户缩放比例小于最小缩放比例，则缩放回最小缩放比例
                    if (getScale() < mMinScale) {
                        RectF rect = getDisplayRect();
                        if (rect != null) {
                            v.post(new AnimatedZoomRunnable(getScale(), mMinScale,
                                    rect.centerX(), rect.centerY()));
                            handled = true;
                        }
                    } else if (getScale() > mMaxScale) {
                        RectF rect = getDisplayRect();
                        if (rect != null) {
                            v.post(new AnimatedZoomRunnable(getScale(), mMaxScale,
                                    rect.centerX(), rect.centerY()));
                            handled = true;
                        }
                    }
                }
            }

            // 尝试使用缩放/拖动检测器
            if (mScaleDragDetector != null) {
                boolean wasScaling = mScaleDragDetector.isScaling();
                boolean wasDragging = mScaleDragDetector.isDragging();

                handled = mScaleDragDetector.onTouchEvent(ev);

                boolean noScale = !wasScaling && !mScaleDragDetector.isScaling();
                boolean noDrag = !wasDragging && !mScaleDragDetector.isDragging();

                mBlockParentIntercept = noScale && noDrag;
            }

            // 检查用户是否双击
            if (mGestureDetector != null && mGestureDetector.onTouchEvent(ev)) {
                handled = true;
            }

        }

        return handled;
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAllowParentInterceptOnEdge = allow;
    }

    public void setMinimumScale(float minimumScale) {
        Util.checkZoomLevels(minimumScale, mMidScale, mMaxScale);
        mMinScale = minimumScale;
    }

    public void setMediumScale(float mediumScale) {
        Util.checkZoomLevels(mMinScale, mediumScale, mMaxScale);
        mMidScale = mediumScale;
    }

    public void setMaximumScale(float maximumScale) {
        Util.checkZoomLevels(mMinScale, mMidScale, maximumScale);
        mMaxScale = maximumScale;
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        Util.checkZoomLevels(minimumScale, mediumScale, maximumScale);
        mMinScale = minimumScale;
        mMidScale = mediumScale;
        mMaxScale = maximumScale;
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        mLongClickListener = listener;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnMatrixChangeListener(OnMatrixChangeListener listener) {
        mMatrixChangeListener = listener;
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mPhotoTapListener = listener;
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener mOutsidePhotoTapListener) {
        this.mOutsidePhotoTapListener = mOutsidePhotoTapListener;
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        mViewTapListener = listener;
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        mOnViewDragListener = listener;
    }

    public void setScale(float scale) {
        setScale(scale, false);
    }

    public void setScale(float scale, boolean animate) {
        setScale(getMediumScale(),
                (float) mImageView.getRight() / 2,
                (float) mImageView.getBottom() / 2,
                true);
    }

    public void setScale(float scale, float focalX, float focalY,
                         boolean animate) {
        // 检查缩放比例是否在范围内
        if (scale < mMinScale || scale > mMaxScale) {
            throw new IllegalArgumentException("Scale must be within the range of minScale and maxScale");
        }

        if (animate) {
            mImageView.post(new AnimatedZoomRunnable(getScale(), scale,
                    focalX, focalY));
        } else {
            mSuppMatrix.setScale(scale, scale, focalX, focalY);
            checkAndDisplayMatrix();
        }
    }

    /**
     * 设置缩放插值器
     *
     * @param interpolator 缩放插值器
     */
    public void setZoomInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setScaleType(ScaleType scaleType) {
        if (Util.isSupportedScaleType(scaleType) && scaleType != mScaleType) {
            mScaleType = scaleType;
            update();
        }
    }

    public boolean isZoomable() {
        return mZoomEnabled;
    }

    public void setZoomable(boolean zoomable) {
        mZoomEnabled = zoomable;
        update();
    }

    public void update() {
        if (mZoomEnabled) {
            // 使用当前Drawable更新基础矩阵
            updateBaseMatrix(mImageView.getDrawable());
        } else {
            // 重置矩阵
            resetMatrix();
        }
    }

    /**
     * 获取显示矩阵
     *
     * @param matrix 目标矩阵
     */
    public void getDisplayMatrix(Matrix matrix) {
        matrix.set(getDrawMatrix());
    }

    /**
     * 获取当前支持的矩阵
     */
    public void getSuppMatrix(Matrix matrix) {
        matrix.set(mSuppMatrix);
    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    public Matrix getImageMatrix() {
        return mDrawMatrix;
    }

    public void setZoomTransitionDuration(int milliseconds) {
        this.mZoomDuration = milliseconds;
    }

    /**
     * 辅助方法，从矩阵中获取所需的值
     *
     * @param matrix     要解析的矩阵
     * @param whichValue 要返回的值的类型（Matrix.M*）
     * @return 返回的值
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * 重置矩阵为 FIT_CENTER，并显示其内容
     */
    private void resetMatrix() {
        mSuppMatrix.reset();
        setRotationBy(mBaseRotation);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
        mImageView.setImageMatrix(matrix);

        // 如果需要，调用 MatrixChangedListener
        if (mMatrixChangeListener != null) {
            RectF displayRect = getDisplayRect(matrix);
            if (displayRect != null) {
                mMatrixChangeListener.onMatrixChanged(displayRect);
            }
        }
    }

    /**
     * 辅助方法，检查矩阵并显示结果
     */
    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    /**
     * 辅助方法，将提供的矩阵映射到当前Drawable上
     *
     * @param matrix - 要映射到Drawable上的矩阵
     * @return RectF - 显示的矩形
     */
    private RectF getDisplayRect(Matrix matrix) {
        Drawable d = mImageView.getDrawable();
        if (d != null) {
            mDisplayRect.set(0, 0, d.getIntrinsicWidth(),
                    d.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    /**
     * 计算FIT_CENTER的矩阵
     *
     * @param drawable - 正在显示的Drawable
     */
    private void updateBaseMatrix(Drawable drawable) {
        if (drawable == null) {
            return;
        }

        final float viewWidth = getImageViewWidth(mImageView);
        final float viewHeight = getImageViewHeight(mImageView);
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        mBaseMatrix.reset();

        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;

        if (mScaleType == ScaleType.CENTER) {
            mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
                    (viewHeight - drawableHeight) / 2F);

        } else if (mScaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);

            if ((int) mBaseRotation % 180 != 0) {
                mTempSrc = new RectF(0, 0, drawableHeight, drawableWidth);
            }

            switch (mScaleType) {
                case FIT_CENTER -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
                case FIT_START -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START);
                case FIT_END -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END);
                case FIT_XY -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL);
                default -> {
                }
            }
        }

        resetMatrix();
    }

    private boolean checkMatrixBounds() {

        final RectF rect = getDisplayRect(getDrawMatrix());
        if (rect == null) {
            return false;
        }

        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getImageViewHeight(mImageView);
        if (height <= viewHeight) {
            deltaY = switch (mScaleType) {
                case FIT_START -> -rect.top;
                case FIT_END -> viewHeight - height - rect.top;
                default -> (viewHeight - height) / 2 - rect.top;
            };
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = getImageViewWidth(mImageView);
        if (width <= viewWidth) {
            deltaX = switch (mScaleType) {
                case FIT_START -> -rect.left;
                case FIT_END -> viewWidth - width - rect.left;
                default -> (viewWidth - width) / 2 - rect.left;
            };
            mScrollEdge = EDGE_BOTH;
        } else if (rect.left > 0) {
            mScrollEdge = EDGE_LEFT;
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            mScrollEdge = EDGE_RIGHT;
        } else {
            mScrollEdge = EDGE_NONE;
        }

        // 最后实际平移矩阵
        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    private int getImageViewWidth(android.widget.ImageView imageView) {
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(android.widget.ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    private void cancelFling() {
        if (mCurrentFlingRunnable != null) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    private class AnimatedZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        }

        @Override
        public void run() {

            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();

            onGestureListener.onScale(deltaScale, mFocalX, mFocalY);

            // 还没有达到目标缩放比例，所以再次发送自己
            if (t < 1f) {
                Util.postOnAnimation(mImageView, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
            t = Math.min(1f, t);
            t = mInterpolator.getInterpolation(t);
            return t;
        }
    }

    private class FlingRunnable implements Runnable {

        private final OverScroller mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
            mScroller = new OverScroller(context);
        }

        public void cancelFling() {
            mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX,
                          int velocityY) {
            final RectF rect = getDisplayRect();
            if (rect == null) {
                return;
            }

            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;

            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }

            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }

            mCurrentX = startX;
            mCurrentY = startY;

            // 如果可以移动，则进行fling操作
            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX,
                        maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
            // 置之不理
            if (mScroller.isFinished()) {
                return;
            }

            if (mScroller.computeScrollOffset()) {

                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();

                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                checkAndDisplayMatrix();

                mCurrentX = newX;
                mCurrentY = newY;

                // 发送动画
                Util.postOnAnimation(mImageView, this);
            }
        }
    }
}
