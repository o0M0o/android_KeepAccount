package wxm.KeepAccount.ui.data.report

import android.os.Bundle

import wxm.KeepAccount.R
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.frg.FrgSupportSwitcher

/**
 * date year report
 * Created by WangXM on 2017/2/15.
 */
class FrgReportYear : FrgSupportSwitcher<FrgSupportBaseAdv>() {
    init {
        setupFrgID(R.layout.frg_report, R.id.fl_page_holder)
    }

    override fun setupFragment(bundle: Bundle?) {}
}
