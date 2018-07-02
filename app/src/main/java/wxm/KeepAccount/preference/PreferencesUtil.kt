package wxm.KeepAccount.preference

import android.content.Context
import android.content.SharedPreferences
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.R
import wxm.KeepAccount.define.EAction
import wxm.KeepAccount.event.PreferenceChange
import wxm.KeepAccount.utility.AppUtil
import java.util.*
import kotlin.collections.HashMap

/**
 * preference helper
 * Created by WangXM on 2016/6/18.
 */
class PreferencesUtil : PreferenceBase() {
    override fun loadPreference(pn:String, pd:String):String {
        return getPreferences().getString(pn, pd)
    }

    override fun savePreference(pn:String, pv:String)    {
        getPreferences().edit().putString(pn, pv).apply()
    }


    /// BEGIN PRIVATE
    private fun getPreferences(): SharedPreferences =
            AppUtil.self.getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE)
    /// END PRIVATE

    companion object {
        private const val PROPERTIES_NAME = "keep_account_properties"
        private const val DIVIDER_CHAR = ":"
        private const val DIVIDER_CHAR_1 = " "

        const val SET_PAY_COLOR = "pay"
        const val SET_INCOME_COLOR = "income"
        const val SET_BUDGET_UESED_COLOR = "budget_used"
        const val SET_BUDGET_BALANCE_COLOR = "budget_balance"

        private const val SET_HOT_ACTION = "hot_action"
        private const val SET_CHART_COLOR = "chart_color"

        private val preferenceObj = PreferencesUtil()

        /// BEGIN
        /**
         * load first page action setting
         * @return      action in first page
         */
        fun loadHotAction(): Array<String> {
            return String.format(Locale.CHINA,
                    "%s$DIVIDER_CHAR%s$DIVIDER_CHAR%s" +
                            "$DIVIDER_CHAR%s$DIVIDER_CHAR%s$DIVIDER_CHAR%s" +
                            "$DIVIDER_CHAR%s$DIVIDER_CHAR%s",
                    EAction.ADD_DATA.actName, EAction.LOOK_DATA.actName, EAction.CALENDAR_VIEW.actName,
                    EAction.LOOK_BUDGET.actName, EAction.ADD_BUDGET.actName, EAction.SYNC_SMS.actName,
                    EAction.ADD_DEBT.actName, EAction.LOGOUT.actName).let {
                preferenceObj.loadPreference(SET_HOT_ACTION, it)
            }.let {
                preferenceObj.parsePreference(it, DIVIDER_CHAR)
            }
        }

        /**
         * save fist page action
         * @param acts  action in fist page
         */
        fun saveHotAction(acts: List<String>) {
            preferenceObj.savePreference(SET_HOT_ACTION,
                    preferenceObj.parseToPreferences(acts, DIVIDER_CHAR))
            EventBus.getDefault().post(PreferenceChange(SET_HOT_ACTION))
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
                preferenceObj.loadPreference(SET_CHART_COLOR, it)
            }.let {
                val ret = HashMap<String, Int>()
                preferenceObj.parsePreference(it, DIVIDER_CHAR_1, DIVIDER_CHAR)
                        .forEach {
                            ret[it.key] = Integer.parseInt(it.value)
                        }

                ret
            }
        }

        /**
         * save chart color setting
         * @param ccs   color setting
         */
        fun saveChartColor(ccs: HashMap<String, Int>) {
            preferenceObj.savePreference(SET_HOT_ACTION,
                    preferenceObj.parseToPreferences(ccs, DIVIDER_CHAR_1,  DIVIDER_CHAR))
            EventBus.getDefault().post(PreferenceChange(SET_CHART_COLOR))
        }
        /// END
    }
}
