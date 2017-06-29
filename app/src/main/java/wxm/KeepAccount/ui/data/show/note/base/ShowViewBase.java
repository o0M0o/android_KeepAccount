package wxm.KeepAccount.ui.data.show.note.base;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;

import butterknife.BindView;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilitySupportBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;

/**
 * viewhelper基础类
 * Created by 123 on 2016/9/14.
 */
public abstract class ShowViewBase
        extends FrgUtilitySupportBase {

    // 视图过滤数据
    protected boolean mBFilter;
    protected final LinkedList<String> mFilterPara;
    protected final Timestamp mTSLastLoadViewTime = new Timestamp(0);

    @BindView(R.id.rl_attach_button)
    RelativeLayout mRLAttachButton;

    @BindView(R.id.rl_accpet_giveup)
    RelativeLayout mRLAccept;

    @BindView(R.id.rl_filter)
    RelativeLayout mRLFilter;

    protected ShowViewBase() {
        mBFilter = false;
        mFilterPara = new LinkedList<>();
    }

    @Override
    protected void enterActivity() {
        Log.d(LOG_TAG, "in enterActivity");
        super.enterActivity();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void leaveActivity() {
        Log.d(LOG_TAG, "in leaveActivity");
        EventBus.getDefault().unregister(this);

        super.leaveActivity();
    }

    public void loadView()  {
        refreshData();
    }

    /**
     * 更新数据
     */
    @CallSuper
    protected void refreshData() {
        mTSLastLoadViewTime.setTime(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * 获取视图所在的activity
     *
     * @return 若成功返回activity，失败返回null;
     */
    protected ACNoteShow getRootActivity() {
        Context ct = getContext();
        if (ct instanceof ACNoteShow) {
            return UtilFun.cast(ct);
        }

        return null;
    }

    /**
     * 设置附加layout可见性
     *
     * @param visible 若为 :
     *                1. {@code View.GONE}, 不可见
     *                2. {@code View.VISIBLE}, 可见
     */
    protected void setAttachLayoutVisible(int visible) {
        mRLAttachButton.setVisibility(visible);
    }


    /**
     * 设置过滤layout可见性
     *
     * @param visible 若为 :
     *                1. {@code View.GONE}, 不可见
     *                2. {@code View.VISIBLE}, 可见
     */
    protected void setFilterLayoutVisible(int visible) {
        if (View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        mRLFilter.setVisibility(visible);
    }


    /**
     * 设置接受-放弃layout可见性
     *
     * @param visible 若为 :
     *                1. {@code View.GONE}, 不可见
     *                2. {@code View.VISIBLE}, 可见
     */
    protected void setAccpetGiveupLayoutVisible(int visible) {
        if (View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        mRLAccept.setVisibility(visible);
    }
}
