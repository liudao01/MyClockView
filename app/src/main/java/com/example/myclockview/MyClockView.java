package com.example.myclockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;

/**
 * Created by liuml on 2021/10/27 13:38
 */
public class MyClockView extends View {


    public MyClockView(Context context) {
        this(context, null);
    }

    public MyClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int radius = 400;//半径 后面动态

    private Paint mOutCirclePaint = null;//外层圆画笔
    private Paint textPaint = null;//文字画笔
    private Paint linePaint = null;//刻度画笔

    /**
     * 用于测量文本的宽、高度（这里主要是来获取高度）
     */
    private Rect textBounds = new Rect();


    private Paint mPaint = null;

    private int mWidth = 0, mHeight = 0;
    private int padding;
    private PointF center = new PointF();


    private String[] clockNumbers24 = {"24", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",};

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasureSize(true, widthMeasureSpec);
        mHeight = getMeasureSize(false, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * 获取View尺寸
     *
     * @param isWidth 是否是width，不是的话，是height
     */
    private int getMeasureSize(boolean isWidth, int measureSpec) {

        int result = 0;

        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                if (isWidth) {
                    result = getSuggestedMinimumWidth();
                } else {
                    result = getSuggestedMinimumHeight();
                }
                break;
            case MeasureSpec.AT_MOST:
                if (isWidth) {
                    result = Math.min(specSize, mWidth);
                } else {
                    result = Math.min(specSize, mHeight);
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        initPaint();

        if (mHeight == 0) {
            mWidth = right - left;
            mHeight = bottom - top;
            padding = Math.min(mWidth, mHeight) / 10;
        }

        center.x = (left + right) / 2;
        center.y = (bottom - top) / 2;

    }

    private void initPaint() {
        //绘制
        /**
         * Paint.Style.FILL设置只绘制图形内容
         * Paint.Style.STROKE设置只绘制图形的边
         * Paint.Style.FILL_AND_STROKE设置都绘制
         */
//        mOutCirclePaint.setStyle(Paint.Style.STROKE);
//        mOutCirclePaint.setStrokeWidth(60f);//设置线宽

        mOutCirclePaint = new Paint();
        mOutCirclePaint.setAntiAlias(true);//设置Paint为无锯齿

        textPaint = new Paint();
        textPaint.setTextSize(30);
        textPaint.setAntiAlias(true);//设置Paint为无锯齿
        textPaint.setColor(getContext().getResources().getColor(R.color.gary));


        linePaint = new Paint();
        linePaint.setAntiAlias(true);//设置Paint为无锯齿
        linePaint.setColor(getContext().getResources().getColor(R.color.gary));//设置灰色
//        textPaint.setColor(708090)

//        StokPaint = new Paint();
//        StokPaint.setAntiAlias(true);
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //onSizeChanged() 在控件大小发生改变时调用。初始化会被调用一次
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //先设置画布为view的中心点  把当前画布的原点移到(center.x,center.y),后面的操作都以(10,10)作为参照点，默认原点为(0,0)
        canvas.save();

        //画刻度和文字
        drawClockText(canvas);
        //画刻度
        drawLine(canvas);
        //画最外面的圆
        drawOutCircle(canvas);

        canvas.restore();//把当前画布返回（调整）到上一个save()状态之前
    }

    //画刻度
    private void drawLine(Canvas canvas) {
        //刻度长度为20
        canvas.drawLine(center.x, center.y - radius, center.x, center.y - radius - 20, linePaint);
    }

    //画外面的时间文字
    private void drawClockText(Canvas canvas) {

//            通过旋转画布的方式快速设置刻度
//        canvas.rotate(6);

        //x，y才是文本真正的准确坐标，需要减去文本的自身宽、高因素
        int x, y;
//        textPaint.setColor(getContext().getResources().getColor(R.color.gary));
        for (int i = 0; i <= clockNumbers24.length - 1; i++) {

            //画刻度 与文字
//            drawText(canvas, clockNumbers24[i]);

            textPaint.setStrokeWidth(20);
            drawLine(canvas);

            Rect rect = new Rect();
            textPaint.getTextBounds(clockNumbers24[i], 0, clockNumbers24[i].length(), rect);
            int height = rect.height();
            int width = rect.width();
            canvas.drawText(clockNumbers24[i], center.x - width / 2, center.y - radius - 35, textPaint);

//            计算画布每次需要旋转的角度
            canvas.rotate(360 / clockNumbers24.length, getWidth() / 2, getHeight() / 2);//以圆中心进行旋转
        }


        //绘制完后，把画布状态复原
//        canvas.restore();
    }

    private void drawText(Canvas canvas, String string) {
        mPaint.getTextBounds(string, 0, string.length(), textBounds);
//        x = (int) (preX - mPaint.measureText(string) / 2);
//        y = preY - textBounds.height();//从文本的中心点处开始绘制

        canvas.drawText(string, center.x, center.y - radius - 35, textPaint);

    }

    private void drawOutCircle(Canvas canvas) {

//        canvas.translate(center.x, center.y);
        mOutCirclePaint.setColor(Color.BLUE);
        //绘制
        /**
         * Paint.Style.FILL设置只绘制图形内容
         * Paint.Style.STROKE设置只绘制图形的边
         * Paint.Style.FILL_AND_STROKE设置都绘制
         */
//        mOutCirclePaint.setStyle(Paint.Style.STROKE);
//        mOutCirclePaint.setStrokeWidth(60f);//设置线宽

        canvas.drawCircle(center.x, center.y, radius, mOutCirclePaint);//画大圆


    }

    /**
     * 获取当前时间
     *
     * @return 时间戳long
     */
    public static long getNowTime() {

        return System.currentTimeMillis();
    }


}