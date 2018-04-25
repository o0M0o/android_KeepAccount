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

import java.util.ArrayList

import wxm.KeepAccount.ui.base.Switcher.PageSwitcher
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.Helper.ResourceHelper
import wxm.KeepAccount.ui.data.edit.NoteEdit.utility.PageIncomeEdit
import wxm.KeepAccount.ui.data.edit.NoteEdit.utility.PagePayEdit
import wxm.KeepAccount.ui.data.edit.base.TFEditBase

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
        if(null == savedInstanceState) {
            // for vp
            val ac = activity as AppCompatActivity
            val adapter = PagerAdapter(ac.supportFragmentManager)
            mVPPager.adapter = adapter

            mRLIncome.setOnClickListener { _ -> mSWer.doSelect(mRLIncome) }

            mRLPay.setOnClickListener { _ -> mSWer.doSelect(mRLPay) }

            mSWer.addSelector(mRLPay,
                    Runnable {
                        mRLPay.setBackgroundResource(R.drawable.rl_item_left)
                        (mRLPay.findViewById<View>(R.id.tv_tag) as TextView)
                                .setTextColor(ResourceHelper.mCRTextWhite)

                        mVPPager.currentItem = POS_PAY
                    },
                    Runnable {
                        mRLPay.setBackgroundResource(R.drawable.rl_item_left_nosel)
                        (mRLPay.findViewById<View>(R.id.tv_tag) as TextView)
                                .setTextColor(ResourceHelper.mCRTextFit)
                    })
            mSWer.addSelector(mRLIncome,
                    Runnable {
                        mRLIncome.setBackgroundResource(R.drawable.rl_item_right)
                        (mRLIncome.findViewById<View>(R.id.tv_tag) as TextView)
                                .setTextColor(ResourceHelper.mCRTextWhite)

                        mVPPager.currentItem = POS_INCOME
                    },
                    Runnable {
                        mRLIncome.setBackgroundResource(R.drawable.rl_item_right_nosel)
                        (mRLIncome.findViewById<View>(R.id.tv_tag) as TextView)
                                .setTextColor(ResourceHelper.mCRTextFit)
                    })

            mSWer.doSelect(mRLPay)
        }
    }

    fun onAccept(): Boolean {
        val ob = mSWer.selected ?: return false

        val pa = UtilFun.cast<PagerAdapter>(mVPPager.adapter)
        val tb = UtilFun.cast<TFEditBase>(pa.getItem(if (ob === mRLPay) POS_PAY else POS_INCOME))
        return tb.onAccept()
    }

    /// PRIVATE BEGIN
    /// PRIVATE END

    /**
     * fragment adapter
     */
    private inner class PagerAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
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

    companion object {
        private const val POS_PAY = 0
        private const val POS_INCOME = 1
    }
}
