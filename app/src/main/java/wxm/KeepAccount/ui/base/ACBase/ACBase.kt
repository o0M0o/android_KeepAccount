package wxm.KeepAccount.ui.base.ACBase

import android.os.Bundle
import android.support.v7.widget.Toolbar
import wxm.KeepAccount.R
import wxm.androidutil.improve.let1
import wxm.androidutil.ui.activity.ACSwitcherActivity

/**
 * @author      WangXM
 * @version     create：2018/6/14
 */
abstract class ACBase<T>  : ACSwitcherActivity<T>(){
    override fun setupToolbar(tb: Toolbar?) {
        super.setupToolbar(tb)
        tb?.let1 {
            it.setNavigationIcon(R.drawable.ic_back_32_white)
            it.titleMarginStart = 0

            it.setTitleTextAppearance(this, R.style.AppTheme_ActionBar_Text)
            it.overflowIcon?.setTint(getColor(R.color.white))
        }
    }
}