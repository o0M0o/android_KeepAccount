package wxm.KeepAccount.ui.data.show.note;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.KeepAccount.ui.base.Switcher.FrgSwitcher;
import wxm.androidutil.ExActivity.BaseAppCompatActivity;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * for Note show
 * Created by wxm on 2016/12/1.
 */
public class ACNoteShow extends BaseAppCompatActivity {

    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_USR_LOGOUT;

        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACNoteShow";
        mFGSupportHolder = new FrgNoteShow();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mu_note_show, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_switch: {
                FrgSwitcher hot = ((FrgNoteShow) mFGSupportHolder).getHotTabItem();
                if (null != hot) {
                    hot.switchPage();
                }
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        ((FrgNoteShow) mFGSupportHolder).refreshUI();
    }

    /**
     * open/close page touch
     * @param bflag     true open page touch
     */
    public void disableViewPageTouch(boolean bflag) {
        ((FrgNoteShow) mFGSupportHolder).disableViewPageTouch(bflag);
    }

    /**
     * jump to page with name
     * @param tabname   tab name
     */
    public void jumpByTabName(String tabname) {
        ((FrgNoteShow) mFGSupportHolder).jumpByTabName(tabname);
    }
}
