package wxm.KeepAccount.ui.data.show.note.base

import android.content.Context
import android.view.View
import android.widget.RelativeLayout

import java.util.LinkedList

import butterknife.BindView
import kotterknife.bindView
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.data.show.note.ACNoteShow

/**
 * base for show record
 * Created by xiaoming wang on 2016/9/14.
 */
abstract class ShowViewBase protected constructor() : FrgSupportBaseAdv() {
    private val mRLAttachButton: RelativeLayout by bindView(R.id.rl_attach_button)
    private val mRLAccept: RelativeLayout by bindView(R.id.rl_accpet_giveup)
    private val mRLFilter: RelativeLayout by bindView(R.id.rl_filter)
    
    // filter for view
    protected var mBFilter: Boolean = false
    protected val mFilterPara: LinkedList<String> = LinkedList()

    /**
     * get root activity
     * @return      activity if success else null
     */
    protected val rootActivity: ACNoteShow?
        get() {
            val ct = context
            return if (ct is ACNoteShow) {
                UtilFun.cast<ACNoteShow>(ct)
            } else null

        }

    /**
     * set layout visibility
     * @param visible can be :
     * 1. `View.GONE`
     * 2. `View.VISIBLE`
     */
    protected fun setAttachLayoutVisible(visible: Int) {
        mRLAttachButton.visibility = visible
    }

    /**
     * set filter layout visibility
     * @param visible can be :
     * 1. `View.GONE`
     * 2. `View.VISIBLE`
     */
    protected fun setFilterLayoutVisible(visible: Int) {
        if (View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE)

        mRLFilter.visibility = visible
    }

    /**
     * set accept/give-up layout visibility
     * @param visible can be :
     * 1. `View.GONE`
     * 2. `View.VISIBLE`
     */
    protected fun setAcceptGiveUpLayoutVisible(visible: Int) {
        if (View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE)

        mRLAccept.visibility = visible
    }
}
