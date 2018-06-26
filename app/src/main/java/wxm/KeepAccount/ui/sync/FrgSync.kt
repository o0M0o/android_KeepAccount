package wxm.KeepAccount.ui.sync


import android.annotation.SuppressLint
import android.content.CursorLoader
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.AbsListView
import android.widget.ListView
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.item.SmsParseItem
import wxm.KeepAccount.ui.data.edit.SmsToNote.ACSmsToNote
import wxm.KeepAccount.utility.AppUtil
import wxm.androidutil.improve.doJudge
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
    private val mIBReset: IconButton by bindView(R.id.ib_reset_sms)

    private val mPtNum = Pattern.compile("(\\D*)[1-9](\\d+)(.*)")
    private val mPtKeyWord = Pattern.compile("(.*)(交易|消费|存入|转出|汇款)(.*)")

    private val mLsSms = LinkedList<SmsItem>()
    private var mLVPrvHotPos = 0

    override fun getLayoutID(): Int = R.layout.frg_sync_sms
    override fun isUseEventBus(): Boolean = true

    /**
     * for sms event
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSmsEvent(event: SmsEvent) {
        val doToNote = { ty: String ->
            Intent(context, ACSmsToNote::class.java).apply {
                putExtra(ACSmsToNote.FIELD_NOTE_TYPE, ty)
                putExtra(ACSmsToNote.FIELD_SMS, mLsSms.find { it.id == event.smsId }!!)
            }.let1 {
                startActivityForResult(it, REQ_TRANS_SMS)
            }
        }

        when (event.eventType) {
            SmsEvent.EVENT_DELETE -> {
                AppUtil.smsParseDBUtility.addParseResult(event.smsId, SmsParseItem.FIELD_VAL_REMOVE)
                loadSMS()
            }

            SmsEvent.EVENT_TO_INCOME -> {
                doToNote(ACSmsToNote.FIELD_NT_INCOME)
            }

            SmsEvent.EVENT_TO_PAY -> {
                doToNote(ACSmsToNote.FIELD_NT_PAY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQ_TRANS_SMS == requestCode && GlobalDef.INTRET_SURE == resultCode && null != data) {
            val sms = data.getParcelableExtra<SmsItem>(ACSmsToNote.FIELD_SMS)!!
            val ty = data.getStringExtra(ACSmsToNote.FIELD_NOTE_TYPE)!!.let {
                if (it == ACSmsToNote.FIELD_NT_PAY)
                    SmsParseItem.FIELD_VAL_TO_PAY
                else
                    SmsParseItem.FIELD_VAL_TO_INCOME
            }

            AppUtil.smsParseDBUtility.addParseResult(sms.id, ty)
            loadSMS()
        }
    }

    override fun initUI(bundle: Bundle?) {
        mIBReload.setOnClickListener { _ ->
            loadSMS()
        }

        mIBReset.setOnClickListener { _ ->
            AppUtil.smsParseDBUtility.clean()
            loadSMS()
        }

        mLVSms.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            }

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                mLVPrvHotPos = firstVisibleItem
            }
        })

        loadSMS()
    }

    /// PRIVATE BEGIN
    private fun loadSMS() {
        mLsSms.clear()

        @SuppressLint("StaticFieldLeak")
        val obj = object : CursorLoader(context,
                Uri.parse("content://sms/inbox"),
                arrayOf(K_ID, K_ADDRESS, K_PERSON, K_BODY, K_DATE, K_TYPE),
                null, null, "$K_ID DESC") {
            override fun deliverResult(cursor: Cursor?) {
                super.deliverResult(cursor)
                cursor?.let1 {
                    it.moveToFirst()
                    do {
                        parseSms(it).let1 {
                            if (checkSms(it)) {
                                TagLog.i("check pass : ${it.body}")

                                mLsSms.add(it)
                            } else {
                                TagLog.i("check not pass : ${it.body}")
                            }
                        }
                    } while (it.moveToNext())

                    loadLV()
                }
            }
        }
        obj.startLoading()
    }

    /**
     * load sms to listview
     */
    private fun loadLV() {
        ArrayList<HashMap<String, SmsItem>>().apply {
            addAll(mLsSms.map {
                HashMap<String, SmsItem>().apply {
                    put(SmsAdapter.KEY_DATA, it)
                }
            })
        }.let1 {
            mLVSms.adapter = SmsAdapter(context!!, it)
        }

        mLVSms.setSelection((mLVPrvHotPos >= 0).doJudge(mLVPrvHotPos, 0))
    }

    /**
     * return true if [sms] can be parse
     */
    private fun checkSms(sms: SmsItem): Boolean {
        if (mPtNum.matcher(sms.body).matches() && mPtKeyWord.matcher(sms.body).matches()) {
            val st = AppUtil.smsParseDBUtility.getParseResult(sms.id)
            return SmsParseItem.FIELD_VAL_NONE == st
        }

        return false
    }

    /**
     * get [SmsItem] from sms in [cr]
     */
    private fun parseSms(cr: Cursor): SmsItem {
        val getSz = { k: String -> cr.getString(cr.getColumnIndex(k))!! }
        val getLong = { k: String -> cr.getLong(cr.getColumnIndex(k)) }

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

        private const val REQ_TRANS_SMS = 1
    }
}
