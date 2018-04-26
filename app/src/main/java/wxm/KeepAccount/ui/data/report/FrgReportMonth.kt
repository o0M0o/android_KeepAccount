package wxm.KeepAccount.ui.data.report

import android.os.Bundle
import wxm.KeepAccount.R
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.FrgUtility.FrgSupportSwitcher

/**
 * data month report
 * Created by WangXM on 2017/2/15.
 */
class FrgReportMonth : FrgSupportSwitcher<FrgSupportBaseAdv>() {
    init {
        setupFrgID(R.layout.vw_report, R.id.fl_page_holder)
    }

    override fun setupFragment(bundle: Bundle?) {}
}
