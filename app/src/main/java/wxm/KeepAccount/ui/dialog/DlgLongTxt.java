package wxm.KeepAccount.ui.dialog;

import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;

/**
 * 日期选择对话框
 * Created by ookoo on 2016/11/1.
 */
public class DlgLongTxt extends DlgOKOrNOBase {
    @BindView(R.id.et_long_txt)
    EditText mETLongTxt;

    private String mSZInitLongTxt;

    /**
     * 获取长文本
     *
     * @return 长文本
     */
    public String getLongTxt() {
        return mETLongTxt.getText().toString();
    }

    /**
     * 设置长文本
     *
     * @param lt 初始长文本
     */
    public void setLongTxt(String lt) {
        mSZInitLongTxt = lt;
    }

    @Override
    protected View InitDlgView() {
        InitDlgTitle("编辑内容", "接受", "放弃");
        View vw = View.inflate(getActivity(), R.layout.dlg_long_txt, null);
        ButterKnife.bind(this, vw);

        if (!UtilFun.StringIsNullOrEmpty(mSZInitLongTxt))
            mETLongTxt.setText(mSZInitLongTxt);
        return vw;
    }
}
