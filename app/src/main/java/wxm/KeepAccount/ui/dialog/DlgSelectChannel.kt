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
import wxm.androidutil.improve.doJudge
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.ViewHolder

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
            @Suppress("UNCHECKED_CAST")
            val act = (parent.adapter.getItem(position) as Map<String, String>)[DGVButtonAdapter.KEY_ACT_NAME]!!
            if (mLSHotChannel.contains(act)) {
                mLSHotChannel.remove(act)
                view.background = DlgResource.mDAChannelNoSel
            } else {
                mLSHotChannel.add(act)
                view.background = DlgResource.mDAChannelSel
            }
        }

        val lsData = ArrayList<HashMap<String, String>>().apply {
            EAction.values().forEach {
                add(HashMap<String, String>().apply {
                    put(DGVButtonAdapter.KEY_ACT_NAME, it.actName)
                })
            }
        }

        gv.adapter = GVChannelAdapter(activity!!, lsData, arrayOf(DGVButtonAdapter.KEY_ACT_NAME),
                        intArrayOf(R.id.tv_name))
    }


    /**
     * adapter for gridview
     */
    inner class GVChannelAdapter
        internal constructor(context: Context, data: List<Map<String, *>>,
                            from: Array<String?>, to: IntArray)
        : MoreAdapter(context, data, R.layout.gi_channel, from, to) {

        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            @Suppress("UNCHECKED_CAST")
            val hv = (getItem(pos) as Map<String, String>)[DGVButtonAdapter.KEY_ACT_NAME]!!

            vhHolder.convertView.background = mLSHotChannel.contains(hv).doJudge(
                DlgResource.mDAChannelSel, DlgResource.mDAChannelNoSel)

            // for image
            EAction.getIcon(hv)!!.let {
                vhHolder.getView<ImageView>(R.id.iv_act)!!.apply {
                    setImageBitmap(it)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
        }
    }
}
