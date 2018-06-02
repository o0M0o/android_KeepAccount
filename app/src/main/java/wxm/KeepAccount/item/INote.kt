package wxm.KeepAccount.item

import wxm.KeepAccount.define.IncomeNoteItem
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.*
import wxm.androidutil.time.*

/**
 * Created by WangXM on 2016/10/21.
 * interface for note
 */
interface INote {
    val isPayNote: Boolean

    val isIncomeNote: Boolean

    var id: Int

    var info: String

    var note: String?

    var amount: BigDecimal

    val valToStr: String

    val tsToStr: String

    val tsYearTag:String
        get() {
            val cl = ts.toCalendar()
            return "${cl.getYear()}"
        }

    val tsYearMonthTag:String
        get() {
            val cl = ts.toCalendar()
            return String.format(Locale.CHINA, "${cl.getYear()}-%02d", cl.getMonth())
        }

    val tsYearMonthDayTag:String
        get() {
            val cl = ts.toCalendar()
            return String.format(Locale.CHINA, "${cl.getYear()}-%02d-%02d",
                    cl.getMonth(), cl.getDayInMonth())
        }

    var ts: Timestamp

    var usr: UsrItem?

    var budget: BudgetItem?

    fun toPayNote(): PayNoteItem?

    fun toIncomeNote(): IncomeNoteItem?
}