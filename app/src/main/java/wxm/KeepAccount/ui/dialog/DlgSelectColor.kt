package wxm.KeepAccount.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.SimpleAdapter
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.androidutil.util.UtilFun
import java.util.*

/**
 * select color
 * Created by WangXM on 2016/10/10.
 */
class DlgSelectColor : DlgOKOrNOBase(), AdapterView.OnItemClickListener {
    private var mHotPos = GlobalDef.INVALID_ID

    val selectedColor: Int
        get() = if (GlobalDef.INVALID_ID != mHotPos)
            ContextUtil.getColor(ARR_COLOR[mHotPos])
        else
            GlobalDef.INVALID_ID

    /**
     * click handler for gridview item
     * @param parent   param
     * @param view     param
     * @param position param
     * @param id       param
     */
    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        view.setBackgroundColor(ContextUtil.getColor(R.color.red))
        mHotPos = if (GlobalDef.INVALID_ID == mHotPos) {
            position
        } else {
            parent.getChildAt(mHotPos).setBackgroundColor(ContextUtil.getColor(R.color.white))
            position
        }
    }

    override fun createDlgView(savedInstanceState: Bundle?): View {
        initDlgTitle("选择颜色", "接受", "放弃")
        return View.inflate(activity, R.layout.dlg_select_color, null)
    }

    override fun initDlgView(savedInstanceState: Bundle?) {
        val gv: GridView = findDlgChildView(R.id.gv_colors)!!
        gv.onItemClickListener = this

        val lsData = ArrayList<HashMap<String, Any>>()
        for (i in ARR_COLOR) {
            val hm = HashMap<String, Any>()
            hm[PARA_COLOR] = ContextUtil.getColor(i)
            lsData.add(hm)
        }

        val ga = GVChannelAdapter(activity, lsData, arrayOf(), intArrayOf())
        gv.adapter = ga
        ga.notifyDataSetChanged()
    }


    /**
     * adapter for gridview
     */
    inner class GVChannelAdapter internal constructor(context: Context, data: List<Map<String, *>>,
                                                      from: Array<String>, to: IntArray) : SimpleAdapter(context, data, R.layout.gi_color, from, to) {

        override fun getViewTypeCount(): Int {
            val orgCount = count
            return if (orgCount < 1) 1 else orgCount
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getView(position: Int, view: View?, arg2: ViewGroup?): View? {
            val v = super.getView(position, view, arg2)
            if (null != v) {
                val hmd = UtilFun.cast<HashMap<String, Any>>(getItem(position))
                val c = UtilFun.cast<Int>(hmd[PARA_COLOR])

                // for image
                val iv = UtilFun.cast<ImageView>(v.findViewById(R.id.iv_color))!!
                iv.setBackgroundColor(c)
            }

            return v
        }
    }

    companion object {
        private const val PARA_COLOR = "color"
        private val ARR_COLOR = intArrayOf(
                R.color.aliceblue,
                R.color.antiquewhite,
                R.color.lightyellow,
                R.color.snow,
                R.color.lavenderblush,
                R.color.bisque,
                R.color.pink,
                R.color.orange,
                R.color.coral,
                R.color.hotpink,
                R.color.orangered,
                R.color.magenta,
                R.color.violet,
                R.color.darksalmon,
                R.color.lavender,
                R.color.burlywood,
                R.color.crimson,
                R.color.firebrick,
                R.color.powderblue,
                R.color.lightsteelblue,
                R.color.paleturquoise,
                R.color.greenyellow,
                R.color.darkgrey,
                R.color.lawngreen,
                R.color.lightslategrey,
                R.color.cadetblue,
                R.color.indigo,
                R.color.lightseagreen,
                R.color.cyan)
    }
}
