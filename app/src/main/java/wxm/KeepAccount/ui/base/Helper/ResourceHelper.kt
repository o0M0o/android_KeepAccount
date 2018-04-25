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
    val mCRLVLineOne: Int = ContextUtil.getColor(R.color.color_1)
    val mCRLVLineTwo: Int = ContextUtil.getColor(R.color.color_2)

    val mCRLVItemTransFull: Int = ContextUtil.getColor(R.color.trans_full)

    val mCRLVItemNoSel: Int = ContextUtil.getColor(R.color.red_ff725f_half)
    val mCRLVItemSel: Int =  ContextUtil.getColor(R.color.red_ff725f)

    val mCRTextWhite: Int = ContextUtil.getColor(R.color.white)
    val mCRTextFit: Int = ContextUtil.getColor(R.color.text_fit)
}
