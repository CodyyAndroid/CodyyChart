package com.codyy.codyychart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.codyy.chart.views.BarChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * 柱状图
 */
public class BarActivity extends Activity {
    //柱状图
    BarChartView mChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);
        init();
    }

    private void init() {

        mChartView = findViewById(R.id.bar_chart_view);
        //city
        List<String> cities = new ArrayList<>();
        cities.add("北京");
        cities.add("上海");
        cities.add("广州");
        cities.add("深圳");
        cities.add("苏州");

        mChartView.setCities(cities);
        List<Float> values = new ArrayList<>();
        values.add(55.0f);
        values.add(25.0f);
        values.add(45.0f);
        values.add(65.0f);
        values.add(85.0f);

        //数值。
        mChartView.setPercentValues(values);
    }

    public static void start(Context context , Class to){
        Intent inteng = new Intent(context.getApplicationContext(),to);
        context.startActivity(inteng);
    }
}
