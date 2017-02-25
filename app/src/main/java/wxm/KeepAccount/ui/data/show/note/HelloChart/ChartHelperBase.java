package wxm.KeepAccount.ui.data.show.note.HelloChart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.util.UtilFun;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PreviewColumnChartView;
import wxm.KeepAccount.utility.PreferencesUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.ShowData.ShowViewHelperBase;

/**
 * chart view base helper
 * Created by wxm on 2016/9/29.
 */
abstract class ChartHelperBase extends ShowViewHelperBase {

    ColumnChartData         mChartData;
    ColumnChartData         mPreviewData;

    float   mPrvWidth = 12;
    HashMap<String, Integer>    mHMColor;

    @BindView(R.id.chart)
    ColumnChartView         mChart;

    @BindView(R.id.chart_preview)
    PreviewColumnChartView  mPreviewChart;

    @BindView(R.id.iv_income)
    ImageView   mIVIncome;

    @BindView(R.id.iv_pay)
    ImageView   mIVPay;


    ChartHelperBase()    {
        super();
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.chart_pager, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        mBFilter = false;
        mHMColor = PreferencesUtil.loadChartColor();

        // 主chart需要响应触摸滚动事件
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
        mPreviewChart.setOnTouchListener((v, event) -> {
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
        });

        refreshData();
    }

    @Override
    protected void loadUI() {
        refreshAttachLayout();

        // 展示条
        mIVIncome.setBackgroundColor(mHMColor.get(PreferencesUtil.SET_INCOME_COLOR));
        mIVPay.setBackgroundColor(mHMColor.get(PreferencesUtil.SET_PAY_COLOR));

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

    /**
     * 设置扩大/缩小viewport
     *
     * @param v    激活view
     */
    @OnClick({R.id.bt_less_viewport, R.id.bt_more_viewport})
    public void onLessOrMoreView(View v)    {
        final Button bt_less = UtilFun.cast(getView().findViewById(R.id.bt_less_viewport));
        int vid = v.getId();
        switch (vid)    {
            case R.id.bt_less_viewport :    {
                mPrvWidth += 0.2;
                refreshViewPort();

                if(!bt_less.isClickable() && 1 < mPrvWidth)
                    bt_less.setClickable(true);
            }
            break;

            case R.id.bt_more_viewport :    {
                if(1 < mPrvWidth) {
                    mPrvWidth -= 0.2;
                    refreshViewPort();
                } else  {
                    bt_less.setClickable(false);
                }
            }
            break;
        }
    }

    @Override
    protected void giveUpFilter()   {
        mBFilter = false;
        loadUI();
    }

    @Override
    public void filterView(List<String> ls_tag) {
        if(null != ls_tag) {
            mBFilter = true;
            mFilterPara.clear();
            mFilterPara.addAll(ls_tag);
        } else  {
            mBFilter = false;
        }

        loadUI();
    }

    private void refreshAttachLayout()    {
        setAttachLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(View.GONE);
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
