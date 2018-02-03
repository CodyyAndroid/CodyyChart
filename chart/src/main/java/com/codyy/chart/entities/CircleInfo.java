package com.codyy.chart.entities;

/**
 * 记录一个圆的基本信息，与安心点坐标 和 半径
 * Created by poe on 19/04/17.
 */

public class CircleInfo {
    /**
     * 圆心坐标
     */
    private CirclePoint centerPoint;
    /**
     * 半径
     */
    private double radius;

    public CirclePoint getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(CirclePoint centerPoint) {
        this.centerPoint = centerPoint;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
