package wxm.KeepAccount.ui.welcome.base

import android.app.Activity
import android.content.Intent
import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.improve.let1

/**
 * @author      WangXM
 * @version     create：2018/5/28
 */
interface PageBase  {

    /**
     * if return true, means page can leave
     */
    fun leavePage(): Boolean

    fun doLogout(ac:Activity) {
        ac.let1 {
            it.setResult(GlobalDef.INTRET_USR_LOGOUT, Intent())
            it.finish()
        }
    }
}