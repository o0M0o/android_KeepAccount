package wxm.KeepAccount.ui.utility

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter

import java.util.HashMap
import java.util.Locale

import wxm.KeepAccount.ui.base.Helper.ViewHelper
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.ViewHolder.ViewHolder
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.INote
import wxm.KeepAccount.ui.data.edit.Note.ACPreveiwAndEdit
import wxm.uilib.SwipeLayout.SwipeLayout

/**
 * adapter for note detail
 * Created by WangXM on 2017/1/23.
 */
class AdapterNoteDetail(private val mCTSelf: Context, data: List<Map<String, *>>) : SimpleAdapter(mCTSelf, data, R.layout.liit_data_swipe_holder, arrayOf(), intArrayOf()) {
    internal data class ViewTag(var mContent: View?, var mRight: View?)

    override fun getViewTypeCount(): Int {
        val orgCount = count
        return if (orgCount < 1) 1 else orgCount
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vh = ViewHolder.get(mCTSelf, convertView, R.layout.liit_data_swipe_holder)
        val hmData = UtilFun.cast_t<HashMap<String, INote>>(getItem(position))
        val itemData = hmData[K_NODE]

        val sl = vh.getView<SwipeLayout>(R.id.swipe)
        var vt: ViewTag? = sl.tag as ViewTag?
        if (null == vt) {
            val vhSwipe = ViewHelper(sl)

            vt = ViewTag(mContent = null, mRight = vhSwipe.getChildView(R.id.cl_swipe_right))
            if (itemData!!.isPayNote) {
                vhSwipe.setVisibility(R.id.rl_income, View.GONE)

                vt.mContent = vhSwipe.getChildView(R.id.rl_pay)
                initPay(vt.mContent, itemData)
            } else {
                vhSwipe.setVisibility(R.id.rl_pay, View.GONE)

                vt.mContent = vhSwipe.getChildView(R.id.rl_income)
                initIncome(vt.mContent, itemData)
            }

            val vhRight = ViewHelper(vt.mRight)
            vhRight.setTag(R.id.iv_delete, itemData)
            vhRight.setTag(R.id.iv_edit, itemData)

            vhRight.getChildView<View>(R.id.iv_delete).setOnClickListener { v ->
                val alertDialog = AlertDialog.Builder(mCTSelf).setTitle("删除数据").setMessage("此操作不能恢复，是否继续操作!").setPositiveButton("是") { dialog, which ->
                    val data = v.tag as INote
                    val al = listOf(data.id)

                    if (data.isPayNote) {
                        ContextUtil.payIncomeUtility.deletePayNotes(al)
                    } else {
                        ContextUtil.payIncomeUtility.deleteIncomeNotes(al)
                    }
                }.setNegativeButton("否") { dialog, which -> }.create()
                alertDialog.show()
            }

            vhRight.getChildView<View>(R.id.iv_edit).setOnClickListener { v ->
                val intent: Intent
                val data = v.tag as INote
                intent = Intent(mCTSelf, ACPreveiwAndEdit::class.java)
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.id)
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE,
                        if (data.isPayNote) GlobalDef.STR_RECORD_PAY else GlobalDef.STR_RECORD_INCOME)

                mCTSelf.startActivity(intent)
            }

            sl.tag = vt
        }

        return vh.convertView
    }

    /**
     * init pay node
     * @param vh    view holder
     * @param data  pay data
     */
    private fun initPay(vh: View?, data: INote) {
        val pn = data.toPayNote()
        val vHelper = ViewHelper(vh)

        vHelper.setText(R.id.tv_pay_title, pn!!.info)

        val bi = pn.budget
        val b_name = if (bi == null) "" else bi.name
        if (!UtilFun.StringIsNullOrEmpty(b_name)) {
            vHelper.setText(R.id.tv_pay_budget, b_name)
        } else {
            vHelper.setVisibility(R.id.tv_pay_budget, View.GONE)
            vHelper.setVisibility(R.id.iv_pay_budget, View.GONE)
        }

        vHelper.setText(R.id.tv_pay_amount, String.format(Locale.CHINA, "- %s", pn.valToStr))
        vHelper.setText(R.id.tv_pay_time, pn.ts.toString().substring(11, 16))

        // for look detail
        vHelper.setVisibility(R.id.iv_pay_action, View.GONE)

        // for budget
        if (UtilFun.StringIsNullOrEmpty(b_name)) {
            vHelper.setVisibility(R.id.rl_budget, View.GONE)
        }

        // for note
        val nt = pn.note
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            vHelper.setVisibility(R.id.rl_pay_note, View.GONE)
        } else {
            vHelper.setText(R.id.tv_pay_note, nt)
        }
    }

    /**
     * init income node
     * @param vh    view holder
     * @param data  income data
     */
    private fun initIncome(vh: View?, data: INote) {
        val i_n = data.toIncomeNote()
        val vHelper = ViewHelper(vh)

        vHelper.setText(R.id.tv_income_title, i_n!!.info)

        vHelper.setText(R.id.tv_income_amount, i_n.valToStr)
        vHelper.setText(R.id.tv_income_time, i_n.ts.toString().substring(11, 16))

        // for look detail
        vHelper.setVisibility(R.id.iv_income_action, View.GONE)

        // for note
        val nt = i_n.note
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            vHelper.setVisibility(R.id.rl_income_note, View.GONE)
        } else {
            vHelper.setText(R.id.tv_income_note, nt)
        }
    }

    companion object {
        const val K_NODE = "node"
    }
}
