package wxm.KeepAccount.ui.utility

import android.content.Context
import android.content.Intent
import android.view.View
import wxm.KeepAccount.R
import wxm.KeepAccount.db.PayIncomeDBUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.improve.*
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.item.IncomeNoteItem
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.data.edit.NoteEdit.ACNoteEdit
import wxm.KeepAccount.utility.AppUtil
import wxm.androidutil.improve.let1
import wxm.androidutil.time.toCalendar
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.ViewHelper
import wxm.androidutil.ui.view.ViewHolder
import wxm.uilib.SwipeLayout.SwipeLayout
import java.sql.Timestamp
import java.util.*

/**
 * adapter for note detail
 * Created by WangXM on 2017/1/23.
 */
class AdapterNoteRangeDetail(ct: Context, data: List<Map<String, INote>>)
    : MoreAdapter(ct, data, R.layout.liit_data_swipe_holder) {
    private val mSelfContext = ct

    internal data class ViewTag(var mContent: View, var mRight: View)

    override fun loadView(pos: Int, vhHolder: ViewHolder) {
        @Suppress("UNCHECKED_CAST")
        val itemData = (getItem(pos) as Map<String, INote>)[K_NODE]!!

        val sl = vhHolder.getView<SwipeLayout>(R.id.swipe)!!
        var vt: ViewTag? = sl.tag as ViewTag?
        if (null == vt) {
            vt = ViewTag(mContent = vhHolder.getView(if (itemData is PayNoteItem) R.id.rl_pay else R.id.rl_income),
                    mRight = vhHolder.getView(R.id.cl_swipe_right))
            if (itemData is PayNoteItem) {
                vhHolder.getView<View>(R.id.rl_income).visibility = View.GONE
                initPay(vt.mContent, itemData)
            } else {
                vhHolder.getView<View>(R.id.rl_pay).visibility = View.GONE
                initIncome(vt.mContent, itemData as IncomeNoteItem)
            }

            ViewHelper(vt.mRight).let1 {
                it.setTag(R.id.iv_delete, itemData)
                it.setTag(R.id.iv_edit, itemData)

                it.getChildView<View>(R.id.iv_delete)!!.setOnClickListener { v ->
                    DlgAlert.showAlert(mSelfContext, "删除数据", "此操作不能恢复，是否继续!"
                    ) { b ->
                        b.setPositiveButton("是") { _, _ ->
                            (v.tag as INote).let {
                                if (it is PayNoteItem) {
                                    PayIncomeDBUtility.instance.deletePayNotes(listOf(it.id))
                                } else {
                                    PayIncomeDBUtility.instance.deleteIncomeNotes(listOf(it.id))
                                }
                            }
                        }
                        b.setNegativeButton("否") { _, _ -> }
                    }
                }

                it.getChildView<View>(R.id.iv_edit)!!.setOnClickListener { v ->
                    previewEditNote(v.tag as INote)
                }
            }

            vt.mContent.setOnClickListener{
                previewEditNote(itemData)
            }

            sl.tag = vt
        }
    }

    private fun previewEditNote(data: INote) {
        Intent(mSelfContext, ACNoteEdit::class.java).let1 {
            it.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.id)
            it.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, data.noteType())
            mSelfContext.startActivity(it)
        }
    }

    /**
     * init pay node
     * @param vw    root view
     * @param data  pay data
     */
    private fun initPay(vw: View, data: PayNoteItem) {
        ViewHelper(vw).let1 { vh ->
            vh.setText(R.id.tv_pay_title, data.info)

            (data.budget?.name).let1 {
                if (!it.isNullOrEmpty()) {
                    vh.setText(R.id.tv_pay_budget, it!!)
                } else {
                    vh.setVisibility(R.id.tv_pay_budget, View.GONE)
                    vh.setVisibility(R.id.rl_budget, View.GONE)
                }
            }

            vh.setText(R.id.tv_pay_amount, String.format(Locale.CHINA, "- %s", data.amount.toMoneyStr()))
            vh.setText(R.id.tv_pay_time, getDateStr(data.ts))

            // for note
            data.note.let1 {
                if (it.isNullOrEmpty()) {
                    vh.setVisibility(R.id.rl_pay_note, View.GONE)
                } else {
                    vh.setText(R.id.tv_pay_note, it!!)
                }
            }
        }
    }

    /**
     * init income node
     * @param vw    root view
     * @param data  income data
     */
    private fun initIncome(vw: View, data: IncomeNoteItem) {
        ViewHelper(vw).let1 { vh ->
            vh.setText(R.id.tv_income_title, data.info)

            vh.setText(R.id.tv_income_amount, data.amount.toMoneyStr())
            vh.setText(R.id.tv_income_time, getDateStr(data.ts))

            // for note
            data.note.let1 {
                if (it.isNullOrEmpty()) {
                    vh.setVisibility(R.id.rl_income_note, View.GONE)
                } else {
                    vh.setText(R.id.tv_income_note, it!!)
                }
            }
        }
    }

    companion object {
        const val K_NODE = "node"

        private fun getDateStr(ts: Timestamp): String   {
            return "${ts.toDayStr()} ${ts.toDayInWeekStr()} ${ts.toHourMinuteStr()}"
        }
    }
}
