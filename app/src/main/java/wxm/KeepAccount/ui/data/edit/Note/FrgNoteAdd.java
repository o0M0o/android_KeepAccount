package wxm.KeepAccount.ui.data.edit.Note;


import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditIncome;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditPay;
import wxm.KeepAccount.ui.data.edit.base.TFEditBase;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * for login
 * Created by ookoo on 2016/11/29.
 */
public class FrgNoteAdd extends FrgUtilityBase {
    protected final static int POS_PAY = 0;
    protected final static int POS_INCOME = 1;

    @BindView(R.id.cl_header)
    ConstraintLayout mCLHeader;

    @BindView(R.id.tab_pager)
    ViewPager mVPPager;

    @BindView(R.id.rl_pay)
    RelativeLayout mRLPay;

    @BindView(R.id.rl_income)
    RelativeLayout mRLIncome;

    private int mCRWhite;
    private int mCRTextFit;
    private RelativeLayout mRLHot;

    // for data
    private String mAction;
    private PayNoteItem mOldPayNote;
    private IncomeNoteItem mOldIncomeNote;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        LOG_TAG = "FrgNoteAdd";
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
        Bundle bd = getArguments();
        mAction = bd.getString(ACNoteAdd.PARA_ACTION);
        if (GlobalDef.STR_MODIFY.equals(mAction)) {
            int pid = bd.getInt(ACNoteAdd.PARA_NOTE_PAY, GlobalDef.INVALID_ID);
            int iid = bd.getInt(ACNoteAdd.PARA_NOTE_INCOME, GlobalDef.INVALID_ID);
            if (GlobalDef.INVALID_ID != pid) {
                mOldPayNote = ContextUtil.getPayIncomeUtility().getPayDBUtility().getData(pid);
            } else if (GlobalDef.INVALID_ID != iid) {
                mOldIncomeNote = ContextUtil.getPayIncomeUtility().getIncomeDBUtility().getData(iid);
            } else {
                Log.e(LOG_TAG, "调用intent缺少'PARA_NOTE_PAY'和'PARA_NOTE_INCOME'参数");
                return;
            }

            mCLHeader.setVisibility(View.GONE);
        }

        AppCompatActivity ac = (AppCompatActivity) getActivity();
        PagerAdapter adapter = new PagerAdapter(ac.getSupportFragmentManager());
        mVPPager.setAdapter(adapter);

        enableRLStatus(mRLPay, true);
        mRLIncome.setOnClickListener(v -> {
            if(!isEnableRL(mRLIncome))  {
                enableRLStatus(mRLIncome, true);
                mVPPager.setCurrentItem(POS_INCOME);
            }
        });

        mRLPay.setOnClickListener(v -> {
            if(!isEnableRL(mRLPay))  {
                enableRLStatus(mRLPay, true);
                mVPPager.setCurrentItem(POS_PAY);
            }
        });
    }

    @Override
    protected void loadUI() {
    }

    /**
     * 得到当前选中的tab item
     * @return 当前选中的tab item
     */
    public TFEditBase getHotTabItem() {
        int pos = isEnableRL(mRLPay) ? POS_PAY : POS_INCOME;
        PagerAdapter pa = UtilFun.cast(mVPPager.getAdapter());
        return UtilFun.cast(pa.getItem(pos));
    }

    /// PRIVATE BEGIN
    /**
     * 修改rl状态
     * @param rl        待修改rl
     * @param bStatus   新状态
     */
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

    /**
     * 判断rl是否enable
     * @param rl    待检查rl
     * @return      是否enable
     */
    private Boolean isEnableRL(RelativeLayout rl)  {
        return rl == mRLHot;
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

            if(GlobalDef.STR_CREATE.equals(mAction) || null != mOldPayNote) {
                TFEditPay tp = new TFEditPay();
                tp.setCurData(mAction, mOldPayNote);
                tp.setArguments(bd);
                mALFra.add(tp);
            }

            if(GlobalDef.STR_CREATE.equals(mAction) || null != mOldIncomeNote) {
                TFEditIncome ti = new TFEditIncome();
                ti.setCurData(mAction, mOldIncomeNote);
                ti.setArguments(bd);
                mALFra.add(ti);
            }
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
