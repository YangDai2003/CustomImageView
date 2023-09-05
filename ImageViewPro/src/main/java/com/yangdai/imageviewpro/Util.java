package com.yangdai.imageviewpro;

import android.view.View;
import android.widget.ImageView;

class Util {
    private static final int SIXTY_FPS_INTERVAL = 1000 / 60;

    public static void postOnAnimation(View view, Runnable runnable) {
        view.postOnAnimation(runnable);
    }

    static void checkZoomLevels(float minZoom, float midZoom,
                                float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException(
                    "Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException(
                    "Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value");
        }
    }

    static boolean hasDrawable(ImageView imageView) {
        return imageView.getDrawable() != null;
    }

    static boolean isSupportedScaleType(final ImageView.ScaleType scaleType) {
        if (scaleType == null) {
            return false;
        }
        if (scaleType == ImageView.ScaleType.MATRIX) {
            throw new IllegalStateException("Matrix scale type is not supported");
        }
        return true;
    }
}
