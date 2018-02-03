package com.codyy.codyychart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.codyy.chart.entities.CNP;
import com.codyy.chart.views.CNPArcView;
import com.codyy.chart.views.CNPChartView;

import java.util.ArrayList;
import java.util.List;


public class CircleActivity extends Activity {

    private CNPArcView mArcView;
    private CNPChartView mChartView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        mArcView = findViewById(R.id.cnp_arc_view);
        mChartView = findViewById(R.id.cnp_chart_view);
        showCircle();
    }

    private void showCircle() {
        //2018/2/1 显示图形统计图
        List<CNP> data = new ArrayList<>();
        data.add(new CNP("语文",1354));
        data.add(new CNP("化学",1354));
        data.add(new CNP("美术",1354));
        data.add(new CNP("数学",1354));
        data.add(new CNP("英语",1354));
        data.add(new CNP("生物",1354));
        data.add(new CNP("地理",1354));

        int total = 11000;
        mArcView.configData(total,data);

        List<CNP> data2 = new ArrayList<>();
        data2.add(new CNP("本学期录制课程",1354));
        data2.add(new CNP("省级分享课程",1354));
        data2.add(new CNP("市级分享课程",1354));
        data2.add(new CNP("区县级分享课程",1354));
        data2.add(new CNP("乡镇分享课程",1354));
        data2.add(new CNP("校内分享课程",1354));

        mChartView.configData(total,data2);
    }

    public static void start(Context context , Class to){
        Intent inteng = new Intent(context.getApplicationContext(),to);
        context.startActivity(inteng);
    }
}
