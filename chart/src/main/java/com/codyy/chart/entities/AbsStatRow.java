package com.codyy.chart.entities;


/**
 * 统计表一行数据
 * Created by ldh on 2017/5/2.
 */

public abstract class AbsStatRow {
    private String rowTitle;

    public AbsStatRow() {
    }

    public AbsStatRow(String rowTitle) {
        this.rowTitle = rowTitle;
    }

    public String getRowTitle() {
        return rowTitle;
    }

    public void setRowTitle(String rowTitle) {
        this.rowTitle = rowTitle;
    }

    public abstract String getCellByIndex(int index);
    public abstract int getCellStatus(int index);

}
