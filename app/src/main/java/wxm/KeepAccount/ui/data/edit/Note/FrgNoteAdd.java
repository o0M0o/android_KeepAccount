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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.base.ResourceHelper;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditIncome;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditPay;
import wxm.KeepAccount.ui.data.edit.base.TFEditBase;

/**
 * for login
 * Created by ookoo on 2016/11/29.
 */
public class FrgNoteAdd extends FrgUtilityBase {
    protected final static int POS_PAY = 0;
    protected final static int POS_INCOME = 1;

    @BindView(R.id.bt_accept)
    Button mBTAccept;

    @BindView(R.id.cl_header)
    ConstraintLayout mCLHeader;

    @BindView(R.id.vp_pages)
    ViewPager mVPPager;

    @BindView(R.id.rl_pay)
    RelativeLayout mRLPay;

    @BindView(R.id.rl_income)
    RelativeLayout mRLIncome;

    // for helper data
    private class pageHelper    {
        RelativeLayout mRLSelector;
        int  mPageIdx;
    }
    private pageHelper[] mPHHelper;
    private pageHelper mPHHot;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        LOG_TAG = "FrgNoteAdd";
        View rootView = inflater.inflate(R.layout.vw_note_add, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        // for vp
        AppCompatActivity ac = (AppCompatActivity) getActivity();
        PagerAdapter adapter = new PagerAdapter(ac.getSupportFragmentManager());
        mVPPager.setAdapter(adapter);
        mVPPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                enableRLStatus(mPHHelper[position].mRLSelector);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mRLIncome.setOnClickListener(v -> {
            if(!isEnableRL(mRLIncome))  {
                enableRLStatus(mRLIncome);
            }
        });

        mRLPay.setOnClickListener(v -> {
            if(!isEnableRL(mRLPay))  {
                enableRLStatus(mRLPay);
            }
        });

        mPHHelper = new pageHelper[POS_INCOME + 1];
        mPHHelper[POS_PAY] = new pageHelper();
        mPHHelper[POS_PAY].mRLSelector = mRLPay;
        mPHHelper[POS_PAY].mPageIdx = POS_PAY;

        mPHHelper[POS_INCOME] = new pageHelper();
        mPHHelper[POS_INCOME].mRLSelector = mRLIncome;
        mPHHelper[POS_INCOME].mPageIdx = POS_INCOME;

        mBTAccept.setOnClickListener(v -> {
            int pos = isEnableRL(mRLPay) ? POS_PAY : POS_INCOME;
            PagerAdapter pa = UtilFun.cast(mVPPager.getAdapter());
            TFEditBase tb = UtilFun.cast(pa.getItem(pos));
            if (tb.onAccept()) {
                ac.finish();
            }
        });

        enableRLStatus(mRLPay);
    }

    @Override
    protected void loadUI() {
    }

    /// PRIVATE BEGIN
    /**
     * 修改rl状态
     * @param rl        待修改rl
     */
    private void enableRLStatus(RelativeLayout rl)  {
        for(pageHelper ph : mPHHelper)  {
            if(rl == ph.mRLSelector)    {
                setRLStatus(ph.mRLSelector, true);
                mPHHot = ph;
            } else {
                setRLStatus(ph.mRLSelector, false);
            }
        }

        mVPPager.setCurrentItem(mPHHot.mPageIdx);
    }

    private void setRLStatus(RelativeLayout rl, boolean bIsSelected)    {
        int res;
        if(rl == mRLPay)    {
            res = bIsSelected ? R.drawable.rl_item_left : R.drawable.rl_item_left_nosel;
        }  else {
            res = bIsSelected ? R.drawable.rl_item_right : R.drawable.rl_item_right_nosel;
        }

        rl.setBackgroundResource(res);
        ((TextView)rl.findViewById(R.id.tv_tag))
                .setTextColor(bIsSelected ? ResourceHelper.mCRTextWhite : ResourceHelper.mCRTextFit);
    }

    /**
     * 判断rl是否enable
     * @param rl    待检查rl
     * @return      是否enable
     */
    private Boolean isEnableRL(RelativeLayout rl)  {
        return rl == mPHHot.mRLSelector;
    }
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
