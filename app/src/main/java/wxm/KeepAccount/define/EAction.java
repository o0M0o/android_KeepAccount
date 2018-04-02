package wxm.KeepAccount.define;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import wxm.KeepAccount.R;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * Action in welcome page
 * Created by WangXM on 2018/2/19.
 */
public enum EAction {
    CALENDAR_VIEW("数据日历", R.drawable.ic_calendar_view),
    LOOK_DATA("查看数据", R.drawable.ic_act_look_data),
    LOOK_BUDGET("查看预算", R.drawable.ic_wallet),
    ADD_BUDGET("添加预算", R.drawable.ic_act_add_budget),
    ADD_DATA("添加记录", R.drawable.ic_bt_add),
    LOGOUT("退出登录", R.drawable.ic_bt_logout);

    private String szName;
    private Bitmap bmIcon;

    EAction(String name, int bm_id)    {
        szName = name;
        bmIcon = BitmapFactory.decodeResource(
                    ContextUtil.getInstance().getResources(), bm_id);
    }

    /**
     * get action name
     * @return      name
     */
    public String getName()   {
        return szName;
    }

    /**
     * get icon
     * @return      icon
     */
    public Bitmap getIcon() {
        return bmIcon;
    }

    /**
     * get action name
     * @param name  for icon
     * @return      icon
     */
    public static Bitmap getIcon(String name)  {
        EAction ea = getEAction(name);
        return null == ea ? null : ea.getIcon();
    }

    /**
     * get EAction from name
     * @param name    name for action
     * @return      EAction or null
     */
    public static EAction getEAction(String name) {
        for(EAction et : EAction.values())    {
            if(et.szName.equals(name))
                return et;
        }

        return null;
    }
}
