package wxm.KeepAccount.utility;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import wxm.KeepAccount.R;

/**
 * 动作辅助类
 * Created by 123 on 2016/9/21.
 */
public class ActionHelper {
    public final static String ACT_CALENDAR_VIEW    = "数据日历";
    public final static String ACT_LOOK_DATA        = "查看数据";
    public final static String ACT_LOOK_BUDGET      = "查看预算";
    public final static String ACT_ADD_BUDGET       = "添加预算";
    public final static String ACT_ADD_DATA         = "添加记录";
    public final static String ACT_LOGOUT           = "退出登录";
    public final static String ACT_LOOK_REMIND      = "查看提醒";
    public final static String ACT_ADD_REMIND       = "添加提醒";

    public final static String[] ACTION_NAMES   =   {
            ACT_LOOK_DATA
            ,ACT_LOOK_BUDGET
            ,ACT_ADD_BUDGET
            ,ACT_ADD_DATA
            ,ACT_CALENDAR_VIEW
            //,ACT_LOOK_REMIND
            //,ACT_ADD_REMIND
            ,ACT_LOGOUT
    };


    /**
     * 根据动作的名字得到其对应的bitmap
     * @param act_name  动作名
     * @return  对应的bitmap
     */
    public static Bitmap getBitMapFromName(String act_name)    {
        Resources res = ContextUtil.getInstance().getResources();
        Bitmap bm = null;
        switch (act_name)     {
            case ACT_CALENDAR_VIEW :
                bm = BitmapFactory.decodeResource(res, R.drawable.ic_calendar_view);
                break;

            case ACT_LOOK_DATA :
                bm = BitmapFactory.decodeResource(res, R.drawable.ic_act_look_data);
                break;

            case ACT_LOOK_BUDGET :
                bm = BitmapFactory.decodeResource(res, R.drawable.ic_wallet);
                break;

            case ACT_ADD_BUDGET :
                bm = BitmapFactory.decodeResource(res, R.drawable.ic_act_add_budget);
                break;

            case ACT_ADD_DATA:
                bm = BitmapFactory.decodeResource(res, R.drawable.ic_bt_add);
                break;

            case ACT_LOGOUT :
                bm = BitmapFactory.decodeResource(res, R.drawable.ic_bt_logout);
                break;

            case ACT_LOOK_REMIND :
                bm = BitmapFactory.decodeResource(res, R.drawable.ic_alarm);
                break;

            case ACT_ADD_REMIND :
                bm = BitmapFactory.decodeResource(res, R.drawable.ic_act_add_alarm);
                break;
        }

        return bm;
    }
}
