package wxm.KeepAccount.ui.data.edit.NoteEdit.utility

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText
import wxm.KeepAccount.ui.base.TouchUI.TouchTextView
import wxm.KeepAccount.ui.data.edit.base.IEdit
import wxm.KeepAccount.ui.dialog.DlgLongTxt
import wxm.KeepAccount.ui.dialog.DlgSelectRecordType
import wxm.KeepAccount.utility.AppUtil
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.androidutil.util.doJudge
import java.lang.String.format
import java.math.BigDecimal
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * edit pay
 * Created by WangXM on2016/9/28.
 */
class PagePayEdit : FrgSupportBaseAdv(), IEdit {
    private val mETInfo: TouchEditText by bindView(R.id.ar_et_info)
    private val mETDate: TouchEditText by bindView(R.id.ar_et_date)
    private val mETAmount: TouchEditText by bindView(R.id.ar_et_amount)
    private val mSPBudget: Spinner by bindView(R.id.ar_sp_budget)
    private val mTVBudget: TextView by bindView(R.id.ar_tv_budget)
    private val mTVNote: TouchTextView by bindView(R.id.tv_note)

    private val mSZDefNote: String = AppBase.getString(R.string.notice_input_note)
    private var mOldPayNote: PayNoteItem? = null

    override fun setEditData(data: Any) {
        mOldPayNote = data as PayNoteItem
    }

    override fun refillData() {
        if (isVisible) {
            mOldPayNote?.let {
                it.info = mETInfo.text.toString()

                val szVal = mETAmount.text.toString()
                it.amount = if (UtilFun.StringIsNullOrEmpty(szVal))
                    BigDecimal.ZERO
                else BigDecimal(szVal)

                val szNote = mTVNote.text.toString()
                it.note = if (mSZDefNote == szNote) null else szNote

                val szDate = mETDate.text.toString() + ":00"
                it.ts =
                        try {
                            ToolUtil.stringToTimestamp(szDate)
                        } catch (ex: Exception) {
                            Timestamp(0)
                        }

                it.budget = null
                val pos = mSPBudget.selectedItemPosition
                if (AdapterView.INVALID_POSITION != pos && 0 != pos) {
                    val bi = AppUtil.budgetUtility.getBudgetByName(mSPBudget.selectedItem as String)
                    if (null != bi) {
                        it.budget = bi
                    }
                }
            }
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.page_edit_pay
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun loadUI(bundle: Bundle?) {
        val paraDate = arguments?.getString(GlobalDef.STR_RECORD_DATE)
        mTVNote.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        mOldPayNote?.let {
            val bi = it.budget
            if (null != bi) {
                val bn = bi.name
                val cc = mSPBudget.adapter.count
                for (i in 0 until cc) {
                    val bni = UtilFun.cast<String>(mSPBudget.adapter.getItem(i))
                    if (bn == bni) {
                        mSPBudget.setSelection(i)
                        break
                    }
                }
            }

            mETDate.setText(paraDate ?: it.tsToStr.substring(0, 16))
            mETInfo.setText(it.info)

            val szNote = it.note
            mTVNote.text = if (UtilFun.StringIsNullOrEmpty(szNote)) mSZDefNote else szNote

            mETAmount.setText(it.valToStr)
        }
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            // 填充预算数据
            val lsBudgetName = ArrayList<String>()
            lsBudgetName.add("无预算(不使用预算)")
            AppUtil.budgetUtility.budgetForCurUsr?.forEach {
                lsBudgetName.add(it.name)
            }

            val spAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, lsBudgetName)
            spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mSPBudget.adapter = spAdapter

            // 填充其他数据
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

            val listener = View.OnTouchListener { v, event -> onTouchChildView(v, event) }
            mETInfo.setOnTouchListener(listener)
            mETDate.setOnTouchListener(listener)
            mTVNote.setOnTouchListener(listener)
        }

        loadUI(bundle)
    }

    private fun onTouchChildView(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (v.id) {
                    R.id.ar_et_info -> {
                        val dp = DlgSelectRecordType()
                        dp.setOldType(GlobalDef.STR_RECORD_PAY, mETInfo.text.toString())
                        dp.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                            override fun onDialogPositiveResult(dialog: DialogFragment) {
                                val dpCur = UtilFun.cast_t<DlgSelectRecordType>(dialog)
                                val curInfo = dpCur.curType
                                mETInfo.setText(curInfo)
                                mETInfo.requestFocus()
                            }

                            override fun onDialogNegativeResult(dialog: DialogFragment) {
                                mETInfo.requestFocus()
                            }
                        })

                        dp.show(fragmentManager, "选择类型")
                    }

                    R.id.ar_et_date -> {
                        val sd = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
                        val jDate = try {
                            sd.parse(mETDate.text.toString())
                        } catch (e: ParseException) {
                            e.printStackTrace()
                            Date()
                        }

                        val cd = Calendar.getInstance()
                        cd.time = jDate

                        val dt = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            val strDate = format(Locale.CHINA, "%04d-%02d-%02d",
                                    year, month + 1, dayOfMonth)

                            val ot = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                                val tm = format(Locale.CHINA, "%s %02d:%02d",
                                        strDate, hourOfDay, minute)

                                mETDate.setText(tm)
                                mETDate.requestFocus()
                            }

                            val td = TimePickerDialog(context, ot,
                                    cd.get(Calendar.HOUR_OF_DAY),
                                    cd.get(Calendar.MINUTE), true)
                            td.show()
                        }

                        val dd = DatePickerDialog(context, dt,
                                cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),
                                cd.get(Calendar.DAY_OF_MONTH))
                        dd.show()
                    }

                    R.id.tv_note -> {
                        val szNote = mTVNote.text.toString()
                        val lt = if (mSZDefNote == szNote) "" else szNote

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
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                v.performClick()
            }
        }

        return true
    }

    override fun onAccept(): Boolean {
        return checkResult() && fillResult()
    }

    /**
     * check data validity
     * @return      true if data validity
     */
    private fun checkResult(): Boolean {
        val ac = activity ?: return false

        if (UtilFun.StringIsNullOrEmpty(mETAmount.text.toString())) {
            val dlg = AlertDialog.Builder(ac)
                        .setTitle("警告").setMessage("请输入支出数值!")
                        .create()

            dlg.show()
            return false
        }

        if (UtilFun.StringIsNullOrEmpty(mETInfo.text.toString())) {
            val dlg = AlertDialog.Builder(ac)
                    .setTitle("警告").setMessage("请输入支出信息!")
                    .create()

            dlg.show()
            return false
        }

        if (UtilFun.StringIsNullOrEmpty(mETDate.text.toString())) {
            val dlg = AlertDialog.Builder(ac)
                    .setTitle("警告").setMessage("请输入支出日期!")
                    .create()

            dlg.show()
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

        mOldPayNote?.let {
            val bCreate = GlobalDef.INVALID_ID == it.id
            val bRet = if (bCreate)
                1 == AppUtil.payIncomeUtility.addPayNotes(listOf(it))
            else
                AppUtil.payIncomeUtility.payDBUtility.modifyData(it)
            if (!bRet) {
                DlgAlert.showAlert(context!!, R.string.dlg_warn,
                        bCreate.doJudge(R.string.dlg_create_data_failure, R.string.dlg_modify_data_failure))
            }
            return bRet
        }

        return false
    }
}
