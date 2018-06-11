package wxm.KeepAccount.ui.data.edit.NoteEdit.page

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import kotterknife.bindView

import wxm.KeepAccount.R
import wxm.KeepAccount.improve.*
import wxm.KeepAccount.item.IncomeNoteItem
import wxm.KeepAccount.ui.data.edit.base.IPreview
import wxm.KeepAccount.ui.preview.ACImagePreview
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.let1

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
                mTVDayInWeek.text = data.ts.toDayInWeekStr()

                val pn = (data.tag == null).doJudge(
                        {if(data.images.isEmpty()) ""
                        else data.images[0].imagePath},
                        {data.tag as String}
                )
                pn.isEmpty().doJudge(
                        { mRLImage.visibility = View.GONE },
                        {
                            mRLImage.visibility = View.VISIBLE

                            mIVImage.setImagePath(pn)
                            mIVImage.setOnClickListener({ _ ->
                                Intent(activity!!, ACImagePreview::class.java).let1 {
                                    it.putExtra(ACImagePreview.IMAGE_FILE_PATH, pn)
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
