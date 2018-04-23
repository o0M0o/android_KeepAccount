package wxm.KeepAccount.define

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil

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
    LOGOUT("退出登录", R.drawable.ic_bt_logout);

    /**
     * get icon
     * @return      icon
     */
    val icon: Bitmap = BitmapFactory.decodeResource(
            ContextUtil.instance!!.resources, bm_id)

    companion object {
        /**
         * get action name
         * @param name  for icon
         * @return      icon
         */
        fun getIcon(name: String): Bitmap? {
            for(it in EAction.values()) {
                if(it.actName == name) {
                    return it.icon
                }
            }

            return null
        }

        fun getEAction(act: String): EAction? {
            for(it in EAction.values()) {
                if(it.actName == act) {
                    return it
                }
            }

            return null
        }
    }
}
