package wxm.KeepAccount.ui.fragment.HelloChart;

import android.content.res.Resources;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.wxm.andriodutillib.util.UtilFun;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.R;

/**
 * 加载日chart视图
 * Created by wxm on 2016/9/29.
 */
public class DailyChartHelper extends ChartHelperBase {
    @Override
    protected void reloadData() {
        Resources res = getRootActivity().getResources();
        HashMap<String, ArrayList<Object>> ret = AppModel.getPayIncomeUtility().GetAllNotesToDay();

        int id_col = 0;
        List<AxisValue> axisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        for(String k : ret.keySet())    {
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
            values.add(new SubcolumnValue(pay.floatValue(), res.getColor(R.color.aqua)));
            values.add(new SubcolumnValue(income.floatValue(), res.getColor(R.color.forestgreen)));

            Column cd = new Column(values);
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
        }

        int ic = 0;
        for(AxisValue i : mPreviewData.getAxisXBottom().getValues())     {
            i.setLabel(String.valueOf(ic));
            ic++;
        }
    }
}
