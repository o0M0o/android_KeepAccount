package wxm.KeepAccount.ui.dialog

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView

import java.util.Calendar
import java.util.Locale

import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.KeepAccount.R
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.util.doJudge

/**
 * select day range for report
 * Created by WangXM on 2017/2/15.
 */
class DlgSelectReportDays : DlgOKOrNOBase() {
    private val mSZInputDay: String = AppBase.getString(R.string.hint_input_day)

    private lateinit var mTVStartDay: TextView
    private lateinit var mTVEndDay: TextView

    /**
     * get start day
     * @return      start day
     */
    var startDay: String? = null
        private set
    /**
     * get end day
     * @return      end day
     */
    var endDay: String? = null
        private set

    override fun createDlgView(savedInstanceState: Bundle?): View {
        initDlgTitle(mSZInputDay, "接受", "放弃")
        return View.inflate(activity, R.layout.dlg_select_report_days, null)
    }

    override fun initDlgView(savedInstanceState: Bundle?) {
        mTVStartDay = findDlgChildView(R.id.tv_start_day)!!
        mTVEndDay = findDlgChildView(R.id.tv_end_day)!!

        endDay = null
        startDay = null

        mTVStartDay.setOnClickListener({ v -> tvClicks(v) })
        mTVEndDay.setOnClickListener({ v -> tvClicks(v) })

        // 默认是过去3个月的数据
        val cd = Calendar.getInstance()
        endDay = dayToSZ(cd.get(Calendar.YEAR),
                cd.get(Calendar.MONTH) + 1, cd.get(Calendar.DAY_OF_MONTH))
        mTVEndDay.text = endDay

        cd.add(Calendar.MONTH, -3)
        startDay = dayToSZ(cd.get(Calendar.YEAR),
                cd.get(Calendar.MONTH) + 1, cd.get(Calendar.DAY_OF_MONTH))
        mTVStartDay.text = startDay
    }

    /**
     * handler for click event
     * @param v     clicked view
     */
    private fun tvClicks(v: View) {
        when (v.id) {
            R.id.tv_start_day -> {
                val y = Integer.valueOf(startDay!!.substring(0, 4))
                val m = Integer.valueOf(startDay!!.substring(5, 7)) - 1
                val d = Integer.valueOf(startDay!!.substring(8, 10))

                DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            startDay = dayToSZ(year, month + 1, dayOfMonth)
                            mTVStartDay.text = startDay
                        },
                        y, m, d).show()

            }

            R.id.tv_end_day -> {
                val y = Integer.valueOf(endDay!!.substring(0, 4))
                val m = Integer.valueOf(endDay!!.substring(5, 7)) - 1
                val d = Integer.valueOf(endDay!!.substring(8, 10))

                DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            endDay = dayToSZ(year, month + 1, dayOfMonth)
                            mTVEndDay.text = endDay
                        },
                        y, m, d).show()
            }
        }
    }

    /**
     * check whether data is ok
     * @return      true if ok
     */
    override fun checkBeforeOK(): Boolean {
        if (null == startDay || null == endDay) {
            DlgAlert.showAlert(context!!, R.string.dlg_warn,
                    (null == startDay).doJudge("未选择开始日期!", "未选择结束日期"))
            return false
        }

        if (startDay!! >= endDay!!) {
            DlgAlert.showAlert(context!!, R.string.dlg_warn,
                    "开始日期($startDay)比结束日期($endDay)晚!")
            return false
        }

        return true
    }

    /**
     * parse day to string
     * @param year  年
     * @param month 月(1-12)
     * @param day   日(1-31)
     * @return 字符串
     */
    private fun dayToSZ(year: Int, month: Int, day: Int): String {
        return String.format(Locale.CHINA, "%d-%02d-%02d",
                year, month, day)
    }
}
