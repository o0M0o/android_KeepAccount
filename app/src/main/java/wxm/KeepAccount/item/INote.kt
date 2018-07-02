package wxm.KeepAccount.item

import java.math.BigDecimal
import java.sql.Timestamp

/**
 * Created by WangXM on 2016/10/21.
 * interface for note
 */
interface INote {
    var id: Int

    var info: String

    var note: String?

    var amount: BigDecimal

    var ts: Timestamp

    var usr: UsrItem?

    var tag: Any?

    /**
     * get note type as string
     */
    fun noteType(): String

    companion object {
        const val FILELD_ID = "_id"
        const val FIELD_USR = "usr_id"

        const val FIELD_INFO = "info"
        const val FIELD_NOTE = "note"
        const val FIELD_AMOUNT = "val"
        const val FIELD_TS = "ts"
    }
}
