package wxm.KeepAccount.ui.data.edit.SmsToNote

import android.os.Bundle
import wxm.KeepAccount.R
import wxm.KeepAccount.item.IncomeNoteItem
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.data.edit.NoteEdit.page.*
import wxm.KeepAccount.ui.data.edit.base.*
import wxm.KeepAccount.ui.sync.SmsItem
import wxm.androidutil.improve.let1
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.frg.FrgSupportSwitcher
import java.math.BigDecimal
import java.util.regex.Pattern

/**
 * @author      WangXM
 * @version     create：2018/4/25
 */
class FrgSmsToNote :  FrgSupportSwitcher<FrgSupportBaseAdv>() {
    private val mFrgPay = PgPayEdit()
    private val mFrgIncome = PgIncomeEdit()

    private val mPtNum = Pattern.compile("[1-9](,|\\d*)\\d*(\\.|\\d*)\\d{0,2}元")

    init   {
        setupFrgID(R.layout.frg_sms_to_note, R.id.fl_page_holder)
    }

    override fun setupFragment(savedInstanceState: Bundle?) {
        addChildFrg(mFrgPay)
        addChildFrg(mFrgIncome)
    }

    fun onAccept(): Boolean {
        return (hotPage as IEdit).onAccept()
    }

    override fun loadUI(bundle: Bundle?) {
        if(null == bundle) {
            activity!!.intent!!.let1 {
                val sms = it.getParcelableExtra<SmsItem>(ACSmsToNote.FIELD_SMS)!!
                if(it.getStringExtra(ACSmsToNote.FIELD_NOTE_TYPE) == ACSmsToNote.FIELD_NT_PAY)  {
                    switchToPage(mFrgPay)

                    mFrgPay.setEditData(PayNoteItem().apply {
                        note = sms.body
                        ts = sms.date

                        parseAmount(sms.body)?.let1 {
                            amount = it
                        }
                    })
                    mFrgPay.reInitUI()
                } else  {
                    switchToPage(mFrgIncome)

                    mFrgIncome.setEditData(IncomeNoteItem().apply {
                        note = sms.body
                        ts = sms.date

                        parseAmount(sms.body)?.let1 {
                            amount = it
                        }
                    })
                    mFrgIncome.reInitUI()
                }
            }
        }
    }

    private fun parseAmount(body:String): BigDecimal?    {
        val m = mPtNum.matcher(body)
        if(m.find())    {
            val getRet = {str:String ->
                str.removeSuffix("元").replace(",", "")
                .toBigDecimal()
            }

            return m.group()?.let(getRet)
        }

        return null
    }
}