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
import wxm.KeepAccount.improve.*
import wxm.KeepAccount.item.IncomeNoteItem
import wxm.KeepAccount.item.NoteImageItem
import wxm.KeepAccount.ui.base.ACBase.ACBase
import wxm.KeepAccount.ui.data.edit.NoteEdit.FrgNoteEdit
import wxm.KeepAccount.ui.data.edit.NoteEdit.page.base.PicLVAdapter
import wxm.KeepAccount.ui.data.edit.base.IPreview
import wxm.KeepAccount.ui.preview.ACImagePreview
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.let1
import java.util.HashMap

/**
 * preview fragment for income
 * Created by WangXM on 2016/10/29.
 */
class PgIncomePreview : FrgSupportBaseAdv(), IPreview {
    private val mTVAmount: TextView by bindView(R.id.tv_amount)
    private val mTVInfo: TextView by bindView(R.id.tv_info)
    private val mTVDate: TextView by bindView(R.id.tv_date)
    private val mTVDayInWeek: TextView by bindView(R.id.tv_day_in_week)
    private val mTVTime: TextView by bindView(R.id.tv_time)

    private val mRLImage: ConstraintLayout by bindView(R.id.rl_image)
    private val mLVImage: ListView by bindView(R.id.lv_pic)

    private val mRLNote: RelativeLayout by bindView(R.id.rl_note)
    private val mTVNote: TextView by bindView(R.id.tv_note)

    private var mINData: IncomeNoteItem? = null
    private var mUsrVisible = true

    override fun getLayoutID(): Int = R.layout.pg_income_preview
    override fun isUseEventBus(): Boolean = true

    override fun setPreviewData(data: Any) {
        mINData = data as IncomeNoteItem
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
        if(!mUsrVisible)
            return

        if(PicPath.PREVIEW_PIC == event.action) {
            Intent(activity!!, ACImagePreview::class.java).let1 {
                it.putExtra(ACImagePreview.IMAGE_FILE_PATH, event.picPath)
                activity!!.startActivity(it)
            }
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        if (null == savedInstanceState) {
            if(null != mINData) {
                val data = mINData!!

                mTVAmount.text = data.valToStr
                mTVInfo.text = data.info
                mTVDate.text = data.ts.toDayStr()
                mTVTime.text = data.ts.toHourMinuteStr()
                mTVDayInWeek.text = data.ts.toDayInWeekStr()

                if (data.note.isNullOrEmpty()) {
                    mRLNote.visibility = View.GONE
                } else {
                    mRLNote.visibility = View.VISIBLE
                    mTVNote.text = data.note
                }


                @Suppress("UNCHECKED_CAST")
                val pn = (data.tag == null).doJudge(
                        {
                            if (data.images.isEmpty()) ArrayList()
                            else ArrayList<String>().apply {
                                addAll(data.images.filter { it.status == NoteImageItem.STATUS_USE }.map { it.imagePath })
                            }
                        },
                        { data.tag as List<String> }
                )
                pn.isEmpty().doJudge(
                        { mRLImage.visibility = View.GONE },
                        {
                            mRLImage.visibility = View.VISIBLE

                            ArrayList<Map<String, String>>().apply {
                                addAll(pn.map { HashMap<String, String>().apply { put(PicLVAdapter.PIC_PATH, it) } })
                            }.let1 {
                                mLVImage.adapter = PicLVAdapter(context!!, it, false)
                            }
                        }
                )
            } else  {
                mTVAmount.text = ""
                mTVInfo.text = ""
                mTVNote.text = ""
                mTVDate.text = ""
                mTVTime.text = ""
                mTVDayInWeek.text = ""

                mRLImage.visibility = View.GONE
            }
        }
    }
}
