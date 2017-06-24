package wxm.KeepAccount.ui.data.edit.Note;


import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
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

import org.w3c.dom.Text;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditIncome;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditPay;
import wxm.KeepAccount.ui.data.edit.base.TFEditBase;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * for login
 * Created by ookoo on 2016/11/29.
 */
public class FrgNoteAdd extends FrgUtilityBase {
    protected final static String TAB_PAY = "支出";
    protected final static String TAB_INCOME = "收入";
    @BindView(R.id.tab_pager)
    ViewPager mVPPager;

    @BindView(R.id.rl_pay)
    RelativeLayout mRLPay;

    @BindView(R.id.rl_income)
    RelativeLayout mRLIncome;

    private int mCRWhite;
    private int mCRTextFit;
    private RelativeLayout mRLHot;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        LOG_TAG = "FrgLogin";
        View rootView = inflater.inflate(R.layout.vw_note_add, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        Context ct = ContextUtil.getInstance();
        Resources res = ct.getResources();
        Resources.Theme te = ct.getTheme();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCRWhite = res.getColor(R.color.white, te);
            mCRTextFit= res.getColor(R.color.text_fit, te);
        } else {
            mCRWhite = res.getColor(R.color.white);
            mCRTextFit= res.getColor(R.color.text_fit);
        }

        // for vp
        AppCompatActivity ac = (AppCompatActivity) getActivity();
        PagerAdapter adapter = new PagerAdapter(ac.getSupportFragmentManager(), 2);
        mVPPager.setAdapter(adapter);

        enableRLStatus(mRLPay, true);
        mRLIncome.setOnClickListener(v -> {
            if(!isEnableRL(mRLIncome))  {
                enableRLStatus(mRLIncome, true);
                mVPPager.setCurrentItem(1);
            }
        });

        mRLPay.setOnClickListener(v -> {
            if(!isEnableRL(mRLPay))  {
                enableRLStatus(mRLPay, true);
                mVPPager.setCurrentItem(0);
            }
        });
    }

    @Override
    protected void loadUI() {
    }

    /**
     * 得到当前选中的tab item
     *
     * @return 当前选中的tab item
     */
    public TFEditBase getHotTabItem() {
        int pos = isEnableRL(mRLPay) ? 0 : 1;
        PagerAdapter pa = UtilFun.cast(mVPPager.getAdapter());
        return UtilFun.cast(pa.getItem(pos));
    }

    /// PRIVATE BEGIN
    private void enableRLStatus(RelativeLayout rl, Boolean bStatus)  {
        if(rl == mRLPay) {
            rl.setBackgroundResource(bStatus ?
                    R.drawable.rl_item_left : R.drawable.rl_item_left_nosel);
            mRLIncome.setBackgroundResource(bStatus ?
                    R.drawable.rl_item_right_nosel : R.drawable.rl_item_right);

            ((TextView)rl.findViewById(R.id.tv_tag)).setTextColor(bStatus ? mCRWhite : mCRTextFit);
            ((TextView)mRLIncome.findViewById(R.id.tv_tag)).setTextColor(!bStatus ? mCRWhite : mCRTextFit);

            mRLHot = bStatus ? mRLPay : mRLIncome;
        } else {
            rl.setBackgroundResource(bStatus ?
                    R.drawable.rl_item_right : R.drawable.rl_item_right_nosel);
            mRLPay.setBackgroundResource(bStatus ?
                    R.drawable.rl_item_left_nosel : R.drawable.rl_item_left);

            ((TextView)rl.findViewById(R.id.tv_tag)).setTextColor(bStatus ? mCRWhite : mCRTextFit);
            ((TextView)mRLPay.findViewById(R.id.tv_tag)).setTextColor(!bStatus ? mCRWhite : mCRTextFit);


            mRLHot = !bStatus ? mRLPay : mRLIncome;
        }
    }

    private Boolean isEnableRL(RelativeLayout rl)  {
        return rl == mRLHot;
    }
    /// PRIVATE END

    /**
     * fragment adapter
     */
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        HashMap<String, Fragment> mHMFra;

        PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;

            Bundle bd = getArguments();

            TFEditPay tp = new TFEditPay();
            tp.setCurData(GlobalDef.STR_CREATE, null);
            tp.setArguments(bd);

            TFEditIncome ti = new TFEditIncome();
            ti.setCurData(GlobalDef.STR_CREATE, null);
            ti.setArguments(bd);

            mHMFra = new HashMap<>();
            mHMFra.put(TAB_PAY, tp);
            mHMFra.put(TAB_INCOME, ti);
        }

        @Override
        public Fragment getItem(int position) {
            return mHMFra.get(position == 0 ? TAB_PAY : TAB_INCOME);
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
