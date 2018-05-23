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
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText
import wxm.KeepAccount.ui.base.TouchUI.TouchTextView
import wxm.KeepAccount.ui.data.edit.base.IEdit
import wxm.KeepAccount.ui.dialog.DlgLongTxt
import wxm.KeepAccount.ui.dialog.DlgSelectRecordType
import wxm.KeepAccount.utility.ContextUtil
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import java.lang.String.format
import java.math.BigDecimal
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * edit income
 * Created by WangXM on2016/9/28.
 */
class PageIncomeEdit : FrgSupportBaseAdv(), IEdit {
    private val mETInfo: TouchEditText by bindView(R.id.ar_et_info)
    private val mETDate: TouchEditText by bindView(R.id.ar_et_date)
    private val mETAmount: TouchEditText by bindView(R.id.ar_et_amount)
    private val mTVNote: TouchTextView by bindView(R.id.tv_note)

    private val mSZDefNote: String = AppBase.getString(R.string.notice_input_note)

    private var mOldIncomeNote: IncomeNoteItem? = null

    override fun setEditData(data: Any) {
        mOldIncomeNote = data as IncomeNoteItem
    }

    override fun refillData() {
        if (isVisible) {
            mOldIncomeNote?.let {
                it.info = mETInfo.text.toString()

                val szVal = mETAmount.text.toString()
                it.amount = if (UtilFun.StringIsNullOrEmpty(szVal)) BigDecimal.ZERO else BigDecimal(szVal)

                val szNote = mTVNote.text.toString()
                it.note = if (mSZDefNote == szNote) null else szNote

                val szDate = mETDate.text.toString() + ":00"
                it.ts = try {
                    ToolUtil.stringToTimestamp(szDate)
                } catch (ex: Exception) {
                    Timestamp(0)
                }
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
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

    override fun getLayoutID(): Int {
        return R.layout.page_edit_income
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun loadUI(bundle: Bundle?) {
        val paraDate = arguments?.getString(GlobalDef.STR_RECORD_DATE)
        mOldIncomeNote?.let {
            mETDate.setText(paraDate ?: it.tsToStr!!.substring(0, 16))
            mETInfo.setText(it.info)

            val szNote = it.note
            mTVNote.text = if (UtilFun.StringIsNullOrEmpty(szNote)) mSZDefNote else szNote
            mTVNote.paint.flags = Paint.UNDERLINE_TEXT_FLAG

            mETAmount.setText(it.valToStr)
        }
    }

    override fun onAccept(): Boolean {
        return checkResult() && fillResult()
    }


    /**
     * check record validity
     * @return      true if record validity
     */
    private fun checkResult(): Boolean {
        val ac = activity ?: return false

        if (UtilFun.StringIsNullOrEmpty(mETAmount.text.toString())) {
            val builder = android.app.AlertDialog.Builder(ac)
            builder.setMessage("请输入收入数值!").setTitle("警告")

            val dlg = builder.create()
            dlg.show()
            return false
        }

        if (UtilFun.StringIsNullOrEmpty(mETInfo.text.toString())) {
            val builder = android.app.AlertDialog.Builder(ac)
            builder.setMessage("请输入收入信息!").setTitle("警告")

            val dlg = builder.create()
            dlg.show()
            return false
        }

        if (UtilFun.StringIsNullOrEmpty(mETDate.text.toString())) {
            val builder = android.app.AlertDialog.Builder(ac)
            builder.setMessage("请输入收入日期!").setTitle("警告")

            val dlg = builder.create()
            dlg.show()
            return false
        }

        return true
    }

    /**
     * write record
     * @return      true if success
     */
    private fun fillResult(): Boolean {
        refillData()

        mOldIncomeNote?.let {
            val bCreate = GlobalDef.INVALID_ID == it.id
            val bRet = if (bCreate)
                1 == ContextUtil.payIncomeUtility.addIncomeNotes(listOf(it))
            else
                ContextUtil.payIncomeUtility.incomeDBUtility.modifyData(it)
            if (!bRet) {
                val dlg = AlertDialog.Builder(context)
                        .setTitle("警告")
                        .setMessage(if (bCreate) "创建收入数据失败!" else "更新收入数据失败")
                        .create()
                dlg.show()
            }
            return bRet
        }

        return false
    }

    private fun onTouchChildView(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (v.id) {
                    R.id.ar_et_info -> {
                        val dp = DlgSelectRecordType()
                        dp.setOldType(GlobalDef.STR_RECORD_INCOME, mETInfo.text.toString())
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
                        val jDt = try {
                            sd.parse(mETDate.text.toString())
                        } catch (e: ParseException) {
                            e.printStackTrace()
                            Date()
                        }

                        val cd = Calendar.getInstance()
                        cd.time = jDt

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
                                longTxt =   if (UtilFun.StringIsNullOrEmpty(longTxt)) mSZDefNote
                                            else longTxt

                                mTVNote.text = longTxt
                                mTVNote.paint.flags = Paint.UNDERLINE_TEXT_FLAG
                            }

                            override fun onDialogNegativeResult(dialogFragment: DialogFragment) {}
                        })

                        dlg.show(activity.supportFragmentManager, "edit note")
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                v.performClick()
            }
        }

        return true
    }
}
