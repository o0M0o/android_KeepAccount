package wxm.KeepAccount.ui.data.show.note.ListView;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wxm.KeepAccount.R;
import wxm.androidutil.util.UtilFun;

/**
 * helper for action layout
 * Created by ookoo on 2018/3/26.
 */
public abstract class ActionHelper {
    @BindView(R.id.iv_show_tag)
    ImageView mIVHideShow;

    @BindView(R.id.rl_hide_show)
    RelativeLayout mRLHideShow;

    @BindDrawable(R.drawable.ic_to_left)
    Drawable mDAExpand;

    @BindDrawable(R.drawable.ic_to_right)
    Drawable mDAHide;

    @BindView(R.id.rl_acts)
    RelativeLayout mRLActions;

    @BindView(R.id.rl_action)
    RelativeLayout mRLAction;

    @BindView(R.id.rl_lv_note)
    RelativeLayout mRLLVNote;

    private boolean  mIsShow;

    ActionHelper()    {
        mIsShow = true;
    }

    /**
     * bind layout for ButterKnife
     * @param holder        father view
     */
    public void bind(View holder)   {
        ButterKnife.bind(this, holder);
    }

    /**
     * switch bottom action bar show/hide
     * @param v         param
     */
    @OnClick(R.id.rl_hide_show)
    public void switchActionHideShow(View v) {
        setActsVisibility(!mIsShow);
        mIsShow = !mIsShow;
    }

    /**
     * use this to init self actions
     */
    protected abstract void initActs();

    /**
     * invoke this before use
     */
    protected void init()   {
        setActsVisibility(mIsShow);

        if(mIsShow) {
            initActs();
        }
    }

    /**
     * set visibility for self
     * @param bshow     show UI if true
     */
    private void setActsVisibility(boolean bshow)   {
        ViewGroup.LayoutParams rp = mRLActions.getLayoutParams();

        mIVHideShow.setImageDrawable(bshow ? mDAHide : mDAExpand);
        mRLHideShow.getBackground().setAlpha(bshow ? 255 : 40);
        mRLAction.getBackground().setAlpha(bshow ? 255 : 0);

        rp.width = bshow ? ViewGroup.LayoutParams.MATCH_PARENT : 0;
        mRLActions.setLayoutParams(rp);

        RelativeLayout.LayoutParams rp_lv = UtilFun.cast_t(mRLLVNote.getLayoutParams());
        if (bshow)
            rp_lv.addRule(RelativeLayout.ABOVE, R.id.rl_action);
        else
            rp_lv.removeRule(RelativeLayout.ABOVE);
        mRLLVNote.setLayoutParams(rp_lv);
    }
}
