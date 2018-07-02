package wxm.KeepAccount.define

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import wxm.KeepAccount.R
import wxm.androidutil.app.AppBase
import wxm.androidutil.improve.forObj

/**
 * Action in welcome page
 * Created by WangXM on 2018/2/19.
 */
enum class EAction(val actName: String, bm_id: Int) {
    CALENDAR_VIEW("数据日历", R.drawable.ic_calendar_view),
    LOOK_DATA("查看数据", R.drawable.ic_act_look_data),
    LOOK_BUDGET("查看预算", R.drawable.ic_wallet),
    ADD_BUDGET("添加预算", R.drawable.ic_act_add_budget),
    ADD_DATA("添加记录", R.drawable.ic_bt_add),
    ADD_DEBT("添加借贷", R.drawable.ic_borrow_lend),
    SYNC_SMS("同步数据", R.drawable.ic_sms),
    LOGOUT("退出登录", R.drawable.ic_bt_logout);

    /**
     * get icon
     * @return      icon
     */
    val icon: Bitmap = BitmapFactory.decodeResource(AppBase.getResources(), bm_id)

    companion object {
        /**
         * get action name
         * @param name  for icon
         * @return      icon
         */
        fun getIcon(name: String): Bitmap? {
            return EAction.values().find { it.actName == name }.forObj(
                    {it.icon},
                    {null}
            )
        }

        fun getEAction(act: String): EAction? {
            return EAction.values().find { it.actName == act }
        }
    }
}
