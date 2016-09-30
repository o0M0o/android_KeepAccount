package wxm.KeepAccount.ui.fragment.HelloChart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.PreviewColumnChartView;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * chart view base helper
 * Created by wxm on 2016/9/29.
 */
public abstract class ChartHelperBase extends ShowViewHelperBase {
    private final static String TAG = "ChartHelperBase";

    private ColumnChartView         mChart;
    ColumnChartData         mChartData;
    private PreviewColumnChartView  mPreviewChart;
    ColumnChartData         mPreviewData;

    float   mPrvWidth = 12;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView       = inflater.inflate(R.layout.chart_pager, container, false);
        mBFilter        = false;

        mChart = UtilFun.cast(mSelfView.findViewById(R.id.chart));
        mChart.setOnTouchListener(new View.OnTouchListener() {
            private float prv_x = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.i(TAG, "in chart event = " + event.getAction());
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

        mPreviewChart = UtilFun.cast(mSelfView.findViewById(R.id.chart_preview));
        mPreviewChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.i(TAG, "in preview chart event = " + event.getAction());
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

        return mSelfView;
    }

    @Override
    public void loadView() {
        reloadData();
        refreshView();
    }

    protected abstract void reloadData();

    @Override
    public void checkView() {
        if(getRootActivity().getDayNotesDirty())
            loadView();
    }

    @Override
    public void filterView(List<String> ls_tag) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    protected void refreshView() {
        refreshAttachLayout();

        /* for chart */
        mChart.setColumnChartData(mChartData);
        // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
        // zoom/scroll is unnecessary.
        mChart.setZoomEnabled(false);
        mChart.setScrollEnabled(false);

        mPreviewChart.setColumnChartData(mPreviewData);
        mPreviewChart.setViewportChangeListener(new ViewportListener());

        Viewport tempViewport = new Viewport(mChart.getMaximumViewport());
        //float dx = tempViewport.width() / (float)2.3;
        //tempViewport.inset(dx, 0);
        tempViewport.right = tempViewport.left + mPrvWidth;
        mPreviewChart.setCurrentViewportWithAnimation(tempViewport);
        mPreviewChart.setZoomType(ZoomType.HORIZONTAL);
    }


    private void refreshAttachLayout()    {
        setAttachLayoutVisible(mBFilter ? View.VISIBLE : View.INVISIBLE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.INVISIBLE);
        setAccpetGiveupLayoutVisible(View.INVISIBLE);
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
