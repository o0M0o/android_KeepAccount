package wxm.KeepAccount.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.SimpleAdapter

import java.util.ArrayList
import java.util.HashMap

import wxm.KeepAccount.define.EAction
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.dialog.base.DlgResource
import wxm.KeepAccount.utility.DGVButtonAdapter

/**
 * select follow channel
 * Created by WangXM on 2016/9/20.
 */
class DlgSelectChannel : DlgOKOrNOBase() {
    // for hot channel
    private val mLSHotChannel = ArrayList<String>()

    /**
     * followed channel(channel name)
     * before use dlg, use this set init amount
     */
    var hotChannel: List<String>
        get() = mLSHotChannel
        set(org_hot) {
            mLSHotChannel.addAll(org_hot)
        }

    override fun createDlgView(savedInstanceState: Bundle?): View {
        initDlgTitle("选择首页项", "接受", "放弃")
        return View.inflate(activity, R.layout.dlg_select_channel, null)
    }

    override fun initDlgView(savedInstanceState: Bundle?) {
        val gv: GridView = findDlgChildView(R.id.gv_channels)!!
        gv.setOnItemClickListener { parent, view, position, _ ->
            val hmd = UtilFun.cast<HashMap<String, Any>>(parent.adapter.getItem(position))
            val act = UtilFun.cast<String>(hmd[DGVButtonAdapter.KEY_ACT_NAME])

            val hot = mLSHotChannel.contains(act)
            if (hot) {
                mLSHotChannel.remove(act)
                view.background = DlgResource.mDAChannelNoSel
            } else {
                mLSHotChannel.add(act)
                view.background = DlgResource.mDAChannelSel
            }
        }

        val lsData = ArrayList<HashMap<String, Any>>()
        for (ea in EAction.values()) {
            val hm = HashMap<String, Any>()
            hm[DGVButtonAdapter.KEY_ACT_NAME] = ea.actName

            lsData.add(hm)
        }

        val ga = GVChannelAdapter(activity!!, lsData, arrayOf(DGVButtonAdapter.KEY_ACT_NAME),
                        intArrayOf(R.id.tv_name))
        gv.adapter = ga
        ga.notifyDataSetChanged()
    }


    /**
     * adapter for gridview
     */
    inner class GVChannelAdapter
        internal constructor(context: Context, data: List<Map<String, *>>,
                            from: Array<String>, to: IntArray)
        : SimpleAdapter(context, data, R.layout.gi_channel, from, to) {
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
                val hv = UtilFun.cast<String>(hmd[DGVButtonAdapter.KEY_ACT_NAME])

                v.background = if (mLSHotChannel.contains(hv))
                    DlgResource.mDAChannelSel
                else
                    DlgResource.mDAChannelNoSel

                // for image
                val bm = EAction.getIcon(hv)
                if (null != bm) {
                    val iv = UtilFun.cast_t<ImageView>(v.findViewById(R.id.iv_act))
                    iv.setImageBitmap(bm)
                }
            }

            return v
        }
    }
}
