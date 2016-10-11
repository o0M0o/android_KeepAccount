package wxm.KeepAccount.ui.acutility;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.Setting.TFSettingBase;
import wxm.KeepAccount.ui.fragment.Setting.TFSettingChartColor;
import wxm.KeepAccount.ui.fragment.Setting.TFSettingCheckVersion;
import wxm.KeepAccount.ui.fragment.Setting.TFSettingMain;
import wxm.KeepAccount.ui.fragment.Setting.TFSettingRemind;

public class ACSetting extends AppCompatActivity {
    private ViewPager mVPPages;

    private final static int   PAGE_COUNT              = 4;
    public final static int    PAGE_IDX_MAIN           = 0;
    public final static int    PAGE_IDX_CHECK_VERSION  = 1;
    public final static int    PAGE_IDX_CHART_COLOR    = 2;
    public final static int    PAGE_IDX_REMIND         = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_setting);
        init_view();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_save_giveup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                if(PAGE_IDX_MAIN != mVPPages.getCurrentItem()) {
                    final TFSettingBase tb = getCurrentPage();
                    if(tb.isSettingDirty()) {
                        Dialog alertDialog = new AlertDialog.Builder(this).
                                setTitle("配置已经更改").
                                setMessage("是否保存更改的配置?").
                                setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tb.updateSetting();
                                        change_page(PAGE_IDX_MAIN);
                                    }
                                }).
                                setNegativeButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        change_page(PAGE_IDX_MAIN);
                                    }
                                }).
                                create();
                        alertDialog.show();
                    }
                }
            }
            break;

            case R.id.mi_giveup:    {
                if(PAGE_IDX_MAIN != mVPPages.getCurrentItem()) {
                    change_page(PAGE_IDX_MAIN);
                } else {
                    int ret_data = AppGobalDef.INTRET_GIVEUP;

                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }



    private void init_view() {
        mVPPages = UtilFun.cast(findViewById(R.id.vp_pages));
        assert  null != mVPPages;

        // for pages
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), PAGE_COUNT);
        mVPPages.setAdapter(adapter);
        change_page(PAGE_IDX_MAIN);
    }

    /**
     * 切换到新页面
     * @param new_page 新页面postion
     */
    public void change_page(int new_page)  {
        mVPPages.setCurrentItem(new_page);
    }

    /**
     *  得到当前页
     * @return  当前页实例
     */
    protected TFSettingBase getCurrentPage()   {
        PagerAdapter pa = UtilFun.cast(mVPPages.getAdapter());
        return UtilFun.cast(pa.getItem(mVPPages.getCurrentItem()));
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
