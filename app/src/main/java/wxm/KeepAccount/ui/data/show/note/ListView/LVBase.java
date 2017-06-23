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
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.ShowData.ShowViewHelperBase;
import wxm.uilib.IconButton.IconButton;

/**
 * sub class for listview
 * Created by wxm on 2016/9/29.
 */
public abstract class LVBase extends ShowViewHelperBase {
    /// list item data begin
    protected final static String K_TITLE = "k_title";
    protected final static String K_ABSTRACT = "k_abstract";
    protected final static String K_TIME = "k_time";
    protected final static String K_BUDGET = "k_budget";
    protected final static String K_AMOUNT = "k_amount";
    protected final static String K_NOTE = "k_note";
    protected final static String K_TAG = "k_tag";
    protected final static String K_ID = "k_id";
    protected final static String K_TYPE = "k_type";
    protected final static String K_DETAIL = "k_detail";
    protected final static String K_SUB_TAG = "k_sub_tag";

    protected final static String K_YEAR = "k_year";
    protected final static String K_MONTH = "k_month";
    protected final static String K_DAY_NUMEBER = "k_d_number";
    protected final static String K_DAY_IN_WEEK = "k_d_in_week";
    protected final static String K_YEAR_PAY_COUNT = "k_ypc";
    protected final static String K_YEAR_PAY_AMOUNT = "k_ypa";
    protected final static String K_YEAR_INCOME_COUNT = "k_yic";
    protected final static String K_YEAR_INCOME_AMOUNT = "k_yia";
    protected final static String K_DAY_PAY_COUNT = "k_dpc";
    protected final static String K_DAY_PAY_AMOUNT = "k_dpa";
    protected final static String K_DAY_INCOME_COUNT = "k_dic";
    protected final static String K_DAY_INCOME_AMOUNT = "k_dia";
    protected final static String K_MONTH_PAY_COUNT = "k_mpc";
    protected final static String K_MONTH_PAY_AMOUNT = "k_mpa";
    protected final static String K_MONTH_INCOME_COUNT = "k_mic";
    protected final static String K_MONTH_INCOME_AMOUNT = "k_mia";

    protected final static String K_SHOW = "k_show";
    protected final static String V_SHOW_UNFOLD = "vs_unfold";
    protected final static String V_SHOW_FOLD = "vs_fold";
    /// list item data end

    // 视图数据
    protected final LinkedList<HashMap<String, String>> mMainPara;
    protected final HashMap<String, LinkedList<HashMap<String, String>>> mHMSubPara;
    protected final LinkedList<String> mLLSubFilter = new LinkedList<>();
    protected final LinkedList<View> mLLSubFilterVW = new LinkedList<>();
    // 存放展开节点的数据
    private final LinkedList<String> mUnfoldItems;
    // for filter
    protected boolean mBSelectSubFilter = false;
    protected boolean mBActionExpand;


    // for ui
    //@BindView(R.id.iv_expand)
    //protected ImageView mIVActions;

    @BindView(R.id.iv_show_tag)
    protected ImageView mIVHideShow;

    @BindView(R.id.rl_hide_show)
    protected RelativeLayout mRLHideShow;

    @BindView(R.id.rl_lv_note)
    protected RelativeLayout mRLLVNote;

    @BindView(R.id.rl_acts)
    protected RelativeLayout mRLActions;

    @BindDrawable(R.drawable.ic_to_left)
    protected Drawable mDAExpand;

    @BindDrawable(R.drawable.ic_to_right)
    protected Drawable mDAHide;


    // for addtion action
    @BindView(R.id.ib_report)
    IconButton mIBReport;

    @BindView(R.id.ib_add)
    IconButton mIBAdd;

    @BindView(R.id.ib_delete)
    IconButton mIBDelete;

    @BindView(R.id.ib_refresh)
    IconButton mIBRefresh;

    @BindView(R.id.ib_sort)
    IconButton mIBSort;

    @BindView(R.id.rl_action)
    RelativeLayout mRLAction;

    @BindView(R.id.lv_show)
    ListView mLVShow;

    @BindView(R.id.rl_content)
    RelativeLayout mRLContent;

    @BindView(R.id.pb_loading)
    ProgressBar mPBLoading;

    public LVBase() {
        super();
        mMainPara = new LinkedList<>();
        mHMSubPara = new HashMap<>();
        mUnfoldItems = new LinkedList<>();
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.lv_note_show_pager, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        initActs();
        refreshData();
    }

    /**
     * 初始化底部动作条
     */
    protected abstract void initActs();

    /**
     * 切换底部”动作条“显示/隐藏
     *
     * @param v 点击view
     */
    @OnClick(R.id.rl_hide_show)
    public void switchActionHideShow(View v) {
        ViewGroup.LayoutParams rp = mRLActions.getLayoutParams();
        boolean b_hide = rp.width == 0;

        mIVHideShow.setImageDrawable(b_hide ? mDAHide : mDAExpand);
        mRLHideShow.getBackground().setAlpha(b_hide ? 255 : 40);
        mRLAction.getBackground().setAlpha(b_hide ? 255 : 0);

        rp.width = b_hide ? ViewGroup.LayoutParams.MATCH_PARENT : 0;
        mRLActions.setLayoutParams(rp);

        RelativeLayout.LayoutParams rp_lv = UtilFun.cast_t(mRLLVNote.getLayoutParams());
        if (b_hide)
            rp_lv.addRule(RelativeLayout.ABOVE, R.id.rl_action);
        else
            rp_lv.removeRule(RelativeLayout.ABOVE);
        mRLLVNote.setLayoutParams(rp_lv);
    }

    /**
     * 添加一个展开节点
     * 只记录20个展开节点, 超过数量后将移除最早记录的节点
     *
     * @param tag 展开节点tag
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
     * 移除一个展开节点
     *
     * @param tag 移除节点tag
     */
    protected void removeUnfoldItem(String tag) {
        mUnfoldItems.remove(tag);
    }


    /**
     * 检查一个节点是否是展开节点
     *
     * @param tag 待检查节点tag
     * @return 如果此节点是展开节点，返回true, 否则返回false
     */
    protected boolean checkUnfoldItem(String tag) {
        return mUnfoldItems.contains(tag);
    }


    /**
     * 刷新数据以及视图
     *
     * @param v           for context
     * @param bShowDialog 若为true则显示提醒对话框
     */
    protected void reloadView(final Context v, final boolean bShowDialog) {
        //refreshUI();
        refreshData();
        if (bShowDialog) {
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(v);
            builder.setMessage("数据已刷新!").setTitle("提醒");

            android.app.AlertDialog dlg = builder.create();
            dlg.show();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showLoadingProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        mRLContent.setVisibility(show ? View.GONE : View.VISIBLE);
        mRLContent.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRLContent.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

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
