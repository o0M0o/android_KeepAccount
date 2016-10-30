package wxm.KeepAccount.ui.acutility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.EditData.TFEditBudget;
import wxm.KeepAccount.ui.fragment.EditData.TFPreviewBudget;
import wxm.KeepAccount.ui.fragment.base.TFEditBase;
import wxm.KeepAccount.ui.fragment.base.TFPreviewBase;

/**
 * activity for budget preview & edit
 */
public class ACBudget extends AppCompatActivity {
    public final static String INTENT_LOAD_BUDGETID = "BUDGET_ID";

    private ViewPager mVPPages;

    private final static String     CHANGETO_PREVIEW = "预览";
    private final static String     CHANGETO_EDIT    = "编辑";

    private final static int   PAGE_COUNT              = 2;
    public final static int    PAGE_IDX_PREVIEW        = 0;
    public final static int    PAGE_IDX_EDIT           = 1;

    private MenuItem    mMISwitch;
    private MenuItem    mMISave;
    private MenuItem    mMIGiveup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_budget);

        mVPPages = UtilFun.cast_t(findViewById(R.id.vp_pages));
        PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), PAGE_COUNT);
        mVPPages.setAdapter(adapter);

        Intent it = getIntent();
        int id = it.getIntExtra(INTENT_LOAD_BUDGETID, -1);
        if(-1 != id) {
            BudgetItem bi = AppModel.getBudgetUtility().GetBudgetById(id);
            ((TFPreviewBase)adapter.getItem(PAGE_IDX_PREVIEW)).setPreviewPara(bi);
            mVPPages.setCurrentItem(PAGE_IDX_PREVIEW);
        } else  {
            ((TFEditBudget)adapter.getItem(PAGE_IDX_EDIT)).setCurData(AppGobalDef.STR_CREATE, null);
            mVPPages.setCurrentItem(PAGE_IDX_EDIT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_budget, menu);

        mMISwitch = menu.findItem(R.id.mi_switch);
        mMISave   = menu.findItem(R.id.mi_save);
        mMIGiveup = menu.findItem(R.id.mi_giveup);
        mMISwitch.setTitle(PAGE_IDX_PREVIEW == mVPPages.getCurrentItem() ?
                            CHANGETO_EDIT : CHANGETO_PREVIEW);

        mMISave.setVisible(PAGE_IDX_PREVIEW != mVPPages.getCurrentItem());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_switch : {
                mMISave.setVisible(PAGE_IDX_PREVIEW == mVPPages.getCurrentItem());
                mMISwitch.setTitle(PAGE_IDX_PREVIEW != mVPPages.getCurrentItem() ?
                        CHANGETO_EDIT : CHANGETO_PREVIEW);
                change_page(PAGE_IDX_PREVIEW == mVPPages.getCurrentItem() ?
                        PAGE_IDX_EDIT : PAGE_IDX_PREVIEW);
            }
            break;

            case R.id.mi_save: {
                TFEditBase fr = UtilFun.cast_t(getCurrentPage());
                if(fr.onAccept())    {
                    int ret_data = AppGobalDef.INTRET_SURE;
                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            case R.id.mi_giveup:    {
                int ret_data = AppGobalDef.INTRET_GIVEUP;
                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    /**
     * 切换到新页面
     * @param new_page 新页面postion
     */
    public void change_page(int new_page)  {
        int old_page = mVPPages.getCurrentItem();
        if(new_page != old_page) {
            mVPPages.setCurrentItem(new_page);
            PagerAdapter old_pa = UtilFun.cast_t(mVPPages.getAdapter());
            switch (old_page)   {
                case PAGE_IDX_EDIT :    {
                    TFPreviewBase tp = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_PREVIEW));
                    TFEditBase old_te = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_EDIT));

                    BudgetItem bi = UtilFun.cast(old_te.getCurData());
                    tp.setPreviewPara(bi);
                    tp.reLoadView();
                }
                break;

                case PAGE_IDX_PREVIEW :     {
                    TFEditBase te = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_EDIT));
                    TFPreviewBase old_tp = UtilFun.cast_t(old_pa.getItem(PAGE_IDX_PREVIEW));

                    BudgetItem bi = UtilFun.cast(old_tp.getCurData());
                    te.setCurData(null == bi ? AppGobalDef.STR_CREATE : AppGobalDef.STR_MODIFY, bi);
                    te.reLoadView();
                }
                break;
            }
        }
    }

    /**
     *  得到当前页
     * @return  当前页实例
     */
    protected Fragment getCurrentPage()   {
        PagerAdapter pa = UtilFun.cast_t(mVPPages.getAdapter());
        return UtilFun.cast_t(pa.getItem(mVPPages.getCurrentItem()));
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
            mFRFrags[PAGE_IDX_PREVIEW] = new TFPreviewBudget();
            mFRFrags[PAGE_IDX_EDIT] = new TFEditBudget();
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
