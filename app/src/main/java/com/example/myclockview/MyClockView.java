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

    private Paint mOutCirclePaint = null;//外层圆画笔
    private Paint mInnerCirclePaint = null;//内层圆画笔
    private Paint textPaint = null;//文字画笔
    private Paint linePaint = null;//刻度画笔
    private Paint testPaint = null;//测试画笔

    private float circleWidth = 60f;//外圈圆宽度
    private int lineTextSize = 30;//刻度线大小
    private int textPainSize = 20;//刻度文字大小
    private int textLineSpace = 20;//刻度和文字的间隔

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

    private List<OutCircleData> outCircleDataList;
    private List<OutCircleData> innerCircleDataList;
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
    private String[] clockNumbers12 = {"24", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",};

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasureSize(true, widthMeasureSpec);
        mHeight = getMeasureSize(false, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

        if (getMeasuredWidth() != 0 && getMeasuredHeight() != 0) {
            rectF = new RectF(0, 0, radius * 2, radius * 2);
        }

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

        //刻度文字画笔
        textPaint = new Paint();
        textPaint.setTextSize(lineTextSize);
        textPaint.setAntiAlias(true);//设置Paint为无锯齿
        textPaint.setColor(getContext().getResources().getColor(R.color.gary));
        textPaint.setStrokeWidth(textPainSize);

        //刻度画笔
        linePaint = new Paint();
        linePaint.setAntiAlias(true);//设置Paint为无锯齿
        linePaint.setColor(getContext().getResources().getColor(R.color.gary));//设置灰色
//        textPaint.setColor(708090)

//        StokPaint = new Paint();
//        StokPaint.setAntiAlias(true);

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


    //确定View大小
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.screenWidth = w;     //获取宽高
        this.screenHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.translate(screenWidth / 2, screenHeight / 2);
        //先设置画布为view的中心点  把当前画布的原点移到(center.x,center.y),后面的操作都以(10,10)作为参照点，默认原点为(0,0)
        canvas.save();


        //画外部刻度和文字
        drawClockText(canvas);
        //画外部圆环
        drawMyOutAcr(canvas);
        //画内部圆环
        drawMyInnerAcr(canvas);

        //画内部刻度文字
        drawInnerClockText(canvas);

        canvas.restore();//把当前画布返回（调整）到上一个save()状态之前
    }

    private void drawMyInnerAcr(Canvas canvas) {
//        mInnerCirclePaint
        // 矩形区域
        RectF acrRectF = new RectF();
        acrRectF.set(center.x - radius + circleWidth, center.y - radius + circleWidth, center.x + radius - circleWidth, center.y + radius - circleWidth);
        Log.d(TAG, "drawMyAcr: radius = " + radius);
        for (int i = 0; i < innerCircleDataList.size(); i++) {
            OutCircleData outCircleData = innerCircleDataList.get(i);
            mInnerCirclePaint.setColor(getContext().getResources().getColor(outCircleData.getColor()));
            Log.d(TAG, "drawMyAcr: 圆圈画笔颜色 = " + outCircleData.getColor());
            Log.d(TAG, "drawMyAcr: outCircleData = " + outCircleData.toString());
            //第三个参数是扫过的角度
            canvas.drawArc(acrRectF, outCircleData.getStartAngle(), outCircleData.getSweepAngle(), false, mInnerCirclePaint);
        }
    }

    //画刻度
    private void drawLine(Canvas canvas) {
        //刻度长度为20  circleWidth
        canvas.drawLine(center.x, center.y - radius - circleWidth / 2, center.x,
                center.y - radius - circleWidth, linePaint);
    }

    //画内部刻度
    private void drawInnerLine(Canvas canvas) {
        //刻度长度为20  circleWidth
        canvas.drawLine(center.x, center.y - radius + circleWidth, center.x,
                center.y - radius - circleWidth, linePaint);
    }

    //画外面的时间文字
    private void drawInnerClockText(Canvas canvas) {
        for (int i = 0; i <= clockNumbers12.length - 1; i++) {

            drawInnerLine(canvas);

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
        //绘制完后，把画布状态复原
//        canvas.restore();
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
        //绘制完后，把画布状态复原
//        canvas.restore();
    }

//    private void drawMyView(Canvas canvas, Paint paint, RectF rectF, int startAngle, int endAngle) {
//        paint.setStrokeJoin(Paint.Join.MITER);
//        paint.setStrokeCap(Paint.Cap.SQUARE);
//        canvas.drawArc(rectF, startAngle, endAngle, true, paint);
//    }


    //画扇形
    private void drawMyOutAcr(Canvas canvas) {
        // 矩形区域
        acrRectF.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        Log.d(TAG, "drawMyAcr: radius = " + radius);
        for (int i = 0; i < outCircleDataList.size(); i++) {

            OutCircleData outCircleData = outCircleDataList.get(i);
            mOutCirclePaint.setColor(getContext().getResources().getColor(outCircleData.getColor()));
            Log.d(TAG, "drawMyAcr: 圆圈画笔颜色 = " + outCircleData.getColor());
            Log.d(TAG, "drawMyAcr: outCircleData = " + outCircleData.toString());
            //第三个参数是扫过的角度
            canvas.drawArc(acrRectF, outCircleData.getStartAngle(), outCircleData.getSweepAngle(), false, mOutCirclePaint);
        }
    }

    public void setData() {


    }

    public void initData() {
        outCircleDataList = new ArrayList<>();
        //模拟数据
        for (int i = 0; i < colors.length; i++) {
            OutCircleData outCircleData = new OutCircleData();
            outCircleData.setColor(colors[i]);
            outCircleData.setName("第" + i + "个");
            Log.d(TAG, "initData: colors[i] = " + colors[i]);
            outCircleData.setStartAngle(startAngel);
            outCircleData.setEndAngle(startAngel + sweepAngle);
            outCircleData.setSweepAngle(sweepAngle);
            startAngel = startAngel + sweepAngle;
            Log.d(TAG, "initData: outCircleData  = " + outCircleData.toString());
            outCircleDataList.add(outCircleData);
        }

        innerCircleDataList = new ArrayList<>();
        //模拟数据
        for (int i = colors.length; i > 0; i--) {
            OutCircleData outCircleData = new OutCircleData();
            outCircleData.setName("inner第" + i + "个");
            outCircleData.setColor(colors[i - 1]);
            Log.d(TAG, "inner initData: colors[i-1] = " + colors[i - 1]);
            outCircleData.setStartAngle(startAngel);
            outCircleData.setEndAngle(startAngel + sweepAngle);
            outCircleData.setSweepAngle(sweepAngle);
            startAngel = startAngel + sweepAngle;
            Log.d(TAG, "inner  initData: outCircleData  = " + outCircleData.toString());
            innerCircleDataList.add(outCircleData);
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


    ArrayList<OutCircleData> outList = new ArrayList();

    public void setData(ArrayList<OutCircleData> list) {
        outList = list;
        invalidate();
    }
}