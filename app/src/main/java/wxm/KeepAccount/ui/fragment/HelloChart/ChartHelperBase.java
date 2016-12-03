package wxm.KeepAccount.ui.fragment.HelloChart;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PreviewColumnChartView;
import wxm.KeepAccount.Base.utility.PreferencesUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;

/**
 * chart view base helper
 * Created by wxm on 2016/9/29.
 */
abstract class ChartHelperBase extends ShowViewHelperBase {
    private final static String TAG = "ChartHelperBase";

    private ColumnChartView         mChart;
    ColumnChartData         mChartData;
    private PreviewColumnChartView  mPreviewChart;
    ColumnChartData         mPreviewData;

    float   mPrvWidth = 12;
    HashMap<String, Integer>    mHMColor;

    ChartHelperBase()    {
        super();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView       = inflater.inflate(R.layout.chart_pager, container, false);
        mBFilter        = false;

        // 展示条
        mHMColor = PreferencesUtil.loadChartColor();
        ImageView iv = UtilFun.cast(mSelfView.findViewById(R.id.iv_income));
        assert null != iv;
        iv.setBackgroundColor(mHMColor.get(PreferencesUtil.SET_INCOME_COLOR));

        iv = UtilFun.cast(mSelfView.findViewById(R.id.iv_pay));
        assert null != iv;
        iv.setBackgroundColor(mHMColor.get(PreferencesUtil.SET_PAY_COLOR));

        // 主chart需要响应触摸滚动事件
        mChart = UtilFun.cast(mSelfView.findViewById(R.id.chart));
        mChart.setOnTouchListener(new View.OnTouchListener() {
            private float prv_x = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.i(LOG_TAG, "in chart event = " + event.getAction());
                int act = event.getAction();
                switch (act) {
                    case MotionEvent.ACTION_DOWN :
                        prv_x = event.getX();
                        break;

                    case MotionEvent.ACTION_UP :
                        float cur_x = event.getX();
                        float dif = cur_x - prv_x;
                        if((1 < dif) || (-1 > dif)) {
                            Viewport mcp = mPreviewChart.getMaximumViewport();
                            Viewport cp = mPreviewChart.getCurrentViewport();

                            int cw = mChart.getWidth();
                            float w = (cp.right - cp.left);
                            float m = -1 * (dif / cw) * w;
                            cp.left += m;
                            cp.right += m;
                            if(cp.left < mcp.left)  {
                                cp.left = mcp.left;
                                cp.right = mcp.left + w;
                            }

                            if(cp.right > mcp.right)    {
                                cp.right = mcp.right;
                                cp.left = cp.right - w;
                            }

                            mPreviewChart.setCurrentViewportWithAnimation(cp);
                            prv_x = cur_x;
                        }
                        break;
                }

                return false;
            }
        });

        // 预览chart需要锁定触摸滚屏
        mPreviewChart = UtilFun.cast(mSelfView.findViewById(R.id.chart_preview));
        mPreviewChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.i(LOG_TAG, "in preview chart event = " + event.getAction());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        getRootActivity().disableViewPageTouch(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        getRootActivity().disableViewPageTouch(false);
                        break;

                    default:
                        if(MotionEvent.ACTION_MOVE != event.getAction())
                            getRootActivity().disableViewPageTouch(false);
                        break;
                }

                return false;
            }
        });

        // 设置扩大/缩小viewport
        final Button bt_less = UtilFun.cast(mSelfView.findViewById(R.id.bt_less_viewport));
        Button bt = UtilFun.cast(mSelfView.findViewById(R.id.bt_more_viewport));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrvWidth += 0.2;
                refreshViewPort();

                if(!bt_less.isClickable() && 1 < mPrvWidth)
                    bt_less.setClickable(true);
            }
        });

        bt_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(1 < mPrvWidth) {
                    mPrvWidth -= 0.2;
                    refreshViewPort();
                } else  {
                    bt_less.setClickable(false);
                }
            }
        });

        return mSelfView;
    }

    @Override
    public void loadView() {
        super.loadView();

        reloadData();
        refreshView();
    }

    protected abstract void reloadData();

    @Override
    protected void giveupFilter()   {
        mBFilter = false;
        loadView();
    }

    @Override
    public void filterView(List<String> ls_tag) {
        if(null != ls_tag) {
            mBFilter = true;
            mFilterPara.clear();
            mFilterPara.addAll(ls_tag);
            loadView();
        } else  {
            mBFilter = false;
            loadView();
        }
    }

    @Override
    public void onDataChange() {
    }

    @Override
    protected void refreshView() {
        refreshAttachLayout();

        // 展示条
        mHMColor = PreferencesUtil.loadChartColor();
        ImageView iv = UtilFun.cast(mSelfView.findViewById(R.id.iv_income));
        assert null != iv;
        iv.setBackgroundColor(mHMColor.get(PreferencesUtil.SET_INCOME_COLOR));

        iv = UtilFun.cast(mSelfView.findViewById(R.id.iv_pay));
        assert null != iv;
        iv.setBackgroundColor(mHMColor.get(PreferencesUtil.SET_PAY_COLOR));

        /* for chart */
        mChart.setColumnChartData(mChartData);
        // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
        // zoom/scroll is unnecessary.
        mChart.setZoomEnabled(false);
        mChart.setScrollEnabled(false);
        //mChart.setValueSelectionEnabled(true);

        mPreviewChart.setColumnChartData(mPreviewData);
        mPreviewChart.setViewportChangeListener(new ViewportListener());
        mPreviewChart.setZoomType(ZoomType.HORIZONTAL);
        refreshViewPort();
    }


    private void refreshAttachLayout()    {
        setAttachLayoutVisible(mBFilter ? View.VISIBLE : View.INVISIBLE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.INVISIBLE);
        setAccpetGiveupLayoutVisible(View.INVISIBLE);
    }


    private void refreshViewPort()  {
        Viewport tempViewport = new Viewport(mChart.getMaximumViewport());
        tempViewport.right = tempViewport.left + mPrvWidth;
        mPreviewChart.setCurrentViewportWithAnimation(tempViewport);
    }


    /**
     * Viewport listener for preview chart(lower one). in {@link #onViewportChanged(Viewport)} method change
     * viewport of upper chart.
     */
    private class ViewportListener implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart because usually viewport changes
            // happens to often.
            mChart.setCurrentViewport(newViewport);
        }
    }
}
