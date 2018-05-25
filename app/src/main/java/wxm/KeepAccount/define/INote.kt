package wxm.KeepAccount.define

import java.math.BigDecimal
import java.sql.Timestamp

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

    val valToStr: String?

    val tsToStr: String?

    var ts: Timestamp

    var usr: UsrItem?

    var budget: BudgetItem?

    fun toPayNote(): PayNoteItem?

    fun toIncomeNote(): IncomeNoteItem?
}
