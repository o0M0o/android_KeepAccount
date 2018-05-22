package wxm.KeepAccount.ui.dialog

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.View

import kotterknife.bindView
import wxm.androidutil.dialog.DlgOKOrNOBase
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.RecordTypeItem

/**
 * record info dlg
 * Created by WangXM on 2016/11/1.
 */
class DlgRecordInfo : DlgOKOrNOBase() {
    private lateinit var mTIETName: TextInputEditText
    private lateinit var mTIETNote: TextInputEditText

    private var mOldData: RecordTypeItem? = null
    private var mRecordType: String? = null

    val curDate: RecordTypeItem?
        get() {
            val name = mTIETName.text.toString()
            val note = mTIETNote.text.toString()
            if (UtilFun.StringIsNullOrEmpty(name) || UtilFun.StringIsNullOrEmpty(note))
                return null

            val ri = RecordTypeItem()
            if (null != mOldData)
                ri._id = mOldData!!._id
            ri.itemType = if (GlobalDef.STR_RECORD_PAY == mRecordType)
                RecordTypeItem.DEF_PAY
            else
                RecordTypeItem.DEF_INCOME
            ri.type = name
            ri.note = note
            return ri
        }

    fun setInitDate(initData: RecordTypeItem?) {
        mOldData = initData
    }

    fun setRecordType(type: String) {
        mRecordType = type
    }

    override fun createDlgView(savedInstanceState: Bundle?): View? {
        if (UtilFun.StringIsNullOrEmpty(mRecordType)
                || (GlobalDef.STR_RECORD_PAY != mRecordType && GlobalDef.STR_RECORD_INCOME != mRecordType))
            return null

        initDlgTitle(if (GlobalDef.STR_RECORD_PAY == mRecordType) "添加支付类型" else "添加收入类型",
                "接受", "放弃")
        return View.inflate(activity, R.layout.dlg_add_record_info, null)
    }

    override fun initDlgView(savedInstanceState: Bundle?) {
        mTIETName = findDlgChildView(R.id.ti_name)!!
        mTIETNote = findDlgChildView(R.id.ti_note)!!

        mOldData?.let {
            mTIETName.setText(it.type)
            mTIETNote.setText(it.note)
        }
    }
}
