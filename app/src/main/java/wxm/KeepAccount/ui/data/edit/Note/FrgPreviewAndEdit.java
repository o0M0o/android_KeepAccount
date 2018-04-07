package wxm.KeepAccount.ui.data.edit.Note;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditBudget;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditIncome;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFEditPay;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFPreviewBudget;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFPreviewIncome;
import wxm.KeepAccount.ui.data.edit.Note.utility.TFPreviewPay;
import wxm.KeepAccount.ui.data.edit.base.IPreviewAndEditBase;
import wxm.KeepAccount.ui.data.edit.base.TFEditBase;
import wxm.KeepAccount.ui.data.edit.base.TFPreviewBase;

/**
 * preview/edit record
 * Created by WangXM on 2016/10/30.
 */
public class FrgPreviewAndEdit extends FrgUtilitySupportBase
        implements IPreviewAndEditBase {
    private final static int PAGE_COUNT = 2;
    private final static int PAGE_IDX_PREVIEW = 0;
    private final static int PAGE_IDX_EDIT = 1;
    @BindView(R.id.vp_pages)
    ViewPager mVPPages;
    private String mStrType;
    private String mStrAction;
    private Object mData;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.vw_viewpage, viewGroup, false);
    }

    protected void initUI(Bundle bundle)    {
        PagerAdapter adapter = new PagerAdapter(getFragmentManager());
        mVPPages.setAdapter(adapter);

        if (UtilFun.StringIsNullOrEmpty(mStrAction)
                || UtilFun.StringIsNullOrEmpty(mStrType)
                || (GlobalDef.STR_MODIFY.equals(mStrAction) && null == mData)
                || (GlobalDef.STR_CREATE.equals(mStrAction) && null != mData))
            return;

        ((TFPreviewBase) adapter.getItem(PAGE_IDX_PREVIEW)).setPreviewPara(mData);
        ((TFEditBase) adapter.getItem(PAGE_IDX_EDIT)).setCurData(mStrAction, mData);
        mVPPages.setCurrentItem(GlobalDef.STR_MODIFY.equals(mStrAction) ?
                PAGE_IDX_PREVIEW : PAGE_IDX_EDIT);
    }

    /**
     * check current page is edit
     * @return      true if current page for edit
     */
    @Override
    public boolean isEditPage() {
        return null != mVPPages && mVPPages.getCurrentItem() == PAGE_IDX_EDIT;
    }

    /**
     * check current page is preview
     * @return      true if current page for preview
     */
    @Override
    public boolean isPreviewPage() {
        return null != mVPPages && mVPPages.getCurrentItem() == PAGE_IDX_PREVIEW;
    }

    @Override
    public void toEditPage() {
        if (null == mVPPages)
            return;

        if (isPreviewPage()) {
            mVPPages.setCurrentItem(PAGE_IDX_EDIT);
            if (GlobalDef.STR_MODIFY.equals(mStrAction)) {
                PagerAdapter old_pa = UtilFun.cast_t(mVPPages.getAdapter());
                TFEditBase te = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_EDIT));
                TFPreviewBase old_tp = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_PREVIEW));

                te.setCurData(mStrAction, old_tp.getCurData());
                te.refreshUI();
            }
        }
    }

    @Override
    public void toPreviewPage() {
        if (null == mVPPages)
            return ;

        if (isEditPage()) {
            PagerAdapter old_pa = UtilFun.cast_t(mVPPages.getAdapter());
            TFEditBase old_te = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_EDIT));
            TFPreviewBase tp = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_PREVIEW));

            mVPPages.setCurrentItem(PAGE_IDX_PREVIEW);
            tp.setPreviewPara(old_te.getCurData());
            tp.reLoadView();
        }
    }

    @Override
    public void setCurData(String type, String action, Object obj) {
        mStrType = type;
        mStrAction = action;
        mData = UtilFun.cast(obj);
    }

    @Override
    public boolean onAccept() {
        if (isPreviewPage())
            return false;

        PagerAdapter pa = UtilFun.cast(mVPPages.getAdapter());
        TFEditBase tp = UtilFun.cast_t(pa.getItem(PAGE_IDX_EDIT));
        return tp.onAccept();
    }


    /**
     * fragment adapter
     */
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfFrags;
        private Fragment[] mFRFrags;

        PagerAdapter(FragmentManager fm) {
            super(fm);
            mNumOfFrags = PAGE_COUNT;
            mFRFrags = new Fragment[mNumOfFrags];

            if (GlobalDef.STR_RECORD_INCOME.equals(mStrType)) {
                mFRFrags[PAGE_IDX_PREVIEW] = new TFPreviewIncome();
                mFRFrags[PAGE_IDX_EDIT] = new TFEditIncome();
            } else if (GlobalDef.STR_RECORD_PAY.equals(mStrType)) {
                mFRFrags[PAGE_IDX_PREVIEW] = new TFPreviewPay();
                mFRFrags[PAGE_IDX_EDIT] = new TFEditPay();
            } else {
                mFRFrags[PAGE_IDX_PREVIEW] = new TFPreviewBudget();
                mFRFrags[PAGE_IDX_EDIT] = new TFEditBudget();
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mFRFrags[position];
        }

        @Override
        public int getCount() {
            return mNumOfFrags;
        }
    }
}
