package wxm.KeepAccount.ui.acutility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACHelp;
import wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;
import wxm.KeepAccount.ui.fragment.EditNoteSlidingTabsFragment;

/**
 * 支出/收入数据编辑UI
 */
public class ACNoteEdit extends AppCompatActivity {
    private static final String TAG = "ACNoteEdit";

    public static final String  PARA_ACTION          = "para_action";
    public static final String  PARA_NOTE_PAY        = "note_pay";
    public static final String  PARA_NOTE_INCOME     = "note_income";

    public static final String  LOAD_NOTE_ADD        = "note_add";
    public static final String  LOAD_NOTE_MODIFY     = "note_modify";

    public static final int DEF_NOTE_MAXLEN = 200;

    private String mAction;
    private SlidingTabsColorsFragment mTabFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_edit);

        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        Intent it = getIntent();
        assert null != it;

        mAction = it.getStringExtra(PARA_ACTION);
        if(UtilFun.StringIsNullOrEmpty(mAction)) {
            Log.e(TAG, "调用intent缺少'PARA_ACTION'参数");
            return ;
        }

        // set fragment for tab
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //mTabFragment = new ListViewSlidingTabsFragment();
            mTabFragment = new EditNoteSlidingTabsFragment();

            transaction.replace(R.id.tabfl_note_edit_content, mTabFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_record_actbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.record_memenu_save: {
                if(null != mTabFragment) {
                    EditNoteSlidingTabsFragment etf = UtilFun.cast(mTabFragment);
                    if(etf.onAccpet()) {
                        Intent data = new Intent();
                        setResult(mAction.equals(LOAD_NOTE_ADD) ?
                                AppGobalDef.INTRET_RECORD_ADD
                                : AppGobalDef.INTRET_RECORD_MODIFY, data);
                        finish();
                    }
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
}
