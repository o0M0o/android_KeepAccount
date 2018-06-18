package wxm.KeepAccount.improve

import wxm.androidutil.improve.doJudge
import java.math.BigDecimal
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/5/24
 */

/**
 * to money value string
 */
fun BigDecimal.toMoneyStr(): String   {
    return String.format(Locale.CHINA, "%.02f", this)
}

/**
 * to money value string with signal
 */
fun BigDecimal.toSignalMoneyStr(): String   {
    val isPositive = this.toFloat() > 0
    val value = isPositive.doJudge(this.toFloat(), this.toFloat() * -1)
    return String.format(Locale.CHINA,
            isPositive.doJudge("+ %.02f", "- %.02f"), value)
}
