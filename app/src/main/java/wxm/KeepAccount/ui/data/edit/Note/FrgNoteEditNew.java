package wxm.KeepAccount.ui.data.edit.Note;


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
public class FrgNoteEditNew extends FrgUtilityBase {
    protected final static String TAB_PAY = "支出";
    protected final static String TAB_INCOME = "收入";
    // for ui
    @BindView(R.id.tl_tabs)
    TabLayout mTLTabs;
    @BindView(R.id.tab_pager)
    ViewPager mVPPager;
    // for data
    private String mAction;
    private PayNoteItem mOldPayNote;
    private IncomeNoteItem mOldIncomeNote;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        LOG_TAG = "FrgLogin";
        View rootView = inflater.inflate(R.layout.vw_note_edit_new, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        Bundle bd = getArguments();
        mAction = bd.getString(ACNoteEdit.PARA_ACTION);
        if (GlobalDef.STR_MODIFY.equals(mAction)) {
            int pid = bd.getInt(ACNoteEdit.PARA_NOTE_PAY, GlobalDef.INVALID_ID);
            int iid = bd.getInt(ACNoteEdit.PARA_NOTE_INCOME, GlobalDef.INVALID_ID);
            if (GlobalDef.INVALID_ID != pid) {
                mOldPayNote = ContextUtil.getPayIncomeUtility().getPayDBUtility().getData(pid);
                mTLTabs.addTab(mTLTabs.newTab().setText(TAB_PAY));
            } else if (GlobalDef.INVALID_ID != iid) {
                mOldIncomeNote = ContextUtil.getPayIncomeUtility().getIncomeDBUtility().getData(iid);
                mTLTabs.addTab(mTLTabs.newTab().setText(TAB_INCOME));
            } else {
                Log.e(LOG_TAG, "调用intent缺少'PARA_NOTE_PAY'和'PARA_NOTE_INCOME'参数");
                return;
            }
        } else {
            mTLTabs.addTab(mTLTabs.newTab().setText(TAB_PAY));
            mTLTabs.addTab(mTLTabs.newTab().setText(TAB_INCOME));
        }

        // for vp
        AppCompatActivity ac = (AppCompatActivity) getActivity();
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
    protected void loadUI() {
    }

    /**
     * 得到当前选中的tab item
     *
     * @return 当前选中的tab item
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

            Bundle bd = getArguments();

            TFEditPay tp = new TFEditPay();
            tp.setCurData(mAction, mOldPayNote);
            tp.setArguments(bd);

            TFEditIncome ti = new TFEditIncome();
            ti.setCurData(mAction, mOldIncomeNote);
            ti.setArguments(bd);

            mHMFra = new HashMap<>();
            mHMFra.put(TAB_PAY, tp);
            mHMFra.put(TAB_INCOME, ti);
        }

        @Override
        public Fragment getItem(int position) {
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
