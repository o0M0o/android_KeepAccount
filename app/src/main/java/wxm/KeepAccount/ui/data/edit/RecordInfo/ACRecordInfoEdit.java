package wxm.KeepAccount.ui.data.edit.RecordInfo;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import wxm.androidutil.ExActivity.BaseAppCompatActivity;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;

public class ACRecordInfoEdit extends BaseAppCompatActivity {
    public final static String IT_PARA_RECORDTYPE = "record_type";
    private final TFEditRecordInfo mTFRecordInfo = new TFEditRecordInfo();

    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_GIVEUP;
        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void initFrgHolder() {
        Intent it = getIntent();
        mTFRecordInfo.setCurData("", it.getStringExtra(IT_PARA_RECORDTYPE));

        LOG_TAG = "ACRecordInfoEdit";
        mFGSupportHolder = mTFRecordInfo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mu_preview_edit, menu);

        menu.findItem(R.id.mi_switch).setVisible(false);
        menu.findItem(R.id.mi_giveup).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                if (mTFRecordInfo.onAccept()) {
                    int ret_data = GlobalDef.INTRET_SURE;
                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            case R.id.mi_giveup: {
                int ret_data = GlobalDef.INTRET_GIVEUP;
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
}
