package wxm.KeepAccount.ui.utility

import android.content.Context
import android.content.Intent
import android.view.View
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.INote
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.define.PayNoteItem
import wxm.KeepAccount.ui.base.Helper.ViewHelper
import wxm.KeepAccount.ui.data.edit.NoteEdit.ACNoteEdit
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.ViewHolder
import wxm.androidutil.util.UtilFun
import wxm.uilib.SwipeLayout.SwipeLayout
import java.util.*

/**
 * adapter for note detail
 * Created by WangXM on 2017/1/23.
 */
class AdapterNoteDetail(ct: Context, data: List<Map<String, INote>>)
    : MoreAdapter(ct, data, R.layout.liit_data_swipe_holder) {
    internal data class ViewTag(var mContent: View, var mRight: View)

    override fun loadView(pos: Int, vh: ViewHolder) {
        @Suppress("UNCHECKED_CAST")
        val itemData = (getItem(pos) as Map<String, INote>)[K_NODE]!!

        val sl = vh.getView<SwipeLayout>(R.id.swipe)
        var vt: ViewTag? = sl.tag as ViewTag?
        if (null == vt) {
            vt = ViewTag(mContent = vh.getView(if (itemData.isPayNote) R.id.rl_pay else R.id.rl_income),
                    mRight = vh.getView(R.id.cl_swipe_right))
            if (itemData.isPayNote) {
                vh.getView<View>(R.id.rl_income).visibility = View.GONE
                initPay(vt.mContent, itemData.toPayNote()!!)
            } else {
                vh.getView<View>(R.id.rl_pay).visibility = View.GONE
                initIncome(vt.mContent, itemData.toIncomeNote()!!)
            }

            ViewHelper(vt.mRight).let {
                it.setTag(R.id.iv_delete, itemData)
                it.setTag(R.id.iv_edit, itemData)

                it.getChildView<View>(R.id.iv_delete)!!.setOnClickListener { v ->
                    DlgAlert.showAlert(context, "删除数据", "此操作不能恢复，是否继续!",
                            { b ->
                                b.setPositiveButton("是") { _, _ ->
                                    (v.tag as INote).let {
                                        if (it.isPayNote) {
                                            ContextUtil.payIncomeUtility.deletePayNotes(listOf(it.id))
                                        } else {
                                            ContextUtil.payIncomeUtility.deleteIncomeNotes(listOf(it.id))
                                        }
                                    }
                                }
                                b.setNegativeButton("否") { _, _ -> }
                            })
                }

                it.getChildView<View>(R.id.iv_edit)!!.setOnClickListener { v ->
                    Intent(context, ACNoteEdit::class.java).let {
                        val data = v.tag as INote
                        it.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.id)
                        it.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE,
                                if (data.isPayNote) GlobalDef.STR_RECORD_PAY else GlobalDef.STR_RECORD_INCOME)

                        context.startActivity(it)

                        Unit
                    }
                }

                Unit
            }

            sl.tag = vt
        }
    }

    /**
     * init pay node
     * @param vh    view holder
     * @param data  pay data
     */
    private fun initPay(vh: View, data: PayNoteItem) {
        val vHelper = ViewHelper(vh)
        vHelper.setText(R.id.tv_pay_title, data.info!!)

        val bi = data.budget
        val bName = bi?.name ?: ""
        if (!UtilFun.StringIsNullOrEmpty(bName)) {
            vHelper.setText(R.id.tv_pay_budget, bName)
        } else {
            vHelper.setVisibility(R.id.tv_pay_budget, View.GONE)
            vHelper.setVisibility(R.id.iv_pay_budget, View.GONE)
        }

        vHelper.setText(R.id.tv_pay_amount, String.format(Locale.CHINA, "- %s", data.valToStr))
        vHelper.setText(R.id.tv_pay_time, data.ts.toString().substring(11, 16))

        // for look detail
        vHelper.setVisibility(R.id.iv_pay_action, View.GONE)

        // for budget
        if (UtilFun.StringIsNullOrEmpty(bName)) {
            vHelper.setVisibility(R.id.rl_budget, View.GONE)
        }

        // for note
        val nt = data.note
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            vHelper.setVisibility(R.id.rl_pay_note, View.GONE)
        } else {
            vHelper.setText(R.id.tv_pay_note, nt!!)
        }
    }

    /**
     * init income node
     * @param vh    view holder
     * @param data  income data
     */
    private fun initIncome(vh: View, data: IncomeNoteItem) {
        val vHelper = ViewHelper(vh)
        vHelper.setText(R.id.tv_income_title, data.info!!)

        vHelper.setText(R.id.tv_income_amount, data.valToStr!!)
        vHelper.setText(R.id.tv_income_time, data.ts.toString().substring(11, 16))

        // for look detail
        vHelper.setVisibility(R.id.iv_income_action, View.GONE)

        // for note
        val nt = data.note
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            vHelper.setVisibility(R.id.rl_income_note, View.GONE)
        } else {
            vHelper.setText(R.id.tv_income_note, nt!!)
        }
    }

    companion object {
        const val K_NODE = "node"
    }
}
