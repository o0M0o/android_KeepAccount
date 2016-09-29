package wxm.KeepAccount.ui.fragment.ListView;

import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;

import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;

/**
 * sub class for listview
 * Created by wxm on 2016/9/29.
 */
abstract class ListViewBase extends ShowViewHelperBase {
    private final static String TAG = "ListViewBase";

    final static String MPARA_TITLE      = "MPARA_TITLE";
    final static String MPARA_ABSTRACT   = "MPARA_ABSTRACT";
    final static String MPARA_TAG        = "MPARA_TAG";
    final static String SPARA_TITLE  = "SPARA_TITLE";
    final static String SPARA_DETAIL = "SPARA_DETAIL";
    final static String SPARA_TAG    = "SPARA_TAG";
    final static String SPARA_ID     = "SPARA_ID";
    final static String MPARA_SHOW           = "MPARA_SHOW";
    final static String MPARA_SHOW_UNFOLD    = "SHOW_UNFOLD";
    final static String MPARA_SHOW_FOLD      = "SHOW_FOLD";
    final static String SPARA_TAG_PAY    = "TAG_PAY";
    final static String SPARA_TAG_INCOME = "TAG_INCOME";

    // 视图数据
    final LinkedList<HashMap<String, String>>                     mMainPara;
    final HashMap<String, LinkedList<HashMap<String, String>>>    mHMSubPara;

    // 存放展开节点的数据
    private final LinkedList<String>    mUnfoldItems;

    ListViewBase()   {
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
}
