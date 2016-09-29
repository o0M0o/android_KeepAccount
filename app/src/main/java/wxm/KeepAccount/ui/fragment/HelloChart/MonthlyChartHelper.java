package wxm.KeepAccount.ui.fragment.HelloChart;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import wxm.KeepAccount.R;

/**
 * 加载阅读chart视图
 * Created by wxm on 2016/9/29.
 */
public class MonthlyChartHelper extends ChartHelperBase {
    @Override
    protected void reloadData() {
        int numSubcolumns = 2;
        int numColumns = 50;
        Resources res = getRootActivity().getResources();
        List<AxisValue> axisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < numColumns; ++i) {
            List<SubcolumnValue> values = new ArrayList<>();
            for (int j = 0; j < numSubcolumns; ++j) {
                SubcolumnValue scv = new SubcolumnValue(
                        (float) Math.random() * 50f + 5
                        , j % 2 == 0 ? res.getColor(R.color.aqua)
                        : res.getColor(R.color.forestgreen));
                values.add(scv);
            }

            Column cd = new Column(values);
            columns.add(cd);

            axisValues.add(new AxisValue(i).setLabel("9月22日"));
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
