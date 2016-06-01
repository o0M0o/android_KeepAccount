package com.wxm.KeepAccount.ui.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

/**
 * 展示月数据的charts
 * Created by 123 on 2016/6/1.
 */
public class MonthlyCharts extends ChartsBase {
    final private static String TAG = "MonthlyCharts";

    public MonthlyCharts(Context context)  {
        super(context);
    }

    public MonthlyCharts(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public MonthlyCharts(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public  void ReRenderChart()    {
        Log.i(TAG, "ReRenderChart");
        chartRender();
    }
}
