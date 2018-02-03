package com.codyy.codyychart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.codyy.chart.entities.StatRow;
import com.codyy.chart.entities.StatTableModel;
import com.codyy.chart.fragments.StatTableFragment;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {


    private StatTableFragment mStatTableFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        init();
    }

    private void init() {

        mStatTableFragment = new StatTableFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_fragment,mStatTableFragment);
        ft.commitAllowingStateLoss();

        StatTableModel<StatRow> statTableModel = new StatTableModel<>();

        statTableModel.setTitle("行政区");
        statTableModel.setColumnTitles(new String[]{"总数","使用数","总使用时间","总登录时间"});
        List<StatRow> statRows = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            statRows.add(new StatRow("areaName"+i,
                    "totalClass"+i,
                    "used"+i,
                    "totalUseTime"+i,
                    "totalLogonTime"+i));
        }
        statTableModel.setRows(statRows);
        mStatTableFragment.setTableModel(statTableModel);
    }

    public static void start(Context context , Class to){
        Intent inteng = new Intent(context.getApplicationContext(),to);
        context.startActivity(inteng);
    }
}
