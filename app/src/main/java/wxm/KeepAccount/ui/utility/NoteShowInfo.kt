package wxm.KeepAccount.ui.utility

import wxm.KeepAccount.improve.toMoneyStr
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

    var incomeAmount: BigDecimal = BigDecimal.ZERO

    val balance: BigDecimal
        get() = incomeAmount.subtract(payAmount)
}
