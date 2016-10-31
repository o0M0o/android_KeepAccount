package wxm.KeepAccount.ui.acutility;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.HashMap;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACHelp;
import wxm.KeepAccount.ui.fragment.base.TFEditBase;
import wxm.KeepAccount.ui.fragment.EditData.TFEditIncome;
import wxm.KeepAccount.ui.fragment.EditData.TFEditPay;

/**
 * 支出/收入数据编辑UI
 */
public class ACNoteEdit extends AppCompatActivity {
    private static final String TAG = "ACNoteEdit";

    protected final static String TAB_PAY     = "支出";
    protected final static String TAB_INCOME  = "收入";

    public static final String  PARA_ACTION          = "para_action";
    public static final String  PARA_NOTE_PAY        = "note_pay";
    public static final String  PARA_NOTE_INCOME     = "note_income";

    public static final int DEF_NOTE_MAXLEN = 200;

    private String          mAction;
    private PayNoteItem     mOldPayNote;
    private IncomeNoteItem  mOldIncomeNote;

    private TabLayout mTLTabs;
    private ViewPager mVPTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_edit);

        initView();
    }

    private void initView() {
        Intent it = getIntent();
        assert null != it;

        mAction = it.getStringExtra(PARA_ACTION);
        if(UtilFun.StringIsNullOrEmpty(mAction)) {
            Log.e(TAG, "调用intent缺少'PARA_ACTION'参数");
            return ;
        }

        // for tabs
        mTLTabs = (TabLayout) findViewById(R.id.tl_tabs);
        assert null != mTLTabs;

        if(mAction.equals(AppGobalDef.STR_MODIFY)) {
            int pid = it.getIntExtra(ACNoteEdit.PARA_NOTE_PAY, AppGobalDef.INVALID_ID);
            int iid = it.getIntExtra(ACNoteEdit.PARA_NOTE_INCOME, AppGobalDef.INVALID_ID);
            if(AppGobalDef.INVALID_ID != pid)   {
                mOldPayNote = AppModel.getPayIncomeUtility().GetPayNoteById(pid);
                mTLTabs.addTab(mTLTabs.newTab().setText(TAB_PAY));
            } else if(AppGobalDef.INVALID_ID != iid)    {
                mOldIncomeNote = AppModel.getPayIncomeUtility().GetIncomeNoteById(iid);
                mTLTabs.addTab(mTLTabs.newTab().setText(TAB_INCOME));
            } else  {
                Log.e(TAG, "调用intent缺少'PARA_NOTE_PAY'和'PARA_NOTE_INCOME'参数");
                return;
            }

        } else {
            mTLTabs.addTab(mTLTabs.newTab().setText(TAB_PAY));
            mTLTabs.addTab(mTLTabs.newTab().setText(TAB_INCOME));
        }

        mTLTabs.setTabGravity(TabLayout.GRAVITY_FILL);

        mVPTabs = (ViewPager) findViewById(R.id.tab_pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),
                                        mTLTabs.getTabCount());
        mVPTabs.setAdapter(adapter);
        mVPTabs.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTLTabs));
        mTLTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mVPTabs.setCurrentItem(tab.getPosition());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_record_actbar, menu);
        return true;
    }


    /**
     * 得到当前选中的tab item
     * @return  当前选中的tab item
     */
    private TFEditBase getHotTabItem() {
        int pos = mTLTabs.getSelectedTabPosition();
        PagerAdapter pa = UtilFun.cast(mVPTabs.getAdapter());
        return UtilFun.cast(pa.getItem(pos));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.record_memenu_save: {
                TFEditBase tb = getHotTabItem();
                if(tb.onAccept()) {
                    Intent data = new Intent();
                    setResult(mAction.equals(AppGobalDef.STR_CREATE) ?  AppGobalDef.INTRET_RECORD_ADD
                                : AppGobalDef.INTRET_RECORD_MODIFY,  data);
                    finish();
                }
            }
            break;

            case R.id.record_menu_giveup: {
                int ret_data = AppGobalDef.INTRET_GIVEUP;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            case R.id.recordmenu_help : {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_RECORD);

                startActivityForResult(intent, 1);
            }
            break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /*
    @Override
    protected void onDataChange(int requestCode, int resultCode, Intent data) {
        super.onDataChange(requestCode, resultCode, data);
        if(mTabFragment instanceof STEditNoteFragment)  {
            STEditNoteFragment sf = UtilFun.cast(mTabFragment);
            sf.onDataChange(requestCode, resultCode, data);
        }
    }
    */

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
