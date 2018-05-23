package wxm.KeepAccount.ui.data.show.note.ShowData

import android.os.Bundle

import wxm.KeepAccount.R
import wxm.KeepAccount.ui.data.show.note.HelloChart.BudgetChart
import wxm.KeepAccount.ui.data.show.note.ListView.LVBudget
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.frg.FrgSupportSwitcher

/**
 * for budget
 * Created by WangXM on 2016/10/5.
 */

class TFShowBudget : FrgSupportSwitcher<FrgSupportBaseAdv>() {
    init {
        setupFrgID(R.layout.tf_show_base, R.id.fl_holder)
    }

    override fun setupFragment(bundle: Bundle?) {
        addChildFrg(LVBudget())
        addChildFrg(BudgetChart())
    }
}
