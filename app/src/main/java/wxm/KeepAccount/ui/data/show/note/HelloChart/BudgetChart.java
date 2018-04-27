package wxm.KeepAccount.ui.data.show.note.HelloChart;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.androidutil.util.UtilFun;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PreviewColumnChartView;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.PreferencesUtil;

/**
 * 预算chart辅助类
 * Created by WangXM on 2016/10/7.
 */
public class BudgetChart extends ShowViewBase {
    ColumnChartData mChartData;
    ColumnChartData mPreviewData;
    float mPrvWidth = 12;
    HashMap<String, Integer> mHMColor;
    @BindView(R.id.chart)
    ColumnChartView mChart;
    @BindView(R.id.chart_preview)
    PreviewColumnChartView mPreviewChart;
    @BindView(R.id.sp_budget)
    Spinner mSPBudget;
    @BindView(R.id.iv_remainder)
    ImageView mIVRemainder;
    @BindView(R.id.iv_used)
    ImageView mIVUsed;
    private List<BudgetItem> mSPBudgetData;
    private int mSPBudgetHot = Spinner.INVALID_POSITION;

    @Override
    protected int getLayoutID() {
        return R.layout.chart_budget_pager;
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    protected void initUI(Bundle bundle) {
        setMBFilter(false);
        mHMColor = PreferencesUtil.INSTANCE.loadChartColor();

        // 填充预算数据
        mSPBudgetData = ContextUtil.Companion.getBudgetUtility().getBudgetForCurUsr();
        if (!UtilFun.ListIsNullOrEmpty(mSPBudgetData)) {
            ArrayList<String> data_ls = new ArrayList<>();
            for (BudgetItem i : mSPBudgetData) {
                data_ls.add(i.getName());
            }

            ArrayAdapter<String> spAdapter = new ArrayAdapter<>(getRootActivity(),
                    android.R.layout.simple_spinner_item, data_ls);
            spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSPBudget.setAdapter(spAdapter);

            mSPBudget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mSPBudgetHot = position;
                    refreshData();
                    loadUI(null);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mSPBudgetHot = Spinner.INVALID_POSITION;
                    refreshData();
                    loadUI(null);
                }
            });
        }

        // 主chart需要响应触摸滚动事件
        mChart.setOnTouchListener(new View.OnTouchListener() {
            private float prv_x = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int act = event.getAction();
                switch (act) {
                    case MotionEvent.ACTION_DOWN:
                        prv_x = event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        v.performClick();

                        float cur_x = event.getX();
                        float dif = cur_x - prv_x;
                        if ((1 < dif) || (-1 > dif)) {
                            Viewport mcp = mPreviewChart.getMaximumViewport();
                            Viewport cp = mPreviewChart.getCurrentViewport();

                            int cw = mChart.getWidth();
                            float w = (cp.right - cp.left);
                            float m = -1 * (dif / cw) * w;
                            cp.left += m;
                            cp.right += m;
                            if (cp.left < mcp.left) {
                                cp.left = mcp.left;
                                cp.right = mcp.left + w;
                            }

                            if (cp.right > mcp.right) {
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
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getRootActivity().disableViewPageTouch(true);
                    break;

                case MotionEvent.ACTION_UP:
                    v.performClick();

                    getRootActivity().disableViewPageTouch(false);
                    break;

                default:
                    if (MotionEvent.ACTION_MOVE != event.getAction())
                        getRootActivity().disableViewPageTouch(false);
                    break;
            }

            return false;
        });
    }



    @Override
    protected void loadUI(Bundle bundle) {
        loadUIUtility(false);
    }

    protected void loadUIUtility(boolean b_full) {
        refreshAttachLayout();

        if (b_full) {
            // 展示条
            mIVRemainder.setBackgroundColor(mHMColor.get(PreferencesUtil.SET_BUDGET_BALANCE_COLOR));
            mIVUsed.setBackgroundColor(mHMColor.get(PreferencesUtil.SET_BUDGET_UESED_COLOR));

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
     * 设置扩大/缩小viewport
     *
     * @param v 激活view
     */
    @OnClick({R.id.bt_less_viewport, R.id.bt_more_viewport})
    public void onLessOrMoreView(View v) {
        View vParent = getView();
        if(null == vParent)
            return;

        final Button bt_less = UtilFun.cast(vParent.findViewById(R.id.bt_less_viewport));
        int vid = v.getId();
        switch (vid) {
            case R.id.bt_less_viewport: {
                mPrvWidth += 0.2;
                refreshViewPort();

                if (!bt_less.isClickable() && 1 < mPrvWidth)
                    bt_less.setClickable(true);
            }
            break;

            case R.id.bt_more_viewport: {
                if (1 < mPrvWidth) {
                    mPrvWidth -= 0.2;
                    refreshViewPort();
                } else {
                    bt_less.setClickable(false);
                }
            }
            break;
        }
    }

    protected void refreshData() {
        if (Spinner.INVALID_POSITION == mSPBudgetHot)
            return;

        BudgetItem bi = mSPBudgetData.get(mSPBudgetHot);
        List<PayNoteItem> pays = ContextUtil.Companion.getPayIncomeUtility().getPayNoteByBudget(bi);
        HashMap<String, ArrayList<PayNoteItem>> hm_ret = new HashMap<>();

        ToolUtil.INSTANCE.runInBackground(this.getActivity(),
                () -> {
                    for (PayNoteItem i : pays) {
                        String k = i.getTs().toString().substring(0, 10);
                        ArrayList<PayNoteItem> lsp = hm_ret.get(k);
                        if (UtilFun.ListIsNullOrEmpty(lsp)) {
                            lsp = new ArrayList<>();
                            lsp.add(i);

                            hm_ret.put(k, lsp);
                        } else {
                            lsp.add(i);
                        }
                    }

                    if (0 == hm_ret.size()) {
                        String org_k = bi.getTs().toString().substring(0, 10);
                        hm_ret.put(org_k, new ArrayList<>());
                    }

                    int id_col = 0;
                    List<AxisValue> axisValues = new ArrayList<>();
                    List<Column> columns = new ArrayList<>();
                    ArrayList<String> set_k = new ArrayList<>(hm_ret.keySet());
                    Collections.sort(set_k);
                    BigDecimal all_pay = BigDecimal.ZERO;
                    for (String k : set_k) {
                        BigDecimal pay = BigDecimal.ZERO;
                        ArrayList<PayNoteItem> lsp = hm_ret.get(k);
                        for (PayNoteItem i : lsp) {
                            pay = pay.add(i.getAmount());
                        }

                        all_pay = all_pay.add(pay);
                        BigDecimal left_budget = bi.getAmount().subtract(all_pay);
                        List<SubcolumnValue> values = new ArrayList<>();
                        values.add(new SubcolumnValue(left_budget.floatValue(),
                                mHMColor.get(PreferencesUtil.SET_BUDGET_BALANCE_COLOR)));
                        values.add(new SubcolumnValue(all_pay.floatValue(),
                                mHMColor.get(PreferencesUtil.SET_BUDGET_UESED_COLOR)));

                        Column cd = new Column(values);
                        cd.setHasLabels(true);
                        columns.add(cd);

                        axisValues.add(new AxisValue(id_col).setLabel(k));
                        id_col++;
                    }

                    mChartData = new ColumnChartData(columns);
                    mChartData.setAxisXBottom(new Axis(axisValues));
                    mChartData.setAxisYLeft(new Axis().setHasLines(true));

                    // prepare preview data, is better to use separate deep copy for preview chart.
                    // set color to grey to make preview area more visible.
                    mPreviewData = new ColumnChartData(mChartData);
                    for (Column column : mPreviewData.getColumns()) {
                        for (SubcolumnValue value : column.getValues()) {
                            value.setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
                        }

                        column.setHasLabels(false);
                    }

                    int cc = 0;
                    for (AxisValue i : mPreviewData.getAxisXBottom().getValues()) {
                        if (0 == cc % 5) {
                            String v = new String(i.getLabelAsChars()).substring(0, 7);
                            i.setLabel(v);
                        } else {
                            i.setLabel("");
                        }

                        cc += 1;
                    }
                },
                () -> loadUIUtility(true));
    }

    /**
     * 过滤视图事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(FilterShowEvent event) {
    }

    private void refreshAttachLayout() {
        setAttachLayoutVisible(getMBFilter() ? View.VISIBLE : View.GONE);
        setFilterLayoutVisible(getMBFilter() ? View.VISIBLE : View.GONE);
        setAcceptGiveUpLayoutVisible(View.GONE);
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
