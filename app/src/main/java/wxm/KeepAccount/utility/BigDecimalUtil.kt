package wxm.KeepAccount.utility

import java.math.BigDecimal
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/5/24
 */

fun BigDecimal.toShowString(): String   {
    return String.format(Locale.CHINA, ".02f", this)
}