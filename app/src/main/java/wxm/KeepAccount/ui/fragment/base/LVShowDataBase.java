package wxm.KeepAccount.ui.fragment.base;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * sub class for listview
 * Created by wxm on 2016/9/29.
 */
public abstract class LVShowDataBase extends ShowViewHelperBase {
    private final static String TAG = "LVShowDataBase";

    /// list item data begin
    protected final static String K_TITLE     = "k_title";
    protected final static String K_ABSTRACT  = "k_abstract";
    protected final static String K_TIME      = "k_time";
    protected final static String K_BUDGET    = "k_budget";
    protected final static String K_AMOUNT    = "k_amount";
    protected final static String K_TAG       = "k_tag";
    protected final static String K_ID        = "k_id";
    protected final static String K_TYPE      = "k_type";

    protected final static String K_MONTH             = "k_month";
    protected final static String K_DAY_NUMEBER       = "k_d_number";
    protected final static String K_DAY_IN_WEEK       = "k_d_in_week";
    protected final static String K_YEAR_PAY_COUNT       = "k_ypc";
    protected final static String K_YEAR_PAY_AMOUNT      = "k_ypa";
    protected final static String K_YEAR_INCOME_COUNT    = "k_yic";
    protected final static String K_YRAR_INCOME_AMOUNT   = "k_yia";
    protected final static String K_DAY_PAY_COUNT       = "k_dpc";
    protected final static String K_DAY_PAY_AMOUNT      = "k_dpa";
    protected final static String K_DAY_INCOME_COUNT    = "k_dic";
    protected final static String K_DAY_INCOME_AMOUNT   = "k_dia";
    protected final static String K_MONTH_PAY_COUNT     = "k_mpc";
    protected final static String K_MONTH_PAY_AMOUNT    = "k_mpa";
    protected final static String K_MONTH_INCOME_COUNT   = "k_mic";
    protected final static String K_MONTH_INCOME_AMOUNT  = "k_mia";

    protected final static String K_SHOW           = "k_show";
    protected final static String V_SHOW_UNFOLD    = "vs_unfold";
    protected final static String V_SHOW_FOLD      = "vs_fold";
    /// list item data end

    private final static String[] DAY_IN_WEEK = {
            "星期日", "星期一", "星期二","星期三",
            "星期四","星期五","星期六"};

    // 视图数据
    protected final LinkedList<HashMap<String, String>>                     mMainPara;
    protected final HashMap<String, LinkedList<HashMap<String, String>>>    mHMSubPara;

    // 存放展开节点的数据
    private final LinkedList<String>    mUnfoldItems;

    public LVShowDataBase()   {
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

            //Log.i(TAG, "addUnfoldItem, tag = " + tag);
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


    /**
     * 返回“星期*"
     * @param dw    0-6格式的星期数
     * @return  星期*
     */
    protected String getDayInWeek(int dw) {
        dw--;
        return DAY_IN_WEEK[dw];
    }
}
