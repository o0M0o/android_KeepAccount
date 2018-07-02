package wxm.KeepAccount.ui.data.edit.debt.page

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.MotionEvent
import android.view.View
import android.widget.ListView
import com.theartofdev.edmodo.cropper.CropImage
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.event.PicPath
import wxm.KeepAccount.improve.notExist
import wxm.KeepAccount.improve.toDayHourMinuteStr
import wxm.KeepAccount.improve.toMoneyStr
import wxm.KeepAccount.item.DebtActionItem
import wxm.KeepAccount.item.NoteImageItem
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText
import wxm.KeepAccount.ui.base.TouchUI.TouchTextView
import wxm.KeepAccount.ui.base.buttonEx.ButtonEx
import wxm.KeepAccount.ui.base.page.IAddPicPath
import wxm.KeepAccount.ui.base.page.MoneyTextWatcher
import wxm.KeepAccount.ui.base.page.PicLVAdapter
import wxm.KeepAccount.ui.base.page.pgUtil
import wxm.KeepAccount.ui.data.edit.base.IEdit
import wxm.KeepAccount.ui.dialog.DlgLongTxt
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
import wxm.androidutil.ui.view.ViewHelper
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

/**
 * edit pay
 * Created by WangXM on2016/9/28.
 */
class PgDebtActionEdit : FrgSupportBaseAdv(), IEdit {
    private var mUsrVisible = true

    private val mTVNote: TouchTextView by bindView(R.id.tv_note)
    private val mETDate: TouchEditText by bindView(R.id.ar_et_date)
    private val mETAmount: TouchEditText by bindView(R.id.ar_et_amount)
    private val mLVImage: ListView by bindView(R.id.lv_pic)

    private val mSZDefNote: String = AppBase.getString(R.string.notice_input_note)
    private var mDebtActData: DebtActionItem? = null
    private var mLSImagePath = ArrayList<String>()
    private var mIAddPicPath: IAddPicPath? = null

    override fun getLayoutID(): Int = R.layout.pg_debt_act_edit
    override fun isUseEventBus(): Boolean = true

    override fun setEditData(data: Any) {
        mDebtActData = data as DebtActionItem
    }

    override fun getEditData(): Any? = mDebtActData

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
            }

            PicPath.REFRESH_PIC -> {
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
            mDebtActData?.let1 {
                mTVNote.text.toString().let1 { it1 ->
                    it.note = (mSZDefNote == it1).doJudge("", it1)
                }

                mETAmount.text.toString().let1 { it1 ->
                    it.amount = it1.isEmpty().doJudge(BigDecimal.ZERO, BigDecimal(it1))
                }

                (mETDate.text.toString() + ":00").let1 { it1 ->
                    it.ts = try {
                        ToolUtil.stringToTimestamp(it1)
                    } catch (ex: Exception) {
                        Timestamp(0)
                    }
                }
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            val vh = ViewHelper(view!!)

            mETAmount.addTextChangedListener(MoneyTextWatcher(mETAmount))
            View.OnTouchListener { v, event -> onTouchChildView(v, event) }.let1 {
                mTVNote.setOnTouchListener(it)
                mETDate.setOnTouchListener(it)
            }


            vh.getChildView<ButtonEx>(R.id.ib_add_pic)!!.setOnClickListener {
                mIAddPicPath = object : IAddPicPath {
                    override fun addPicPath(path: String) {
                        if (mLSImagePath.notExist { it == path }) {
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
        mDebtActData?.let1 {
            mETDate.setText(paraDate ?: it.ts.toDayHourMinuteStr())
            mTVNote.text = if (it.note.isEmpty()) mSZDefNote else it.note

            mETAmount.setText(it.amount.toMoneyStr())
            if (it.images.isNotEmpty()) {
                mLSImagePath.clear()
                mLSImagePath.addAll(it.images.filter { it.status == NoteImageItem.STATUS_USE }
                        .map { it.imagePath })
                reloadPics()
            }
        }
    }


    override fun onAccept(): Boolean {
        return checkResult().let {
            if (it) {
                refillData()
            }

            it
        }
    }

    private fun onTouchChildView(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (v.id) {
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

                    R.id.ar_et_date -> {
                        val cd = mETDate.text.toString().let {
                            if (it.isEmpty())
                                Calendar.getInstance()
                            else
                                CalendarUtility.YearMonthDayHourMinute.parse(it)
                        }
                        pgUtil.pickDateDlg(context!!, cd) {
                            mETDate.setText(it.toTimestamp().toDayHourMinuteStr())
                            mETDate.requestFocus()
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

        if (mETDate.text.isNullOrEmpty()) {
            mETDate.error = getString(R.string.error_field_required)
            return false
        }

        return true
    }
}
