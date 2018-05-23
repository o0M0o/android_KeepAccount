package wxm.KeepAccount.ui.data.edit.NoteEdit.utility

import android.os.Bundle
import android.widget.TextView
import kotterknife.bindView

import wxm.KeepAccount.R
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.ui.data.edit.base.IPreview
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.ui.frg.FrgSupportBaseAdv

/**
 * preview fragment for income
 * Created by WangXM on 2016/10/29.
 */
class PageIncomePreview : FrgSupportBaseAdv(), IPreview {
    private val mTVAmount: TextView by bindView(R.id.tv_amount)
    private val mTVInfo: TextView by bindView(R.id.tv_info)
    private val mTVNote: TextView by bindView(R.id.tv_note)
    private val mTVDate: TextView by bindView(R.id.tv_date)
    private val mTVDayInWeek: TextView by bindView(R.id.tv_day_in_week)
    private val mTVTime: TextView by bindView(R.id.tv_time)

    private var mINData: IncomeNoteItem? = null


    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun getLayoutID(): Int {
        return R.layout.page_preview_income
    }

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
                mTVDate.text = ToolUtil.formatDateString(data.ts.toString().substring(0, 10))
                mTVTime.text = data.ts.toString().substring(11, 16)
                mTVDayInWeek.text = ToolUtil.getDayInWeek(data.ts)
            } else  {
                mTVAmount.text = ""
                mTVInfo.text = ""
                mTVNote.text = ""
                mTVDate.text = ""
                mTVTime.text = ""
                mTVDayInWeek.text = ""
            }
        }
    }
}
