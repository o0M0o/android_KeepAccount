package wxm.KeepAccount.ui.fragment.base;

import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * sub class for listview
 * Created by wxm on 2016/9/29.
 */
public abstract class ListViewBase extends ShowViewHelperBase {
    private final static String TAG = "ListViewBase";

    public final static String MPARA_TITLE      = "MPARA_TITLE";
    public final static String MPARA_ABSTRACT   = "MPARA_ABSTRACT";
    public final static String MPARA_TAG        = "MPARA_TAG";
    public final static String SPARA_TITLE  = "SPARA_TITLE";
    public final static String SPARA_DETAIL = "SPARA_DETAIL";
    public final static String SPARA_TAG    = "SPARA_TAG";
    public final static String SPARA_ID     = "SPARA_ID";
    public final static String MPARA_SHOW           = "MPARA_SHOW";
    public final static String MPARA_SHOW_UNFOLD    = "SHOW_UNFOLD";
    public final static String MPARA_SHOW_FOLD      = "SHOW_FOLD";
    public final static String SPARA_TAG_PAY    = "TAG_PAY";
    public final static String SPARA_TAG_INCOME = "TAG_INCOME";

    // 视图数据
    protected final LinkedList<HashMap<String, String>>                     mMainPara;
    protected final HashMap<String, LinkedList<HashMap<String, String>>>    mHMSubPara;

    // 存放展开节点的数据
    private final LinkedList<String>    mUnfoldItems;

    public ListViewBase()   {
        super();
        mMainPara       = new LinkedList<>();
        mHMSubPara      = new HashMap<>();
        mUnfoldItems    = new LinkedList<>();
    }

    /**
     * 添加一个展开节点
     * 只记录20个展开节点, 超过数量后将移除最早记录的节点
     * @param tag   展开节点tag
     */
    protected void addUnfoldItem(String tag)   {
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
    protected void removeUnfoldItem(String tag)   {
        mUnfoldItems.remove(tag);
    }


    /**
     * 检查一个节点是否是展开节点
     * @param tag   待检查节点tag
     * @return  如果此节点是展开节点，返回true, 否则返回false
     */
    protected boolean checkUnfoldItem(String tag)  {
        return mUnfoldItems.contains(tag);
    }
}
