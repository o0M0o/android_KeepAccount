package wxm.KeepAccount.ui.data.edit.NoteEdit.page

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import com.theartofdev.edmodo.cropper.CropImage
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.NoteImageUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.event.PicPath
import wxm.androidutil.improve.let1
import wxm.KeepAccount.item.IncomeNoteItem
import wxm.KeepAccount.item.NoteImageItem
import wxm.KeepAccount.ui.base.ACBase.ACBase
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText
import wxm.KeepAccount.ui.base.TouchUI.TouchTextView
import wxm.KeepAccount.ui.data.edit.NoteEdit.FrgNoteEdit
import wxm.KeepAccount.ui.data.edit.NoteEdit.page.base.IAddPicPath
import wxm.KeepAccount.ui.data.edit.NoteEdit.page.base.PicLVAdapter
import wxm.KeepAccount.ui.data.edit.base.FrgEditBase
import wxm.KeepAccount.ui.data.edit.base.IEdit
import wxm.KeepAccount.ui.dialog.DlgLongTxt
import wxm.KeepAccount.ui.dialog.DlgSelectRecordType
import wxm.KeepAccount.ui.preview.ACImagePreview
import wxm.KeepAccount.utility.*
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.setImagePath
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

    private val mLVImage: ListView by bindView(R.id.lv_pic)
    private val mIBAddImage: ImageButton by bindView(R.id.ib_add_pic)
    private var mLSImagePath = ArrayList<String>()
    private var mIAddPicPath:IAddPicPath? = null

    private val mSZDefNote: String = AppBase.getString(R.string.notice_input_note)

    private var mOldIncomeNote: IncomeNoteItem? = null
    private var mUsrVisible = true

    override fun getLayoutID(): Int = R.layout.pg_income_edit
    override fun isUseEventBus(): Boolean = true

    override fun setEditData(data: Any) {
        mOldIncomeNote = data as IncomeNoteItem
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mUsrVisible = isVisibleToUser
    }

    /**
     * remove/refresh/preview pic in [event]
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPicPath(event: PicPath) {
        if(!mUsrVisible)
            return

        when(event.action)  {
            PicPath.REMOVE_PIC -> {
                if(mLSImagePath.contains(event.picPath)) {
                    mLSImagePath.remove(event.picPath)
                    reloadPics()
                }
            }

            PicPath.REFRESH_PIC ->  {
                mIAddPicPath = object : IAddPicPath {
                    override fun addPicPath(path: String) {
                        mLSImagePath.remove(event.picPath)
                        mLSImagePath.add(path)
                        reloadPics()
                    }
                }

                CropImage.activity().start(activity!!, this)
            }

            PicPath.PREVIEW_PIC ->  {
                Intent(activity!!, ACImagePreview::class.java).let1 {
                    it.putExtra(ACImagePreview.IMAGE_FILE_PATH, event.picPath)
                    activity!!.startActivity(it)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.getActivityResult(data).let1 { result ->
                if (resultCode == Activity.RESULT_OK) {
                    saveImage(result.uri).let1 {
                        if (it.isNotEmpty()) {
                            mIAddPicPath?.addPicPath(it)
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

                it.tag = mLSImagePath
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            mETAmount.addTextChangedListener(MoneyTextWatcher(mETAmount))
            View.OnTouchListener { v, event -> onTouchChildView(v, event) }
                    .let1 {
                        mETInfo.setOnTouchListener(it)
                        //mETAmount.setOnTouchListener(it)
                        mETDate.setOnTouchListener(it)
                        mTVNote.setOnTouchListener(it)
                    }

            mIBAddImage.setOnClickListener{_ ->
                mIAddPicPath = object : IAddPicPath {
                    override fun addPicPath(path: String) {
                        if(!mLSImagePath.contains(path)) {
                            mLSImagePath.add(path)
                            reloadPics()
                        }
                    }
                }

                CropImage.activity().start(activity!!, this)
            }
        }

        loadUI(bundle)
    }

    override fun loadUI(bundle: Bundle?) {
        val paraDate = arguments?.getString(GlobalDef.STR_RECORD_DATE)
        mOldIncomeNote?.let {
            mETDate.setText(paraDate ?: it.tsToStr.substring(0, 16))
            mETInfo.setText(it.info)

            mTVNote.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            it.note.let1 {
                mTVNote.text = if (UtilFun.StringIsNullOrEmpty(it)) mSZDefNote else it
            }

            mETAmount.setText(it.valToStr)
            if (it.images.isNotEmpty()) {
                mLSImagePath.clear()
                mLSImagePath.addAll(it.images.filter { it.status == NoteImageItem.STATUS_USE }
                        .map { it.imagePath })
                reloadPics()
            }
        }
    }

    override fun onAccept(): Boolean {
        return checkResult() && fillResult()
    }


    /// BEGIN PRIVATE
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
                        val cd = Calendar.getInstance().apply {
                            time = try {
                                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).parse(mETDate.text.toString())
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
                        DlgLongTxt().let1 { dlg ->
                            dlg.longTxt = mTVNote.text.toString()
                                    .let { if (mSZDefNote == it) "" else it }
                            dlg.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                                override fun onDialogPositiveResult(dialogFragment: DialogFragment) {
                                    mTVNote.text = (dialogFragment as DlgLongTxt).longTxt.let {
                                        if (it.isEmpty()) mSZDefNote
                                        else it
                                    }
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



    private fun reloadPics()    {
        ArrayList<Map<String, String>>().apply {
            addAll(mLSImagePath.map { HashMap<String, String>().apply { put(PicLVAdapter.PIC_PATH, it) } })
        }.let1 {
            mLVImage.adapter = PicLVAdapter(context!!,  it, true)
        }
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
            val bRet = bCreate.doJudge(
                    { 1 == AppUtil.payIncomeUtility.addIncomeNotes(listOf(it)) },
                    { AppUtil.payIncomeUtility.incomeDBUtility.modifyData(it) }
            )

            if (bRet) {
                refreshNoteImage(it, mLSImagePath, bCreate)
            } else {
                DlgAlert.showAlert(context!!, R.string.dlg_warn,
                        bCreate.doJudge(R.string.dlg_create_data_failure, R.string.dlg_modify_data_failure))
            }

            return bRet
        }

        return false
    }
    /// END PRIVATE
}
