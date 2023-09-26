package com.yangdai.imageviewpro;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class RoundRectImageView extends AppCompatImageView {
    private static final int RECT = 1;
    private static final int CIRCLE = 2;
    private Paint mPaint;
    private Bitmap mRawBitmap;
    private BitmapShader mShader;
    private Matrix matrix;
    private Paint mBorderPaint;

    private RectF mRectBorder;
    private RectF mRectBitmap;

    //边框的宽度
    private int mBorderWidth;

    //边框的颜色
    private int mBorderColor;

    /**
     * 0 ClAMP ： Bitmap以其内容的最后一行像素填充剩余的高的空白或者最后一列像素填充剩余宽空白
     * 1 MIRROR ：Bitmap以其内容以镜像的方式填充剩余空白
     * 2 REPEAT ：Bitmap以其内容以重复的方式填充剩余空白
     */
    private int mTileY;

    private int mTileX;

    //边角的半径
    private int mRoundRadius;


    /**
     * circle 圆形 默认
     * rect 圆角矩形
     */
    private int mShapeType;


    public RoundRectImageView(Context context) {
        this(context, null);
    }

    public RoundRectImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundRectImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundRectImageView);
        mTileX = array.getInt(R.styleable.RoundRectImageView_mTileX, 0);
        mTileY = array.getInt(R.styleable.RoundRectImageView_mTileY, 0);
        mBorderColor = array.getColor(R.styleable.RoundRectImageView_mBorderColor, 0xFF0080FF);
        mBorderWidth = array.getDimensionPixelOffset(R.styleable.RoundRectImageView_mBorderWidth, 0);
        mShapeType = array.getInt(R.styleable.RoundRectImageView_mShapeType, CIRCLE);
        mRoundRadius = array.getDimensionPixelOffset(R.styleable.RoundRectImageView_mRoundRadius, 0);
        array.recycle();
    }

    /**
     * 在onDraw中不要有过多复杂的逻辑，和过于复杂多余的计算，否则会导致绘制不全的现象
     * 在onDraw方法中如果一个值是多次使用的，就通过变量先计算好，不要每次用的时候才计算，影响计算的效率
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //获取设置图片的Bitmap对象。
        Bitmap bitmap = getBitmap(getDrawable());
        if (bitmap != null) {
            //支持Padding的属性
            final int paddingLeft = getPaddingLeft();
            final int paddingRight = getPaddingRight();
            final int paddingTop = getPaddingTop();
            final int paddingBottom = getPaddingBottom();
            //通过减去padding的属性值，获得图片正真的高度
            float width = getWidth() - paddingLeft - paddingRight;
            float height = getHeight() - paddingTop - paddingBottom;
            float diameter = Math.min(width, height);
            //如果是矩形，则直接是宽高，是圆形为半径
            float dstWidth = mShapeType == RECT ? width : diameter;
            float dstHeight = mShapeType == RECT ? height : diameter;
            float doubleBorderWidth = mBorderWidth * 2.0f;
            float halfBorderWidth = mBorderWidth / 2.0f;
            //判断是否已经创建，或者复用
            if (mShader == null || !bitmap.equals(mRawBitmap)) {
                mRawBitmap = bitmap;
                mShader = createBitmapShader(mRawBitmap, mTileX, mTileY);
            }
            if (mShader != null) {
                //设置缩放比例，缩放的比例应该是正式的宽度与原来的宽度的比。而宽度和高度还需要减去边框的宽度*2
                matrix.setScale((dstWidth - doubleBorderWidth) / mRawBitmap.getWidth(), (dstHeight - doubleBorderWidth) / mRawBitmap.getHeight());
                //为着色器色器设置矩阵
                mShader.setLocalMatrix(matrix);
            }
            //画笔设置着色器
            mPaint.setShader(mShader);
            //如果设置了边框的，对边框的画笔进行设置
            if (mBorderWidth > 0) {
                //设置的样式是边框
                mBorderPaint.setStyle(Paint.Style.STROKE);
                //边框的宽度
                mBorderPaint.setStrokeWidth(mBorderWidth);
                //如果是不设置边框得 使边框得画笔变为透明
                mBorderPaint.setColor(mBorderColor);
            }
            if (mShapeType == RECT) {
                //画矩形
                createRoundRect(canvas, width, height, doubleBorderWidth, halfBorderWidth);
            } else {
                //画圆
                createCircle(canvas, diameter / 2.0f, halfBorderWidth);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 画矩形
     *
     * @param canvas            画布
     * @param width             图形的宽度
     * @param height            图形的高度
     * @param doubleBorderWidth 边框的宽度*2
     * @param halfBorderWidth   边框的宽度/2
     */
    private void createRoundRect(Canvas canvas, float width, float height, float doubleBorderWidth, float halfBorderWidth) {
        //边框的矩形，至于为什么要减去一半的原因是
        // 绘制带边框的矩形（其他形状同理），矩形的边界是边框的中心，而不是边框的边界，
        // 所以在绘制这些带边框的形状时，需要减去边框宽度的一半
        mRectBorder.set(halfBorderWidth, halfBorderWidth, width - halfBorderWidth, height - halfBorderWidth);
        //图形的矩形
        mRectBitmap.set(0.0f, 0.0f, width - doubleBorderWidth, height - doubleBorderWidth);
        float bitmapRadius = Math.max((mRoundRadius - mBorderWidth), 0.0f);
        if (mBorderWidth > 0) {
            float rectRadius = Math.max(mRoundRadius - halfBorderWidth, 0.0f);
            //画边边框
            canvas.drawRoundRect(mRectBorder, rectRadius, rectRadius, mBorderPaint);
            //画布平移
            canvas.translate(mBorderWidth, mBorderWidth);
        }
        //画图像得
        canvas.drawRoundRect(mRectBitmap, bitmapRadius, bitmapRadius, mPaint);
    }

    /**
     * 画圆形
     */
    private void createCircle(Canvas canvas, float radius, float halfBorderWidth) {
        //图形正真的半径还要减去边框的宽度
        float realRadius = radius - mBorderWidth;
        if (mBorderWidth > 0) {
            //画边框，画边框的半径一定要减去边框的一边
            canvas.drawCircle(radius, radius, radius - halfBorderWidth, mBorderPaint);
            //平移画布
            canvas.translate(mBorderWidth, mBorderWidth);
        }
        canvas.drawCircle(realRadius, realRadius, realRadius, mPaint);
    }


    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);//设置抗锯齿
        //该方法千万别放到onDraw()方法里面调用，否则会不停的重绘的，因为该方法调用了invalidate() 方法
        //View Layer 绘制所消耗的实际时间是比不使用 View Layer 时要高的，所以要慎重使用。所以我们将View Layer关闭
        //否则会出现黑色背景的现象
        setLayerType(View.LAYER_TYPE_NONE, null);
        matrix = new Matrix();
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setAntiAlias(true);
        mRectBitmap = new RectF();
        mRectBorder = new RectF();
    }

    /**
     * 根据不同的类型获取Bitmap
     */
    public Bitmap getBitmap(Drawable drawable) {
        //如果是图片类型则直接返回
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof ColorDrawable) {
            //颜色类型
            Rect rect = drawable.getBounds();
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            int color = ((ColorDrawable) drawable).getColor();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 根据不同的mTileX，mTileY创建BitmapShader
     */
    public BitmapShader createBitmapShader(Bitmap bitmap, int mTileX, int mTileY) {
        BitmapShader.TileMode tileModeX;
        BitmapShader.TileMode tileModeY;
        switch (mTileX) {
            case 1:
                tileModeX = BitmapShader.TileMode.MIRROR;
                break;
            case 2:
                tileModeX = BitmapShader.TileMode.REPEAT;
                break;
            default:
                tileModeX = BitmapShader.TileMode.CLAMP;
        }

        switch (mTileY) {
            case 1:
                tileModeY = BitmapShader.TileMode.MIRROR;
                break;
            case 2:
                tileModeY = BitmapShader.TileMode.REPEAT;
                break;
            default:
                tileModeY = BitmapShader.TileMode.CLAMP;
                break;
        }
        return new BitmapShader(bitmap, tileModeX, tileModeY);

    }

    public int getTileX() {
        return mTileX;
    }

    public void setTileX(int mTileX) {
        this.mTileX = mTileX;
    }

    public int getTileY() {
        return mTileY;
    }

    public void setTileY(int mTileY) {
        this.mTileY = mTileY;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public Bitmap getmRawBitmap() {
        return mRawBitmap;
    }

    public void setmRawBitmap(Bitmap mRawBitmap) {
        this.mRawBitmap = mRawBitmap;
    }

    public BitmapShader getmShader() {
        return mShader;
    }

    public void setmShader(BitmapShader mShader) {
        this.mShader = mShader;
    }

    @Override
    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Paint getmBorderPaint() {
        return mBorderPaint;
    }

    public void setmBorderPaint(Paint mBorderPaint) {
        this.mBorderPaint = mBorderPaint;
    }

    public RectF getmRectBorder() {
        return mRectBorder;
    }

    public void setmRectBorder(RectF mRectBorder) {
        this.mRectBorder = mRectBorder;
    }

    public RectF getmRectBitmap() {
        return mRectBitmap;
    }

    public void setmRectBitmap(RectF mRectBitmap) {
        this.mRectBitmap = mRectBitmap;
    }

    public int getmBorderWidth() {
        return mBorderWidth;
    }

    public void setmBorderWidth(int mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    public int getmBorderColor() {
        return mBorderColor;
    }

    public void setmBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

    public int getmRoundRadius() {
        return mRoundRadius;
    }

    public void setmRoundRadius(int mRoundRadius) {
        this.mRoundRadius = mRoundRadius;
    }

    public int getmShapeType() {
        return mShapeType;
    }

    public void setmShapeType(int mShapeType) {
        this.mShapeType = mShapeType;
    }
}
