package wxm.KeepAccount.ui.dialog.base

import android.graphics.drawable.Drawable

import wxm.KeepAccount.R
import wxm.androidutil.app.AppBase

/**
 * dialog会用到的资源
 * Created by WangXM on 2017/3/8.
 */
object DlgResource {
    // for channel item shape
    val mDAChannelSel: Drawable = AppBase.getDrawable(R.drawable.gi_channel_sel_shape)
    val mDAChannelNoSel: Drawable = AppBase.getDrawable(R.drawable.gi_channel_no_sel_shape)

    // for sort icon
    val mDASortDown: Drawable = AppBase.getDrawable(R.drawable.ic_sort_down_1)
    val mDASortUp: Drawable = AppBase.getDrawable(R.drawable.ic_sort_up_1)

    // for show string
    val mSZSortByNameDown: String = AppBase.getString(R.string.cn_sort_down_by_name)
    val mSZSortByNameUp: String = AppBase.getString(R.string.cn_sort_up_by_name)

    // for color
    val mCLSelected: Int = AppBase.getColor(R.color.peachpuff)
    val mCLNotSelected: Int = AppBase.getColor(R.color.white)
}
