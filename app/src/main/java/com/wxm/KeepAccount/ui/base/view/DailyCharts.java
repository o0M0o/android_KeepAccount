package com.wxm.KeepAccount.ui.base.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import com.wxm.KeepAccount.BaseLib.AppGobalDef;
import com.wxm.KeepAccount.BaseLib.RecordItem;

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
    }

    public DailyCharts(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public DailyCharts(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        chartLabels.addAll(ls_date);

        // 计算 Y axis
        List<Double> dsA = new LinkedList<>();
        List<Double> dsB = new LinkedList<>();
        Double retMax = 0d;
        Double retMin = 0d;
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

            if(retA > retMax)   {
                retMax = retA;
            }

            if(retB > retMax)   {
                retMax = retB;
            }

            if(retA < retMin)   {
                retMax = retA;
            }

            if(retB < retMin)   {
                retMax = retB;
            }

            dsA.add(retA);
            dsB.add(retB);
        }

        BarDataset.add(new BarData(AppGobalDef.CNSTR_RECORD_INCOME, dsA, Color.BLUE));
        BarDataset.add(new BarData(AppGobalDef.CNSTR_RECORD_PAY, dsB, Color.CYAN));

        if(retMax > 0d) {
            chart.getDataAxis().setAxisMax((retMax / 100) * 100 + 100);
            chart.getDataAxis().setAxisMin((retMin / 100) * 100);

            int step = (int) ((retMax - retMin) / 6);
            chart.getDataAxis().setAxisSteps(step);
        }
        else    {
            chart.getDataAxis().setAxisMax(700);
            chart.getDataAxis().setAxisMin(0);
            chart.getDataAxis().setAxisSteps(7);
        }

        chartRender();
    }
}
