package wxm.KeepAccount.ui.data.show.note.page

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.improve.toDayStr
import wxm.KeepAccount.improve.toMoneyStr
import wxm.KeepAccount.improve.toYearMonthDayTag
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.ui.data.show.note.ACNoteDetail
import wxm.KeepAccount.ui.data.show.note.base.ValueShow
import wxm.KeepAccount.ui.utility.AdapterNoteRangeDetail
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.ui.utility.NoteShowInfo
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


/**
 * for daily detail info
 * Created by WangXM on 2017/01/20.
 */
class FrgNoteDetail : FrgSupportBaseAdv() {
    // for date & type
    private val mTVStartDay: TextView by bindView(R.id.tv_start_day)
    private val mTVEndDay: TextView by bindView(R.id.tv_end_day)
    private val mTVType: TextView by bindView(R.id.tv_type)

    // for note
    private val mLVBody: ListView by bindView(R.id.lv_note)

    // for stat
    private val mVSDataUI: ValueShow by bindView(R.id.vs_daily_info)

    // for data
    private var mShowPay: Boolean = true
    private var mShowIncome: Boolean = true
    private lateinit var mDayStart: Timestamp
    private lateinit var mDayEnd: Timestamp
    private var mLSDayContents: ArrayList<INote> = ArrayList()

    override fun getLayoutID(): Int = R.layout.pg_note_detail
    override fun isUseEventBus(): Boolean = true

    /**
     * handler for DB data change
     * @param event     for event
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBDataChangeEvent(event: DBDataChangeEvent) {
        initUI(null)
    }


    override fun initUI(bundle: Bundle?) {
        mDayStart = arguments!!.getSerializable(ACNoteDetail.KEY_START_DAY)!! as Timestamp
        mDayEnd = arguments!!.getSerializable(ACNoteDetail.KEY_END_DAY)!! as Timestamp

        mShowPay = arguments!!.getBoolean(GlobalDef.STR_RECORD_PAY)
        mShowIncome = arguments!!.getBoolean(GlobalDef.STR_RECORD_INCOME)
        if(mShowIncome && mShowPay) {
            mTVType.text = String.format(Locale.CHINA, "%s/%s",
                    getString(R.string.cn_income), getString(R.string.cn_pay))
        } else  {
            if(mShowPay)    {
                mTVType.text = getString(R.string.cn_pay)
            } else  {
                mTVType.text = getString(R.string.cn_income)
            }
        }

        mLSDayContents.clear()
        NoteDataHelper.getNotesBetweenDays(mDayStart.toYearMonthDayTag(), mDayEnd.toYearMonthDayTag())
                .values.forEach {
                    it.filter { (it.noteType() == GlobalDef.STR_RECORD_PAY && mShowPay)
                    || (it.noteType() == GlobalDef.STR_RECORD_INCOME && mShowIncome)}.forEach {
                        mLSDayContents.add(it)
                    }
                }

        loadUI(bundle)
    }

    override fun loadUI(bundle: Bundle?) {
        mTVStartDay.text = mDayStart.toDayStr()
        mTVEndDay.text = mDayEnd.toDayStr()
        loadDayInfo()
        loadDayNotes()
    }

    /// PRIVATE BEGIN

    /**
     * load day info
     */
    private fun loadDayInfo() {
        val ni = NoteShowInfo().apply {
            mLSDayContents.forEach {
                if (GlobalDef.STR_RECORD_PAY == it.noteType()) {
                    payCount += 1
                    payAmount += it.amount
                } else {
                    incomeCount += 1
                    incomeAmount += it.amount
                }
            }
        }

        HashMap<String, Any>().let {
            it[ValueShow.ATTR_PAY_COUNT] = ni.payCount.toString()
            it[ValueShow.ATTR_PAY_AMOUNT] = ni.payAmount.toMoneyStr()
            it[ValueShow.ATTR_INCOME_COUNT] = ni.incomeCount.toString()
            it[ValueShow.ATTR_INCOME_AMOUNT] = ni.incomeAmount.toMoneyStr()

            mVSDataUI.adjustAttribute(it)
        }
    }

    /**
     * load day data
     */
    private fun loadDayNotes() {
        val para = LinkedList<HashMap<String, INote>>()
        mLSDayContents.let {
            it.sortedBy { it.ts }.forEach {
                para.add(HashMap<String, INote>().apply { put(AdapterNoteRangeDetail.K_NODE, it) })
            }
        }

        mLVBody.adapter = AdapterNoteRangeDetail(activity!!, para)
    }

    /// PRIVATE END
}
