package wxm.KeepAccount.ui.welcome.page.stat


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.welcome.base.PageBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv

import com.flyco.tablayout.SegmentTabLayout
import java.util.ArrayList

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class DayStat : FrgSupportBaseAdv() {
    override fun getLayoutID(): Int = R.layout.pg_stat_day
}
