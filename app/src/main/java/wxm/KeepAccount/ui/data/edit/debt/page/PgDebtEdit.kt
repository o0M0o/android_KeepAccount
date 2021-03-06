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
import wxm.KeepAccount.event.PicPath
import wxm.KeepAccount.improve.notExist
import wxm.KeepAccount.improve.toDayHourMinuteStr
import wxm.KeepAccount.item.DebtNoteItem
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText
import wxm.KeepAccount.ui.base.TouchUI.TouchTextView
import wxm.KeepAccount.ui.base.buttonEx.ButtonEx
import wxm.KeepAccount.ui.base.page.MoneyTextWatcher
import wxm.KeepAccount.ui.base.page.IAddPicPath
import wxm.KeepAccount.ui.base.page.PicLVAdapter
import wxm.KeepAccount.ui.base.page.PgUtil
import wxm.KeepAccount.ui.data.edit.base.IEdit
import wxm.KeepAccount.ui.data.edit.debt.ACDebtActionCreate
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
import kotlin.collections.Map
import kotlin.collections.map

/**
 * edit pay
 * Created by WangXM on2016/9/28.
 */
class PgDebtEdit : FrgSupportBaseAdv(), IEdit {
    private val mETDate: TouchEditText by bindView(R.id.ar_et_date)
    private val mETAmount: TouchEditText by bindView(R.id.ar_et_amount)
    private val mTVNote: TouchTextView by bindView(R.id.tv_note)
    private val mLVImage: ListView by bindView(R.id.lv_pic)

    private val mSZDefNote: String = AppBase.getString(R.string.notice_input_note)
    private var mUsrVisible = true
    private var mIAddPicPath: IAddPicPath? = null
    private var mDebtData: DebtNoteItem? = null
    private var mLSImagePath = ArrayList<String>()

    override fun getLayoutID(): Int = R.layout.pg_debt_edit
    override fun isUseEventBus(): Boolean = true

    override fun setEditData(data: Any) {
        mDebtData = data as DebtNoteItem
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
            mDebtData?.let1 {
                mETAmount.text.toString().let1 { it1 ->
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

                it.tag = mLSImagePath
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            val vh = ViewHelper(view!!)

            mETAmount.addTextChangedListener(MoneyTextWatcher(mETAmount))
            View.OnTouchListener { v, event -> onTouchChildView(v, event) }.let1 {
                mETDate.setOnTouchListener(it)
                mTVNote.setOnTouchListener(it)
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

            vh.getChildView<ButtonEx>(R.id.ib_add_flow)!!.setOnClickListener {
                ACDebtActionCreate.start(activity!!, this)
            }
        }

        loadUI(bundle)
    }

    override fun loadUI(bundle: Bundle?) {
    }


    override fun onAccept(): Boolean {
        return checkResult().let {
            if(it)  {
                refillData()
            }

            it
        }
    }

    private fun onTouchChildView(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (v.id) {
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

    private fun reloadPics() {
        ArrayList<Map<String, String>>().apply {
            addAll(mLSImagePath.map { HashMap<String, String>().apply { put(PicLVAdapter.PIC_PATH, it) } })
        }.let1 {
            mLVImage.adapter = PicLVAdapter(context!!, it, true)
        }
    }

    /**
     * return true if data validity
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
