package wxm.KeepAccount.ui.data.edit.NoteCreate


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.flyco.tablayout.SegmentTabLayout
import com.flyco.tablayout.listener.OnTabSelectListener
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.item.IncomeNoteItem
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.data.edit.NoteEdit.page.PgIncomeEdit
import wxm.KeepAccount.ui.data.edit.NoteEdit.page.PgPayEdit
import wxm.KeepAccount.ui.data.edit.base.IEdit
import wxm.KeepAccount.improve.let1
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import java.util.*

/**
 * UI for add note
 * Created by WangXM on 2016/11/29.
 */
class FrgNoteCreate : FrgSupportBaseAdv() {
    private val mVPPager: ViewPager by bindView(R.id.vp_pages)
    private val mTLTab: SegmentTabLayout  by bindView(R.id.tl_type)

    override fun getLayoutID(): Int = R.layout.vw_note_add

    override fun initUI(savedInstanceState: Bundle?) {
        // for vp
        mVPPager.adapter = PagerAdapter(activity!!.supportFragmentManager).apply {
            (getItem(POS_PAY) as IEdit).setEditData(PayNoteItem())
            (getItem(POS_INCOME) as IEdit).setEditData(IncomeNoteItem())
        }

        mTLTab.setTabData(arrayOf(getString(R.string.cn_pay), getString(R.string.cn_income)))
        mTLTab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                mVPPager.currentItem = position
            }

            override fun onTabReselect(position: Int) {
            }
        })

        mVPPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mTLTab.currentTab = position
            }
        })

        loadUI(savedInstanceState)
    }

    fun onAccept(): Boolean {
        return ((mVPPager.adapter as PagerAdapter).getItem(mVPPager.currentItem) as IEdit).onAccept()
    }

    /// PRIVATE BEGIN
    /**
     * fragment adapter
     */
    private inner class PagerAdapter internal constructor(fm: FragmentManager)
        : FragmentStatePagerAdapter(fm) {
        internal var mALFra: ArrayList<Fragment> = ArrayList()

        init {
            val bd = arguments
            PgPayEdit().apply { arguments = bd }.let1 {
                mALFra.add(it)
            }

            PgIncomeEdit().apply { arguments = bd }.let1 {
                mALFra.add(it)
            }
        }

        override fun getItem(position: Int): Fragment {
            return mALFra[position]
        }

        override fun getCount(): Int {
            return mALFra.size
        }
    }
    /// PRIVATE END

    companion object {
        private const val POS_PAY = 0
        private const val POS_INCOME = 1
    }
}
