package wxm.KeepAccount.ui.data.edit.NoteEdit.page

import android.os.Bundle
import android.widget.TextView
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.item.BudgetItem
import wxm.KeepAccount.ui.data.edit.base.IPreview
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import java.util.*

/**
 * preview fragment for budget
 * Created by WangXM on 2016/10/29.
 */
class PgBudgetPreview : FrgSupportBaseAdv(), IPreview {
    private val mTVAllAmount: TextView by bindView(R.id.tv_all_amount)
    private val mTVLeaveAmount: TextView by bindView(R.id.tv_leave_amount)
    private val mTVName: TextView by bindView(R.id.tv_name)
    private val mTVNote: TextView by bindView(R.id.tv_note)

    private var mBIData: BudgetItem? = null

    override fun setPreviewData(data: Any) {
        mBIData = data as BudgetItem
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun getLayoutID(): Int {
        return R.layout.vw_budget_preview
    }

    override fun initUI(savedInstanceState: Bundle?) {
        if (null != mBIData) {
            val data = mBIData!!

            mTVName.text = data.name
            mTVNote.text = data.note
            mTVAllAmount.text = String.format(Locale.CHINA, "%.02f", data.amount)

            val ra = String.format(Locale.CHINA, "%.02f", data.remainderAmount)
            mTVLeaveAmount.text = ra
            if (ra.startsWith("-")) {
                mTVLeaveAmount.setTextColor(AppBase.getColor(R.color.darkred))
            }
        } else {
            mTVName.text = ""
            mTVNote.text = ""
            mTVAllAmount.text = ""
            mTVLeaveAmount.text = ""
        }
    }
}
