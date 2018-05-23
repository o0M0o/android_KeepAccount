package wxm.KeepAccount.ui.dialog

import android.os.Bundle
import android.view.View
import android.widget.EditText

import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R

/**
 * longtext dlg
 * Created by WangXM on 2016/11/1.
 */
class DlgLongTxt : DlgOKOrNOBase() {
    private lateinit var mETLongTxt: EditText
    private var mSZInitLongTxt: String? = null

    /**
     * get usr input longtext
     * @return      usr input
     */
    /**
     * set longtext
     * @param lt    origin longtext
     */
    var longTxt: String
        get() = mETLongTxt.text.toString()
        set(lt) {
            this.mSZInitLongTxt = lt
        }

    override fun createDlgView(savedInstanceState: Bundle?): View {
        initDlgTitle("编辑内容", "接受", "放弃")
        return View.inflate(activity, R.layout.dlg_long_txt, null)
    }

    override fun initDlgView(savedInstanceState: Bundle?) {
        mETLongTxt = findDlgChildView(R.id.et_long_txt)!!

        if (!UtilFun.StringIsNullOrEmpty(mSZInitLongTxt))
            mETLongTxt.setText(mSZInitLongTxt)
    }
}
