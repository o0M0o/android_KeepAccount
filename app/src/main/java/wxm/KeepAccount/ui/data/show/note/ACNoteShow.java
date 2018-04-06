package wxm.KeepAccount.ui.data.show.note;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.androidutil.FrgUtility.FrgSupportSwitcher;
import wxm.androidutil.Switcher.ACSwitcherActivity;

/**
 * for Note show
 * Created by WangXM on2016/12/1.
 */
public class ACNoteShow extends ACSwitcherActivity<FrgNoteShow> {
    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_USR_LOGOUT;

        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void setupFragment(Bundle bundle) {
        addFragment(new FrgNoteShow());
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
                FrgSupportSwitcher hot = getHotFragment().getHotTabItem();
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
        getHotFragment().refreshUI();
    }

    /**
     * open/close page touch
     * @param bflag     true open page touch
     */
    public void disableViewPageTouch(boolean bflag) {
        getHotFragment().disableViewPageTouch(bflag);
    }

    /**
     * jump to page with name
     * @param tabname   tab name
     */
    public void jumpByTabName(String tabname) {
        getHotFragment().jumpByTabName(tabname);
    }
}
