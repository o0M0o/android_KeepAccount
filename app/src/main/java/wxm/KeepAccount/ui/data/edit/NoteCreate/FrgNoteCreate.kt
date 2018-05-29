package wxm.KeepAccount.ui.data.edit.NoteCreate


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.base.Helper.ResourceHelper
import wxm.KeepAccount.ui.base.Switcher.PageSwitcher
import wxm.KeepAccount.ui.data.edit.NoteEdit.utility.PageIncomeEdit
import wxm.KeepAccount.ui.data.edit.NoteEdit.utility.PagePayEdit
import wxm.KeepAccount.ui.data.edit.base.IEdit
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.log.TagLog
import java.util.*

/**
 * UI for add note
 * Created by WangXM on 2016/11/29.
 */
class FrgNoteCreate : FrgSupportBaseAdv() {
    private val mVPPager: ViewPager by bindView(R.id.vp_pages)
    private val mRLPay: RelativeLayout by bindView(R.id.rl_pay)
    private val mRLIncome: RelativeLayout by bindView(R.id.rl_income)

    // for helper data
    private val mSWer: PageSwitcher = PageSwitcher()

    override fun getLayoutID(): Int {
        return R.layout.vw_note_add
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(savedInstanceState: Bundle?) {
        if (null == savedInstanceState) {
            // for vp
            val ac = activity as AppCompatActivity
            val adapter = PagerAdapter(ac.supportFragmentManager)
            (adapter.getItem(POS_PAY) as IEdit).setEditData(PayNoteItem())
            (adapter.getItem(POS_INCOME) as IEdit).setEditData(IncomeNoteItem())
            mVPPager.adapter = adapter

            mRLIncome.setOnClickListener { _ -> mSWer.doSelect(mRLIncome) }
            mRLPay.setOnClickListener { _ -> mSWer.doSelect(mRLPay) }

            mSWer.addSelector(mRLPay,
                    {
                        mRLPay.setBackgroundResource(R.drawable.rl_item_left)
                        (mRLPay.findViewById<View>(R.id.tv_tag) as TextView)
                                .setTextColor(ResourceHelper.mCRTextWhite)

                        if (mVPPager.currentItem != POS_PAY)
                            mVPPager.currentItem = POS_PAY
                    },
                    {
                        mRLPay.setBackgroundResource(R.drawable.rl_item_left_nosel)
                        (mRLPay.findViewById<View>(R.id.tv_tag) as TextView)
                                .setTextColor(ResourceHelper.mCRTextFit)
                    })
            mSWer.addSelector(mRLIncome,
                    {
                        mRLIncome.setBackgroundResource(R.drawable.rl_item_right)
                        (mRLIncome.findViewById<View>(R.id.tv_tag) as TextView)
                                .setTextColor(ResourceHelper.mCRTextWhite)

                        if (mVPPager.currentItem != POS_INCOME)
                            mVPPager.currentItem = POS_INCOME
                    },
                    {
                        mRLIncome.setBackgroundResource(R.drawable.rl_item_right_nosel)
                        (mRLIncome.findViewById<View>(R.id.tv_tag) as TextView)
                                .setTextColor(ResourceHelper.mCRTextFit)
                    })

            mVPPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    TagLog.i("in onPageScrollStateChanged, state=$state")
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    TagLog.i("in onPageScrolled, position=$position, " +
                            "positionOffset=$positionOffset, positionOffsetPixels=$positionOffsetPixels")
                    if(0 == positionOffsetPixels)   {
                        when(position) {
                            POS_PAY -> mSWer.doSelect(mRLPay)
                            POS_INCOME -> mSWer.doSelect(mRLIncome)
                        }
                    }
                }

                override fun onPageSelected(position: Int) {
                    TagLog.i( "in onPageSelected, state=$position")
                }
            })
        }

        loadUI(savedInstanceState)
    }

    fun onAccept(): Boolean {
        val ob = mSWer.selected ?: return false

        val pa = mVPPager.adapter as PagerAdapter
        val tb = (pa.getItem(if (ob === mRLPay) POS_PAY else POS_INCOME)) as IEdit
        return tb.onAccept()
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

            val tp = PagePayEdit()
            tp.arguments = bd
            mALFra.add(tp)

            val ti = PageIncomeEdit()
            ti.arguments = bd
            mALFra.add(ti)
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
