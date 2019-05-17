package com.codyy.codyychart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codyy.chart.entities.CNP;
import com.codyy.chart.views.PArrowChart;

import java.util.ArrayList;
import java.util.List;

/**
 * 箭头的环形统计图.
 */
public class CircleArrowActivity extends Activity {

    private PArrowChart mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_arrow);
        mView = findViewById(R.id.arrow_chart);
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

        int total = 6770;
        mView.configData(total,data);
    }

    public static void start(Context context , Class to){
        Intent inteng = new Intent(context.getApplicationContext(),to);
        context.startActivity(inteng);
    }
}
