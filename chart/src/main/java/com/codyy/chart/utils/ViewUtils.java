package com.codyy.chart.utils;

import android.content.Context;

import com.codyy.chart.entities.CirclePoint;

public class ViewUtils {

    /**
     * 计算所跨越的区域
     * @param count
     * @return
     */
    public static float getSweepAngle(int count,int total) {
        float result = 0;
        result = (count*360f)/total;
        return result;
    }

    /**
     * dp -> px .
     * @param applicationContext
     * @param num
     * @return
     */
    public static int dp2dx(Context applicationContext,int num){
        return (int)(applicationContext.getResources().getDisplayMetrics().density*num);
    }


    /**
     * 根据角度计算圆弧的中心点坐标
     * @param centerPoint
     * @param angle
     * @param raius
     * @return
     */
    public static CirclePoint caculatePoint(CirclePoint centerPoint, float angle,int raius){
        CirclePoint resultPoint = new CirclePoint();

        int x = centerPoint.x+(int)(raius*Math.cos(angle*3.14/180));
        int y = centerPoint.y+(int)(raius*Math.sin(angle*3.14/180));

        resultPoint.setX(x);
        resultPoint.setY(y);

        return resultPoint;
    }
}
