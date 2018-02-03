package com.codyy.chart.entities;

public class CNP {
    /**
     * 统计名称
     */
    private String name;
    /**
     * 统计数据
     */
    private int count;

    public CNP(String name, int count) {
        this.name = name;
        this.count = count;
    }

    /*
     * return {name+" "+count}
     */
    public String getTitle(){
        return name+" "+count;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
