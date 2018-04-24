package wxm.KeepAccount.ui.dialog

import android.view.View
import android.widget.EditText

import kotterknife.bindView
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R

/**
 * longtext dlg
 * Created by WangXM on 2016/11/1.
 */
class DlgLongTxt : DlgOKOrNOBase() {
    private val mETLongTxt: EditText by bindView(R.id.et_long_txt)
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
            mSZInitLongTxt = lt
        }

    override fun InitDlgView(): View {
        InitDlgTitle("编辑内容", "接受", "放弃")
        val vw = View.inflate(activity, R.layout.dlg_long_txt, null)

        if (!UtilFun.StringIsNullOrEmpty(mSZInitLongTxt))
            mETLongTxt.setText(mSZInitLongTxt)
        return vw
    }
}
