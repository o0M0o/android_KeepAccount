package wxm.KeepAccount.ui.data.show.note

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.sql.Timestamp
import java.text.ParseException
import java.util.ArrayList
import java.util.Calendar
import java.util.Collections
import java.util.HashMap
import java.util.LinkedList
import java.util.Locale

import butterknife.BindColor
import butterknife.BindView
import butterknife.OnClick
import wxm.androidutil.frgUtil.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.INote
import wxm.KeepAccount.ui.data.edit.NoteCreate.ACNoteCreate
import wxm.KeepAccount.ui.data.show.note.base.ValueShow
import wxm.KeepAccount.ui.utility.AdapterNoteDetail
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.ui.utility.NoteShowInfo
import wxm.KeepAccount.utility.ToolUtil
import wxm.uilib.IconButton.IconButton


/**
 * for daily detail info
 * Created by WangXM on 2017/01/20.
 */
class FrgDailyDetail : FrgSupportBaseAdv() {
    // 展示时间信息的UI
    @BindView(R.id.tv_day)
    internal var mTVMonthDay: TextView? = null

    @BindView(R.id.tv_year_month)
    internal var mTVYearMonth: TextView? = null

    @BindView(R.id.tv_day_in_week)
    internal var mTVDayInWeek: TextView? = null

    // 展示数据的UI
    @BindView(R.id.lv_note)
    internal var mLVBody: ListView? = null

    // 跳转日期的UI
    @BindView(R.id.rl_prv)
    internal var mRLPrv: RelativeLayout? = null

    @BindView(R.id.rl_next)
    internal var mRLNext: RelativeLayout? = null

    // 展示日统计数据的UI
    @BindView(R.id.vs_daily_info)
    internal var mVSDataUI: ValueShow? = null

    @BindView(R.id.login_progress)
    internal var mPBLoginProgress: ProgressBar? = null

    // create new data
    @BindView(R.id.ib_add)
    internal var mIBAdd: IconButton? = null

    // for color
    @BindColor(R.color.darkred)
    internal var mCLPay: Int = 0

    @BindColor(R.color.darkslategrey)
    internal var mCLIncome: Int = 0

    // for data
    private var mSZHotDay: String? = null
    private var mLSDayContents: List<INote>? = null

    override fun initUI(bundle: Bundle?) {
        if (UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            mSZHotDay = arguments.getString(ACDailyDetail.getK_HOTDAY())
        }

        if (!UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            mLSDayContents = NoteDataHelper.getNotesByDay(mSZHotDay!!)
        }

        loadUI(bundle)
    }

    override fun getLayoutID(): Int {
        return R.layout.vw_daily_detail
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    override fun loadUI(bundle: Bundle?) {
        if (UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            setVisibility(View.INVISIBLE)
            return
        }

        setVisibility(View.VISIBLE)
        loadDayHeader()
        loadDayInfo()
        loadDayNotes()
    }

    /**
     * handler for DB data change
     * @param event     for event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBDataChangeEvent(event: DBDataChangeEvent) {
        initUI(null)
    }


    /**
     * process day prior/next browse
     * @param view      for button
     */
    @OnClick(R.id.rl_prv, R.id.rl_next)
    fun dayButtonClick(view: View) {
        val org_day = mSZHotDay

        val vid = view.id
        when (vid) {
            R.id.rl_prv -> {
                val prv_day = NoteDataHelper.getPrvDay(mSZHotDay!!)
                if (!UtilFun.StringIsNullOrEmpty(prv_day)) {
                    mSZHotDay = prv_day

                    if (View.VISIBLE != mRLNext!!.visibility)
                        mRLNext!!.visibility = View.VISIBLE
                } else {
                    mRLPrv!!.visibility = View.GONE
                }
            }

            R.id.rl_next -> {
                val next_day = NoteDataHelper.getNextDay(mSZHotDay!!)
                if (!UtilFun.StringIsNullOrEmpty(next_day)) {
                    mSZHotDay = next_day

                    if (View.VISIBLE != mRLPrv!!.visibility)
                        mRLPrv!!.visibility = View.VISIBLE
                } else {
                    mRLNext!!.visibility = View.GONE
                }
            }
        }

        if (!UtilFun.StringIsNullOrEmpty(mSZHotDay) && org_day != mSZHotDay) {
            mLSDayContents = NoteDataHelper.getNotesByDay(mSZHotDay!!)
            loadUI(null)
        }
    }


    /**
     * click on action
     * @param view      for action
     */
    @OnClick(R.id.ib_add)
    fun dayActionClick(view: View) {
        val vid = view.id
        when (vid) {
        // 添加数据
            R.id.ib_add -> {
                val intent = Intent(activity, ACNoteCreate::class.java)
                val cal = Calendar.getInstance()
                cal.timeInMillis = System.currentTimeMillis()
                intent.putExtra(GlobalDef.STR_RECORD_DATE,
                        String.format(Locale.CHINA, "%s %02d:%02d", mSZHotDay, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)))

                startActivityForResult(intent, 1)
            }
        }
    }

    /// PRIVATE BEGIN

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources
                .getInteger(android.R.integer.config_shortAnimTime)

        mPBLoginProgress!!.visibility = if (show) View.VISIBLE else View.GONE
        mPBLoginProgress!!.animate().setDuration(shortAnimTime.toLong())
                .alpha((if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        mPBLoginProgress!!.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    /**
     * load day info header
     */
    private fun loadDayHeader() {
        val arr = mSZHotDay!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        mTVMonthDay!!.text = arr[2]
        mTVYearMonth!!.text = String.format(Locale.CHINA, "%s年%s月", arr[0], arr[1])

        try {
            val ts = ToolUtil.stringToTimestamp(mSZHotDay!!)
            mTVDayInWeek!!.text = ToolUtil.getDayInWeek(ts)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    /**
     * load day info
     */
    private fun loadDayInfo() {
        val ni = NoteDataHelper.getInfoByDay(mSZHotDay!!)

        val p_count: String
        val i_count: String
        val p_amount: String
        val i_amount: String
        if (null != ni) {
            p_count = ni.payCount.toString()
            i_count = ni.incomeCount.toString()
            p_amount = ni.szPayAmount
            i_amount = ni.szIncomeAmount
        } else {
            p_count = "0"
            i_count = "0"
            p_amount = "0.00"
            i_amount = "0.00"
        }

        val hm = HashMap<String, Any>()
        hm[ValueShow.ATTR_PAY_COUNT] = p_count
        hm[ValueShow.ATTR_PAY_AMOUNT] = p_amount
        hm[ValueShow.ATTR_INCOME_COUNT] = i_count
        hm[ValueShow.ATTR_INCOME_AMOUNT] = i_amount
        mVSDataUI!!.adjustAttribute(hm)
    }

    /**
     * load day data
     */
    private fun loadDayNotes() {
        val c_para = LinkedList<HashMap<String, INote>>()
        if (!UtilFun.ListIsNullOrEmpty(mLSDayContents)) {
            Collections.sort(mLSDayContents!!) { t1, t2 -> t1.ts.compareTo(t2.ts) }
            for (ci in mLSDayContents!!) {
                val hm = HashMap<String, INote>()
                hm[AdapterNoteDetail.K_NODE] = ci

                c_para.add(hm)
            }
        }

        val ap = AdapterNoteDetail(activity, c_para)
        mLVBody!!.adapter = ap
        ap.notifyDataSetChanged()
    }

    /**
     * set UI visibility
     * @param vis       visibility param
     */
    private fun setVisibility(vis: Int) {
        mTVMonthDay!!.visibility = vis
        mTVYearMonth!!.visibility = vis
        mLVBody!!.visibility = vis
    }
    /// PRIVATE END
}
