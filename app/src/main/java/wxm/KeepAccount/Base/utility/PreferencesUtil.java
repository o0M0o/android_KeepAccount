package wxm.KeepAccount.Base.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import wxm.KeepAccount.R;

/**
 * 配置管理类
 * Created by 123 on 2016/6/18.
 */
public class PreferencesUtil {
    private final static String PROPERTIES_NAME = "keepaccount_properties";
    private final static String SET_HOT_ACTION  = "hot_action";
    private final static String SET_CHART_COLOR = "chart_color";

    public final static String SET_PAY_COLOR    = "pay";
    public final static String SET_INCOME_COLOR = "income";
    public final static String SET_BUDGET_UESED_COLOR       = "budget_used";
    public final static String SET_BUDGET_BALANCE_COLOR     = "budget_balance";

    /// BEGIN
    /**
     * 加载首页上动作配置
     * @return   动作配置
     */
    public static List<String> loadHotAction()     {
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
     * 保存首页上动作
     * @param acts 动作
     */
    public static void saveHotAction(List<String> acts)     {
        SharedPreferences param = ContextUtil.getInstance()
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE);

        String pr = parseToPreferences(acts);
        param.edit().putString(SET_HOT_ACTION, pr).apply();
    }
    /// END


    /// BEGIN
    /**
     * 加载chart颜色配置
     * @return   颜色配置
     */
    public static HashMap<String, Integer> loadChartColor()     {
        SharedPreferences param = ContextUtil.getInstance()
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE);

        Resources res = ContextUtil.getInstance().getResources();
        String sb = SET_PAY_COLOR + ":" + String.valueOf(res.getColor(R.color.sienna)) +
                " " + SET_INCOME_COLOR + ":" + String.valueOf(res.getColor(R.color.teal)) +
                " " + SET_BUDGET_UESED_COLOR + ":" + String.valueOf(res.getColor(R.color.sienna)) +
                " " + SET_BUDGET_BALANCE_COLOR + ":" + String.valueOf(res.getColor(R.color.teal));

        String load = param.getString(SET_CHART_COLOR, sb);
        return parseChartColors(load);
    }


    /**
     * 保存chart颜色配置
     * @param ccs  配色配置
     */
    public static void saveChartColor(HashMap<String, Integer> ccs)     {
        SharedPreferences param = ContextUtil.getInstance()
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE);

        String pr = parseChartColorsToString(ccs);
        param.edit().putString(SET_CHART_COLOR, pr).apply();
    }
    /// END


    /// BEGIN PRIVATE
    /**
     * 把配置字符串解析成结果
     * @param pr        配置字符串
     * @return          结果
     */
    private static List<String> parsePreferences(String pr)  {
        ArrayList<String> ret = new ArrayList<>();
        String[] pr_arr = pr.split(":");
        Collections.addAll(ret, pr_arr);

        return ret;
    }

    /**
     * 把动作解析成配置字符串
     * @param acts      待解析动作
     * @return          配置字符串
     */
    private static String parseToPreferences(List<String> acts)  {
        boolean ff = true;
        StringBuilder sb = new StringBuilder();
        for(String i : acts)    {
            if(!ff) {
                sb.append(":");
            }   else    {
                ff = false;
            }

            sb.append(i);
        }

        return sb.toString();
    }

    /**
     * 从配置字符串解析配置
     * @param cc    配置字符串
     * @return   解析配置
     */
    private static HashMap<String, Integer> parseChartColors(String cc)  {
        HashMap<String, Integer> ret = new HashMap<>();
        String[] cclns = cc.split(" ");
        for(String i : cclns)   {
            String[] iilns = i.split(":");

            ret.put(iilns[0], Integer.parseInt(iilns[1]));
        }

        return ret;
    }


    private static String parseChartColorsToString(HashMap<String, Integer> hmcc)  {
        StringBuilder ret = new StringBuilder();
        for(String i : hmcc.keySet())   {
            StringBuilder sb = new StringBuilder();
            sb.append(i).append(":").append(hmcc.get(i).toString());

            if(0 == ret.length())
                ret.append(sb);
            else
                ret.append(" ").append(sb);
        }

        return ret.toString();
    }
    /// END PRIVATE
}
