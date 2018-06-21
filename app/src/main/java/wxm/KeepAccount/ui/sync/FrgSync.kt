package wxm.KeepAccount.ui.sync


import android.annotation.SuppressLint
import android.content.CursorLoader
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.ListView
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.improve.toDayHourMinuteStr
import wxm.androidutil.improve.let1
import wxm.androidutil.log.TagLog
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import java.sql.Timestamp
import java.util.regex.Pattern


/**
 * for login
 * Created by WangXM on 2016/11/29.
 */
class FrgSync : FrgSupportBaseAdv() {
    private val mLVSms: ListView by bindView(R.id.lv_sms)

    private val ptNum = Pattern.compile("(\\D*)[1-9](\\d+)(.*)")
    private val ptKeyWord = Pattern.compile("(.*)(交易|消费|存入|转出|汇款)(.*)")

    override fun getLayoutID(): Int = R.layout.frg_sync_sms
    // override fun isUseEventBus(): Boolean = true

    /**
     * for DB data change
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBEvent(event: SmsEvent) {
        TagLog.i("pass check : ${event.sms}")
    }
     */

    override fun initUI(bundle: Bundle?) {
        loadSMS()
    }

    /// PRIVATE BEGIN
    private fun loadSMS() {
        val smsInbox = Uri.parse("content://sms/inbox")
        val projection = arrayOf("_id", "address", "person", "body", "date", "type")

        val obj = @SuppressLint("StaticFieldLeak")
        object : CursorLoader(context, smsInbox, projection, null, null, null) {
            override fun deliverResult(cursor: Cursor?) {
                super.deliverResult(cursor)
                cursor?.let1 {
                    val ret = ArrayList<HashMap<String, String>>()
                    val idxAddress = it.getColumnIndex("address")
                    val idxBody = it.getColumnIndex("body")
                    val idxDate = it.getColumnIndex("date")

                    it.moveToFirst()
                    do {
                        val content = it.getString(idxBody)
                        if (checkSms(content)) {
                            TagLog.i("check pass : $content")
                            //EventBus.getDefault().post(SmsEvent(content, true))
                            ret.add(HashMap<String, String>()
                                    .apply {
                                        put(SmsAdapter.KEY_SENDER, it.getString(idxAddress))
                                        put(SmsAdapter.KEY_DATE,
                                                Timestamp(it.getLong(idxDate)).toDayHourMinuteStr())
                                        put(SmsAdapter.KEY_CONTENT, content)
                                    })
                        } else  {
                            TagLog.i("check not pass : $content")
                        }
                    } while (it.moveToNext())

                    mLVSms.adapter = SmsAdapter(context, ret)
                }
            }
        }
        obj.startLoading()
    }

    private fun checkSms(sms: String): Boolean {
        if(ptNum.matcher(sms).matches())    {
            return ptKeyWord.matcher(sms).matches()
        }

        return false
    }
    /// PRIVATE END
}
