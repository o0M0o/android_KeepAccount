package wxm.KeepAccount.ui.data.edit.Note;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.util.UtilFun;
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
 * 查看/编辑收入数据
 * Created by 123 on 2016/10/30.
 */
public class FrgPreviewAndEdit extends Fragment
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
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vw_viewpage, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mVPPages = UtilFun.cast_t(view.findViewById(R.id.vp_pages));
            init_view();
        }
    }

    /**
     * 当前页是否是编辑页
     *
     * @return 若是编辑页，返回true
     */
    @Override
    public boolean isEditPage() {
        return null != mVPPages && mVPPages.getCurrentItem() == PAGE_IDX_EDIT;
    }

    /**
     * 当前页是否是预览页
     *
     * @return 若是预览页，返回true
     */
    @Override
    public boolean isPreviewPage() {
        return null != mVPPages && mVPPages.getCurrentItem() == PAGE_IDX_PREVIEW;
    }


    /**
     * 切换至编辑页
     *
     * @return 切换成功返回true
     */
    @Override
    public boolean toEditPage() {
        if (null == mVPPages)
            return false;

        if (isPreviewPage()) {
            mVPPages.setCurrentItem(PAGE_IDX_EDIT);
            if (GlobalDef.STR_MODIFY.equals(mStrAction)) {
                PagerAdapter old_pa = UtilFun.cast_t(mVPPages.getAdapter());
                TFEditBase te = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_EDIT));
                TFPreviewBase old_tp = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_PREVIEW));

                te.setCurData(mStrAction, old_tp.getCurData());
                te.reLoadView();
            }
        }
        return true;
    }

    /**
     * 切换至预览页
     *
     * @return 切换成功返回true
     */
    @Override
    public boolean toPreviewPage() {
        if (null == mVPPages)
            return false;

        if (isEditPage()) {
            PagerAdapter old_pa = UtilFun.cast_t(mVPPages.getAdapter());
            TFEditBase old_te = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_EDIT));
            TFPreviewBase tp = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_PREVIEW));

            mVPPages.setCurrentItem(PAGE_IDX_PREVIEW);
            tp.setPreviewPara(old_te.getCurData());
            tp.reLoadView();
        }
        return true;
    }

    private void init_view() {
        PagerAdapter adapter = new PagerAdapter(getFragmentManager(), PAGE_COUNT);
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

        PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            mNumOfFrags = NumOfTabs;
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
