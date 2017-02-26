package wxm.KeepAccount.ui.data.show.note.HelloChart;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
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
import wxm.KeepAccount.utility.PreferencesUtil;
import wxm.KeepAccount.ui.utility.NoteShowDataHelper;

/**
 * 加载年度chart视图
 * Created by wxm on 2016/9/29.
 */
public class YearlyChartHelper extends ChartHelperBase {
    public YearlyChartHelper()   {
        super();
        mPrvWidth = 6;

        LOG_TAG = "YearlyChartHelper";
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        HashMap<String, ArrayList<INote>> ret = NoteShowDataHelper.getInstance().getNotesForYear();

        int id_col = 0;
        List<AxisValue> axisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        ArrayList<String> set_k = new ArrayList<>(ret.keySet());
        Collections.sort(set_k);
        for(String k : set_k)    {
            BigDecimal pay = BigDecimal.ZERO;
            BigDecimal income = BigDecimal.ZERO;
            for(Object i : ret.get(k))  {
                if(i instanceof PayNoteItem)    {
                    PayNoteItem pi = UtilFun.cast(i);
                    pay = pay.add(pi.getVal());
                }   else    {
                    IncomeNoteItem ii = UtilFun.cast(i);
                    income = income.add(ii.getVal());
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

        for(AxisValue i : mPreviewData.getAxisXBottom().getValues())     {
            String v = new String(i.getLabelAsChars()).substring(0, 4);
            i.setLabel(v);
        }
    }

    /**
     * 过滤视图事件
     * @param event     事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(FilterShowEvent event) {
    }
}
