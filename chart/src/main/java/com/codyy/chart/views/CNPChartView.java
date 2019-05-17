package com.codyy.chart.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;

import com.codyy.chart.R;
import com.codyy.chart.entities.CNP;
import com.codyy.chart.entities.CirclePoint;
import com.codyy.chart.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * circle null point chart .
 * @author caixingming
 * create at 2018/02/01
 */
public class CNPChartView extends View {

    private static final String TAG = "CNPChartView";

    private int mRadius = 300;//半径
    private int mLineWidth = 20;//线条的粗度.
    private Paint mPaint;
    private int mTotalCount = 1000;//总数.
    private float mBeginAngle = 270;//默认从下方开始绘制.
    private List<CNP> mData = new ArrayList<>();
    private SparseIntArray colors  = new SparseIntArray();
    private int mTipRadius = 15;//文字前的圆点半径.


    public CNPChartView(Context context) {
        super(context);
        init(null);
    }

    public CNPChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }



    public CNPChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CNPChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if(!isInEditMode()){
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);

            mRadius = getResources().getDisplayMetrics().widthPixels/5;
            mLineWidth = ViewUtils.dp2dx(getContext().getApplicationContext(),6);
            //init colors.
            colors.append(1,getResources().getColor(R.color.cnp_color1));
            colors.append(2,getResources().getColor(R.color.cnp_color2));
            colors.append(3,getResources().getColor(R.color.cnp_color3));
            colors.append(4,getResources().getColor(R.color.cnp_color4));
            colors.append(5,getResources().getColor(R.color.cnp_color5));
            colors.append(6,getResources().getColor(R.color.cnp_color6));
            colors.append(7,getResources().getColor(R.color.cnp_color7));
            colors.append(8,getResources().getColor(R.color.cnp_color8));
        }
    }


    /**
     * 数据配置.
     * @param total
     * @param datas
     */
    public void configData(int total,List<CNP> datas){
        //初始化启动绘制的角度.
        mBeginAngle = 270;
        if(total <= 0 || datas == null) return;

        mTotalCount = total;
        mData = datas;
        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getHeight();

        //换一个圆
        CirclePoint p = new CirclePoint();
        p.setX(screenWidth/4);
        p.setY(screenHeight/2);
        //非空验证
        if(!mData.isEmpty()&&mData.size()<1) return;


        RectF rect ;

        //绘制学科
        int index = 0;
        for(CNP cnp : mData) {
            index++;
            rect = getRectF(screenWidth,screenHeight);
            mPaint.setStrokeWidth(mLineWidth);
            mPaint.setColor(getResources().getColor(R.color.cnp_color_gray));
            canvas.drawCircle(p.getX(),p.getY(),mRadius+mLineWidth/2,mPaint);
            mPaint.setColor(colors.get(index));
            float sweepAngle = ViewUtils.getSweepAngle(cnp.getCount(),mTotalCount);
            canvas.drawArc(rect,mBeginAngle,-sweepAngle,false,mPaint);

            mRadius-=ViewUtils.dp2dx(getContext().getApplicationContext(),12);
            //circle point
            mPaint.setStyle(Paint.Style.FILL);
            int px = screenWidth/2+ViewUtils.dp2dx(getContext().getApplicationContext(),5);
            int py = screenHeight/2 - mRadius+ViewUtils.dp2dx(getContext().getApplicationContext(),12)*(index-1);
            //tips point
            canvas.drawCircle(px,py,mTipRadius,mPaint);
            //tips txt.
            drawDesc(canvas,px,py,cnp.getTitle(),screenWidth);
            mPaint.setStyle(Paint.Style.STROKE);
        }
    }

    private void drawDesc(Canvas canvas, int x,int y ,String text,int screenWidth) {
        //计算文字位置
        float dip = getResources().getDisplayMetrics().density;
        //计算文字居中.
        int textWidth = ViewUtils.dp2dx(getContext().getApplicationContext(),50);
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(14 * dip);//字体36px
        int moveY = (int)(5*dip);
        int moveX = ViewUtils.dp2dx(getContext().getApplicationContext(),10);

        Rect targetRect = new Rect(x, y-moveY,screenWidth, y+moveY);
        int baseline = targetRect.bottom;//文字底线.
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, x+moveX, baseline, mPaint);
    }

    public RectF getRectF(int screenWidth, int screenHeight) {
        int x = screenWidth/4-mLineWidth/2-mRadius;
        int y = screenHeight/2-mLineWidth/2-mRadius;
        int bottom = screenHeight/2+mLineWidth/2+mRadius;
        int right = screenWidth/4+mLineWidth/2+mRadius;
        RectF rect = new RectF(x,y,right,bottom);
        return rect;
    }
}
