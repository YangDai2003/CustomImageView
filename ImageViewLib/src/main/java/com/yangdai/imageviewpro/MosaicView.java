package com.yangdai.imageviewpro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MosaicView extends View {

    private final Paint paint;
    private float cornerRadius;
    private int tileSize; // 设置方块的大小

    public MosaicView(Context context) {
        this(context, null);
    }

    public MosaicView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MosaicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MosaicView);
        cornerRadius = a.getDimensionPixelSize(R.styleable.MosaicView_cornerRadius, 0);
        int color = a.getColor(R.styleable.MosaicView_mosaicColor, Color.GRAY);
        paint.setColor(color);
        tileSize = a.getDimensionPixelSize(R.styleable.MosaicView_tileSize, 40);
        a.recycle();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        Bitmap mosaicBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas mosaicCanvas = new Canvas(mosaicBitmap);

        for (int x = 0; x < width; x += tileSize) {
            for (int y = 0; y < height; y += tileSize) {
                if ((x / tileSize) % 2 == (y / tileSize) % 2) {
                    mosaicCanvas.drawRect(x, y, x + tileSize, y + tileSize, paint);
                }
            }
        }

        // 裁切成圆角
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, width, height), dpToPx(cornerRadius), dpToPx(cornerRadius), Path.Direction.CW);
        canvas.clipPath(path);

        // 绘制裁切后的马赛克图像
        canvas.drawBitmap(mosaicBitmap, 0, 0, null);
    }

    /**
     * @noinspection SameParameterValue
     */
    private float dpToPx(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return dp * density;
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }
}
