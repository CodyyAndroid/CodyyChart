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
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
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
 *  {@link <http://ogd2b73e2.bkt.clouddn.com/circlenullpoint.jpg>}
 */
public class CNPArcView extends View {

    private static final String TAG = "CNPChartView";

    private int mRadius = 300;//半径
    private int mLineWidth = 50;//线条的粗度.
    private Paint mPaint;
    private int mTotalCount = 1000;//总数
    private int mExtraCount = 100;//其他.
    private float mBeginAngle = 90;//默认从下方开始绘制.
    private List<CNP> mData = new ArrayList<>();
    private SparseIntArray colors  = new SparseIntArray();

    public CNPArcView(Context context) {
        super(context);
        init(null);
    }

    public CNPArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CNPArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CNPArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if(!isInEditMode()){
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);

            mRadius = getResources().getDisplayMetrics().widthPixels/6;
            mLineWidth = ViewUtils.dp2dx(getContext().getApplicationContext(),20);
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
        mBeginAngle = 90;

        if(total <= 0 || datas == null) return;
        mTotalCount = total;
        mData = datas;
        if(mData.size()>0){
            mExtraCount = mTotalCount;
            for(CNP cc: mData){
                mExtraCount = mExtraCount - cc.getCount();
            }
            if(mExtraCount < 0 )mExtraCount = 0;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        //计算屏幕的宽度
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getHeight();

        CirclePoint p = new CirclePoint();
        p.setX(screenWidth/2);
        p.setY(screenHeight/2);

        //绘制圆环
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(mLineWidth);
        canvas.drawCircle(p.getX(),p.getY(),mRadius+mLineWidth/2,mPaint);

        int x = screenWidth/2-mLineWidth/2-mRadius;
        int y = screenHeight/2-mLineWidth/2-mRadius;
        int bottom = screenHeight/2+mLineWidth/2+mRadius;
        int right = screenWidth/2+mLineWidth/2+mRadius;
        RectF rect = new RectF(x,y,right,bottom);

        //非空验证
        if(!mData.isEmpty()&&mData.size()<1) return;
        //绘制学科
        int index = 0;
        for(CNP cnp : mData){
            index++;
            mPaint.setStrokeWidth(mLineWidth);
            mPaint.setColor(colors.get(index));
            float sweepAngle = ViewUtils.getSweepAngle(cnp.getCount(),mTotalCount);
            canvas.drawArc(rect,mBeginAngle,sweepAngle,false,mPaint);
            drawDesc(canvas,p,cnp.getTitle(),mBeginAngle+sweepAngle/2);
            mBeginAngle +=sweepAngle;
            mPaint.setStyle(Paint.Style.STROKE);
        }
        //绘制其他
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setColor(colors.get(8));
        float sweepAngle = ViewUtils.getSweepAngle(mExtraCount,mTotalCount);
        canvas.drawArc(rect,mBeginAngle,sweepAngle,false,mPaint);
        drawDesc(canvas,p,"其他 "+mExtraCount,mBeginAngle+sweepAngle/2);
        mBeginAngle +=sweepAngle;
        mPaint.setStyle(Paint.Style.STROKE);
    }



    /**
     *  根据位置绘制对应的说明文字.
     * @param canvas
     * @param p
     * @param text
     * @param angle  角度
     */
    private void drawDesc(Canvas canvas, CirclePoint p,String text,float angle) {
        Log.i(TAG,"angle : "+angle);
        // 2018/2/1 计算圆弧的中心点坐标并画出折线
        CirclePoint p1 = ViewUtils.caculatePoint(p,angle,mRadius+mLineWidth);
        //计算文字位置
        float dip = getResources().getDisplayMetrics().density;
        //计算文字居中.
        int textWidth = ViewUtils.dp2dx(getContext().getApplicationContext(),30);
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(14 * dip);//字体36px
        angle = angle%360;//对360度取余
        int moveY = angle<180?(int)(100*Math.abs(Math.sin(angle))+20):(int)(-100*Math.abs(Math.sin(angle))-20);
        int moveX = 160+textWidth;
        if(angle<90||angle>270){
            moveX = -160;
        }
        Rect targetRect = new Rect((p1.getX()-moveX), (p1.getY()+moveY), (p1.getX()-moveX+textWidth), (p1.getY()+moveY));
        int baseline = targetRect.bottom;//文字底线.
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, targetRect.centerX(), baseline, mPaint);
        //画说明的折线
        CirclePoint lin1 = new CirclePoint();
        lin1.setX(moveX<0?(p1.getX()+100-60):(p1.getX()-100));
        lin1.setY(p1.getY()+moveY-15);

        canvas.drawLine(lin1.x,lin1.y,(float)(lin1.x+60),lin1.y,mPaint);
        canvas.drawLine(moveX>0?(lin1.x+60):lin1.x,lin1.y,p1.x,p1.y,mPaint);
    }
}
