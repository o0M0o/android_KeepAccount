package wxm.KeepAccount.ui.acinterface;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import butterknife.ButterKnife;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowBase;
import wxm.KeepAccount.ui.fragment.utility.FrgNoteShow;

public class ACNoteShow extends AppCompatActivity {
    private final static String TAG = "ACNoteShow";
    private FrgNoteShow     mFGNoteShow = new FrgNoteShow();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_show);

        ButterKnife.bind(this);
        init_ui(savedInstanceState);
    }

    private void init_ui(Bundle savedInstanceState) {
        // for left menu(go back)
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
        });

        if(null == savedInstanceState)  {
            FragmentTransaction t =  getFragmentManager().beginTransaction();
            t.add(R.id.fl_holder, mFGNoteShow);
            t.commit();
        }
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
                TFShowBase hot = mFGNoteShow.getHotTabItem();
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
        mFGNoteShow.disableViewPageTouch(bflag);
    }


    /**
     * 跳至对应名称的标签页
     * @param tabname 需跳转标签页的名字
     */
    public void jumpByTabName(String tabname)  {
        mFGNoteShow.jumpByTabName(tabname);
    }


    /**
     * 过滤视图数据
     * @param ls_tag 过滤数据项
     */
    public void filterView(List<String> ls_tag) {
        mFGNoteShow.filterView(ls_tag);
    }
}
