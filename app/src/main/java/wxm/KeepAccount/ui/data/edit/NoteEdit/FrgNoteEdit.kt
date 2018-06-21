package wxm.KeepAccount.ui.data.edit.NoteEdit

import android.os.Bundle
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.define.*
import wxm.KeepAccount.item.BudgetItem
import wxm.KeepAccount.item.IncomeNoteItem
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.data.edit.NoteEdit.page.*
import wxm.KeepAccount.ui.data.edit.base.*
import wxm.androidutil.improve.let1
import wxm.androidutil.ui.frg.FrgSupportBaseAdv

/**
 * @author      WangXM
 * @version     create：2018/4/25
 */
class FrgNoteEdit :  FrgEditBase() {
    private val mVPPages: ViewPager by bindView(R.id.vp_pages)
    private var mCurData: Any? = null
    private var mCurDataType: String? = null

    override fun getLayoutID(): Int = R.layout.vw_viewpage

    override fun isEditStatus(): Boolean {
        return mVPPages.currentItem == PAGE_IDX_EDIT
    }

    override fun isPreviewStatus(): Boolean {
        return mVPPages.currentItem == PAGE_IDX_PREVIEW
    }

    override fun setCurData(type: String,  obj: Any?) {
        mCurDataType = type
        mCurData = obj

        obj?.let {
            val clone = it as IPublicClone
            mCurData = clone.publicClone()
        }
    }

    override fun getCurData(): Any {
        return mCurData!!
    }

    override fun onAccept(): Boolean {
        if(isPreviewStatus())
            toEditStatus()

        val pa = mVPPages.adapter as PagerAdapter
        return (pa.getItem(PAGE_IDX_EDIT) as IEdit).onAccept()
    }

    override fun toPreviewStatus() {
        if(!isPreviewStatus()) {
            val pa = mVPPages.adapter as PagerAdapter
            (pa.getItem(PAGE_IDX_EDIT) as IEdit).refillData()

            mVPPages.currentItem = PAGE_IDX_PREVIEW
            pa.getItem(PAGE_IDX_PREVIEW).reInitUI()
        }
    }

    override fun toEditStatus() {
        if(!isEditStatus()) {
            mVPPages.currentItem = PAGE_IDX_EDIT
        }
    }


    override fun initUI(bundle: Bundle?) {
        if(null == bundle) {
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

            PagerAdapter(fragmentManager!!).let1 {
                (it.getItem(PAGE_IDX_PREVIEW) as IPreview).setPreviewData(mCurData!!)
                (it.getItem(PAGE_IDX_EDIT) as IEdit).setEditData(mCurData!!)
                mVPPages.adapter = it
                mVPPages.currentItem = idx
            }
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
                    arrayOf(PgIncomePreview(), PgIncomeEdit())
                }

                GlobalDef.STR_RECORD_PAY -> {
                    arrayOf(PgPayPreview(), PgPayEdit())
                }

                else -> {
                    arrayOf(PgBudgetPreview(), PgBudgetEdit())
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