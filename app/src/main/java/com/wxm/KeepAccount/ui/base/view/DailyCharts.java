package com.wxm.KeepAccount.ui.base.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import com.wxm.KeepAccount.BaseLib.AppGobalDef;
import com.wxm.KeepAccount.BaseLib.RecordItem;
import com.wxm.KeepAccount.R;

import org.xclcharts.chart.BarData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 展示日数据的charts
 * Created by 123 on 2016/6/1.
 */
public class DailyCharts extends ChartsBase {
    final private static String TAG = "DailyCharts";

    public DailyCharts(Context context)  {
        super(context);
        chartTitle = "日统计";
        subChartTitle = "(每日收支统计)";
    }

    public DailyCharts(Context context, AttributeSet attrs){
        super(context, attrs);
        chartTitle = "日统计";
        subChartTitle = "(每日收支统计)";
    }

    public DailyCharts(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        chartTitle = "日统计";
        subChartTitle = "(每日收支统计)";
    }

    @Override
    public  void RenderChart(List<RecordItem> ls_data)    {
        Log.i(TAG, "RenderChart");

        chartLabels.clear();
        BarDataset.clear();

        // 日期作为x axis
        ArrayList<String> ls_date = new ArrayList<>();
        HashMap<String, ArrayList<RecordItem>> rimap = new HashMap<>();
        for (RecordItem ri: ls_data) {
            String day = ri.record_ts.toString().substring(0, 10);

            if(!ls_date.contains(day))  {
                ls_date.add(day);
            }

            if(null == rimap.get(day))  {
                rimap.put(day, new ArrayList<RecordItem>());
            }

            rimap.get(day).add(ri);
        }

        Collections.sort(ls_date);
        Collections.reverse(ls_date);
        chartLabels.addAll(ls_date);

        // 计算 Y axis
        List<Double> dsA = new LinkedList<>();
        List<Double> dsB = new LinkedList<>();
        for(String day : ls_date)   {
            Double retA = 0d;
            Double retB = 0d;

            for(RecordItem ri : rimap.get(day)) {
                if(ri.record_type.equals(AppGobalDef.CNSTR_RECORD_INCOME))  {
                    retA += ri.record_val.doubleValue();
                }
                else    {
                    retB += ri.record_val.doubleValue();
                }
            }

            dsA.add(retA);
            dsB.add(retB);
        }

        BarDataset.add(new BarData(AppGobalDef.CNSTR_RECORD_INCOME, dsA,
                            Color.BLUE));
        BarDataset.add(new BarData(AppGobalDef.CNSTR_RECORD_PAY, dsB,
                            Color.RED));

        chartRender();
    }
}