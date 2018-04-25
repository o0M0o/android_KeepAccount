package wxm.KeepAccount.ui.data.edit.NoteEdit

import android.os.Bundle
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.define.BudgetItem
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.define.PayNoteItem
import wxm.KeepAccount.ui.data.edit.NoteEdit.utility.*
import wxm.KeepAccount.ui.data.edit.base.*
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv

/**
 * @author      WangXM
 * @version     create：2018/4/25
 */
class FrgNoteEdit :  FrgEditBase() {
    private val mVPPages: ViewPager by bindView(R.id.vp_pages)
    private var mCurData: Any? = null
    private var mCurDataType: String? = null

    override fun isEditStatus(): Boolean {
        return mVPPages.currentItem == PAGE_IDX_EDIT
    }

    override fun isPreviewStatus(): Boolean {
        return mVPPages.currentItem == PAGE_IDX_PREVIEW
    }

    override fun setCurData(type: String,  obj: Any?) {
        mCurDataType = type
        mCurData = obj
    }

    override fun getCurData(): Any {
        return mCurData!!
    }

    override fun onAccept(): Boolean {
        if(isPreviewStatus())
            return true

        val pa = mVPPages.adapter as PagerAdapter
        return (pa.getItem(PAGE_IDX_EDIT) as IEdit).onAccept()
    }

    override fun toPreviewStatus() {
        mVPPages.currentItem = PAGE_IDX_PREVIEW
    }

    override fun toEditStatus() {
        mVPPages.currentItem = PAGE_IDX_EDIT
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun getLayoutID(): Int {
        return R.layout.vw_viewpage
    }

    override fun initUI(bundle: Bundle?) {
        if(null == bundle) {
            val adapter = PagerAdapter(fragmentManager)
            mVPPages.adapter = adapter

            val idx = if(null == mCurData)  PAGE_IDX_EDIT  else PAGE_IDX_PREVIEW
            if(null == mCurData) {
                mCurData = when (mCurDataType!!) {
                    GlobalDef.STR_RECORD_INCOME -> {
                        IncomeNoteItem()
                    }

                    GlobalDef.STR_RECORD_PAY -> {
                        PayNoteItem()
                    }

                    else -> {
                        BudgetItem()
                    }
                }
            }

            (adapter.getItem(PAGE_IDX_PREVIEW) as IPreview).setPreviewData(mCurData!!)
            (adapter.getItem(PAGE_IDX_EDIT) as IEdit).setEditData(mCurData!!)
            mVPPages.currentItem = idx
        }

        loadUI(bundle)
    }


    /**
     * fragment adapter
     */
    inner class PagerAdapter
        internal constructor(fm: FragmentManager)
            : FragmentStatePagerAdapter(fm) {
        private var mNumOfFrags: Int = 0
        private val mFRFrags: Array<FrgSupportBaseAdv>

        init {
            mNumOfFrags = PAGE_COUNT
            mFRFrags = when (mCurDataType!!) {
                GlobalDef.STR_RECORD_INCOME -> {
                    arrayOf(PageIncomePreview(), PageIncomeEdit())
                }

                GlobalDef.STR_RECORD_PAY -> {
                    arrayOf(TFPreviewPay(), PagePayEdit())
                }

                else -> {
                    arrayOf(PageBudgetPreview(), PageBudgetEdit())
                }
            }
        }

        override fun getItem(position: Int): FrgSupportBaseAdv {
            return mFRFrags[position]
        }

        override fun getCount(): Int {
            return mNumOfFrags
        }
    }

    companion object {
        private const val PAGE_COUNT = 2
        private const val PAGE_IDX_PREVIEW = 0
        private const val PAGE_IDX_EDIT = 1
    }
}