package wxm.KeepAccount.ui.dialog;

import android.support.design.widget.TextInputEditText;
import android.view.View;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.db.RecordTypeItem;
import wxm.KeepAccount.R;

/**
 * 日期选择对话框
 * Created by 123 on 2016/11/1.
 */
public class DlgRecordInfo extends DlgOKAndNOBase {
    private RecordTypeItem  mOldData;
    private String          mRecordType;

    TextInputEditText   mTIETName;
    TextInputEditText   mTIETNote;

    public void setInitDate(RecordTypeItem initData)    {
        mOldData = initData;
    }

    public void  setRecordType(String type) {
        mRecordType = type;
    }


    public RecordTypeItem getCurDate()  {
        String name = mTIETName.getText().toString();
        String note = mTIETNote.getText().toString();
        if(UtilFun.StringIsNullOrEmpty(name) || UtilFun.StringIsNullOrEmpty(note))
            return null;

        RecordTypeItem ri = new RecordTypeItem();
        ri.setItemType(AppGobalDef.STR_RECORD_PAY.equals(mRecordType) ?
                RecordTypeItem.DEF_PAY : RecordTypeItem.DEF_INCOME);
        ri.setType(name);
        ri.setNote(note);
        return ri;
    }


    @Override
    protected View InitDlgView() {
        if(UtilFun.StringIsNullOrEmpty(mRecordType)
                || (!AppGobalDef.STR_RECORD_PAY.equals(mRecordType) && !AppGobalDef.STR_RECORD_INCOME.equals(mRecordType)))
            return null;

        InitDlgTitle(AppGobalDef.STR_RECORD_PAY.equals(mRecordType) ? "添加支付类型" : "添加收入类型",
                "接受", "放弃");
        View vw = View.inflate(getActivity(), R.layout.dlg_add_record_info, null);
        mTIETName = UtilFun.cast_t(vw.findViewById(R.id.ti_name));
        mTIETNote = UtilFun.cast_t(vw.findViewById(R.id.ti_note));
        if(null != mOldData)    {
            mTIETName.setText(mOldData.getItemType());
            mTIETNote.setText(mOldData.getNote());
        }

        return vw;
    }
}
