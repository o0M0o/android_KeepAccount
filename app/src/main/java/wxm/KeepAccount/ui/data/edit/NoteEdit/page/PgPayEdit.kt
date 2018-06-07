package wxm.KeepAccount.ui.data.edit.NoteEdit.page

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.theartofdev.edmodo.cropper.CropImage
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.db.NoteImageUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText
import wxm.KeepAccount.ui.base.TouchUI.TouchTextView
import wxm.KeepAccount.ui.data.edit.base.IEdit
import wxm.KeepAccount.ui.dialog.DlgLongTxt
import wxm.KeepAccount.ui.dialog.DlgSelectRecordType
import wxm.KeepAccount.utility.*
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
class PgPayEdit : FrgSupportBaseAdv(), IEdit {
    private val mETInfo: TouchEditText by bindView(R.id.ar_et_info)
    private val mETDate: TouchEditText by bindView(R.id.ar_et_date)
    private val mETAmount: TouchEditText by bindView(R.id.ar_et_amount)
    private val mSPBudget: Spinner by bindView(R.id.ar_sp_budget)
    private val mTVBudget: TextView by bindView(R.id.ar_tv_budget)
    private val mTVNote: TouchTextView by bindView(R.id.tv_note)
    private val mIVImage: ImageView by bindView(R.id.iv_image)

    private val mCLImageHeader: ConstraintLayout by bindView(R.id.cl_image_header)
    private val mIBImageRefresh: ImageButton by bindView(R.id.ib_image_refresh)
    private val mIBImageRemove: ImageButton by bindView(R.id.ib_image_remove)

    private val mSZDefNote: String = AppBase.getString(R.string.notice_input_note)
    private var mOldPayNote: PayNoteItem? = null
    private var mSZImagePath: String = ""

    override fun getLayoutID(): Int = R.layout.pg_edit_pay
    override fun setEditData(data: Any) {
        mOldPayNote = data as PayNoteItem
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.getActivityResult(data).let1 { result ->
                if (resultCode == Activity.RESULT_OK) {
                    saveImage(result.uri).let1 {
                        if (it.isNotEmpty()) {
                            mSZImagePath = it
                            mIVImage.setImagePath(mSZImagePath)
                        }
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    DlgAlert.showAlert(activity!!, R.string.dlg_erro, result.error.toString())
                }
            }
        }
    }

    override fun refillData() {
        if (isVisible) {
            mOldPayNote?.let1 {
                it.info = mETInfo.text.toString()

                mETAmount.text.toString().let1 {it1 ->
                    it.amount = it1.isEmpty().doJudge(BigDecimal.ZERO, BigDecimal(it1))
                }

                mTVNote.text.toString().let1 { it1 ->
                    it.note = (mSZDefNote == it1).doJudge(null, it1)
                }

                (mETDate.text.toString() + ":00").let1 { it1 ->
                    it.ts = try {
                                ToolUtil.stringToTimestamp(it1)
                            } catch (ex: Exception) {
                                Timestamp(0)
                            }
                }

                it.budget = null
                mSPBudget.selectedItemPosition.let1 { it1 ->
                    if (AdapterView.INVALID_POSITION != it1 && 0 != it1) {
                        AppUtil.budgetUtility
                                .getBudgetByName(mSPBudget.selectedItem as String)?.let1 { it2 ->
                                    it.budget = it2
                                }
                    }
                }

                it.images = LinkedList<String>().apply {
                    if(mSZImagePath.isNotEmpty())    add(mSZImagePath)
                }
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            // 填充预算数据
            val lsBudgetName = ArrayList<String>().apply {
                add("无预算(不使用预算)")
                AppUtil.budgetUtility.budgetForCurUsr.forEach {
                    add(it.name)
                }
            }

            mSPBudget.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, lsBudgetName)
                    .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            // 填充其他数据
            mETAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    s.toString().indexOf(".").let1 {
                        if (it >= 0) {
                            if ((s.length - (it + 1)) > 2) {
                                mETAmount.error = "小数点后超过两位数!"
                                mETAmount.setText(s.subSequence(0, it + 3))
                            }
                        }
                    }
                }
            })

            View.OnTouchListener { v, event -> onTouchChildView(v, event) }.let1 {
                mETInfo.setOnTouchListener(it)
                mETDate.setOnTouchListener(it)
                mTVNote.setOnTouchListener(it)
            }

            mIVImage.setOnClickListener({ v ->
                if(mSZImagePath.isEmpty()) {
                    CropImage.activity()
                            .setAspectRatio(1, 1)
                            .start(context!!, this)
                } else  {
                    mCLImageHeader.visibility = (View.GONE == mCLImageHeader.visibility)
                            .doJudge(View.VISIBLE, View.GONE)
                }
            })

            mIBImageRefresh.setOnClickListener({v ->
                mCLImageHeader.visibility = View.GONE
                CropImage.activity()
                            .setAspectRatio(1, 1)
                            .start(context!!, this)
            })

            mIBImageRemove.setOnClickListener({v ->
                mCLImageHeader.visibility = View.GONE

                mSZImagePath = ""
                mOldPayNote?.let1 { pn ->
                    pn.images.forEach {
                        NoteImageUtility.removeImage(pn, it)
                    }
                }

                mIVImage.setImageResource(R.drawable.image_add_pic)
            })
        }

        loadUI(bundle)
    }

    override fun loadUI(bundle: Bundle?) {
        val paraDate = arguments?.getString(GlobalDef.STR_RECORD_DATE)
        mTVNote.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        mCLImageHeader.visibility = View.GONE
        mOldPayNote?.let1 {
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

            it.note.let1 {
                mTVNote.text = if (UtilFun.StringIsNullOrEmpty(it)) mSZDefNote else it
            }

            mETAmount.setText(it.valToStr)
            if (it.images.isNotEmpty()) {
                mSZImagePath = it.images[0]
                mIVImage.setImagePath(mSZImagePath)
            }
        }
    }


    private fun onTouchChildView(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (v.id) {
                    R.id.ar_et_info -> {
                        DlgSelectRecordType().let1 { dp ->
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
                    }

                    R.id.ar_et_date -> {
                        val sd = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
                        val cd = Calendar.getInstance()
                        cd.time = sd.let {
                            try {
                                sd.parse(mETDate.text.toString())
                            } catch (e: ParseException) {
                                e.printStackTrace()
                                Date()
                            }
                        }

                        val dt = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            val strDate = format(Locale.CHINA, "%04d-%02d-%02d",
                                    year, month + 1, dayOfMonth)

                            val ot = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                                val tm = format(Locale.CHINA, "%s %02d:%02d",
                                        strDate, hourOfDay, minute)

                                mETDate.setText(tm)
                                mETDate.requestFocus()
                            }

                            TimePickerDialog(context, ot,
                                    cd.get(Calendar.HOUR_OF_DAY),
                                    cd.get(Calendar.MINUTE), true).show()
                        }

                        DatePickerDialog(context, dt,
                                cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),
                                cd.get(Calendar.DAY_OF_MONTH)).show()
                    }

                    R.id.tv_note -> {
                        val szNote = mTVNote.text.toString()
                        val lt = if (mSZDefNote == szNote) "" else szNote

                        DlgLongTxt().let1 { dlg ->
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
        if (mETAmount.text.isNullOrEmpty()) {
            mETAmount.error = getString(R.string.error_field_required)
            return false
        }

        if (mETInfo.text.isNullOrEmpty()) {
            mETInfo.error = getString(R.string.error_field_required)
            return false
        }

        if (mETDate.text.isNullOrEmpty()) {
            mETDate.error = getString(R.string.error_field_required)
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
            var bRet = bCreate.doJudge(
                    { 1 == AppUtil.payIncomeUtility.addPayNotes(listOf(it)) },
                    { AppUtil.payIncomeUtility.payDBUtility.modifyData(it) }
            )

            bRet.doJudge(
                    {
                        if (mSZImagePath.isNotEmpty()) {
                            bRet = NoteImageUtility.addImage(it, mSZImagePath)
                        }
                    },
                    {
                        DlgAlert.showAlert(context!!, R.string.dlg_warn,
                                bCreate.doJudge(R.string.dlg_create_data_failure, R.string.dlg_modify_data_failure))
                    }
            )

            return bRet
        }

        return false
    }
}
