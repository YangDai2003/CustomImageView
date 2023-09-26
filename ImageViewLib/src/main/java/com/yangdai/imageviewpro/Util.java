package com.yangdai.imageviewpro;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

class Util {

    public static void postOnAnimation(View view, Runnable runnable) {
        view.postOnAnimation(runnable);
    }

    static void checkZoomLevels(float minZoom, float midZoom,
                                float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException("Minimum zoom has to be less than Medium zoom.");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException("Medium zoom has to be less than Maximum zoom.");
        }
    }

    static boolean hasDrawable(AppCompatImageView imageView) {
        return imageView.getDrawable() != null;
    }

    static boolean isSupportedScaleType(final AppCompatImageView.ScaleType scaleType) {
        if (scaleType == null) {
            return false;
        }
        if (scaleType == AppCompatImageView.ScaleType.MATRIX) {
            throw new IllegalStateException("Matrix scale type is not supported");
        }
        return true;
    }
}
