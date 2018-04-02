package wxm.KeepAccount.ui.data.edit.Note;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.SwitcherActivity.ACSwitcherActivity;
import wxm.KeepAccount.define.GlobalDef;
import wxm.androidutil.util.UtilFun;

/**
 * income/pay record UI
 */
public class ACNoteAdd extends ACSwitcherActivity<FrgNoteAdd> {
    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_GIVEUP;

        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void initUi(Bundle savedInstanceState)    {
        super.initUi(savedInstanceState);
        // for holder
        FrgNoteAdd hf = new FrgNoteAdd();
        Intent it = getIntent();
        assert null != it;

        Bundle bd = new Bundle();
        String date = it.getStringExtra(GlobalDef.STR_RECORD_DATE);
        if (!UtilFun.StringIsNullOrEmpty(date)) {
            bd.putString(GlobalDef.STR_RECORD_DATE, date);
        }
        hf.setArguments(bd);

        addFragment(hf);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mu_note_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                FrgNoteAdd hf = getHotFragment();
                if (hf.onAccept()) {
                    int ret_data = GlobalDef.INTRET_SURE;
                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            case R.id.mi_giveup: {
                leaveActivity();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }
}
