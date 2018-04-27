package wxm.KeepAccount.ui.base.Switcher

import java.util.*

/**
 * for page switch
 * Created by WangXM on 2018/3/25.
 */
class PageSwitcher {
    private val mPHHelper: ArrayList<PageHelper> = ArrayList()
    private var mIdxHot: Int = INVALID_POS

    /**
     * get object for selected
     * @return      object that selected else null
     */
    val selected: Any?
        get() = if (INVALID_POS == mIdxHot) null else mPHHelper[mIdxHot].mRLSelector

    /**
     * holder for selector
     */
    data class PageHelper(val mRLSelector: Any,
                          val mRAEnable: () -> Unit, val mRADisable: () -> Unit)

    /**
     * add selector
     * @param oj    selector object
     * @param ea    run when doSelect
     * @param da    run when unselected
     */
    fun addSelector(oj: Any, ea: () -> Unit, da: () -> Unit) {
        da()
        mPHHelper.add(PageHelper(oj, ea, da))
    }

    /**
     * select an selector
     * do nothing if selector is selected
     * @param oj    selector object
     */
    fun doSelect(oj: Any) {
        mPHHelper.find { it.mRLSelector === oj && mIdxHot != mPHHelper.indexOf(it) }?.let {
            if (INVALID_POS != mIdxHot) {
                mPHHelper[mIdxHot].mRADisable()
            }

            it.mRAEnable()
            mIdxHot = mPHHelper.indexOf(it)
        }
    }

    companion object {
        private const val INVALID_POS = -1
    }
}


