package wxm.KeepAccount.improve

import android.support.annotation.IdRes
import android.support.annotation.StringRes
import wxm.androidutil.ui.view.ViewHelper

/**
 * @author      WangXM
 * @version     create：2018/6/2
 */

fun ViewHelper.setText(@IdRes vId: Int, @StringRes sId:Int) {
    setText(vId, getContext().getString(sId))
}

fun ViewHelper.setText(@IdRes vId: Int, @StringRes sId:Int, vararg args:Any) {
    setText(vId, getContext().getString(sId, args))
}