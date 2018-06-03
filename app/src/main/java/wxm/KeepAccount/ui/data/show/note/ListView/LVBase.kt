package wxm.KeepAccount.ui.data.show.note.ListView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout

import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase
import wxm.androidutil.ui.dialog.DlgAlert
import java.util.*

/**
 * listview base for show record
 * Created by WangXM on2016/9/29.
 */
abstract class LVBase : ShowViewBase() {
    protected val mLVShow: ListView by bindView(R.id.lv_show)
    private val mRLContent: RelativeLayout by bindView(R.id.rl_content)
    private val mPBLoading: ProgressBar by bindView(R.id.pb_loading)

    // view data
    protected val mLLSubFilter = LinkedList<String>()
    protected val mLLSubFilterVW = LinkedList<View>()

    // unfold data
    private val mUnfoldItems: LinkedList<String> = LinkedList()

    // for filter
    protected var mBSelectSubFilter = false
    protected var mBActionExpand: Boolean = false

    protected lateinit var mAHActs: ActionHelper

    /**
     * helper for record detail
     */
    data class RecordDetail(val mPayCount:String, val mPayAmount:String,
                            val mIncomeCount:String, val mIncomeAmount:String)

    /**
     * helper for node fold/unfold
     */
    internal enum class EShowFold(val showStatus: String) {
        UNFOLD("vs_unfold"),
        FOLD("vs_fold");

        fun isFold(): Boolean   {
            return showStatus == FOLD.showStatus
        }

        companion object {
            /**
             * use fold status get EShowFold
             * @param fold      if true, node fold
             * @return          EShowFold object
             */
            fun getByFold(fold: Boolean): EShowFold {
                return if (fold) FOLD else UNFOLD
            }

            /**
             * use name get EShowFold
             * @param nm        name for node status
             * @return          EShowFold object
             */
            fun getByShowStatus(nm: String): EShowFold {
                return if (nm == FOLD.showStatus) FOLD else UNFOLD
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        mAHActs.bind(view!!)
    }

    override fun getLayoutID(): Int {
        return R.layout.lv_note_show_pager
    }

    /**
     * add unfold node
     * only record 20 node, then will remove first node
     * @param tag       tag for unfold node
     */
    protected fun addUnfoldItem(tag: String) {
        if (!mUnfoldItems.contains(tag)) {
            if (20 < mUnfoldItems.size)
                mUnfoldItems.removeFirst()

            //TagLog.i(LOG_TAG, "addUnfoldItem, tag = " + tag);
            mUnfoldItems.addLast(tag)
        }
    }

    /**
     * remove unfold item
     * @param tag       tag for need removed
     */
    protected fun removeUnfoldItem(tag: String) {
        mUnfoldItems.remove(tag)
    }


    /**
     * check one item if is unfold
     * @param tag       tag for wait checked item
     * @return          true if unfold else false
     */
    protected fun checkUnfoldItem(tag: String): Boolean {
        return mUnfoldItems.contains(tag)
    }


    /**
     * reload data & view
     * @param bShowDialog   show dialog if true
     */
    protected fun reloadView(bShowDialog: Boolean) {
        reInitUI()
        if (bShowDialog) {
            DlgAlert.showAlert(context!!, R.string.dlg_prompt, R.string.dlg_data_refreshed)
        }
    }

    /**
     * Shows the progress UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected fun showLoadingProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources
                .getInteger(android.R.integer.config_shortAnimTime)

        mRLContent.visibility = if (show) View.GONE else View.VISIBLE
        /*
        mRLContent.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRLContent.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        */

        mPBLoading.visibility = if (show) View.VISIBLE else View.GONE
        mPBLoading.animate().setDuration(shortAnimTime.toLong())
                .alpha((if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        mPBLoading.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }
}
