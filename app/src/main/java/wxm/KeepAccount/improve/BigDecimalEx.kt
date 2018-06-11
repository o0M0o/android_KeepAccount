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
fun BigDecimal.toMoneyString(): String   {
    return String.format(Locale.CHINA, "%.02f", this)
}

/**
 * to money value string with signal
 */
fun BigDecimal.toSignalMoneyString(): String   {
    return String.format(Locale.CHINA,
            (this.toFloat() > 0).doJudge("+ %.02f", "%.02f"), this)
}
