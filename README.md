# MyClockView
MyClockView


仿照 爱时间app 写的自定义时间控件

爱时间的 控件  
![](https://liudao01.github.io/picture/img/202202201238580.jpg)


我写的控件

![](https://liudao01.github.io/picture/img/202202201239164.jpg)

可以看到我写的在指针,刻度上面 是比他要精细一些的. 后面的点击事件.还有中间文字的绘制 都是一些套路,我的时间也不够多.就不写了.

主要思想是绘制下面几个要点
1. 外层圆环 外层刻度 外层时间
2. 内层圆环 内层刻度 内层时间
3. 中心圆  中心文字

注意点是刻度的绘制和文字的绘制. 需要使用Canvas的画布的旋转技巧 这样可以比较简单的绘制一些刻度的旋转角度. 


```java
package com.example.myclockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuml on 2021/10/27 13:38
 */
public class MyClockView extends View {


    //绘制扇形教程
    //https://www.jianshu.com/p/70c48029e301

    private String TAG = "MyClockView";

    private RectF acrRectF;

    public MyClockView(Context context) {
        this(context, null);
    }

    public MyClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initData();
    }

    private int radius = 350;//半径 后面动态
    private int centerRadius = 180;//半径 后面动态

    private Paint mOutCirclePaint = null;//外层圆画笔
    private Paint mInnerCirclePaint = null;//内层圆画笔
    private Paint mCenterCirclePaint = null;//中心圆画笔

    private Paint textPaint = null;//文字画笔
    private Paint linePaint = null;//刻度画笔
    private Paint testPaint = null;//测试画笔

    private Paint centerTextPaint = null;//测试画笔

    private float circleWidth = 60f;//外圈圆宽度
    private int lineTextSize = 30;//刻度线文字大小
    private int lineLength = 20;//刻度线长度
    private int textPainSize = 20;//刻度文字大小
    private int textLineSpace = 20;//刻度和文字的间隔

//    private int current

    private int drawTextBegin = 0;

    private RectF rectF;

    private int screenWidth;
    private int screenHeight;


    //模拟数据------------
    // 定义几个颜色
    private int[] colors = {
            R.color.color_fff9331f,
            R.color.theme_title_color,
            R.color.teal_200,
            R.color.color_4d1a1a1a,
            R.color.color_EBB57A
    };


    private int currentColor = R.color.color_1A1A1A;
    // 开始角度
    private float startAngel = 0f;
    // 扫过角度
    private float sweepAngle = 360 / 5f;

    private List<CircleData> circleDataList;
    //模拟数据------------
    /**
     * 用于测量文本的宽、高度（这里主要是来获取高度）
     */
    private Rect textBounds = new Rect();


    private Paint mPaint = null;

    private int mWidth = 0, mHeight = 0;
    private int padding;
    private PointF center = new PointF();


    private String[] clockNumbers24 = {"24", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",};
    private String[] clockNumbers12 = {"12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",};

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasureSize(true, widthMeasureSpec);
        mHeight = getMeasureSize(false, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * 获取View尺寸 测量控件大小
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

        center.x = screenWidth / 2;
        center.y = screenHeight / 2;

        //设置将要用来画扇形的矩形的轮廓
//        drawTextBegin = (int) (center.y + circleWidth);

    }

    //确定View大小
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.screenWidth = w;     //获取宽高
        this.screenHeight = h;
    }

    private void initPaint() {
        //绘制
        /**
         * Paint.Style.FILL设置只绘制图形内容
         * Paint.Style.STROKE设置只绘制图形的边
         * Paint.Style.FILL_AND_STROKE设置都绘制
         */

        //外圈圆 画笔
        mOutCirclePaint = new Paint();
        mOutCirclePaint.setAntiAlias(true);//设置Paint为无锯齿
        mOutCirclePaint.setStyle(Paint.Style.STROKE);
        mOutCirclePaint.setStrokeWidth(circleWidth);//设置线宽
        mOutCirclePaint.setColor(Color.BLUE);

        //内圈圆 画笔
        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setAntiAlias(true);//设置Paint为无锯齿
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);
        mInnerCirclePaint.setStrokeWidth(circleWidth);//设置线宽
        mInnerCirclePaint.setColor(Color.BLUE);

        //中心圆画笔
        mCenterCirclePaint = new Paint();
        mCenterCirclePaint.setAntiAlias(true);//设置Paint为无锯齿
        mCenterCirclePaint.setStrokeWidth(circleWidth);//设置线宽
        mCenterCirclePaint.setColor(Color.BLUE);

        //刻度文字画笔
        textPaint = new Paint();
        textPaint.setTextSize(lineTextSize);
        textPaint.setAntiAlias(true);//设置Paint为无锯齿
        textPaint.setColor(getContext().getResources().getColor(R.color.gary));
        textPaint.setStrokeWidth(textPainSize);

        //
        centerTextPaint = new Paint();
        centerTextPaint.setTextSize(lineTextSize);
        centerTextPaint.setAntiAlias(true);//设置Paint为无锯齿
        centerTextPaint.setColor(getContext().getResources().getColor(R.color.white));
        centerTextPaint.setStrokeWidth(textPainSize);

        //刻度画笔
        linePaint = new Paint();
        linePaint.setAntiAlias(true);//设置Paint为无锯齿
        linePaint.setColor(getContext().getResources().getColor(R.color.gary));//设置灰色

        //测试用的画笔
        testPaint = new Paint();
        testPaint.setTextSize(lineTextSize);
        testPaint.setAntiAlias(true);//设置Paint为无锯齿
        testPaint.setColor(getContext().getResources().getColor(R.color.design_default_color_error));
//        testPaint.setStrokeWidth(10);

        //初始化区域
        rectF = new RectF();
        acrRectF = new RectF();
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        //画外部刻度和文字
        drawClockText(canvas);
        //画外部圆环
        drawMyOutAcr(canvas);
        //画内部圆环
        drawMyInnerAcr(canvas);

        //画内部刻度文字
        drawInnerClockText(canvas);

        //绘制中心圆
        drawCenterCircle(canvas);

        canvas.restore();//把当前画布返回（调整）到上一个save()状态之前
    }

    private void drawCenterCircle(Canvas canvas) {
        canvas.drawCircle(center.x, center.y, centerRadius, mCenterCirclePaint);
    }

    private void drawMyInnerAcr(Canvas canvas) {
        // 矩形区域
        RectF acrRectF = new RectF();
        acrRectF.set(center.x - radius + circleWidth, center.y - radius + circleWidth,
                center.x + radius - circleWidth, center.y + radius - circleWidth);
        Log.d(TAG, "drawMyAcr: radius = " + radius);
        for (int i = 0; i < circleDataList.size(); i++) {
            CircleData circleData = circleDataList.get(i);
            //内部圆环
            if (circleData.getType() == 0) {
                mInnerCirclePaint.setColor(getContext().getResources().getColor(circleData.getColor()));
                Log.d(TAG, "drawMyAcr: 圆圈画笔颜色 = " + circleData.getColor());
                Log.d(TAG, "drawMyAcr: outCircleData = " + circleData.toString());
                //第三个参数是扫过的角度
                canvas.drawArc(acrRectF, circleData.getStartAngle(), circleData.getSweepAngle(),
                        false, mInnerCirclePaint);
            }
        }
    }

    //画外部刻度
    private void drawLine(Canvas canvas) {
        //刻度长度为20  circleWidth
        float begin = center.y - radius - circleWidth / 2;
        canvas.drawLine(center.x, begin, center.x,
                center.y - radius - circleWidth / 2 - lineLength, linePaint);
    }

    //画内部刻度
    private void drawInnerLine(Canvas canvas) {
        float begin = center.y - radius + circleWidth + circleWidth / 2;
        //刻度长度为20  circleWidth
        canvas.drawLine(center.x, begin, center.x,
                begin + lineLength, linePaint);
    }

    //画内部的时间文字
    private void drawInnerClockText(Canvas canvas) {
        for (int i = 0; i <= clockNumbers12.length - 1; i++) {

            drawInnerLine(canvas);

            Rect rect = new Rect();
            textPaint.getTextBounds(clockNumbers12[i], 0, clockNumbers12[i].length(), rect);
            int height = rect.height();
            int width = rect.width();
            canvas.drawText(clockNumbers12[i], center.x - width / 2,
                    center.y - radius + circleWidth * 2 + textLineSpace + 5, textPaint);
//            通过旋转画布的方式快速设置刻度
//            计算画布每次需要旋转的角度
            canvas.rotate(360 / clockNumbers24.length, mWidth / 2, mHeight / 2);//以圆中心进行旋转
        }
    }

    //画外面的时间文字
    private void drawClockText(Canvas canvas) {
        for (int i = 0; i <= clockNumbers24.length - 1; i++) {

            drawLine(canvas);

            Rect rect = new Rect();
            textPaint.getTextBounds(clockNumbers24[i], 0, clockNumbers24[i].length(), rect);
            int height = rect.height();
            int width = rect.width();
            canvas.drawText(clockNumbers24[i], center.x - width / 2,
                    center.y - radius - circleWidth - textLineSpace, textPaint);
//            通过旋转画布的方式快速设置刻度
//            计算画布每次需要旋转的角度
            canvas.rotate(360 / clockNumbers24.length, mWidth / 2, mHeight / 2);//以圆中心进行旋转
        }
    }


    //画外部扇形
    private void drawMyOutAcr(Canvas canvas) {
        // 矩形区域
        acrRectF.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        Log.d(TAG, "drawMyAcr: radius = " + radius);
        for (int i = 0; i < circleDataList.size(); i++) {

            CircleData circleData = circleDataList.get(i);
            if (circleData.getType() == 1) {
                mOutCirclePaint.setColor(getContext().getResources().getColor(circleData.getColor()));
                Log.d(TAG, "drawMyAcr: 圆圈画笔颜色 = " + circleData.getColor());
                Log.d(TAG, "drawMyAcr: outCircleData = " + circleData.toString());
                //第三个参数是扫过的角度
                canvas.drawArc(acrRectF, circleData.getStartAngle(), circleData.getSweepAngle(), false,
                        mOutCirclePaint);
            }
        }
    }


    public void initData() {
        circleDataList = new ArrayList<>();
        //模拟数据
        for (int i = 0; i < colors.length; i++) {
            CircleData circleData = new CircleData();
            circleData.setColor(colors[i]);
            circleData.setName("第" + i + "个");
            circleData.setType(1);
            Log.d(TAG, "initData: colors[i] = " + colors[i]);
            circleData.setStartAngle(startAngel);
            circleData.setEndAngle(startAngel + sweepAngle);
            circleData.setSweepAngle(sweepAngle);
            startAngel = startAngel + sweepAngle;
            Log.d(TAG, "initData: outCircleData  = " + circleData.toString());
            circleDataList.add(circleData);
        }

        //模拟数据
        for (int i = colors.length; i > 0; i--) {
            CircleData circleData = new CircleData();
            circleData.setName("inner第" + i + "个");
            circleData.setColor(colors[i - 1]);
            circleData.setType(0);
            Log.d(TAG, "inner initData: colors[i-1] = " + colors[i - 1]);
            circleData.setStartAngle(startAngel);
            circleData.setEndAngle(startAngel + sweepAngle);
            circleData.setSweepAngle(sweepAngle);
            startAngel = startAngel + sweepAngle;
            Log.d(TAG, "inner  initData: outCircleData  = " + circleData.toString());
            circleDataList.add(circleData);
        }
    }

    /**
     * 获取当前时间
     *
     * @return 时间戳long
     */
    public static long getNowTime() {

        return System.currentTimeMillis();
    }


    ArrayList<CircleData> outList = new ArrayList();

    //扩展 外部改变数据更新view
    public void setData(ArrayList<CircleData> list) {
        outList = list;
        invalidate();
    }
}

```