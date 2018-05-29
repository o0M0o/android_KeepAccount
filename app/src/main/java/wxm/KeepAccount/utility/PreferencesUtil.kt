package wxm.KeepAccount.utility

import android.content.Context
import wxm.KeepAccount.R
import wxm.KeepAccount.define.EAction
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
        val param = AppUtil.self.getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE)
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
        AppUtil.self.getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE)
                .edit().putString(SET_HOT_ACTION, parseToPreferences(acts)).apply()
    }
    /// END


    /// BEGIN
    /**
     * load chart color setting
     * @return  color setting
     */
    fun loadChartColor(): HashMap<String, Int> {
        return AppUtil.self.let {
            "$SET_PAY_COLOR:${it.getColor(R.color.sienna)}" +
                    " $SET_INCOME_COLOR:${it.getColor(R.color.teal)}" +
                    " $SET_BUDGET_UESED_COLOR:${it.getColor(R.color.sienna)}" +
                    " $SET_BUDGET_BALANCE_COLOR:${it.getColor(R.color.teal)}"
        }.let {
            AppUtil.self
                    .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE)
                    .getString(SET_CHART_COLOR, it)!!
        }.let {
            parseChartColors(it)
        }
    }


    /**
     * save chart color setting
     * @param ccs   color setting
     */
    fun saveChartColor(ccs: HashMap<String, Int>) {
        AppUtil.self.getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE).apply {
            edit().putString(SET_CHART_COLOR, parseChartColorsToString(ccs)).apply()
        }
    }
    /// END


    /// BEGIN PRIVATE
    /**
     * use [delimiter] split lns to arry
     */
    private fun splitString(lns: String, delimiter: String): Array<String> {
        return lns.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     * parse setting string
     * @param pr    setting string
     * @return      settings
     */
    private fun parsePreferences(pr: String): List<String> {
        return ArrayList<String>().apply {
            Collections.addAll(this, *splitString(pr, ":"))
        }
    }

    /**
     * parse settings to string
     * @param acts      settings
     * @return          setting string
     */
    private fun parseToPreferences(acts: List<String>): String {
        var ff = true
        val sb = StringBuilder()
        acts.forEach {
            if (!ff) {
                sb.append(":")
            } else {
                ff = false
            }

            sb.append(it)
        }

        return sb.toString()
    }

    /**
     * parse setting string for chart color
     * @param cc    setting string
     * @return      chart color setting
     */
    private fun parseChartColors(cc: String): HashMap<String, Int> {
        return HashMap<String, Int>().apply {
            splitString(cc, " ").forEach {
                splitString(it, ":").let {
                    put(it[0], Integer.parseInt(it[1]))
                }
            }
        }
    }

    /**
     * parse chart color to setting string
     * @param hmCC      chart color setting
     * @return          setting string
     */
    private fun parseChartColorsToString(hmCC: HashMap<String, Int>): String {
        val ret = StringBuilder()
        hmCC.forEach { t, u ->
            StringBuilder().apply {
                append(t).append(":").append(u.toString())
            }.let {
                if (ret.isEmpty())
                    ret.append(it)
                else
                    ret.append(" ").append(it)

                Unit
            }
        }

        return ret.toString()
    }
    /// END PRIVATE
}
