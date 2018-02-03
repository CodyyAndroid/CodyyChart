package com.codyy.chart.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 柱状图
 * Created by LDH on 2017/4/14.
 */

public class BarChartView extends View {
    private static final String TAG = "BarCharView";
    /**
     * top5城市
     */
    private List<String> mCities = new ArrayList<>(5);
    /**
     * top5城市的合格率
     */
    private List<Float> mPercentValues = new ArrayList<>(5);

    /**
     * X坐标轴的颜色
     */
    private int mXpirotColor = Color.parseColor("#dcdcdc");
    /**
     * X坐标轴画笔
     */
    private Paint mLinePaint;
    /**
     * X坐标轴圆点的颜色
     */
    private int mSubjectPtColor = Color.parseColor("#dcdcdc");
    /**
     * X坐标轴圆点的半径
     */
    private float mSubjectPtRadius = 5f;
    /**
     * 圆点画笔
     */
    private Paint mCirclePaint;
    /**
     * x坐标文字颜色
     */
    private int mXTextColor = Color.parseColor("#333333");
    /**
     * x坐标文字大小
     */
    private float mXTextSize = 13f;
    /**
     * X坐标轴的画笔
     */
    private TextPaint mXTextPaint;
    /**
     * 柱状图的值的画笔
     */
    private TextPaint mValuePaint;
    /**
     * 柱状图和X坐标之间的距离
     */
    private float mYdistance = 15f;
    /**
     * 柱状图之间的间距
     */
    private float mXdistance = 60f;
    /**
     * 柱状图间距个数，默认为4
     */
    private int mDistanceCount = 4;
    /**
     * 柱状背景的宽度
     */
    private float mShadowWidth;
    /**
     * 柱状背景的高度
     */
    private float mShadowHeight;
    /**
     * 柱状背景的颜色
     */
    private int mShadowColor = Color.parseColor("#FFF1F1F1");
    /**
     * 柱状图起始颜色
     */
    private int mBarBgStartColor = Color.parseColor("#4ca2f8");
    /**
     * 柱状图结束颜色
     */
    private int mBarBgEndColor = Color.parseColor("#FF4CEAFD");
    /**
     * 柱状背景画笔
     */
    private Paint mShadowPaint;
    /**
     * 柱状图画笔
     */
    private Paint mBarPaint;
    /**
     * 柱状图宽度，应为阴影的三分之一
     */
    private float mBarWidth;
    /**
     * 数值颜色
     */
    private int mValueColor = Color.parseColor("#333333");
    /**
     * 数值大小
     */
    private float mValueTextSize = 12f;
    /**
     * 柱状图数量
     */
    private int mBarCount = 5;
    /**
     * view的宽高
     */
    private float mViewWidth, mViewHeight;
    /**
     * X轴到底部的距离
     */
    private static final int X_TO_BOTTOM_DISTANCE = 100;
    /**
     * X轴内容到底部的距离
     */
    private static final int X_CONTENT_TO_BOTTOM_DISTANCE = 40;
    /**
     * 柱状条到底部的距离
     */
    private static final int BAR_TO_BOTTOM_DISTANCE = 140;
    /**
     * 每次刷新间隔时间，用于动画
     */
    private static final int VIEW_REFRESH_TIME = 20;
    /**
     * 柱状图动画总时长，默认1秒
     */
    private int mAnimationTime = 600;
    /**
     * 动画比例
     */
    private int mRefreshCount = mAnimationTime / VIEW_REFRESH_TIME;

    private int mAnimationPercent = 1;
    /**
     * 动画线程
     */
    private Thread mThread;
    /**
     * 屏幕的边界，用于判断某个view是否在屏幕上
     */
    private Rect mRect;

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 设置5个城市
     *
     * @param cities
     */
    public void setCities(List<String> cities) {
        mCities = cities;
        //mDistanceCount = mCities.size() - 1;
        //mBarCount = mCities.size();
    }

    /**
     * 设置5个城市的数值
     *
     * @param percentValues
     */
    public void setPercentValues(List<Float> percentValues) {
        mPercentValues = percentValues;
        calMax();
    }

    /**
     * 设置动画时长
     *
     * @param animationTime
     */
    public void setAnimationTime(int animationTime) {
        mAnimationTime = animationTime;
    }

    /**
     * 初始化画笔等
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, @Nullable AttributeSet attrs) {
        calMax();
        mLinePaint = new Paint();
        mLinePaint.setColor(mXpirotColor);
        mLinePaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mSubjectPtColor);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mXTextPaint = new TextPaint();
        mXTextPaint.setAntiAlias(true);
        mXTextPaint.setColor(mXTextColor);
        mXTextPaint.setStyle(Paint.Style.FILL);
        mXTextPaint.setTextAlign(Paint.Align.CENTER);
        mXTextPaint.setTextSize(dip2px(getContext(), mXTextSize));

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(mShadowColor);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStyle(Paint.Style.FILL);

        mValuePaint = new TextPaint();
        mValuePaint.setAntiAlias(true);
        mValuePaint.setTextAlign(Paint.Align.CENTER);
        mValuePaint.setColor(mXTextColor);
        mValuePaint.setTextSize(dip2px(getContext(), mValueTextSize));

        //获取屏幕宽高
        Point point = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getSize(point);
        int screenWidth = point.x;
        int screenHeight = point.y;
        mRect = new Rect(0, 0, screenWidth, screenHeight);
    }

    /**
     * 设置开始动画
     */
    public void setAnimation() {
        invalidate();
    }

    /**
     * 动画归0
     */
    public void setInitial() {
        mAnimationPercent = 0;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //画X轴
        canvas.drawLine(0, mViewHeight - X_TO_BOTTOM_DISTANCE, mViewWidth, mViewHeight - X_TO_BOTTOM_DISTANCE, mLinePaint);
        //画X轴圆点
        for (int i = 0; i < mBarCount; i++) {
            canvas.drawCircle((i * mXdistance + i * mShadowWidth + mShadowWidth / 2),
                    mViewHeight - X_TO_BOTTOM_DISTANCE, mSubjectPtRadius, mCirclePaint);
        }
        //画X轴内容
        for (int i = 0; i < mBarCount; i++) {
            if (mCities.size() > 0 && i < mCities.size()) {
                if (mCities.get(i) != null) {
                    canvas.drawText(mCities.get(i).length() > 4 ? (mCities.get(i).substring(0, 3) + "...") : mCities.get(i), (i * mXdistance + i * mShadowWidth + mShadowWidth / 2),
                            mViewHeight - X_CONTENT_TO_BOTTOM_DISTANCE, mXTextPaint);
                }
            }
        }
        //画阴影
        for (int i = 0; i < mBarCount; i++) {
            RectF rectF = new RectF(i * (mShadowWidth + mXdistance), 0,
                    i * mXdistance + i * mShadowWidth + mShadowWidth, mShadowHeight);
            canvas.drawRect(rectF, mShadowPaint);
        }

        //画柱状图及柱状图数值
        for (int i = 0; i < mBarCount; i++) {
            if (mPercentValues.size() > 0 && i < mPercentValues.size()) {
                RectF rectF = new RectF(i * mShadowWidth + i * mXdistance + mShadowWidth / 3 - 10,
                        mShadowHeight - (mPercentValues.get(i) * mAverigePer) / mRefreshCount * mAnimationPercent,
                        i * mShadowWidth + i * mXdistance + 2 * mShadowWidth / 3 + 10,
                        mViewHeight - BAR_TO_BOTTOM_DISTANCE);

                //颜色渐变
                LinearGradient gradient = new LinearGradient(i * mXdistance + i * mShadowWidth + mShadowWidth / 2,
                        mShadowHeight - (mPercentValues.get(i) * mAverigePer) / mRefreshCount * mAnimationPercent,
                        i * mXdistance + i * mShadowWidth + mShadowWidth / 2,
                        mViewHeight - 180,
                        mBarBgStartColor,
                        mBarBgEndColor,
                        Shader.TileMode.CLAMP);
                mBarPaint.setShader(gradient);
                if (mPercentValues.get(i) > 0) {
                    canvas.drawRoundRect(rectF, 35f, 35f, mBarPaint);
                }

                canvas.drawText(new BigDecimal(mPercentValues.get(i) / mRefreshCount * mAnimationPercent).setScale(0, BigDecimal.ROUND_HALF_UP) +"",
                        i * mXdistance + i * mShadowWidth + mShadowWidth / 2,
                        mShadowHeight - (mPercentValues.get(i) * mAverigePer) / mRefreshCount * mAnimationPercent - 15, mValuePaint);
            } else {
                canvas.drawText("0", i * mXdistance + i * mShadowWidth + mShadowWidth / 2,
                        mShadowHeight - 15, mValuePaint);
            }
        }
        canvas.save();
        canvas.restore();
        Log.d(TAG, "draw");

        //执行动画
        if (getLocalVisibleRect(mRect)) {
            if (mAnimationPercent < mRefreshCount) {
                invalidate();
                mAnimationPercent++;
            }
        } else {
            setInitial();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        //计算各个view的大小
        mShadowWidth = (mViewWidth - mDistanceCount * mXdistance) / mBarCount;
        mShadowHeight = mViewHeight - 160;
        if (mMaxValue > 0) {
            mAverigePer = (mShadowHeight - SPACE_TOP_VALUE) / mMaxValue;
        }
        mBarWidth = mShadowWidth / 3;
        Log.d(TAG, "init mShadowWidth:" + mShadowWidth + "mBarWidth:" + mBarWidth);
        invalidate();
        Log.d(TAG, "onSizeChanged width:" + mViewHeight + "  height:" + mViewHeight);
    }

    private float mMaxValue;
    /**
     * 每个单位数值占多高
     */
    private float mAverigePer;
    /**
     * 留出距离来显示顶部的数值
     */
    private static final int SPACE_TOP_VALUE = 50;

    /**
     * 计算最大值
     *
     * @return
     */
    private float calMax() {
        for (int i = 0; i < mPercentValues.size(); i++) {
            if (mMaxValue < mPercentValues.get(i)) {
                mMaxValue = mPercentValues.get(i);
            }
        }
        return mMaxValue;
    }


    /**
     * dp转化为 px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
