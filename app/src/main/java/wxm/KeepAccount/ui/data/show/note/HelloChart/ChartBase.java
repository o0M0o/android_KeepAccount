package wxm.KeepAccount.ui.data.show.note.HelloChart;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Viewport;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.TouchUI.TouchColumnChartView;
import wxm.KeepAccount.ui.base.TouchUI.TouchPreviewColumnChartView;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;
import wxm.KeepAccount.utility.PreferencesUtil;

/**
 * chart view base helper
 * Created by WangXM on2016/9/29.
 */
abstract class ChartBase extends ShowViewBase {
    ColumnChartData mChartData;
    ColumnChartData mPreviewData;

    float mPrvWidth = 12;
    HashMap<String, Integer> mHMColor;

    @BindView(R.id.chart)
    TouchColumnChartView mChart;

    @BindView(R.id.chart_preview)
    TouchPreviewColumnChartView mPreviewChart;

    @BindView(R.id.iv_income)
    ImageView mIVIncome;

    @BindView(R.id.iv_pay)
    ImageView mIVPay;

    @BindView(R.id.bt_less_viewport)
    Button mBTLessViewPort;

    @Override
    protected int getLayoutID() {
        return R.layout.chart_pager;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initUI(Bundle bundle) {
        mBFilter = false;
        mHMColor = PreferencesUtil.INSTANCE.loadChartColor();

        // main chart need respond touch event
        mChart.setOnTouchListener(new View.OnTouchListener() {
            private float mDownX = -1;

            private Viewport mVPMax;
            private Viewport mVPOrg;

            private int mChartWidth;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int act = event.getAction();
                switch (act) {
                    case MotionEvent.ACTION_DOWN: {
                        mDownX = event.getX();
                        mVPOrg = new Viewport(mPreviewChart.getCurrentViewport());
                        mVPMax = mPreviewChart.getMaximumViewport();

                        mChartWidth = mChart.getWidth();
                    }
                    break;

                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP: {
                        float dif = (event.getX() - mDownX) / mChartWidth;

                        if(0.05 < Math.abs(dif) || act == MotionEvent.ACTION_UP) {
                            Viewport cp = new Viewport(mVPOrg);
                            if (cp.left > mVPMax.left || cp.right < mVPMax.right) {
                                float w = cp.width();
                                float m = -(dif * w);
                                cp.offset(m, 0);
                                if (cp.left < mVPMax.left) {
                                    cp.left = mVPMax.left;
                                    cp.right = mVPMax.left + w;
                                }

                                if (cp.right > mVPMax.right) {
                                    cp.right = mVPMax.right;
                                    cp.left = cp.right - w;
                                }

                                mPreviewChart.setCurrentViewport(cp);
                            }
                        }
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
                    if (MotionEvent.ACTION_MOVE != event.getAction())
                        getRootActivity().disableViewPageTouch(false);
                    break;
            }

            return false;
        });

        refreshData();
    }

    @Override
    protected void loadUI(Bundle bundle) {
        loadUIUtility(false);
    }


    protected void loadUIUtility(boolean b_full) {
        refreshAttachLayout();

        if (b_full) {
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
    }


    /**
     * adjust viewport
     *
     * @param v clicked view
     */
    @OnClick({R.id.bt_less_viewport, R.id.bt_more_viewport, R.id.bt_giveup_filter})
    public void onLessOrMoreView(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.bt_less_viewport: {
                mPrvWidth += 0.2;
                refreshViewPort();

                if (!mBTLessViewPort.isClickable() && 1 < mPrvWidth)
                    mBTLessViewPort.setClickable(true);
            }
            break;

            case R.id.bt_more_viewport: {
                if (1 < mPrvWidth) {
                    mPrvWidth -= 0.2;
                    refreshViewPort();
                } else {
                    mBTLessViewPort.setClickable(false);
                }
            }
            break;

            case R.id.bt_giveup_filter: {
                mBFilter = false;
                refreshData();
            }
            break;
        }
    }

    protected abstract void refreshData();


    private void refreshAttachLayout() {
        setAttachLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(View.GONE);
    }


    private void refreshViewPort() {
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
