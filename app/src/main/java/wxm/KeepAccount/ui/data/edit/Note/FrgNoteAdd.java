package wxm.KeepAccount.ui.data.edit.Note;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import wxm.KeepAccount.ui.base.Switcher.PageSwitcher;
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.base.Helper.ResourceHelper;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditIncome;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditPay;
import wxm.KeepAccount.ui.data.edit.base.TFEditBase;

/**
 * UI for add note
 * Created by WangXM on 2016/11/29.
 */
public class FrgNoteAdd extends FrgSupportBaseAdv {
    protected final static int POS_PAY = 0;
    protected final static int POS_INCOME = 1;

    @BindView(R.id.cl_header)
    ConstraintLayout mCLHeader;

    @BindView(R.id.vp_pages)
    ViewPager mVPPager;

    @BindView(R.id.rl_pay)
    RelativeLayout mRLPay;

    @BindView(R.id.rl_income)
    RelativeLayout mRLIncome;

    // for helper data
    PageSwitcher mSWer;

    @Override
    protected int getLayoutID() {
        return R.layout.vw_note_add;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {
        // for vp
        AppCompatActivity ac = (AppCompatActivity) getActivity();
        PagerAdapter adapter = new PagerAdapter(ac.getSupportFragmentManager());
        mVPPager.setAdapter(adapter);

        mRLIncome.setOnClickListener(v -> {
            mSWer.doSelect(mRLIncome);
        });

        mRLPay.setOnClickListener(v -> {
            mSWer.doSelect(mRLPay);
        });

        mSWer = new PageSwitcher();
        mSWer.addSelector(mRLPay,
                () -> {
                    mRLPay.setBackgroundResource(R.drawable.rl_item_left);
                    ((TextView)mRLPay.findViewById(R.id.tv_tag))
                            .setTextColor(ResourceHelper.mCRTextWhite);

                    mVPPager.setCurrentItem(POS_PAY);
                },
                () -> {
                    mRLPay.setBackgroundResource(R.drawable.rl_item_left_nosel);
                    ((TextView)mRLPay.findViewById(R.id.tv_tag))
                            .setTextColor(ResourceHelper.mCRTextFit);
                });
        mSWer.addSelector(mRLIncome,
                () -> {
                    mRLIncome.setBackgroundResource(R.drawable.rl_item_right);
                    ((TextView)mRLIncome.findViewById(R.id.tv_tag))
                            .setTextColor(ResourceHelper.mCRTextWhite);

                    mVPPager.setCurrentItem(POS_INCOME);
                },
                () -> {
                    mRLIncome.setBackgroundResource(R.drawable.rl_item_right_nosel);
                    ((TextView)mRLIncome.findViewById(R.id.tv_tag))
                            .setTextColor(ResourceHelper.mCRTextFit);
                });

        mSWer.doSelect(mRLPay);
    }

    public boolean onAccept() {
        Object ob = mSWer.getSelected();
        if(null == ob)
            return false;

        PagerAdapter pa = UtilFun.cast(mVPPager.getAdapter());
        TFEditBase tb = UtilFun.cast(pa.getItem(ob == mRLPay ? POS_PAY : POS_INCOME));
        return tb.onAccept();
    }

    /// PRIVATE BEGIN
    /// PRIVATE END

    /**
     * fragment adapter
     */
    private class PagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> mALFra;

        PagerAdapter(FragmentManager fm) {
            super(fm);
            mALFra = new ArrayList<>();
            Bundle bd = getArguments();

            TFEditPay tp = new TFEditPay();
            tp.setCurData(GlobalDef.STR_CREATE, null);
            tp.setArguments(bd);
            mALFra.add(tp);

            TFEditIncome ti = new TFEditIncome();
            ti.setCurData(GlobalDef.STR_CREATE, null);
            ti.setArguments(bd);
            mALFra.add(ti);
        }

        @Override
        public Fragment getItem(int position) {
            return mALFra.get(position);
        }

        @Override
        public int getCount() {
            return mALFra.size();
        }
    }
}
