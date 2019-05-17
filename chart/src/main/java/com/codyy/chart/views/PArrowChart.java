package com.codyy.chart.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;

import com.codyy.chart.R;
import com.codyy.chart.entities.CNP;
import com.codyy.chart.entities.CirclePoint;
import com.codyy.chart.utils.ViewUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *  首尾相接环形统计图.
 *  recycling arrow combine an circle chart.
 * @author caixingming 2019/05/16
 */
public class PArrowChart extends View {

    private static final String TAG = "CNPChartView";

    private int mRadius = 300;//半径
    private int mLineWidth = 50;//线条的粗度.
    private Paint mPaint;
    private Paint mArrowPaint;
    /**
     * 箭头的高度：底和定点的距离
     */
    private float mArrowSize =50;
    /**
     * 半个三角形的顶角度数，h = tan(degree)*arrowSize
     */
    private float mArrowDegree = (float) (Math.PI/5);

    private int mTotalCount = 1000;//总数
    private int mExtraCount = 100;//其他.
    private float mBeginAngle = -90;//默认从正上方(270度)开始绘制.
    private List<CNP> mData = new ArrayList<>();
    private SparseIntArray colors  = new SparseIntArray();

    public PArrowChart(Context context) {
        super(context);
        init(null);
    }

    public PArrowChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PArrowChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PArrowChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if(!isInEditMode()){
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);

            mArrowPaint = new Paint();
            mArrowPaint.setAntiAlias(true);// 抗锯齿效果
            mArrowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mArrowPaint.setColor(Color.parseColor("#000000"));
            mArrowPaint.setStrokeCap(Paint.Cap.ROUND);
            mArrowPaint.setStrokeWidth(5);

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
            mArrowPaint.setColor(colors.get(index));
            float sweepAngle = ViewUtils.getSweepAngle(cnp.getCount(),mTotalCount);
            canvas.drawArc(rect,mBeginAngle,sweepAngle>20?sweepAngle-20:sweepAngle,false,mPaint);
            //将我们要画箭头的位置旋转到顶部，方便我们计算箭头的坐标，绘制完箭头在将画布恢复
            canvas.save();
            float centx = p.getX();
            float centy = p.getY();
            // 旋转画布
            canvas.rotate(mBeginAngle+sweepAngle*2, centx, centy);
            //绘制箭头
            float relaRadius = mRadius+mLineWidth/2;
            Path triangle = new Path();
            triangle.moveTo(centx-10, (float) (centy - relaRadius - mArrowSize*Math.tan(mArrowDegree)));
            triangle.lineTo(centx-10, (float) (centy - relaRadius + mArrowSize*Math.tan(mArrowDegree)));
            triangle.lineTo(centx + mArrowSize-10, centy - relaRadius);
            triangle.close();
            canvas.drawPath(triangle,mArrowPaint);
            canvas.restore();
            //----绘制箭头结束------

            mBeginAngle +=sweepAngle;
            mPaint.setStyle(Paint.Style.STROKE);
        }
        //绘制总数.
        drawTotal(canvas,p);
        //绘制下面文字.
        drawInfo(canvas,p);
    }


    /**
     * 绘制总数.
     * @param canvas
     * @param center 中心原点.
     */
    private void drawTotal(Canvas canvas, CirclePoint center){
        //计算文字位置
        float dip = getResources().getDisplayMetrics().density;
        //计算文字居中.
        int textWidth = ViewUtils.dp2dx(getContext().getApplicationContext(),50);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(colors.get(8));
        mPaint.setTextSize(24 * dip);//字体36px
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        Rect targetRect = new Rect(center.getX()-textWidth/2,center.getY()-(int)(30*dip),center.getX()+textWidth/2,center.getY());
        int baseline = targetRect.bottom;//文字底线.
        canvas.drawText("总时长", targetRect.centerX(), baseline-top/2-bottom/2, mPaint);
        //总时长.
        Rect targetRectNum = new Rect(center.getX()-textWidth/2,center.getY()+(int)(10*dip),center.getX()+textWidth/2,center.getY()+(int)(30*dip));
        baseline = targetRectNum.bottom;//文字底线.
        canvas.drawText(String.valueOf(mTotalCount), targetRectNum.centerX(), baseline-top/2-bottom/2, mPaint);
    }

    //画出文字介绍信息.
    private void drawInfo(Canvas canvas, CirclePoint center){
        int i= 0;
        for(CNP cnp : mData){
            i++;
            //计算文字居中.
            int textWidth = ViewUtils.dp2dx(getContext().getApplicationContext(),180);

            //draw rect .
            int margin = ViewUtils.dp2dx(getContext().getApplicationContext(),16);
            int height = ViewUtils.dp2dx(getContext().getApplicationContext(),30);
            int top = center.getY()+mRadius+mLineWidth+height*2;
//            计算行数，增加对应行高
            int lineNum = (i-1)/2;
            int columnNum = (i-1)%2;

            mPaint.setTextAlign(Paint.Align.LEFT);
            int rectLeft = columnNum==0?(center.getX() -textWidth - margin):(center.getX()+margin+height*2);
            int rectTop = top-margin/2+(lineNum)*(height+margin);
            int rectRight = rectLeft+margin;
            int rectBottom = rectTop+margin;
            Rect rect = new Rect(rectLeft,rectTop, rectRight,rectBottom);
            mPaint.setColor(colors.get(i));
            canvas.drawRect(rect,mPaint);
            //draw text .
            Rect rectText = new Rect(rectRight+margin,rectTop, rectRight+margin+textWidth,rectTop+height);
            //计算百分比.
            String showTxt = cnp.getName();//+" "+myPercent(cnp.getCount(),mTotalCount);
            canvas.drawText(showTxt,rectText.left,rectText.centerY()+margin/4,mPaint);

            //20% 灰色显示
            float[] nameWidths =new float[10] ;
            mPaint.getTextWidths(showTxt,nameWidths);
            //蓝色文本的宽度 为 widths
            float blueWidth = 0.0F;
            for (int j = 0; j < nameWidths.length; j++) {
                blueWidth += nameWidths[j];
            }
            //设置mpaint颜色 为灰色
            mPaint.setColor(getResources().getColor(R.color.cnp_color_gray));
            //计算出红色文本的起点X坐标，画红色文本
            String percent = myPercent(cnp.getCount(),mTotalCount);
            canvas.drawText(percent, rectText.left + blueWidth+margin, rectText.centerY()+margin/4, mPaint);
        }
    }

    public static String myPercent(int y, int z) {
        String baifenbi = "";// 接受百分比的值
        double baiy = y * 1.0;
        double baiz = z * 1.0;
        double fen = baiy/ baiz;
        // NumberFormat nf = NumberFormat.getPercentInstance(); 注释掉的也是一种方法
        // nf.setMinimumFractionDigits( 2 ); 保留到小数点后几位
        DecimalFormat df1 = new DecimalFormat("##%"); // ##.00%
        // 百分比格式，后面不足2位的用0补齐
        // baifenbi=nf.format(fen);
        baifenbi = df1.format(fen);
        System.out.println(baifenbi);
        return baifenbi;
    }


}
