package wxm.KeepAccount.utility

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.TextView

import java.util.ArrayList
import java.util.HashMap

import wxm.KeepAccount.define.EAction
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R

/**
 * drag grid view adapter for buttons in welcome activity
 * Created by WangXM on 2016/9/19.
 */
class DGVButtonAdapter(private val mCTContext: Context, data: List<Map<String, *>>)
        : SimpleAdapter(mCTContext, data, R.layout.gi_button,
                arrayOf(""), IntArray(0)) {
    /**
     * get current action
     * @return      action
     */
    val curAction: List<String>
        get() {
            val retLs = ArrayList<String>()
            val ic = count
            for (i in 0 until ic) {
                val hmd = UtilFun.cast<HashMap<String, Any>>(getItem(i))
                val hv = UtilFun.cast<String>(hmd[HKEY_ACT_NAME])
                retLs.add(hv)
            }

            return retLs
        }

    override fun getViewTypeCount(): Int {
        val orgCt = count
        return if (orgCt < 1) 1 else orgCt
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getView(position: Int, view: View?, arg2: ViewGroup?): View? {
        val v = super.getView(position, view, arg2)
        if (null != v) {
            val hmd = UtilFun.cast<HashMap<String, Any>>(getItem(position))
            val hv = UtilFun.cast<String>(hmd[HKEY_ACT_NAME])

            val tv = UtilFun.cast<TextView>(v.findViewById(R.id.tv_name))
            tv.text = hv

            if (mCTContext is View.OnClickListener) {
                val ac_cl = UtilFun.cast<View.OnClickListener>(mCTContext)
                //bt.setOnClickListener(ac_cl);
                v.setOnClickListener(ac_cl)
            }

            // for image
            val iv = UtilFun.cast<ImageView>(v.findViewById(R.id.iv_image))!!
            val bm = EAction.getIcon(hv)
            if (null != bm) {
                iv.setImageBitmap(bm)
                iv.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

        return v
    }

    companion object {
        const val HKEY_ACT_NAME = "HKEY_ACT_NAME"
    }
}
