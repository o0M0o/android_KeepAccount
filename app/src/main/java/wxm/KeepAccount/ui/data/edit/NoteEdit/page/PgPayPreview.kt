package wxm.KeepAccount.ui.data.edit.NoteEdit.page

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.event.PicPath
import wxm.KeepAccount.improve.toDayInWeekStr
import wxm.KeepAccount.improve.toDayStr
import wxm.KeepAccount.improve.toHourMinuteStr
import wxm.KeepAccount.improve.toMoneyStr
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.base.page.PicLVAdapter
import wxm.KeepAccount.ui.data.edit.base.IPreview
import wxm.KeepAccount.ui.preview.ACImagePreview
import wxm.androidutil.improve.let1
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import java.util.HashMap
import kotlin.collections.ArrayList

/**
 * preview fragment for budget
 * Created by WangXM on 2016/10/29.
 */
class PgPayPreview : FrgSupportBaseAdv(), IPreview {
    private val mTVAmount: TextView by bindView(R.id.tv_amount)
    private val mTVInfo: TextView by bindView(R.id.tv_info)
    private val mTVNote: TextView by bindView(R.id.tv_note)
    private val mTVBudget: TextView by bindView(R.id.tv_budget_name)
    private val mTVDate: TextView by bindView(R.id.tv_date)
    private val mTVDayInWeek: TextView by bindView(R.id.tv_day_in_week)
    private val mTVTime: TextView by bindView(R.id.tv_time)

    private val mRLImage: ConstraintLayout by bindView(R.id.rl_image)
    private val mLVImage: ListView by bindView(R.id.lv_pic)

    private val mRLBudget: RelativeLayout by bindView(R.id.rl_budget)
    private val mRLNote: RelativeLayout by bindView(R.id.rl_note)

    private var mPayData: PayNoteItem? = null
    private var mUsrVisible = true

    override fun getLayoutID(): Int = R.layout.pg_pay_preview
    override fun isUseEventBus(): Boolean = true

    override fun setPreviewData(data: Any) {
        mPayData = data as PayNoteItem
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mUsrVisible = isVisibleToUser
    }

    /**
     * preview pic
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPreviewPicPath(event: PicPath) {
        if (!mUsrVisible)
            return

        if (PicPath.PREVIEW_PIC == event.action) {
            Intent(activity!!, ACImagePreview::class.java).let1 {
                it.putExtra(ACImagePreview.IMAGE_FILE_PATH, event.picPath)
                activity!!.startActivity(it)
            }
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        if (null == savedInstanceState) {
            if (null != mPayData) {
                val data = mPayData!!

                mTVAmount.text = data.amount.toMoneyStr()
                mTVInfo.text = data.info

                if (data.note.isNullOrEmpty()) {
                    mRLNote.visibility = View.GONE
                } else {
                    mRLNote.visibility = View.VISIBLE
                    mTVNote.text = data.note
                }

                (if (null == data.budget) "" else data.budget!!.name).let1 {
                    if (it.isEmpty()) {
                        mRLBudget.visibility = View.GONE
                    } else {
                        mRLBudget.visibility = View.VISIBLE
                        mTVBudget.text = it
                    }
                }

                mTVDate.text = data.ts.toDayStr()
                mTVTime.text = data.ts.toHourMinuteStr()
                mTVDayInWeek.text = data.ts.toDayInWeekStr()

                if (data.images.isEmpty()) {
                    mRLImage.visibility = View.GONE
                } else {
                    mRLImage.visibility = View.VISIBLE

                    ArrayList<Map<String, String>>().apply {
                        addAll(data.images.map {
                            HashMap<String, String>()
                                    .apply { put(PicLVAdapter.PIC_PATH, it.imagePath) }
                        })
                    }.let1 {
                        mLVImage.adapter = PicLVAdapter(context!!, it, false)
                    }
                }
            } else {
                mTVAmount.text = ""
                mTVInfo.text = ""
                mTVNote.text = ""
                mTVBudget.text = ""
                mTVDate.text = ""
                mTVDayInWeek.text = ""
                mTVTime.text = ""
                mRLImage.visibility = View.GONE
            }
        }
    }
}
