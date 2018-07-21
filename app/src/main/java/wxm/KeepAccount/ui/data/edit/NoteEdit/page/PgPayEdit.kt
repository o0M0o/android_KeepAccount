package wxm.KeepAccount.ui.data.edit.NoteEdit.page

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.theartofdev.edmodo.cropper.CropImage
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.BudgetDBUtility
import wxm.KeepAccount.db.PayIncomeDBUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.event.PicPath
import wxm.KeepAccount.improve.toDayHourMinuteStr
import wxm.KeepAccount.improve.toMoneyStr
import wxm.KeepAccount.item.NoteImageItem
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText
import wxm.KeepAccount.ui.base.TouchUI.TouchTextView
import wxm.KeepAccount.ui.base.page.IAddPicPath
import wxm.KeepAccount.ui.base.page.MoneyTextWatcher
import wxm.KeepAccount.ui.base.page.PicLVAdapter
import wxm.KeepAccount.ui.base.page.PgUtil
import wxm.KeepAccount.ui.base.page.PgUtil.refreshNoteImage
import wxm.KeepAccount.ui.data.edit.base.IEdit
import wxm.KeepAccount.ui.dialog.DlgLongTxt
import wxm.KeepAccount.ui.dialog.DlgSelectRecordType
import wxm.KeepAccount.ui.preview.ACImagePreview
import wxm.KeepAccount.utility.ToolUtil
import wxm.KeepAccount.utility.saveImage
import wxm.androidutil.app.AppBase
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.let1
import wxm.androidutil.time.CalendarUtility
import wxm.androidutil.time.toTimestamp
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

/**
 * edit pay
 * Created by WangXM on2016/9/28.
 */
class PgPayEdit : FrgSupportBaseAdv(), IEdit {
    private val mETInfo: TouchEditText by bindView(R.id.ar_et_info)
    private val mETDate: TouchEditText by bindView(R.id.ar_et_date)
    private val mETAmount: TouchEditText by bindView(R.id.ar_et_amount)
    private val mSPBudget: Spinner by bindView(R.id.ar_sp_budget)
    private val mTVNote: TouchTextView by bindView(R.id.tv_note)

    private val mLVImage: ListView by bindView(R.id.lv_pic)
    private val mIBAddImage: ImageButton by bindView(R.id.ib_add_pic)
    private var mLSImagePath = ArrayList<String>()
    private var mIAddPicPath: IAddPicPath? = null

    private val mSZDefNote: String = AppBase.getString(R.string.notice_input_note)
    private var mOldPayNote: PayNoteItem? = null
    private var mUsrVisible = true

    override fun getLayoutID(): Int = R.layout.pg_pay_edit
    override fun isUseEventBus(): Boolean = true

    override fun setEditData(data: Any) {
        mOldPayNote = data as PayNoteItem
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
        if (!mUsrVisible)
            return

        when (event.action) {
            PicPath.REMOVE_PIC -> {
                if (mLSImagePath.contains(event.picPath)) {
                    mLSImagePath.remove(event.picPath)
                    reloadPics()
                }
            }

            PicPath.REFRESH_PIC -> {
                mIAddPicPath = object : IAddPicPath {
                    override fun addPicPath(path: String) {
                        mLSImagePath.remove(event.picPath)
                        mLSImagePath.add(path)
                        reloadPics()
                    }
                }

                CropImage.activity().start(activity!!, this)
            }

            PicPath.PREVIEW_PIC -> {
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
            mOldPayNote?.let1 {
                it.info = mETInfo.text.toString()

                mETAmount.text.toString().let1 { it1 ->
                    it.amount = it1.isEmpty().doJudge(BigDecimal.ZERO, BigDecimal(it1))
                }

                mTVNote.text.toString().let1 { it1 ->
                    it.note = (mSZDefNote == it1).doJudge(null, it1)
                }

                it.ts = ToolUtil.stringToTimestamp(mETDate.text.toString())

                it.budget = null
                mSPBudget.selectedItemPosition.let1 { it1 ->
                    if (AdapterView.INVALID_POSITION != it1 && 0 != it1) {
                        BudgetDBUtility.instance
                                .getBudgetByName(mSPBudget.selectedItem as String)?.let1 { it2 ->
                                    it.budget = it2
                                }
                    }
                }

                refreshNoteImage(it, mLSImagePath)
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            // 填充预算数据
            val lsBudgetName = ArrayList<String>().apply {
                add("无预算(不使用预算)")
                BudgetDBUtility.instance.budgetForCurUsr.forEach {
                    add(it.name)
                }
            }

            mSPBudget.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, lsBudgetName)
                    .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            mETAmount.addTextChangedListener(MoneyTextWatcher(mETAmount))
            View.OnTouchListener { v, event -> onTouchChildView(v, event) }.let1 {
                mETInfo.setOnTouchListener(it)
                mETDate.setOnTouchListener(it)
                mTVNote.setOnTouchListener(it)
            }

            mIBAddImage.setOnClickListener { _ ->
                mIAddPicPath = object : IAddPicPath {
                    override fun addPicPath(path: String) {
                        if (!mLSImagePath.contains(path)) {
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
        mTVNote.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        mOldPayNote?.let1 {
            it.budget?.let1 {
                val cc = mSPBudget.adapter.count
                for (i in 0 until cc) {
                    if (it.name == (mSPBudget.adapter.getItem(i) as String)) {
                        mSPBudget.setSelection(i)
                        break
                    }
                }
            }

            mETDate.setText(paraDate ?: it.ts.toDayHourMinuteStr())
            mETInfo.setText(it.info)

            it.note.let1 {
                mTVNote.text = if (it.isNullOrEmpty()) mSZDefNote else it
            }

            mETAmount.setText(it.amount.toMoneyStr())
            if (it.images.isNotEmpty()) {
                mLSImagePath.clear()
                mLSImagePath.addAll(it.images.filter { it.status == NoteImageItem.STATUS_USE }
                        .map { it.imagePath })
                reloadPics()
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
                                    mETInfo.setText((dialog as DlgSelectRecordType).curType)
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
                        val cd = mETDate.text.toString().let {
                            if (it.isEmpty())
                                Calendar.getInstance()
                            else
                                CalendarUtility.YearMonthDayHourMinute.parse(it)
                        }
                        PgUtil.pickDateDlg(context!!, cd) {
                            mETDate.setText(it.toTimestamp().toDayHourMinuteStr())
                            mETDate.requestFocus()
                        }
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

    override fun onAccept(): Boolean {
        return checkResult() && fillResult()
    }


    private fun reloadPics() {
        ArrayList<Map<String, String>>().apply {
            addAll(mLSImagePath.map { HashMap<String, String>().apply { put(PicLVAdapter.PIC_PATH, it) } })
        }.let1 {
            mLVImage.adapter = PicLVAdapter(context!!, it, true)
        }
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
            val bRet = bCreate.doJudge(
                    { 1 == PayIncomeDBUtility.instance.addPayNotes(listOf(it)) },
                    { PayIncomeDBUtility.instance.payDBUtility.modifyData(it) }
            )

            if (!bRet) {
                DlgAlert.showAlert(context!!, R.string.dlg_warn,
                        bCreate.doJudge(R.string.dlg_create_data_failure, R.string.dlg_modify_data_failure))
            }

            return bRet
        }

        return false
    }
}
