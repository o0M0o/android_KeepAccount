package wxm.KeepAccount.ui.base.Switcher

import java.util.ArrayList

/**
 * for page switch
 * Created by WangXM on 2018/3/25.
 */
class PageSwitcher {
    private val mPHHelper: ArrayList<PageHelper> = ArrayList()
    private var mIdxHot: Int = 0

    /**
     * get object for selected
     * @return      object that selected else null
     */
    val selected: Any?
        get() = if (INVALID_POS == mIdxHot) null else mPHHelper[mIdxHot].mRLSelector

    /**
     * holder for selector
     */
    internal inner class PageHelper {
        var mRLSelector: Any? = null
        var mSelfIdx: Int = 0

        var mRAEnable: Runnable? = null
        var mRADisable: Runnable? = null
    }

    /**
     * add selector
     * @param oj    selector object
     * @param ea    run when doSelect
     * @param da    run when unselected
     */
    fun addSelector(oj: Any, ea: Runnable, da: Runnable) {
        da.run()

        val ph = PageHelper()
        ph.mRLSelector = oj
        ph.mSelfIdx = mPHHelper.size

        ph.mRAEnable = ea
        ph.mRADisable = da

        mPHHelper.add(ph)
    }

    /**
     * select an selector
     * do nothing if selector is selected
     * @param oj    selector object
     */
    fun doSelect(oj: Any) {
        var cph: PageHelper? = null
        for (ph in mPHHelper) {
            if (ph.mRLSelector === oj) {
                cph = ph
                break
            }
        }

        if (null == cph || mIdxHot == cph.mSelfIdx)
            return

        val oldHot = mIdxHot
        mIdxHot = cph.mSelfIdx
        if (INVALID_POS != oldHot) {
            mPHHelper[oldHot].mRADisable!!.run()
        }

        cph.mRAEnable!!.run()
    }

    companion object {
        private const val INVALID_POS = -1
    }
}


