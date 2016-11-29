package wxm.KeepAccount.ui.fragment.utility;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.Setting.TFSettingBase;
import wxm.KeepAccount.ui.fragment.Setting.TFSettingChartColor;
import wxm.KeepAccount.ui.fragment.Setting.TFSettingCheckVersion;
import wxm.KeepAccount.ui.fragment.Setting.TFSettingMain;
import wxm.KeepAccount.ui.fragment.Setting.TFSettingRemind;

/**
 * for app setting
 * Created by ookoo on 2016/11/29.
 */
public class FrgSetting extends FrgUtilityBase {
    private final static int   PAGE_COUNT              = 4;
    public final static int    PAGE_IDX_MAIN           = 0;
    public final static int    PAGE_IDX_CHECK_VERSION  = 1;
    public final static int    PAGE_IDX_CHART_COLOR    = 2;
    public final static int    PAGE_IDX_REMIND         = 3;


    @BindView(R.id.vp_pages)
    ViewPager mVPPage;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgSetting";
        View rootView = layoutInflater.inflate(R.layout.vw_setting, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {

    }

    @Override
    protected void initUiInfo() {
        AppCompatActivity  a_ac = UtilFun.cast_t(getActivity());

        // for pages
        final PagerAdapter adapter = new PagerAdapter
                (a_ac.getSupportFragmentManager(), PAGE_COUNT);
        mVPPage.setAdapter(adapter);
        change_page(PAGE_IDX_MAIN);
    }


    /**
     * 切换到新页面
     * @param new_page 新页面postion
     */
    public void change_page(int new_page)  {
        mVPPage.setCurrentItem(new_page);
    }

    /**
     *  得到当前页
     * @return  当前页实例
     */
    public TFSettingBase getCurrentPage()   {
        PagerAdapter pa = UtilFun.cast_t(mVPPage.getAdapter());
        return UtilFun.cast_t(pa.getItem(mVPPage.getCurrentItem()));
    }

    public int getCurrentItem() {
        return mVPPage.getCurrentItem();
    }

    public void setCurrentItem(int idx) {
        mVPPage.setCurrentItem(idx);
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
            mFRFrags[PAGE_IDX_MAIN] = new TFSettingMain();
            mFRFrags[PAGE_IDX_CHECK_VERSION] = new TFSettingCheckVersion();
            mFRFrags[PAGE_IDX_CHART_COLOR] = new TFSettingChartColor();
            mFRFrags[PAGE_IDX_REMIND] = new TFSettingRemind();
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
