package wxm.KeepAccount.ui.data.show.note.ShowData;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilitySupportBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;

/**
 * viewhelper基础类
 * Created by 123 on 2016/9/14.
 */
public abstract class ShowViewHelperBase
    extends FrgUtilitySupportBase   {

    protected String    LOG_TAG = "ShowViewHelperBase";

    // 视图过滤数据
    protected boolean                       mBFilter;
    protected final LinkedList<String>      mFilterPara;

    protected final Timestamp mTSLastLoadViewTime = new Timestamp(0);

    @BindView(R.id.rl_attach_button)
    RelativeLayout  mRLAttachButton;

    /*
    @BindView(R.id.bt_giveup)
    ImageButton     mIBGiveup;

    @BindView(R.id.bt_accpet)
    ImageButton     mIBAccpet;
    */

    @BindView(R.id.rl_accpet_giveup)
    RelativeLayout  mRLAccpetGiveup;

    @BindView(R.id.bt_giveup_filter)
    ImageButton     mIBFilter;

    @BindView(R.id.rl_filter)
    RelativeLayout  mRLFilter;

    protected ShowViewHelperBase()   {
        mBFilter        = false;
        mFilterPara     = new LinkedList<>();
    }

    /**
     * 过滤视图
     * @param ls_tag   过滤参数 :
     *                 1. 如果为null则不过滤
     *                 2. 如果不为null, 但为空则过滤（不显示任何数据)
     */
    public abstract void filterView(List<String> ls_tag);


    /**
     * 更新视图
     */
    protected void refreshView()    {
        initUiInfo();
    }

    /**
     * 更新数据
     */
    @CallSuper
    protected void refreshData()    {
        mTSLastLoadViewTime.setTime(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * 取消过滤
     */
    protected void giveUpFilter()   {
        mBFilter = false;
        refreshView();
    }


    /**
     * 获取视图所在的activity
     * @return  若成功返回activity，失败返回null;
     */
    protected ACNoteShow getRootActivity()  {
        Context ct = getContext();
        if(ct instanceof ACNoteShow) {
            return UtilFun.cast(ct);
        }

        return null;
    }

    /**
     * 设置附加layout可见性
     * @param visible  若为 :
     *                  1. {@code View.GONE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    protected void setAttachLayoutVisible(int visible)   {
        mRLAttachButton.setVisibility(visible);
    }


    /**
     * 设置过滤layout可见性
     * @param visible  若为 :
     *                  1. {@code View.GONE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    protected void setFilterLayoutVisible(int visible)   {
        if(View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        mRLFilter.setVisibility(visible);
        mIBFilter.setOnClickListener(v -> giveUpFilter());
    }


    /**
     * 设置接受-放弃layout可见性
     * @param visible  若为 :
     *                  1. {@code View.GONE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    protected void setAccpetGiveupLayoutVisible(int visible)   {
        if(View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        mRLAccpetGiveup.setVisibility(visible);
    }
}
