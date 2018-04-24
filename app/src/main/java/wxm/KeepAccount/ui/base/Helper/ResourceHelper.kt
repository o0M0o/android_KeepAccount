package wxm.KeepAccount.ui.base.Helper

import android.content.Context
import android.content.res.Resources
import android.os.Build

import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil

/**
 * Resource helper
 * Created by WangXM on 2017/3/3.
 */
object ResourceHelper {
    var mCRLVLineOne: Int = 0
    var mCRLVLineTwo: Int = 0

    var mCRLVItemTransFull: Int = 0

    var mCRLVItemNoSel: Int = 0
    var mCRLVItemSel: Int = 0

    var mCRTextWhite: Int = 0
    var mCRTextFit: Int = 0

    init {
        val ct = ContextUtil.instance
        if (null != ct) {
            val res = ct.resources
            val te = ct.theme

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCRLVLineOne = res.getColor(R.color.color_1, te)
                mCRLVLineTwo = res.getColor(R.color.color_2, te)

                mCRLVItemTransFull = res.getColor(R.color.trans_full, te)

                mCRLVItemNoSel = res.getColor(R.color.red_ff725f_half, te)
                mCRLVItemSel = res.getColor(R.color.red_ff725f, te)

                mCRTextWhite = res.getColor(R.color.white, te)
                mCRTextFit = res.getColor(R.color.text_fit, te)
            } else {
                mCRLVLineOne = res.getColor(R.color.color_1)
                mCRLVLineTwo = res.getColor(R.color.color_2)

                mCRLVItemTransFull = res.getColor(R.color.trans_full)

                mCRLVItemNoSel = res.getColor(R.color.red_ff725f_half)
                mCRLVItemSel = res.getColor(R.color.red_ff725f)

                mCRTextWhite = res.getColor(R.color.white)
                mCRTextFit = res.getColor(R.color.text_fit)
            }
        }
    }
}
