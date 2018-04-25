package wxm.KeepAccount.ui.dialog.base

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build

import java.util.Objects

import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil

/**
 * dialog会用到的资源
 * Created by WangXM on 2017/3/8.
 */
object DlgResource {
    // for channel item shape
    val mDAChannelSel: Drawable = ContextUtil.getDrawable(R.drawable.gi_channel_sel_shape)
    val mDAChannelNoSel: Drawable = ContextUtil.getDrawable(R.drawable.gi_channel_no_sel_shape)

    // for sort icon
    val mDASortDown: Drawable = ContextUtil.getDrawable(R.drawable.ic_sort_down_1)
    val mDASortUp: Drawable = ContextUtil.getDrawable(R.drawable.ic_sort_up_1)

    // for show string
    val mSZSortByNameDown: String = ContextUtil.getString(R.string.cn_sort_down_by_name)
    val mSZSortByNameUp: String = ContextUtil.getString(R.string.cn_sort_up_by_name)

    // for color
    val mCLSelected: Int = ContextUtil.getColor(R.color.peachpuff)
    val mCLNotSelected: Int = ContextUtil.getColor(R.color.white)
}
