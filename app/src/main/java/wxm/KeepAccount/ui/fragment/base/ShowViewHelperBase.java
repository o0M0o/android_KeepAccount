package wxm.KeepAccount.ui.fragment.base;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;

/**
 * viewhelper基础类
 * Created by 123 on 2016/9/14.
 */
public abstract class ShowViewHelperBase implements View.OnClickListener {
    protected String    LOG_TAG = "ShowViewHelperBase";
    protected View      mSelfView;

    // 视图过滤数据
    protected boolean                       mBFilter;
    protected final LinkedList<String>      mFilterPara;

    protected final Timestamp mTSLastLoadViewTime = new Timestamp(0);

    protected ShowViewHelperBase()   {
        mBFilter        = false;
        mSelfView       = null;
        mFilterPara     = new LinkedList<>();
    }

    /**
     *  创建视图
     * @param inflater      视图加载参数
     * @param container     视图所在group参数
     * @return              若成功，返回所创建视图
     */
    public abstract View createView(LayoutInflater inflater, ViewGroup container);

    /**
     * 获得视图
     * @return   返回已经创建的视图
     */
    public View getView()   {
        return mSelfView;
    }

    /**
     * 加载视图
     * 在这里进行视图初始化
     */
    @CallSuper
    public void loadView()  {
        Log.d(LOG_TAG, "loadView");
    }


    /**
     * 检查数据是否更新，然后决定是否重新加载视图
    public abstract void checkView();
     */

    /**
     * 过滤视图
     * @param ls_tag   过滤参数 :
     *                 1. 如果为null则不过滤
     *                 2. 如果不为null, 但为空则过滤（不显示任何数据)
     */
    public abstract void filterView(List<String> ls_tag);

    /**
     * 数据变化后调用
     */
    public abstract void onDataChange();


    /**
     * 仅更新视图
     */
    protected abstract void refreshView();

    /**
     * 取消过滤
     */
    protected void giveupFilter()   {
        mBFilter = false;
        refreshView();
    }

    /**
     * 处理“取消过滤”事件
     * @param v  param
     */
    @Override
    public void onClick(View v) {
        int vid = v.getId();
        switch (vid)    {
            case R.id.bt_giveup_filter :
                giveupFilter();
                break;
        }
    }


    /**
     * 获取视图所在的activity
     * @return  若成功返回activity，失败返回null;
     */
    protected ACNoteShow getRootActivity()  {
        Context ct = mSelfView.getContext();
        if(ct instanceof ACNoteShow) {
            return UtilFun.cast(ct);
        }

        return null;
    }

    /**
     * 设置附加layout可见性
     * @param visible  若为 :
     *                  1. {@code View.INVISIBLE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    protected void setAttachLayoutVisible(int visible)   {
        RelativeLayout rl = UtilFun.cast(mSelfView.findViewById(R.id.rl_attach_button));
        assert null != rl;
        setLayoutVisible(rl, visible);
    }


    /**
     * 设置过滤layout可见性
     * @param visible  若为 :
     *                  1. {@code View.INVISIBLE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    protected void setFilterLayoutVisible(int visible)   {
        if(View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        RelativeLayout rl = UtilFun.cast(mSelfView.findViewById(R.id.rl_filter));
        assert null != rl;
        setLayoutVisible(rl, visible);

        ImageButton bt = UtilFun.cast(rl.findViewById(R.id.bt_giveup_filter));
        assert null != bt;
        bt.setOnClickListener(this);
    }


    /**
     * 设置接受-放弃layout可见性
     * @param visible  若为 :
     *                  1. {@code View.INVISIBLE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    protected void setAccpetGiveupLayoutVisible(int visible)   {
        if(View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        RelativeLayout rl = UtilFun.cast(mSelfView.findViewById(R.id.rl_accpet_giveup));
        assert null != rl;
        setLayoutVisible(rl, visible);

        ImageButton bt = UtilFun.cast(rl.findViewById(R.id.bt_giveup));
        assert null != bt;
        bt.setOnClickListener(this);

        bt = UtilFun.cast(rl.findViewById(R.id.bt_accpet));
        assert null != bt;
        bt.setOnClickListener(this);
    }

    /**
     * 设置layout可见性
     * 仅调整可见性，其它设置保持不变
     * @param visible  若为 :
     *                  1. {@code View.INVISIBLE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    protected void setLayoutVisible(ViewGroup rl, int visible)    {
        ViewGroup.LayoutParams param = rl.getLayoutParams();
        //param.width = rl.getWidth();
        param.width = ViewGroup.LayoutParams.MATCH_PARENT;
        param.height = View.INVISIBLE == visible ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT;
        rl.setLayoutParams(param);
    }
}
