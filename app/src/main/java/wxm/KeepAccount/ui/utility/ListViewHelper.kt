package wxm.KeepAccount.ui.utility

import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ListView

/**
 * Helper class for ListView
 * Created by WangXM on 2017/2/21.
 */
object ListViewHelper {
    /**
     * sub listview height have problem, use this to correct
     * @param listView      for view
     */
    fun setListViewHeightBasedOnChildren(listView: ListView) {
        var totalHeight = 0
        val sap = listView.adapter
        var i = 0
        val len = sap.count
        while (i < len) {
            // listAdapter.getCount()返回数据项的数目
            val listItem = sap.getView(i, null, listView)
            // 计算子项View 的宽高
            listItem.measure(0, 0)
            // 统计所有子项的总高度
            totalHeight += listItem.measuredHeight
            i++
        }

        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (sap.count - 1)
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.layoutParams = params
    }
}
