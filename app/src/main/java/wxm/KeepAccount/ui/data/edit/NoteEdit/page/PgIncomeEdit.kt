package wxm.KeepAccount.ui.data.edit.NoteEdit.page

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.theartofdev.edmodo.cropper.CropImage
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.db.NoteImageUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.item.IncomeNoteItem
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
import java.io.File
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
class PgIncomeEdit : FrgSupportBaseAdv(), IEdit {
    private val mETInfo: TouchEditText by bindView(R.id.ar_et_info)
    private val mETDate: TouchEditText by bindView(R.id.ar_et_date)
    private val mETAmount: TouchEditText by bindView(R.id.ar_et_amount)
    private val mTVNote: TouchTextView by bindView(R.id.tv_note)

    private val mLLImageHeader: LinearLayout by bindView(R.id.ll_image_header)
    private val mIBImageRefresh: ImageButton by bindView(R.id.ib_image_refresh)
    private val mIBImageRemove: ImageButton by bindView(R.id.ib_image_remove)
    private val mIVImage: ImageView by bindView(R.id.iv_image)

    private var mSZImagePath: String = ""
    private val mSZDefNote: String = AppBase.getString(R.string.notice_input_note)

    private var mOldIncomeNote: IncomeNoteItem? = null

    override fun getLayoutID(): Int = R.layout.pg_edit_income
    override fun setEditData(data: Any) {
        mOldIncomeNote = data as IncomeNoteItem
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

                it.tag = mSZImagePath
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            mETAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    s.toString().indexOf(".").let1 {
                        if (it >= 0) {
                            if (s.length - (it + 1) > 2) {
                                mETAmount.error = "小数点后超过两位数!"
                                mETAmount.setText(s.subSequence(0, it + 3))
                            }
                        }
                    }
                }
            })

            View.OnTouchListener { v, event -> onTouchChildView(v, event) }.let1 {
                mETInfo.setOnTouchListener(it)
                //mETAmount.setOnTouchListener(it)
                mETDate.setOnTouchListener(it)
                mTVNote.setOnTouchListener(it)
            }

            mIVImage.setOnClickListener({v ->
                if(mSZImagePath.isEmpty()) {
                    CropImage.activity().start(context!!, this)
                } else  {
                    mLLImageHeader.visibility = (View.GONE == mLLImageHeader.visibility)
                            .doJudge(View.VISIBLE, View.GONE)
                }
            })

            mIBImageRefresh.setOnClickListener({v ->
                mLLImageHeader.visibility = View.GONE
                CropImage.activity().start(context!!, this)
            })

            mIBImageRemove.setOnClickListener({v ->
                mOldIncomeNote?.let1 { pn ->
                    NoteImageUtility.removeImage(pn, mSZImagePath)
                }

                mSZImagePath = ""
                mIVImage.setImageResource(R.drawable.image_add_pic)
                mLLImageHeader.visibility = View.GONE
            })
        }

        loadUI(bundle)
    }

    override fun loadUI(bundle: Bundle?) {
        val paraDate = arguments?.getString(GlobalDef.STR_RECORD_DATE)
        mLLImageHeader.visibility = View.GONE
        mOldIncomeNote?.let {
            mETDate.setText(paraDate ?: it.tsToStr.substring(0, 16))
            mETInfo.setText(it.info)

            mTVNote.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            it.note.let1 {
                mTVNote.text = if (UtilFun.StringIsNullOrEmpty(it)) mSZDefNote else it
            }

            mETAmount.setText(it.valToStr)
            if(it.images.isNotEmpty())  {
                mSZImagePath = it.images[0].imagePath
                mIVImage.setImagePath(mSZImagePath)
            }
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
     * write record
     * @return      true if success
     */
    private fun fillResult(): Boolean {
        refillData()

        mOldIncomeNote?.let {
            val bCreate = GlobalDef.INVALID_ID == it.id
            var bRet = bCreate.doJudge(
                    { 1 == AppUtil.payIncomeUtility.addIncomeNotes(listOf(it)) },
                    { AppUtil.payIncomeUtility.incomeDBUtility.modifyData(it) }
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

    private fun onTouchChildView(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (v.id) {
                    /*
                    R.id.ar_et_amount ->    {
                        if(BigDecimal.ZERO.toMoneyString() == mETAmount.text.toString())    {
                            mETAmount.text.clear()
                        }
                    }
                    */

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
}
