package wxm.KeepAccount.ui.fragment.HelloChart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
    private ColumnChartView         mChart;
    ColumnChartData         mChartData;
    private PreviewColumnChartView  mPreviewChart;
    ColumnChartData         mPreviewData;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView       = inflater.inflate(R.layout.chart_pager, container, false);
        mBFilter        = false;

        mChart = UtilFun.cast(mSelfView.findViewById(R.id.chart));
        mPreviewChart = UtilFun.cast(mSelfView.findViewById(R.id.chart_preview));
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
        float dy = tempViewport.height() / 4;
        tempViewport.inset(0, dy);
        mPreviewChart.setCurrentViewportWithAnimation(tempViewport);
        mPreviewChart.setZoomType(ZoomType.VERTICAL);
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
