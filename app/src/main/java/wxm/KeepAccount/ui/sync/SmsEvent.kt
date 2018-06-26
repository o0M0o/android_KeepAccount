package wxm.KeepAccount.ui.sync

/**
 * @author      WangXM
 * @version     create：2018/6/22
 */
data class SmsEvent(val smsId:Long, val eventType:Int)    {
    companion object {
        const val EVENT_DELETE = 1
        const val EVENT_TO_PAY = 2
        const val EVENT_TO_INCOME = 3
    }
}
