package wxm.KeepAccount.ui.utility

import java.math.BigDecimal
import java.util.Locale

/**
 * for show data
 * Created by WangXM on 2016/10/24.
 */
class NoteShowInfo {
    var payCount: Int = 0
    var incomeCount: Int = 0

    var payAmount: BigDecimal = BigDecimal.ZERO
        set(mPayAmount) {
            field = mPayAmount
            szPayAmount = String.format(Locale.CHINA, "%.02f", field)
        }
    var szPayAmount: String = "0"

    var incomeAmount: BigDecimal = BigDecimal.ZERO
        set(mIncomeAmount) {
            field = mIncomeAmount
            szIncomeAmount = String.format(Locale.CHINA, "%.02f", field)
        }
    var szIncomeAmount: String = "0"

    val balance: BigDecimal
        get() = incomeAmount.subtract(payAmount)

    init {
    }
}
