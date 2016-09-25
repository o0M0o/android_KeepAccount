package wxm.KeepAccount.Base.utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 配置管理类
 * Created by 123 on 2016/6/18.
 */
public class PreferencesUtil {
    private final static String PROPERTIES_NAME = "keepaccount_properties";
    private final static String SET_HOT_ACTION = "hot_action";

    /**
     * 加载首页上动作配置
     * @return   动作配置
     */
    public static List<String> loadHotAction()     {
        SharedPreferences param = ContextUtil.getInstance()
                                    .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE);

        String def = String.format(Locale.CHINA,
                        "%s:%s:%s:%s:%s",
                        ActionHelper.ACT_ADD_DATA, ActionHelper.ACT_LOOK_DATA,
                        ActionHelper.ACT_LOOK_BUDGET, ActionHelper.ACT_ADD_BUDGET,
                        ActionHelper.ACT_LOGOUT);

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
}
