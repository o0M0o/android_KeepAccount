package wxm.KeepAccount.ui.dialog

import android.app.DatePickerDialog
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import kotterknife.bindView

import java.util.Calendar
import java.util.Locale

import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil

/**
 * select day range for report
 * Created by WangXM on 2017/2/15.
 */
class DlgSelectReportDays : DlgOKOrNOBase() {
    private val mSZInputDay: String = ContextUtil.getString(R.string.hint_input_day)

    private val mTVStartDay: TextView by bindView(R.id.tv_start_day)
    private val mTVEndDay: TextView by bindView(R.id.tv_end_day)

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

    override fun InitDlgView(): View {
        endDay = null
        startDay = null

        InitDlgTitle("选择起始日期", "接受", "放弃")
        val vw = View.inflate(activity, R.layout.dlg_select_report_days, null)

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

        return vw
    }

    /**
     * handler for click event
     * @param v     clicked view
     */
    private fun tvClicks(v: View) {
        var dialog: DatePickerDialog? = null
        when (v.id) {
            R.id.tv_start_day -> {
                val y = Integer.valueOf(startDay!!.substring(0, 4))
                val m = Integer.valueOf(startDay!!.substring(5, 7)) - 1
                val d = Integer.valueOf(startDay!!.substring(8, 10))

                dialog = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            startDay = dayToSZ(year, month + 1, dayOfMonth)
                            mTVStartDay!!.text = startDay
                        },
                        y, m, d)

            }

            R.id.tv_end_day -> {
                val y = Integer.valueOf(endDay!!.substring(0, 4))
                val m = Integer.valueOf(endDay!!.substring(5, 7)) - 1
                val d = Integer.valueOf(endDay!!.substring(8, 10))

                dialog = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            endDay = dayToSZ(year, month + 1, dayOfMonth)
                            mTVEndDay!!.text = endDay
                        },
                        y, m, d)
            }
        }

        if (null != dialog)
            dialog.show()
    }

    /**
     * check whether data is ok
     * @return      true if ok
     */
    override fun checkBeforeOK(): Boolean {
        val sz_alert = "警告"
        val sz_sure = "确定"
        if (null == startDay || null == endDay) {
            val msg = if (null == startDay) "未选择开始日期!" else "未选择结束日期"

            AlertDialog.Builder(context)
                    .setTitle(sz_alert)
                    .setMessage(msg)
                    .setPositiveButton(sz_sure, null)
                    .show()
            return false
        }

        if (0 <= startDay!!.compareTo(endDay!!)) {
            val msg = String.format(Locale.CHINA,
                    "开始日期(%s)比结束日期(%s)晚!", startDay, endDay)

            AlertDialog.Builder(context)
                    .setTitle(sz_alert)
                    .setMessage(msg)
                    .setPositiveButton(sz_sure, null)
                    .show()
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
