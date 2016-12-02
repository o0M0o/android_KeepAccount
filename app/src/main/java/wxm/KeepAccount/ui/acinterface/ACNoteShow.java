package wxm.KeepAccount.ui.acinterface;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.define.BaseAppCompatActivity;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBase;
import wxm.KeepAccount.ui.fragment.utility.FrgNoteShow;

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
        inflater.inflate(R.menu.note_show, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_help: {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_RECORD);

                startActivityForResult(intent, 1);
            }
            break;

            case R.id.mi_switch:   {
                TFShowBase hot = ((FrgNoteShow)mFGHolder).getHotTabItem();
                if(null != hot)  {
                    hot.switchPage();
                }
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }


    /**
     * 关闭/打开触摸功能
     * @param bflag  若为true则打开触摸功能，否则关闭触摸功能
     */
    public void disableViewPageTouch(boolean bflag) {
        ((FrgNoteShow)mFGHolder).disableViewPageTouch(bflag);
    }


    /**
     * 跳至对应名称的标签页
     * @param tabname 需跳转标签页的名字
     */
    public void jumpByTabName(String tabname)  {
        ((FrgNoteShow)mFGHolder).jumpByTabName(tabname);
    }


    /**
     * 过滤视图数据
     * @param ls_tag 过滤数据项
     */
    public void filterView(List<String> ls_tag) {
        ((FrgNoteShow)mFGHolder).filterView(ls_tag);
    }
}
