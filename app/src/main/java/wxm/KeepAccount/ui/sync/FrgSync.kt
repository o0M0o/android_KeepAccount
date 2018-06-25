package wxm.KeepAccount.ui.sync


import android.annotation.SuppressLint
import android.content.CursorLoader
import android.database.Cursor
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.widget.ListView
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.androidutil.improve.let1
import wxm.androidutil.log.TagLog
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.uilib.IconButton.IconButton
import java.sql.Timestamp
import java.util.*
import java.util.regex.Pattern


/**
 * for login
 * Created by WangXM on 2016/11/29.
 */
class FrgSync : FrgSupportBaseAdv() {
    private val mLVSms: ListView by bindView(R.id.lv_sms)
    private val mIBReload: IconButton by bindView(R.id.ib_reload_sms)

    private val mPtNum = Pattern.compile("(\\D*)[1-9](\\d+)(.*)")
    private val mPtKeyWord = Pattern.compile("(.*)(交易|消费|存入|转出|汇款)(.*)")

    private val mLsSms = LinkedList<SmsItem>()

    override fun getLayoutID(): Int = R.layout.frg_sync_sms

    /**
    override fun isUseEventBus(): Boolean = true

     * for DB data change
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBEvent(event: SmsEvent) {
        TagLog.i("pass check : ${event.sms}")
    }
     */

    override fun initUI(bundle: Bundle?) {
        loadSMS()

        mIBReload.setOnClickListener{_ ->
            loadSMS()
        }
    }

    /// PRIVATE BEGIN
    private fun loadSMS() {
        mLsSms.clear()
        val smsInbox = Uri.parse("content://sms/inbox")
        val projection = arrayOf(K_ID, K_ADDRESS, K_PERSON, K_BODY, K_DATE, K_TYPE)

        val obj = @SuppressLint("StaticFieldLeak")
        object : CursorLoader(context, smsInbox, projection,
                null, null, "$K_ID DESC") {
            override fun deliverResult(cursor: Cursor?) {
                super.deliverResult(cursor)
                cursor?.let1 {
                    it.moveToFirst()
                    do {
                        val si = parseSms(it)
                        if (checkSms(si)) {
                            TagLog.i("check pass : ${si.body}")

                            mLsSms.add(si)
                        } else  {
                            TagLog.i("check not pass : ${si.body}")
                        }
                    } while (it.moveToNext())

                    loadLV()
                }
            }
        }
        obj.startLoading()
    }


    private fun loadLV()    {
        ArrayList<HashMap<String, SmsItem>>().apply {
            addAll(mLsSms.map { HashMap<String, SmsItem>().apply {
                put(SmsAdapter.KEY_DATA, it)
            } })
        }.let1 {
            mLVSms.adapter = SmsAdapter(context!!, it)
        }
    }

    private fun checkSms(sms: SmsItem): Boolean {
        if(mPtNum.matcher(sms.body).matches())    {
            return mPtKeyWord.matcher(sms.body).matches()
        }

        return false
    }

    private fun parseSms(cr:Cursor): SmsItem    {
        val getSz = {k:String -> cr.getString(cr.getColumnIndex(k))!!}
        val getLong = {k:String -> cr.getLong(cr.getColumnIndex(k))}

        return SmsItem(getLong(K_ID), Timestamp(getLong(K_DATE)),
                getSz(K_ADDRESS), getSz(K_BODY), getSz(K_TYPE))
    }
    /// PRIVATE END

    companion object {
        private const val K_ID = "_id"
        private const val K_ADDRESS = "address"
        private const val K_PERSON = "person"
        private const val K_BODY = "body"
        private const val K_DATE = "date"
        private const val K_TYPE = "type"
    }
}
