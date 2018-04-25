package wxm.KeepAccount.utility

import android.support.annotation.IdRes
import android.view.View

/**
 * @author      WangXM
 * @version     create：2018/4/24
 */
object EventHelper {
    fun setOnClickListener(vwParent: View, @IdRes vwChildId: IntArray, listener: View.OnClickListener)     {
        for(@IdRes id in vwChildId) {
            val v:View = vwParent.findViewById(id)
            v.setOnClickListener(listener)
        }
    }
}