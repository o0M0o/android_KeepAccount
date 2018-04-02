package wxm.KeepAccount.ui.dialog;

import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;

/**
 * longtext dlg
 * Created by WangXM on 2016/11/1.
 */
public class DlgLongTxt extends DlgOKOrNOBase {
    @BindView(R.id.et_long_txt)
    EditText mETLongTxt;

    private String mSZInitLongTxt;

    /**
     * get usr input longtext
     * @return      usr input
     */
    public String getLongTxt() {
        return mETLongTxt.getText().toString();
    }

    /**
     * set longtext
     * @param lt    origin longtext
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
