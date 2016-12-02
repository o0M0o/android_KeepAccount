package wxm.KeepAccount.ui.fragment.utility;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.define.AppGobalDef;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.fragment.EditData.TFEditIncome;
import wxm.KeepAccount.ui.fragment.EditData.TFEditPay;
import wxm.KeepAccount.ui.fragment.base.TFEditBase;

/**
 * for login
 * Created by ookoo on 2016/11/29.
 */
public class FrgNoteEdit extends FrgUtilityBase {
    // for ui
    @BindView(R.id.tl_tabs)
    TabLayout   mTLTabs;

    @BindView(R.id.tab_pager)
    ViewPager   mVPPager;

    // for data
    private String          mAction;
    private PayNoteItem     mOldPayNote;
    private IncomeNoteItem  mOldIncomeNote;

    protected final static String TAB_PAY     = "支出";
    protected final static String TAB_INCOME  = "收入";

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        LOG_TAG = "FrgLogin";
        View rootView = inflater.inflate(R.layout.vw_note_edit, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        Bundle bd = getArguments();
        mAction = bd.getString(ACNoteEdit.PARA_ACTION);
        if(AppGobalDef.STR_MODIFY.equals(mAction)) {
            int pid = bd.getInt(ACNoteEdit.PARA_NOTE_PAY, AppGobalDef.INVALID_ID);
            int iid = bd.getInt(ACNoteEdit.PARA_NOTE_INCOME, AppGobalDef.INVALID_ID);
            if(AppGobalDef.INVALID_ID != pid)   {
                mOldPayNote = ContextUtil.getPayIncomeUtility().GetPayNoteById(pid);
                mTLTabs.addTab(mTLTabs.newTab().setText(TAB_PAY));
            } else if(AppGobalDef.INVALID_ID != iid)    {
                mOldIncomeNote = ContextUtil.getPayIncomeUtility().GetIncomeNoteById(iid);
                mTLTabs.addTab(mTLTabs.newTab().setText(TAB_INCOME));
            } else  {
                Log.e(LOG_TAG, "调用intent缺少'PARA_NOTE_PAY'和'PARA_NOTE_INCOME'参数");
                return;
            }

        } else {
            mTLTabs.addTab(mTLTabs.newTab().setText(TAB_PAY));
            mTLTabs.addTab(mTLTabs.newTab().setText(TAB_INCOME));
        }

        // for vp
        AppCompatActivity ac = (AppCompatActivity)getActivity();
        PagerAdapter adapter = new PagerAdapter(ac.getSupportFragmentManager(),
                                    mTLTabs.getTabCount());
        mVPPager.setAdapter(adapter);
        mVPPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTLTabs));
        mTLTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mVPPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void initUiInfo() {
    }

    /**
     * 得到当前选中的tab item
     * @return  当前选中的tab item
     */
    public TFEditBase getHotTabItem() {
        int pos = mTLTabs.getSelectedTabPosition();
        PagerAdapter pa = UtilFun.cast(mVPPager.getAdapter());
        return UtilFun.cast(pa.getItem(pos));
    }

    /// PRIVATE BEGIN
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

            TFEditPay tp = new TFEditPay();
            tp.setCurData(mAction, mOldPayNote);

            TFEditIncome ti = new TFEditIncome();
            ti.setCurData(mAction, mOldIncomeNote);

            mHMFra = new HashMap<>();
            mHMFra.put(TAB_PAY, tp);
            mHMFra.put(TAB_INCOME, ti);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            TabLayout.Tab t = mTLTabs.getTabAt(position);
            assert null != t;
            CharSequence t_cs = t.getText();
            assert null != t_cs;
            return mHMFra.get(t_cs.toString());
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
