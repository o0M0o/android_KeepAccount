package wxm.KeepAccount.ui.base.Switcher;

import java.util.ArrayList;

/**
 * for page switch
 * Created by WangXM on 2018/3/25.
 */
public class PageSwitcher {
    private final static int INVALID_POS = -1;

    /**
     * holder for selector
     */
    class pageHelper    {
        Object          mRLSelector;
        int             mSelfIdx;

        Runnable        mRAEnable;
        Runnable        mRADisable;
    }

    private ArrayList<pageHelper>   mPHHelper;
    private int                     mIdxHot;

    public PageSwitcher()   {
        mPHHelper = new ArrayList<>();
        mIdxHot = INVALID_POS;
    }

    /**
     * add selector
     * @param oj    selector object
     * @param ea    run when doSelect
     * @param da    run when unselected
     */
    public void addSelector(Object oj, Runnable ea, Runnable da)    {
        da.run();

        pageHelper ph = new pageHelper();
        ph.mRLSelector = oj;
        ph.mSelfIdx = mPHHelper.size();

        ph.mRAEnable = ea;
        ph.mRADisable = da;

        mPHHelper.add(ph);
    }

    /**
     * select an selector
     * do nothing if selector is selected
     * @param oj    selector object
     */
    public void doSelect(Object oj)  {
        pageHelper cph = null;
        for(pageHelper ph : mPHHelper)    {
            if(ph.mRLSelector == oj)    {
                cph = ph;
                break;
            }
        }

        if(null == cph || mIdxHot == cph.mSelfIdx)
            return;

        int old_hot = mIdxHot;
        mIdxHot = cph.mSelfIdx;
        if(INVALID_POS != old_hot) {
            mPHHelper.get(old_hot).mRADisable.run();
        }

        cph.mRAEnable.run();
    }

    /**
     * get object for selected
     * @return      object that selected else null
     */
    public Object getSelected() {
        return INVALID_POS == mIdxHot ? null : mPHHelper.get(mIdxHot).mRLSelector;
    }
}


