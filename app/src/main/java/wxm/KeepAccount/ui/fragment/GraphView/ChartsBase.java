package wxm.KeepAccount.ui.fragment.GraphView;

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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * charts基类
 * Created by 123 on 2016/6/1.
 */
public abstract class ChartsBase extends ChartView {
    private final String TAG = "ChartsBase";
    protected final BarChart3D chart = new BarChart3D();

    /*
    private static final int[] ITEM_DRAWABLES = {
            R.drawable.ic_leave
            ,R.drawable.ic_switch};
     */

    //标签轴
    protected final List<String> chartLabels = new LinkedList<>();
    //数据轴
    protected final List<BarData> BarDataset = new LinkedList<>();

    protected String chartTitle = "";
    protected String subChartTitle = "";

    final Paint mPaintToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);

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
     * @param ls_data
     */
    public abstract void RenderChart(List<Object> ls_data);

    private void initView()
    {
        //chartLabels();
        //chartDataSet();
        //chartRender();

        //綁定手势滑动事件
        this.bindTouch(this,chart);

        /*
        //for raymenu
        Activity ac = (Activity)getContext();
        RelativeLayout rl = UtilFun.cast(ac.getLayoutInflater()
                                                .inflate(R.layout.rl_raymenu, null));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.bottomMargin = 30;
        rl.setLayoutParams(lp);
        ac.setContentView(rl);
        */
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
        chart.getToolTip().getBorderPaint().setTextSize(24);

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
            //当只有一个数据时，若加入的首数据为0，则该数据不会显示,所以要做下面的workaround
            int lc = BarDataset.size();
            for(int i = 0; i < lc; i++) {
                if(0.01 > Collections.max(BarDataset.get(i).getDataSet()))  {
                    BarDataset.remove(i);

                    lc--;
                    i = 0;
                }
            }

            chart.setDataSource(BarDataset);
            chart.setCategories(chartLabels);
            chart.getPlotLegend().getPaint().setTextSize(24);

            adjustAxis();

            //隐藏轴线和tick
            chart.getDataAxis().hideAxisLine();
            //chart.getDataAxis().setTickMarksVisible(false);

            //标题
            chart.setTitle(chartTitle);
            chart.addSubtitle(subChartTitle);
            chart.setTitleAlign(XEnum.HorizontalAlign.LEFT);


            //背景网格
            chart.getPlotGrid().showHorizontalLines();
            chart.getPlotGrid().showVerticalLines();
            chart.getPlotGrid().showEvenRowBgColor();
            //chart.getPlotGrid().showOddRowBgColor();

            //定义数据轴标签显示格式
//            chart.getDataAxis().setTickLabelRotateAngle(-45);
//            chart.getDataAxis().getTickMarksPaint().
//                    setColor(Color.rgb(186, 20, 26));

            //标签轴文字旋转-45度
            chart.getCategoryAxis().setTickLabelRotateAngle(-45f);
            //横向显示柱形
            chart.setChartDirection(XEnum.Direction.HORIZONTAL);
            //在柱形顶部显示值
            chart.getBar().setItemLabelVisible(true);
            chart.getBar().getItemLabelPaint().setTextSize(22);

            chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack(){

                @Override
                public String textFormatter(String value) {
                    // TODO Auto-generated method stub
                    Double tmp = Double.parseDouble(value);
                    DecimalFormat df=new DecimalFormat("#0");
                    String label = df.format(tmp);
                    return (label +"元");
                }

            });

            //设置标签轴标签交错换行显示
            //chart.getCategoryAxis().setLabelLineFeed(XEnum.LabelLineFeed.EVEN_ODD);

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
                    return df.format(value).toString();
                }});

            //激活点击监听
            chart.ActiveListenItemClick();

            //仅能竖向移动
            chart.setPlotPanMode(XEnum.PanMode.VERTICAL);


            //扩展横向显示范围
            //	chart.getPlotArea().extWidth(200f);

            //标签文字与轴间距
            chart.getCategoryAxis().setTickLabelMargin(5);

            //不使用精确计算，忽略Java计算误差
            chart.disableHighPrecision();

            // 设置轴标签字体大小
            chart.getDataAxis().getTickLabelPaint().setTextSize(28);
            chart.getCategoryAxis().getTickLabelPaint().setTextSize(28);

            refreshChart();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 调整坐标系参数
     */
    private void adjustAxis()   {
        Double max_v = 0d;
        Double min_v = 0d;
        for(BarData bd : BarDataset)    {
            Double max_val = Collections.max(bd.getDataSet());
            Double min_val = Collections.min(bd.getDataSet());

            if(max_val > max_v)
                max_v = max_val;

            if(min_val < min_v)
                min_v = min_val;
        }

        int dif = (int)(max_v - min_v);
        if(dif < 10)        {
            chart.getDataAxis().setAxisSteps(2);
        }
        else    {
            max_v += dif/8;
            chart.getDataAxis().setAxisSteps(dif/6);
        }

        chart.getDataAxis().setAxisMax(max_v);
        chart.getDataAxis().setAxisMin(min_v);
    }
}
