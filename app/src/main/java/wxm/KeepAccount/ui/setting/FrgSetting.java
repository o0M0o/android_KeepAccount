package wxm.KeepAccount.ui.setting;

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
import wxm.KeepAccount.ui.setting.page.TFSettingBase;
import wxm.KeepAccount.ui.setting.page.TFSettingChartColor;
import wxm.KeepAccount.ui.setting.page.TFSettingCheckVersion;
import wxm.KeepAccount.ui.setting.page.TFSettingMain;
import wxm.KeepAccount.ui.setting.page.TFSettingRemind;
import wxm.androidutil.FrgUtility.FrgUtilityBase;;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;

/**
 * for app setting
 * Created by ookoo on 2016/11/29.
 */
public class FrgSetting extends FrgUtilityBase {
    public final static int PAGE_IDX_MAIN = 0;
    public final static int PAGE_IDX_CHECK_VERSION = 1;
    public final static int PAGE_IDX_CHART_COLOR = 2;
    public final static int PAGE_IDX_REMIND = 3;
    private final static int PAGE_COUNT = 4;

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
        AppCompatActivity a_ac = UtilFun.cast_t(getActivity());

        // for pages
        final PagerAdapter adapter = new PagerAdapter(a_ac.getSupportFragmentManager(), this);
        mVPPage.setAdapter(adapter);
    }

    @Override
    protected void loadUI() {
        change_page(PAGE_IDX_MAIN);
    }


    /**
     * change to new page
     * @param new_page  position for new page
     */
    public void change_page(int new_page) {
        mVPPage.setCurrentItem(new_page);
    }

    /**
     * get current page
     * @return      current page
     */
    public TFSettingBase getCurrentPage() {
        PagerAdapter pa = UtilFun.cast_t(mVPPage.getAdapter());
        return UtilFun.cast_t(pa.getItem(mVPPage.getCurrentItem()));
    }

    public int getCurrentItem() {
        return mVPPage.getCurrentItem();
    }

    /**
     * fragment adapter
     */
    class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfFrags;
        private Fragment[] mFRFrags;

        PagerAdapter(FragmentManager fm, FrgSetting holder) {
            super(fm);
            mNumOfFrags = PAGE_COUNT;

            mFRFrags = new Fragment[mNumOfFrags];
            mFRFrags[PAGE_IDX_MAIN] = new TFSettingMain();
            ((TFSettingBase)mFRFrags[PAGE_IDX_MAIN]).setFrgHolder(holder);

            mFRFrags[PAGE_IDX_CHECK_VERSION] = new TFSettingCheckVersion();
            ((TFSettingBase)mFRFrags[PAGE_IDX_CHECK_VERSION]).setFrgHolder(holder);

            mFRFrags[PAGE_IDX_CHART_COLOR] = new TFSettingChartColor();
            ((TFSettingBase)mFRFrags[PAGE_IDX_CHART_COLOR]).setFrgHolder(holder);

            mFRFrags[PAGE_IDX_REMIND] = new TFSettingRemind();
            ((TFSettingBase)mFRFrags[PAGE_IDX_REMIND]).setFrgHolder(holder);
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
