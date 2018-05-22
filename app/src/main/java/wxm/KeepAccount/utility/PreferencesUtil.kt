package wxm.KeepAccount.utility

import android.content.Context
import wxm.KeepAccount.R
import wxm.KeepAccount.define.EAction
import wxm.androidutil.util.UiUtil
import java.util.*

/**
 * preference helper
 * Created by WangXM on 2016/6/18.
 */
object PreferencesUtil {
    const val SET_PAY_COLOR = "pay"
    const val SET_INCOME_COLOR = "income"
    const val SET_BUDGET_UESED_COLOR = "budget_used"
    const val SET_BUDGET_BALANCE_COLOR = "budget_balance"
    private const val PROPERTIES_NAME = "keepaccount_properties"
    private const val SET_HOT_ACTION = "hot_action"
    private const val SET_CHART_COLOR = "chart_color"

    /// BEGIN
    /**
     * load first page action setting
     * @return      action in first page
     */
    fun loadHotAction(): List<String> {
        val param = ContextUtil.self.getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE)
        return String.format(Locale.CHINA,
                "%s:%s:%s:%s:%s:%s",
                EAction.ADD_DATA.actName, EAction.LOOK_DATA.actName,
                EAction.CALENDAR_VIEW.actName, EAction.LOOK_BUDGET.actName,
                EAction.ADD_BUDGET.actName, EAction.LOGOUT.actName).let {
            param.getString(SET_HOT_ACTION, it)
        }.let {
            parsePreferences(it)
        }
    }


    /**
     * save fist page action
     * @param acts  action in fist page
     */
    fun saveHotAction(acts: List<String>) {
        ContextUtil.self.getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE)
                .edit().putString(SET_HOT_ACTION, parseToPreferences(acts)).apply()
    }
    /// END


    /// BEGIN
    /**
     * load chart color setting
     * @return  color setting
     */
    fun loadChartColor(): HashMap<String, Int> {
        val param = ContextUtil.self
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE)

        val ct = ContextUtil.self
        val sb = SET_PAY_COLOR + ":" + UiUtil.getColor(ct, R.color.sienna).toString() +
                " " + SET_INCOME_COLOR + ":" + UiUtil.getColor(ct, R.color.teal).toString() +
                " " + SET_BUDGET_UESED_COLOR + ":" + UiUtil.getColor(ct, R.color.sienna).toString() +
                " " + SET_BUDGET_BALANCE_COLOR + ":" + UiUtil.getColor(ct, R.color.teal).toString()

        val load = param.getString(SET_CHART_COLOR, sb)
        return parseChartColors(load!!)
    }


    /**
     * save chart color setting
     * @param ccs   color setting
     */
    fun saveChartColor(ccs: HashMap<String, Int>) {
        val param = ContextUtil.instance!!
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE)

        val pr = parseChartColorsToString(ccs)
        param.edit().putString(SET_CHART_COLOR, pr).apply()
    }
    /// END


    /// BEGIN PRIVATE

    /**
     * parse setting string
     * @param pr    setting string
     * @return      settings
     */
    private fun parsePreferences(pr: String): List<String> {
        val ret = ArrayList<String>()
        val pr_arr = pr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        Collections.addAll(ret, *pr_arr)

        return ret
    }

    /**
     * parse settings to string
     * @param acts      settings
     * @return          setting string
     */
    private fun parseToPreferences(acts: List<String>): String {
        var ff = true
        val sb = StringBuilder()
        for (i in acts) {
            if (!ff) {
                sb.append(":")
            } else {
                ff = false
            }

            sb.append(i)
        }

        return sb.toString()
    }

    /**
     * parse setting string for chart color
     * @param cc    setting string
     * @return      chart color setting
     */
    private fun parseChartColors(cc: String): HashMap<String, Int> {
        val ret = HashMap<String, Int>()
        val ccLns = cc.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in ccLns) {
            val iiLns = i.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            ret[iiLns[0]] = Integer.parseInt(iiLns[1])
        }

        return ret
    }


    /**
     * parse chart color to setting string
     * @param hmCC      chart color setting
     * @return          setting string
     */
    private fun parseChartColorsToString(hmCC: HashMap<String, Int>): String {
        val ret = StringBuilder()
        for (i in hmCC.keys) {
            val sb = StringBuilder()
            sb.append(i).append(":").append(hmCC[i].toString())

            if (ret.isEmpty())
                ret.append(sb)
            else
                ret.append(" ").append(sb)
        }

        return ret.toString()
    }
    /// END PRIVATE
}
