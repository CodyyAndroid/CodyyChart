package com.codyy.codyychart;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

    private static final String TAG="mainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //柱状图
        findViewById(R.id.tv_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarActivity.start(MainActivity.this,BarActivity.class);
            }
        });

        //饼状图
        findViewById(R.id.tv_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"圆形统计图");
                CircleActivity.start(MainActivity.this,CircleActivity.class);
            }
        });

        //表格
        findViewById(R.id.tv_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleActivity.start(MainActivity.this,ScheduleActivity.class);
            }
        });

    }
}
