package wxm.KeepAccount.ui.data.edit.NoteEdit.utility

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.item.BudgetItem
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.TouchUI.TouchTextView
import wxm.KeepAccount.ui.data.edit.base.IEdit
import wxm.KeepAccount.ui.dialog.DlgLongTxt
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.androidutil.util.doJudge
import java.math.BigDecimal

/**
 * edit budget
 * Created by WangXM on2016/9/28.
 */
class PageBudgetEdit : FrgSupportBaseAdv(), IEdit  {
    private val mETName: TextInputEditText by bindView(R.id.et_budget_name)
    private val mETAmount: TextInputEditText by bindView(R.id.et_budget_amount)
    private val mTVNote: TouchTextView by bindView(R.id.tv_note)

    private val mSZDefNote: String = AppBase.getString(R.string.notice_input_note)
    private var mBIData: BudgetItem? = null

    override fun refillData() {
        if(isVisible)   {
            mBIData?.let {
                val name = mETName.text.toString()
                val amount = mETAmount.text.toString()

                it.name = name
                it.amount = if (UtilFun.StringIsNullOrEmpty(amount)) BigDecimal.ZERO else BigDecimal(amount)

                val sz = mTVNote.text.toString()
                it.note = if (mSZDefNote == sz) null else sz
            }
        }
    }

    override fun setEditData(data: Any) {
        mBIData = data as BudgetItem
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initUI(bundle: Bundle?) {
        mTVNote.paint.flags = Paint.UNDERLINE_TEXT_FLAG

        mTVNote.setOnTouchListener { _, motionEvent ->
            val vSelf = view
            if (null != vSelf && motionEvent.action == MotionEvent.ACTION_DOWN) {
                val tvSz = mTVNote.text.toString()
                val lt = if (mSZDefNote == tvSz) "" else tvSz

                val dlg = DlgLongTxt()
                dlg.longTxt = lt
                dlg.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                    override fun onDialogPositiveResult(dialogFragment: DialogFragment) {
                        var longTxt: String? = (dialogFragment as DlgLongTxt).longTxt
                        if (UtilFun.StringIsNullOrEmpty(longTxt))
                            longTxt = mSZDefNote

                        mTVNote.text = longTxt
                        mTVNote.paint.flags = Paint.UNDERLINE_TEXT_FLAG
                    }

                    override fun onDialogNegativeResult(dialogFragment: DialogFragment) {}
                })

                dlg.show(activity!!.supportFragmentManager, "edit note")
                return@setOnTouchListener true
            }

            false
        }

        mETAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val pos = s.toString().indexOf(".")
                if (pos >= 0) {
                    val afterLen = s.length - (pos + 1)
                    if (afterLen > 2) {
                        mETAmount.error = "小数点后超过两位数!"
                        mETAmount.setText(s.subSequence(0, pos + 3))
                    }
                }
            }
        })

        loadUI(bundle)
    }

    override fun onAccept(): Boolean {
        return checkResult() && fillResult()
    }

    /**
     * check data validity
     * @return      true if data validity
     */
    private fun checkResult(): Boolean {
        if (mETName.text.isEmpty()) {
            DlgAlert.showAlert(context!!, R.string.dlg_warn, R.string.dlg_need_budget_name)
            mETName.requestFocus()
            return false
        }

        val cbi = ContextUtil.budgetUtility.getBudgetByName(mETName.text.toString())
        if (null != cbi && (null == mBIData || mBIData!!._id != cbi._id)) {
            DlgAlert.showAlert(context!!, R.string.dlg_warn, R.string.dlg_already_have_budget_name)
            mETName.requestFocus()
            return false
        }

        if (mETAmount.text.isEmpty()) {
            DlgAlert.showAlert(context!!, R.string.dlg_warn, R.string.dlg_need_budget_amount)
            mETAmount.requestFocus()
            return false
        }

        return true
    }

    /**
     * write data
     * @return      true if success
     */
    private fun fillResult(): Boolean {
        refillData()

        mBIData?.let {
            val newItem = GlobalDef.INVALID_ID == it._id
            val sRet = if (newItem)
                ContextUtil.budgetUtility.createData(it)
            else
                ContextUtil.budgetUtility.modifyData(it)
            if (!sRet) {
                DlgAlert.showAlert(context!!, R.string.dlg_warn,
                        newItem.doJudge(R.string.dlg_create_data_failure, R.string.dlg_modify_data_failure))
            }
            return sRet
        }

        return false
    }


    override fun getLayoutID(): Int {
        return R.layout.vw_budget_edit
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    /**
     * init UI
     */
    override fun loadUI(bundle: Bundle?) {
        mBIData?.let {
            mETName.setText(it.name)
            mETAmount.setText(it.amount.toPlainString())

            mTVNote.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            val note = it.note
            mTVNote.text = if (UtilFun.StringIsNullOrEmpty(note)) mSZDefNote else note
        }
    }
}
