package wxm.KeepAccount.ui.fragment.EditData;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.base.ITFBase;
import wxm.KeepAccount.ui.fragment.base.TFEditBase;
import wxm.KeepAccount.ui.fragment.base.TFPreviewBase;

/**
 * 查看/编辑支付数据
 * Created by 123 on 2016/10/30.
 */
public class TFPay extends Fragment
        implements ITFBase {
    private ViewPager       mVPPages;
    private PayNoteItem     mPNData;
    private View            mSelfView;

    private final static int   PAGE_COUNT              = 2;
    private final static int    PAGE_IDX_PREVIEW        = 0;
    private final static int    PAGE_IDX_EDIT           = 1;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vw_viewpage, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            if(!view.isInEditMode()) {
                mSelfView = view;
                mVPPages = UtilFun.cast_t(view.findViewById(R.id.vp_pages));
                init_view();
            }
        }
    }

    /**
     * 当前页是否是编辑页
     * @return   若是编辑页，返回true
     */
    public boolean isEditPage() {
        return null != mVPPages && mVPPages.getCurrentItem() == PAGE_IDX_EDIT;
    }

    /**
     * 当前页是否是预览页
     * @return   若是预览页，返回true
     */
    public boolean isPreviewPage() {
        return null != mVPPages && mVPPages.getCurrentItem() == PAGE_IDX_PREVIEW;
    }


    /**
     * 切换至编辑页
     * @return  切换成功返回true
     */
    public boolean toEditPage() {
        if(null == mVPPages)
            return  false;

        if(isPreviewPage())     {
            PagerAdapter old_pa = UtilFun.cast_t(mVPPages.getAdapter());
            TFEditBase te = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_EDIT));
            TFPreviewBase old_tp = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_PREVIEW));

            PayNoteItem bi = UtilFun.cast(old_tp.getCurData());
            te.setCurData(null == bi ? AppGobalDef.STR_CREATE : AppGobalDef.STR_MODIFY, bi);
            te.reLoadView();
        }
        return true;
    }

    /**
     * 切换至预览页
     * @return  切换成功返回true
     */
    public boolean toPreviewPage() {
        if(null == mVPPages)
            return  false;

        if(isEditPage())     {
            PagerAdapter old_pa = UtilFun.cast_t(mVPPages.getAdapter());
            TFEditBase old_te = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_EDIT));
            TFPreviewBase tp = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_PREVIEW));

            PayNoteItem bi = UtilFun.cast(old_te.getCurData());
            tp.setPreviewPara(bi);
            tp.reLoadView();
        }
        return true;
    }

    private void init_view() {
        PagerAdapter adapter = new PagerAdapter(getFragmentManager(), PAGE_COUNT);
        mVPPages.setAdapter(adapter);

        if(null != mPNData) {
            ((TFPreviewBase)adapter.getItem(PAGE_IDX_PREVIEW)).setPreviewPara(mPNData);
            mVPPages.setCurrentItem(PAGE_IDX_PREVIEW);
        } else  {
            ((TFEditBase)adapter.getItem(PAGE_IDX_EDIT)).setCurData(AppGobalDef.STR_CREATE, null);
            mVPPages.setCurrentItem(PAGE_IDX_EDIT);
        }
    }

    @Override
    public void setCurData(String action, Object obj) {
        mPNData = UtilFun.cast(obj);
    }

    @Override
    public boolean onAccept() {
        if(isPreviewPage())
            return false;

        PagerAdapter pa = UtilFun.cast(mVPPages.getAdapter());
        TFEditPay tp = UtilFun.cast_t(pa.getItem(PAGE_IDX_EDIT));
        return tp.onAccept();
    }


    /**
     * fragment adapter
     */
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int                 mNumOfFrags;
        private Fragment[]  mFRFrags;

        PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            mNumOfFrags = NumOfTabs;

            mFRFrags = new Fragment[mNumOfFrags];
            mFRFrags[PAGE_IDX_PREVIEW] = new TFPreviewPay();
            mFRFrags[PAGE_IDX_EDIT] = new TFEditPay();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFRFrags[position];
        }

        @Override
        public int getCount() {
            return mNumOfFrags;
        }
    }
}
