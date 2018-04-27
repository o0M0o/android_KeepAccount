package wxm.KeepAccount.ui.data.show.note.HelloChart;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import wxm.KeepAccount.utility.ToolUtil;
import wxm.androidutil.util.UtilFun;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.utility.PreferencesUtil;

/**
 * daily chart
 * Created by WangXM on2016/9/29.
 */
public class DailyChart extends ChartBase {
    public DailyChart() {
        super();
        mPrvWidth = 4.5f;
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    /**
     * filter UI
     * @param event     for event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(FilterShowEvent event) {
        List<String> e_p = event.getFilterTag();
        if ((NoteDataHelper.TAB_TITLE_MONTHLY.equals(event.getSender()))
                && (null != e_p)) {
            setMBFilter(true);
            getMFilterPara().clear();
            getMFilterPara().addAll(e_p);

            refreshData();
        }
    }

    @Override
    protected void refreshData()  {
        ToolUtil.INSTANCE.runInBackground(this.getActivity(),
                () -> {
                    HashMap<String, ArrayList<INote>> ret = NoteDataHelper.Companion.getInstance().getNotesForDay();
                    if(null == ret)
                        return;

                    int id_col = 0;
                    List<AxisValue> axisValues = new ArrayList<>();
                    List<Column> columns = new ArrayList<>();
                    ArrayList<String> set_k = new ArrayList<>(ret.keySet());
                    Collections.sort(set_k);
                    for (String k : set_k) {
                        boolean ba = true;
                        if (getMBFilter() && !getMFilterPara().isEmpty()) {
                            //String ck = ToolUtil.FormatDateString(k);
                            if (!getMFilterPara().contains(k))
                                ba = false;
                        }

                        if (ba) {
                            BigDecimal pay = BigDecimal.ZERO;
                            BigDecimal income = BigDecimal.ZERO;
                            for (Object i : ret.get(k)) {
                                if (i instanceof PayNoteItem) {
                                    PayNoteItem pi = UtilFun.cast(i);
                                    pay = pay.add(pi.getAmount());
                                } else {
                                    IncomeNoteItem ii = UtilFun.cast(i);
                                    income = income.add(ii.getAmount());
                                }
                            }

                            List<SubcolumnValue> values = new ArrayList<>();
                            values.add(new SubcolumnValue(pay.floatValue(),
                                    mHMColor.get(PreferencesUtil.SET_PAY_COLOR)));
                            values.add(new SubcolumnValue(income.floatValue(),
                                    mHMColor.get(PreferencesUtil.SET_INCOME_COLOR)));

                            Column cd = new Column(values);
                            cd.setHasLabels(true);
                            columns.add(cd);

                            axisValues.add(new AxisValue(id_col).setLabel(k));
                            id_col++;
                        }
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
}
