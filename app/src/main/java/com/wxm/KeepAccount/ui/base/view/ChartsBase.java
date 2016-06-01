package com.wxm.KeepAccount.ui.base.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.xclcharts.chart.BarChart3D;
import org.xclcharts.chart.BarData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterDoubleCallBack;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.BarPosition;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * charts基类
 * Created by 123 on 2016/6/1.
 */
public abstract class ChartsBase extends ChartView {
    private String TAG = "ChartsBase";
    private BarChart3D chart = new BarChart3D();

    //标签轴
    private List<String> chartLabels = new LinkedList<String>();
    //数据轴
    private List<BarData> BarDataset = new LinkedList<BarData>();

    Paint mPaintToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ChartsBase(Context context)  {
        super(context);
        initView();
    }

    public ChartsBase(Context context, AttributeSet attrs){
        super(context, attrs);
        initView();
    }

    public ChartsBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * 重新绘制chart
     */
    public abstract void ReRenderChart();

    private void initView()
    {
        chartLabels();
        chartDataSet();
        chartRender();

        //綁定手势滑动事件
        this.bindTouch(this,chart);
    }

    //Demo中bar chart所使用的默认偏移值。
    //偏移出来的空间用于显示tick,axistitle....
    protected int[] getBarLnDefaultSpadding()
    {
        int [] ltrb = new int[4];
        ltrb[0] = DensityUtil.dip2px(getContext(), 40); //left
        ltrb[1] = DensityUtil.dip2px(getContext(), 60); //top
        ltrb[2] = DensityUtil.dip2px(getContext(), 20); //right
        ltrb[3] = DensityUtil.dip2px(getContext(), 40); //bottom
        return ltrb;
    }

    protected int[] getPieDefaultSpadding()
    {
        int [] ltrb = new int[4];
        ltrb[0] = DensityUtil.dip2px(getContext(), 20); //left
        ltrb[1] = DensityUtil.dip2px(getContext(), 65); //top
        ltrb[2] = DensityUtil.dip2px(getContext(), 20); //right
        ltrb[3] = DensityUtil.dip2px(getContext(), 20); //bottom
        return ltrb;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //图所占范围大小
        chart.setChartRange(w,h);
    }

    @Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        super.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            triggerClick(event.getX(),event.getY());
        }
        return true;
    }

    //触发监听
    private void triggerClick(float x,float y)
    {
        if(!chart.getListenItemClickStatus()) return ;

        BarPosition record = chart.getPositionRecord(x,y);
        if( null == record) return;

        BarData bData = BarDataset.get(record.getDataID());
        Double bValue = bData.getDataSet().get(record.getDataChildID());

        //在点击处显示tooltip
        mPaintToolTip.setColor(Color.WHITE);
        chart.getToolTip().getBackgroundPaint().setColor(Color.rgb(75, 202, 255));
        chart.getToolTip().getBorderPaint().setColor(Color.RED);
        chart.getToolTip().setCurrentXY(x,y);
        chart.getToolTip().addToolTip(
                " Current Value:" +Double.toString(bValue),mPaintToolTip);
        chart.getToolTip().getBackgroundPaint().setAlpha(100);
        this.invalidate();
    }


    protected void chartRender()
    {
        try {

            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int [] ltrb = getBarLnDefaultSpadding();
            chart.setPadding( DensityUtil.dip2px(getContext(), 50),ltrb[1],
                    ltrb[2], ltrb[3] + DensityUtil.dip2px(getContext(), 25) );

            //显示边框
            chart.showRoundBorder();


            //数据源
            chart.setDataSource(BarDataset);
            chart.setCategories(chartLabels);

            //坐标系
            chart.getDataAxis().setAxisMax(500);
            chart.getDataAxis().setAxisMin(100);
            chart.getDataAxis().setAxisSteps(100);
            //chart.getCategoryAxis().setAxisTickLabelsRotateAngle(-45f);

            //隐藏轴线和tick
            chart.getDataAxis().hideAxisLine();
            //chart.getDataAxis().setTickMarksVisible(false);

            //标题
            chart.setTitle("本周水果销售情况");
            chart.addSubtitle("(XCL-Charts Demo)");
            chart.setTitleAlign(XEnum.HorizontalAlign.RIGHT);


            //背景网格
            chart.getPlotGrid().showHorizontalLines();
            chart.getPlotGrid().showVerticalLines();
            chart.getPlotGrid().showEvenRowBgColor();
            chart.getPlotGrid().showOddRowBgColor();


            //定义数据轴标签显示格式
            chart.getDataAxis().setTickLabelRotateAngle(-45);
            chart.getDataAxis().getTickMarksPaint().
                    setColor(Color.rgb(186, 20, 26));
            chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack(){

                @Override
                public String textFormatter(String value) {
                    // TODO Auto-generated method stub
                    Double tmp = Double.parseDouble(value);
                    DecimalFormat df=new DecimalFormat("#0");
                    String label = df.format(tmp).toString();
                    return (label +"公斤");
                }

            });

            //设置标签轴标签交错换行显示
            chart.getCategoryAxis().setLabelLineFeed(XEnum.LabelLineFeed.EVEN_ODD);

            //定义标签轴标签显示格式
            chart.getCategoryAxis().setLabelFormatter(new IFormatterTextCallBack(){

                @Override
                public String textFormatter(String value) {
                    // TODO Auto-generated method stub
                    return value;
                }

            });
            //定义柱形上标签显示格式
            chart.getBar().setItemLabelVisible(true);
            chart.setItemLabelFormatter(new IFormatterDoubleCallBack() {
                @Override
                public String doubleFormatter(Double value) {
                    // TODO Auto-generated method stub
                    DecimalFormat df=new DecimalFormat("#0.00");
                    String label = df.format(value).toString();
                    return label;
                }});

            //激活点击监听
            chart.ActiveListenItemClick();

            //仅能横向移动
            chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);


            //扩展横向显示范围
            //	chart.getPlotArea().extWidth(200f);

            //标签文字与轴间距
            chart.getCategoryAxis().setTickLabelMargin(5);

            //不使用精确计算，忽略Java计算误差
            chart.disableHighPrecision();

            // 设置轴标签字体大小
            chart.getDataAxis().getTickLabelPaint().setTextSize(26);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void chartLabels()
    {
        chartLabels.add("桃子(Peach)");
        chartLabels.add("梨子(Pear)");
        chartLabels.add("香蕉 (Banana)");
        chartLabels.add("苹果");
        chartLabels.add("桔子");
    }


    private void chartDataSet()
    {
        //标签对应的柱形数据集
        List<Double> dataSeriesA= new LinkedList<Double>();
        dataSeriesA.add(200d);
        dataSeriesA.add(250d);
        dataSeriesA.add(400d);
        dataSeriesA.add(450d);
        dataSeriesA.add(150d);

        List<Double> dataSeriesB= new LinkedList<Double>();
        dataSeriesB.add(300d);
        dataSeriesB.add(150d);
        dataSeriesB.add(450d);
        dataSeriesB.add(480d);
        dataSeriesB.add(200d);

        BarDataset.add(new BarData("左边店",dataSeriesA, Color.rgb(252, 210, 9)));
        BarDataset.add(new BarData("右边店",dataSeriesB,Color.rgb(55, 144, 206)));


        List<Double> dataSeriesC= new LinkedList<Double>();
        dataSeriesC.add(270d);
        dataSeriesC.add(180d);
        //dataSeriesC.add(450d);
        //dataSeriesC.add(380d);
        //dataSeriesC.add(230d);
        BarDataset.add(new BarData("右边店2",dataSeriesC,Color.rgb(155, 144, 206)));
    }
}
