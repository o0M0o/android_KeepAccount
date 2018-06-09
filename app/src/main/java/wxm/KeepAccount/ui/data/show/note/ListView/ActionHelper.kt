package wxm.KeepAccount.ui.data.show.note.ListView

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import wxm.KeepAccount.R
import wxm.KeepAccount.improve.let1
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.view.EventHelper

/**
 * helper for action layout
 * Created by WangXM on 2018/3/26.
 */
abstract class ActionHelper internal constructor() {
    private lateinit var mIVHideShow: ImageView
    private lateinit var mRLHideShow: RelativeLayout
    private lateinit var mRLActions: RelativeLayout
    private lateinit var mRLAction: RelativeLayout
    private lateinit var mRLLVNote: RelativeLayout

    private val mDAExpand: Drawable = AppBase.getDrawable(R.drawable.ic_to_left)
    private val mDAHide: Drawable = AppBase.getDrawable(R.drawable.ic_to_right)

    private var mIsShow: Boolean = false

    init {
        mIsShow = true
    }

    /**
     * bind layout for ButterKnife
     * @param holder        father view
     */
    fun bind(holder: View) {
        mIVHideShow = holder.findViewById(R.id.iv_show_tag)
        mRLHideShow = holder.findViewById(R.id.rl_hide_show)
        mRLActions = holder.findViewById(R.id.rl_acts)
        mRLLVNote = holder.findViewById(R.id.rl_lv_note)
        mRLAction = holder.findViewById(R.id.rl_action)

        EventHelper.setOnClickOperator(holder,
                intArrayOf(R.id.rl_hide_show),
                { _ ->
                    setActsVisibility(!mIsShow)
                    mIsShow = !mIsShow
                })

        setActsVisibility(mIsShow)
        if (mIsShow) {
            initActs(holder)
        }
    }


    /**
     * use this to init self actions
     */
    protected abstract fun initActs(parentView: View)


    /**
     * if [bShow] is true set view visibility
     */
    private fun setActsVisibility(bShow: Boolean) {
        val rp = mRLActions.layoutParams

        mIVHideShow.setImageDrawable(if (bShow) mDAHide else mDAExpand)
        mRLHideShow.background.alpha = if (bShow) 255 else 40
        mRLAction.background.alpha = if (bShow) 255 else 0

        rp.width = if (bShow) ViewGroup.LayoutParams.MATCH_PARENT else 0
        mRLActions.layoutParams = rp

        (mRLLVNote.layoutParams as RelativeLayout.LayoutParams).let1 {
            if (bShow)
                it.addRule(RelativeLayout.ABOVE, R.id.rl_action)
            else
                it.removeRule(RelativeLayout.ABOVE)
            mRLLVNote.layoutParams = it
        }
    }
}
