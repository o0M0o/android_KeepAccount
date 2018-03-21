package wxm.KeepAccount.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import wxm.KeepAccount.R;
import wxm.androidutil.util.UiUtil;

/**
 * preference helper
 * Created by 123 on 2016/6/18.
 */
public class PreferencesUtil {
    public final static String SET_PAY_COLOR = "pay";
    public final static String SET_INCOME_COLOR = "income";
    public final static String SET_BUDGET_UESED_COLOR = "budget_used";
    public final static String SET_BUDGET_BALANCE_COLOR = "budget_balance";
    private final static String PROPERTIES_NAME = "keepaccount_properties";
    private final static String SET_HOT_ACTION = "hot_action";
    private final static String SET_CHART_COLOR = "chart_color";

    /// BEGIN

    /**
     * load first page action setting
     * @return      action in first page
     */
    public static List<String> loadHotAction() {
        SharedPreferences param = ContextUtil.getInstance()
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE);

        String def = String.format(Locale.CHINA,
                "%s:%s:%s:%s:%s:%s",
                ActionHelper.ACT_ADD_DATA, ActionHelper.ACT_LOOK_DATA,
                ActionHelper.ACT_CALENDAR_VIEW, ActionHelper.ACT_LOOK_BUDGET,
                ActionHelper.ACT_ADD_BUDGET, ActionHelper.ACT_LOGOUT);

        String load = param.getString(SET_HOT_ACTION, def);
        return parsePreferences(load);
    }


    /**
     * save fist page action
     * @param acts  action in fist page
     */
    public static void saveHotAction(List<String> acts) {
        SharedPreferences param = ContextUtil.getInstance()
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE);

        String pr = parseToPreferences(acts);
        param.edit().putString(SET_HOT_ACTION, pr).apply();
    }
    /// END


    /// BEGIN

    /**
     * load chart color setting
     * @return  color setting
     */
    public static HashMap<String, Integer> loadChartColor() {
        SharedPreferences param = ContextUtil.getInstance()
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE);

        Context ct = ContextUtil.getInstance();
        String sb = SET_PAY_COLOR + ":" + String.valueOf(UiUtil.getColor(ct, R.color.sienna)) +
                " " + SET_INCOME_COLOR + ":" + String.valueOf(UiUtil.getColor(ct, R.color.teal)) +
                " " + SET_BUDGET_UESED_COLOR + ":" + String.valueOf(UiUtil.getColor(ct, R.color.sienna)) +
                " " + SET_BUDGET_BALANCE_COLOR + ":" + String.valueOf(UiUtil.getColor(ct, R.color.teal));

        String load = param.getString(SET_CHART_COLOR, sb);
        return parseChartColors(load);
    }


    /**
     * save chart color setting
     * @param ccs   color setting
     */
    public static void saveChartColor(HashMap<String, Integer> ccs) {
        SharedPreferences param = ContextUtil.getInstance()
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE);

        String pr = parseChartColorsToString(ccs);
        param.edit().putString(SET_CHART_COLOR, pr).apply();
    }
    /// END


    /// BEGIN PRIVATE

    /**
     * parse setting string
     * @param pr    setting string
     * @return      settings
     */
    private static List<String> parsePreferences(String pr) {
        ArrayList<String> ret = new ArrayList<>();
        String[] pr_arr = pr.split(":");
        Collections.addAll(ret, pr_arr);

        return ret;
    }

    /**
     * parse settings to string
     * @param acts      settings
     * @return          setting string
     */
    private static String parseToPreferences(List<String> acts) {
        boolean ff = true;
        StringBuilder sb = new StringBuilder();
        for (String i : acts) {
            if (!ff) {
                sb.append(":");
            } else {
                ff = false;
            }

            sb.append(i);
        }

        return sb.toString();
    }

    /**
     * parse setting string for chart color
     * @param cc    setting string
     * @return      chart color setting
     */
    private static HashMap<String, Integer> parseChartColors(String cc) {
        HashMap<String, Integer> ret = new HashMap<>();
        String[] cclns = cc.split(" ");
        for (String i : cclns) {
            String[] iilns = i.split(":");

            ret.put(iilns[0], Integer.parseInt(iilns[1]));
        }

        return ret;
    }


    /**
     * parse chart color to setting string
     * @param hmcc      chart color setting
     * @return          setting string
     */
    private static String parseChartColorsToString(HashMap<String, Integer> hmcc) {
        StringBuilder ret = new StringBuilder();
        for (String i : hmcc.keySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(i).append(":").append(hmcc.get(i).toString());

            if (0 == ret.length())
                ret.append(sb);
            else
                ret.append(" ").append(sb);
        }

        return ret.toString();
    }
    /// END PRIVATE
}
