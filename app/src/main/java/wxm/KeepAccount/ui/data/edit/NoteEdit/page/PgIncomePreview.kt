package wxm.KeepAccount.ui.data.edit.NoteEdit.page

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import kotterknife.bindView

import wxm.KeepAccount.R
import wxm.KeepAccount.item.IncomeNoteItem
import wxm.KeepAccount.ui.data.edit.base.IPreview
import wxm.KeepAccount.ui.preview.ACImagePreview
import wxm.KeepAccount.utility.let1
import wxm.KeepAccount.utility.setImagePath
import wxm.KeepAccount.utility.toDayStr
import wxm.KeepAccount.utility.toHourMinuteStr
import wxm.androidutil.time.getDayInWeekString
import wxm.androidutil.time.toCalendar
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.util.doJudge
import java.io.File

/**
 * preview fragment for income
 * Created by WangXM on 2016/10/29.
 */
class PgIncomePreview : FrgSupportBaseAdv(), IPreview {
    private val mTVAmount: TextView by bindView(R.id.tv_amount)
    private val mTVInfo: TextView by bindView(R.id.tv_info)
    private val mTVNote: TextView by bindView(R.id.tv_note)
    private val mTVDate: TextView by bindView(R.id.tv_date)
    private val mTVDayInWeek: TextView by bindView(R.id.tv_day_in_week)
    private val mTVTime: TextView by bindView(R.id.tv_time)
    private val mRLImage: RelativeLayout by bindView(R.id.rl_image)
    private val mIVImage: ImageView by bindView(R.id.iv_image)

    private var mINData: IncomeNoteItem? = null

    override fun getLayoutID(): Int = R.layout.pg_preview_income

    override fun setPreviewData(data: Any) {
        mINData = data as IncomeNoteItem
    }

    override fun initUI(savedInstanceState: Bundle?) {
        if (null == savedInstanceState) {
            if(null != mINData) {
                val data = mINData!!

                mTVAmount.text = data.valToStr
                mTVInfo.text = data.info
                mTVNote.text = data.note
                mTVDate.text = data.ts.toDayStr()
                mTVTime.text = data.ts.toHourMinuteStr()
                mTVDayInWeek.text = data.ts.toCalendar().getDayInWeekString()

                data.images.isEmpty().doJudge(
                        { mRLImage.visibility = View.GONE },
                        {
                            mRLImage.visibility = View.VISIBLE

                            val fp = data.images[0]
                            mIVImage.setImagePath(fp)
                            mIVImage.setOnClickListener({ _ ->
                                Intent(activity!!, ACImagePreview::class.java).let1 {
                                    it.putExtra(ACImagePreview.IMAGE_FILE_PATH, fp)
                                    activity!!.startActivity(it)
                                }
                            })
                        })
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
