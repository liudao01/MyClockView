package com.example.myclockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    private int radius = 0;//半径
    private Paint mPaint = null;
    private Paint StokPaint = null;

    private int mWidth = 0, mHeight = 0;
    private int padding;
    private PointF center = new PointF();


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasureSize(true, widthMeasureSpec);
        int height = getMeasureSize(false, heightMeasureSpec);
        setMeasuredDimension(width, height);

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
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        StokPaint = new Paint();
        StokPaint.setAntiAlias(true);

        if (mHeight == 0) {
            mWidth = right - left;
            mHeight = bottom - top;
            padding = Math.min(mWidth, mHeight) / 20;
            radius = Math.min(mWidth, mHeight) / 2 - padding;
        }

        center.x = (left + right) / 2;
        center.y = (bottom - top) / 2;

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
        canvas.translate(center.x, center.y);
        drawOutCircle(canvas);

//        mPaint.setColor(0xff303F8F);
//        canvas.drawCircle(center.x, center.y, radius, mPaint);//背景
//
//        Matrix matrix = new Matrix();
//        mPaint.setColor(Color.LTGRAY);
//        mPaint.setStrokeWidth(3);

//        for (int i = 0; i < 60; i++) {
//            matrix.setRotate(i * 6, center.x, center.y);//以绘制区域中心为远点旋转，并将旋转角度递增，达到转圈的效果
//            canvas.setMatrix(matrix);
//            if (i % 15 == 0) {
//                //最大的刻度，3 6 9 12
//                canvas.drawRect(center.x - 2, padding, center.x + 2, padding + 40, mPaint);
//            } else if (i % 5 == 0) {
//                //小一点的刻度  对应1、2、4、5...
//                canvas.drawRect(center.x - 1.8f, padding, center.x + 1.8f, padding + 24, mPaint);
//            } else {
//                //分针对应的刻度
//                canvas.drawRect(center.x - 1f, padding, center.x + 1f, padding + 12, mPaint);
//            }
//        }
//        matrix.setRotate(0, center.x, center.y);//以绘制区域中心为远点旋转，并将旋转角度递增，达到转圈的效果
//        canvas.setMatrix(matrix);
//
//        mPaint.setTextAlign(Paint.Align.CENTER);
//        for (int i = 0; i < 4; i++) {//画4个数字  3 6 9 12
//            //此处注意drawtex的y坐标，对应的是descent，不是top，也不是bottom
//            String txt = String.valueOf(i == 0 ? 12 : i * 3);
//            mPaint.setTextSize(40);
//            float w = mPaint.measureText(txt);
//            Paint.FontMetrics fm = mPaint.getFontMetrics();
//            float des = fm.descent - fm.top;
//            float x = 0;
//            float y = 0;
//            switch (i) {
//                case 0:
//                    x = center.x;
//                    y = padding + 45 + des;
//                    break;
//                case 1:
//                    x = center.x + radius - 45 - w;
//                    y = center.y + (fm.bottom - fm.top) / 4;
//                    break;
//                case 2:
//                    x = center.x;
//                    y = center.y + radius - 45 - (fm.bottom - fm.top) / 2;
//                    break;
//                default:
//                    x = center.x - radius + 45 + w;
//                    y = center.y + (fm.bottom - fm.top) / 4;
//                    break;
//            }
//            canvas.drawText(txt, x, y, mPaint);//画时刻数字
//        }
//
////        Path path = new Path();
////        path.addArc(new RectF(center.x - 200, padding + 150, center.x + 200, center.y - 150),
////                180, 180);//设置扇形区域边框，以及旋转的角度
////        Paint citePaint = new Paint(mPaint);
////        citePaint.setTextSize(40);
////        citePaint.setStrokeWidth(3);
////        canvas.drawPath(path, citePaint);//可放开查看扇形区域
//////        canvas.drawTextOnPath("lock view from canvas", path, 0, 0, citePaint);//画顶部扇形区域外边框文字
////        canvas.drawText(dateFormat.format(new Date()), center.x, center.y + radius - 150, mPaint);//当前时间  也可以加上星期
//
//        mPaint.setColor(Color.BLACK);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(3);
//        canvas.drawCircle(center.x, center.y, radius, mPaint); //画最外层圆圈，可以优化
//        mPaint.setStyle(Paint.Style.FILL);
//
//        canvas.drawCircle(center.x, center.y, 15, mPaint);//这个圆是为了美观

        canvas.restore();//把当前画布返回（调整）到上一个save()状态之前
    }

    private void drawOutCircle(Canvas canvas) {
        //画最外面的圆

//        canvas.drawCircle(0,0,);

    }

    /**
     * 获取今天的开始时间
     *
     * @return 时间戳long
     */
    public static long getNowTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return System.currentTimeMillis() - c.getTimeInMillis();
    }


}