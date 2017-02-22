package wxm.KeepAccount.ui.utility;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

/**
 * Helper class for ListView
 * Created by ookoo on 2017/2/21.
 */
public class ListViewHelper {
    /**
     * 次级列表视图计算列表高度有错误，使用此函数校正
     * @param listView      列表
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        int totalHeight = 0;
        Adapter sap = listView.getAdapter();
        for (int i = 0, len = sap.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = sap.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (sap.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
