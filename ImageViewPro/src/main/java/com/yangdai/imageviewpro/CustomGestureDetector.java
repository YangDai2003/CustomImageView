package com.yangdai.imageviewpro;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;

import com.yangdai.imageviewpro.interfaces.OnGestureListener;

/**
 * 自定义手势操作
 */
class CustomGestureDetector {

    private static final int INVALID_POINTER_ID = -1;

    private int mActivePointerId = INVALID_POINTER_ID;
    private int mActivePointerIndex = 0;
    private final ScaleGestureDetector mDetector;

    private VelocityTracker mVelocityTracker;
    private boolean mIsDragging;
    private float mLastTouchX;
    private float mLastTouchY;
    private final float mTouchSlop;
    private final float mMinimumVelocity;
    private final OnGestureListener mListener;

    CustomGestureDetector(Context context, OnGestureListener listener) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mTouchSlop = configuration.getScaledTouchSlop();

        mListener = listener;
        ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.OnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();

                if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                    return false;
                }

                mListener.onScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
                return true;
            }

            @Override
            public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
            }
        };
        mDetector = new ScaleGestureDetector(context, mScaleListener);
    }

    private float getActiveX(MotionEvent ev) {
        try {
            return ev.getX(mActivePointerIndex);
        } catch (Exception e) {
            return ev.getX();
        }
    }

    private float getActiveY(MotionEvent ev) {
        try {
            return ev.getY(mActivePointerIndex);
        } catch (Exception e) {
            return ev.getY();
        }
    }

    public boolean isScaling() {
        return mDetector.isInProgress();
    }

    public boolean isDragging() {
        return mIsDragging;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return processTouchEvent(ev);
    }

    private boolean processTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0);
                mVelocityTracker = VelocityTracker.obtain();
                if (null != mVelocityTracker) {
                    mVelocityTracker.addMovement(ev);
                }
                mLastTouchX = getActiveX(ev);
                mLastTouchY = getActiveY(ev);
                mIsDragging = false;
            }
            case MotionEvent.ACTION_MOVE -> {
                final float x = getActiveX(ev);
                final float y = getActiveY(ev);
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;
                if (!mIsDragging) {
                    // 使用勾股定理判断拖动长度是否大于触摸阈值
                    mIsDragging = Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
                }
                if (mIsDragging) {
                    mListener.onDrag(dx, dy);
                    mLastTouchX = x;
                    mLastTouchY = y;

                    if (null != mVelocityTracker) {
                        mVelocityTracker.addMovement(ev);
                    }
                }
            }
            case MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID;
                // 回收
                if (null != mVelocityTracker) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
            }
            case MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID;
                if (mIsDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = getActiveX(ev);
                        mLastTouchY = getActiveY(ev);

                        // 计算最近1000ms内的速度
                        mVelocityTracker.addMovement(ev);
                        mVelocityTracker.computeCurrentVelocity(1000);

                        final float vX = mVelocityTracker.getXVelocity();
                        final float vY = mVelocityTracker.getYVelocity();

                        // 如果速度大于最小速度阈值，则调用监听器
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                            mListener.onFling(mLastTouchX, mLastTouchY, -vX, -vY);
                        }
                    }
                }

                // 回收速度追踪器
                if (null != mVelocityTracker) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
            }
            case MotionEvent.ACTION_POINTER_UP -> {
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // 当前活动指针抬起，选择新的活动指针并相应调整
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                }
            }
            default -> {
            }
        }

        mActivePointerIndex = ev.findPointerIndex(mActivePointerId != INVALID_POINTER_ID ? mActivePointerId : 0);
        return true;
    }
}
