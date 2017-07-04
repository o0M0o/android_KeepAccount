package wxm.KeepAccount.ui.data.show.note;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.androidutil.ExActivity.BaseAppCompatActivity;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.data.show.note.ShowData.TFShowBase;

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
        mFGHolder = new FrgNoteShow();
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
                TFShowBase hot = ((FrgNoteShow) mFGHolder).getHotTabItem();
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
        ((FrgNoteShow) mFGHolder).refreshUI();
    }

    /**
     * 关闭/打开触摸功能
     *
     * @param bflag 若为true则打开触摸功能，否则关闭触摸功能
     */
    public void disableViewPageTouch(boolean bflag) {
        ((FrgNoteShow) mFGHolder).disableViewPageTouch(bflag);
    }

    /**
     * 跳至对应名称的标签页
     *
     * @param tabname 需跳转标签页的名字
     */
    public void jumpByTabName(String tabname) {
        ((FrgNoteShow) mFGHolder).jumpByTabName(tabname);
    }
}
