package wxm.KeepAccount.ui.search


import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.ListView
import android.widget.RadioButton
import android.widget.SearchView
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.db.PayIncomeDBUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.ui.utility.AdapterNoteRangeDetail
import wxm.KeepAccount.utility.AppUtil
import wxm.androidutil.app.AppBase
import wxm.androidutil.improve.let1
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import java.util.*


/**
 * for help
 * Created by WangXM on 2016/11/29.
 */
class FrgNoteSearch : FrgSupportBaseAdv() {
    private val mLVResult: ListView by bindView(R.id.lv_result)
    private val mSVSearch: SearchView by bindView(R.id.sv_search)

    private val mCBAll: CheckBox by bindView(R.id.cb_all)
    private val mCBNote: CheckBox by bindView(R.id.cb_note)
    private val mCBAmount: CheckBox by bindView(R.id.cb_amount)
    private val mCBName: CheckBox by bindView(R.id.cb_name)

    private val mCBPay: CheckBox by bindView(R.id.cb_pay)
    private val mCBIncome: CheckBox by bindView(R.id.cb_income)
    //private val mRBPayIncome: RadioButton by bindView(R.id.rb_pay_income)

    override fun getLayoutID(): Int = R.layout.pg_note_search

    override fun initUI(savedInstanceState: Bundle?) {
        super.initUI(savedInstanceState)
        if(null == savedInstanceState)  {
            initCheckBox()
            initQueryInput()
        }
    }



    private fun initCheckBox()  {
        mCBAll.setOnClickListener   {
            if((it as CheckBox).isChecked)  {
                mCBNote.isChecked = true
                mCBName.isChecked = true
                mCBAmount.isChecked = true
            } else  {
                if(!mCBName.isChecked && !mCBNote.isChecked && !mCBAmount.isChecked)    {
                    alertForNeedSearchColumn()
                }
            }
        }

        mCBName.setOnClickListener  {
            if(!(it as CheckBox).isChecked)  {
                if(!mCBNote.isChecked && !mCBAmount.isChecked)    {
                    alertForNeedSearchColumn()
                }
            }
        }

        mCBNote.setOnClickListener  {
            if(!(it as CheckBox).isChecked)  {
                if(!mCBName.isChecked && !mCBAmount.isChecked)    {
                    alertForNeedSearchColumn()
                }
            }
        }

        mCBAmount.setOnClickListener  {
            if(!(it as CheckBox).isChecked)  {
                if(!mCBName.isChecked && !mCBNote.isChecked)    {
                    alertForNeedSearchColumn()
                }
            }
        }
    }

    private fun initQueryInput()    {
        mSVSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query.isNullOrEmpty() || !checkSearchPara())   {
                    return false
                }

                val tp = ArrayList<String>().apply {
                    if(mCBPay.isChecked)    {
                        add(GlobalDef.STR_RECORD_PAY)
                    }

                    if(mCBIncome.isChecked)    {
                        add(GlobalDef.STR_RECORD_INCOME)
                    }
                }

                val cl = ArrayList<String>().apply {
                    if(mCBName.isChecked)   {
                        add(INote.FIELD_INFO)
                    }

                    if(mCBNote.isChecked)   {
                        add(INote.FIELD_NOTE)
                    }

                    if(mCBAmount.isChecked)   {
                        add(INote.FIELD_AMOUNT)
                    }
                }

                PayIncomeDBUtility.doSearch(query!!, tp.toTypedArray(), cl.toTypedArray()).let1 {
                    loadINote(it)
                }

                hideKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun checkSearchPara(): Boolean   {
        if(!mCBName.isChecked && !mCBNote.isChecked && !mCBAmount.isChecked)    {
            alertForNeedSearchColumn()
            return false
        }

        if(!mCBPay.isChecked && !mCBIncome.isChecked)    {
            alertForNeedSearchType()
            return false
        }

        return true
    }

    private fun alertForNeedSearchColumn()  {
        DlgAlert.showAlert(context!!, R.string.dlg_warn, R.string.dlg_need_search_column)
    }

    private fun alertForNeedSearchType()  {
        DlgAlert.showAlert(context!!, R.string.dlg_warn, R.string.dlg_need_search_type)
    }

    private fun hideKeyboard() {
        AppBase.getSystemService<InputMethodManager>(Context.INPUT_METHOD_SERVICE)?.let1 {
            it.hideSoftInputFromWindow(view!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun loadINote(lsNotes:List<INote>) {
        val para = LinkedList<HashMap<String, INote>>()
        lsNotes.forEach  {
            para.add(HashMap<String, INote>().apply { put(AdapterNoteRangeDetail.K_NODE, it) })
        }

        mLVResult.adapter = AdapterNoteRangeDetail(activity!!, para)
    }
}
