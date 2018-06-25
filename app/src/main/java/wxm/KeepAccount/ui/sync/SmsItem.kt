package wxm.KeepAccount.ui.sync

import java.sql.Timestamp
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/6/22
 */
data class SmsItem(val id:Long, val date: Timestamp, val address:String, val body:String, val type:String)  {
    val status:Int = NOT_CHECK

    companion object {
        const val CHECKED = 1
        const val NOT_CHECK = 1
    }
}
