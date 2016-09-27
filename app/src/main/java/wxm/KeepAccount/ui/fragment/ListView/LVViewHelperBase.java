package wxm.KeepAccount.ui.fragment.ListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;

/**
 * viewhelper基础类
 * Created by 123 on 2016/9/14.
 */
public abstract class LVViewHelperBase implements View.OnClickListener {
    private final static String TAG = "LVViewHelperBase";
    View      mSelfView;

    // 视图数据
    final LinkedList<HashMap<String, String>>                       mMainPara;
    final HashMap<String, LinkedList<HashMap<String, String>>>      mHMSubPara;

    // 视图过滤数据
    boolean               mBFilter;
    final LinkedList<String>    mFilterPara;

    // 存放展开节点的数据
    private final LinkedList<String>    mUnfoldItems;

    LVViewHelperBase()   {
        mBFilter        = false;
        mSelfView       = null;
        mMainPara       = new LinkedList<>();
        mHMSubPara      = new HashMap<>();
        mFilterPara     = new LinkedList<>();
        mUnfoldItems    = new LinkedList<>();
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
    public abstract void loadView();

    /**
     * 检查数据是否更新，然后决定是否重新加载视图
     */
    public abstract void checkView();

    /**
     * 过滤视图
     * @param ls_tag   过滤参数 :
     *                 1. 如果为null则不过滤
     *                 2. 如果不为null, 但为空则过滤（不显示任何数据)
     */
    public abstract void filterView(List<String> ls_tag);

    /**
     * 处理activity返回结果
     * @param requestCode   返回参数
     * @param resultCode    返回参数
     * @param data          返回参数
     */
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);


    /**
     * 添加一个展开节点
     * 只记录20个展开节点, 超过数量后将移除最早记录的节点
     * @param tag   展开节点tag
     */
    void addUnfoldItem(String tag)   {
        if(!mUnfoldItems.contains(tag)) {
            if(20 < mUnfoldItems.size())
                mUnfoldItems.removeFirst();

            Log.i(TAG, "addUnfoldItem, tag = " + tag);
            mUnfoldItems.addLast(tag);
        }
    }

    /**
     * 移除一个展开节点
     * @param tag   移除节点tag
     */
    void removeUnfoldItem(String tag)   {
        mUnfoldItems.remove(tag);
    }


    /**
     * 检查一个节点是否是展开节点
     * @param tag   待检查节点tag
     * @return  如果此节点是展开节点，返回true, 否则返回false
     */
    boolean checkUnfoldItem(String tag)  {
        return mUnfoldItems.contains(tag);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        switch (vid)    {
            case R.id.bt_giveup_filter :
                mBFilter = false;
                refreshView();
                break;
        }
    }

    /**
     * 仅更新视图
     */
    protected abstract void refreshView();

    /**
     * 获取视图所在的activity
     * @return  若成功返回activity，失败返回null;
     */
    ACNoteShow getRootActivity()  {
        Context ct = mSelfView.getContext();
        if(ct instanceof Activity) {
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
    void setAttachLayoutVisible(int visible)   {
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
    void setFilterLayoutVisible(int visible)   {
        if(View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        RelativeLayout rl = UtilFun.cast(mSelfView.findViewById(R.id.rl_filter));
        assert null != rl;
        setLayoutVisible(rl, visible);

        Button bt = UtilFun.cast(rl.findViewById(R.id.bt_giveup_filter));
        assert null != bt;
        bt.setOnClickListener(this);
    }


    /**
     * 设置接受-放弃layout可见性
     * @param visible  若为 :
     *                  1. {@code View.INVISIBLE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    void setAccpetGiveupLayoutVisible(int visible)   {
        if(View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        RelativeLayout rl = UtilFun.cast(mSelfView.findViewById(R.id.rl_accpet_giveup));
        assert null != rl;
        setLayoutVisible(rl, visible);

        Button bt = UtilFun.cast(rl.findViewById(R.id.bt_giveup));
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
    private void setLayoutVisible(RelativeLayout rl, int visible)    {
        int w = RelativeLayout.LayoutParams.MATCH_PARENT;
        int h = 0;
        if(View.INVISIBLE != visible)
            h = RelativeLayout.LayoutParams.WRAP_CONTENT;

        ViewGroup.LayoutParams param = rl.getLayoutParams();
        param.width = w;
        param.height = h;
        rl.setLayoutParams(param);
    }
}
