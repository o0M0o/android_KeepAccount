package wxm.KeepAccount.ui.data.show.note.ListView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.LinkedList;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;
import wxm.uilib.IconButton.IconButton;

/**
 * listview base for show record
 * Created by WangXM on2016/9/29.
 */
public abstract class LVBase extends ShowViewBase {
    /**
     * helper for record detail
     */
    class recordDetail {
        public String   mPayCount;
        public String   mPayAmount;

        public String   mIncomeCount;
        public String   mIncomeAmount;
    }

    /**
     * helper for node fold/unfold
     */
    enum EShowFold {
        UNFOLD("vs_unfold"),
        FOLD("vs_fold");

        private String mSZName;

        EShowFold(String nm)   {
            mSZName = nm;
        }

        /**
         * get self name
         * @return      name
         */
        public String getName() {
            return mSZName;
        }

        /**
         * use fold status get EShowFold
         * @param fold      if true, node fold
         * @return          EShowFold object
         */
        public static EShowFold getByFold(boolean fold)    {
            return fold ? FOLD : UNFOLD;
        }

        /**
         * use name get EShowFold
         * @param nm        name for node status
         * @return          EShowFold object
         */
        public static EShowFold getByName(String nm)  {
            return nm.equals(FOLD.mSZName) ? FOLD : UNFOLD;
        }
    }

    // view data
    protected final LinkedList<String> mLLSubFilter = new LinkedList<>();
    protected final LinkedList<View> mLLSubFilterVW = new LinkedList<>();

    // unfold data
    private final LinkedList<String> mUnfoldItems;

    // for filter
    protected boolean mBSelectSubFilter = false;
    protected boolean mBActionExpand;

    // for ui
    @BindView(R.id.lv_show)
    ListView mLVShow;

    @BindView(R.id.rl_content)
    RelativeLayout mRLContent;

    @BindView(R.id.pb_loading)
    ProgressBar mPBLoading;

    protected ActionHelper     mAHActs;

    public LVBase() {
        super();

        mUnfoldItems = new LinkedList<>();
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.lv_note_show_pager, viewGroup, false);
        mAHActs.bind(rootView);
        mAHActs.init();
        return rootView;
    }

    /**
     * add unfold node
     * only record 20 node, then will remove first node
     * @param tag       tag for unfold node
     */
    protected void addUnfoldItem(String tag) {
        if (!mUnfoldItems.contains(tag)) {
            if (20 < mUnfoldItems.size())
                mUnfoldItems.removeFirst();

            //Log.i(LOG_TAG, "addUnfoldItem, tag = " + tag);
            mUnfoldItems.addLast(tag);
        }
    }

    /**
     * remove unfold item
     * @param tag       tag for need removed
     */
    protected void removeUnfoldItem(String tag) {
        mUnfoldItems.remove(tag);
    }


    /**
     * check one item if is unfold
     * @param tag       tag for wait checked item
     * @return          true if unfold else false
     */
    protected boolean checkUnfoldItem(String tag) {
        return mUnfoldItems.contains(tag);
    }


    /**
     * reload data & view
     * @param v             context
     * @param bShowDialog   show dialog if true
     */
    protected void reloadView(final Context v, final boolean bShowDialog) {
        refreshUI();
        if (bShowDialog) {
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(v);
            builder.setMessage("数据已刷新!").setTitle("提醒");

            android.app.AlertDialog dlg = builder.create();
            dlg.show();
        }
    }

    /**
     * Shows the progress UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showLoadingProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        mRLContent.setVisibility(show ? View.GONE : View.VISIBLE);
        /*
        mRLContent.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRLContent.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        */

        mPBLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        mPBLoading.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPBLoading.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
