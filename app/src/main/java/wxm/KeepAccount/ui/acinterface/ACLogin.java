package wxm.KeepAccount.ui.acinterface;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.ButterKnife;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppMsgDef;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.utility.FrgLogin;

/**
 * A login screen that offers login via email/password.
 */
public class ACLogin extends AppCompatActivity {
    private static final String TAG = "ACLogin";
    private FrgLogin mFGLogin = new FrgLogin();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_login);

        ButterKnife.bind(this);
        init_ui(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mu_login, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acm_mi_leave: {
                Toast.makeText(getApplicationContext(),
                        "退出应用!",
                        Toast.LENGTH_SHORT).show();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return false;
                }

                //finish();
                Process.killProcess(Process.myPid());
            }
            break;

            case R.id.acm_mi_help: {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_MAIN);

                startActivityForResult(intent, 1);
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    /**
     * 初始化UI组件
     */
    private void init_ui(Bundle savedInstanceState) {
        // for frg
        if(null == savedInstanceState) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fl_login, mFGLogin);
            transaction.commit();
        }
    }


    /**
     * 其它activity返回结果
     *
     * @param requestCode request param
     * @param resultCode  result param
     * @param data        data param
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean b_resetview = false;
        if (AppGobalDef.INTRET_USR_ADD == resultCode) {
            Log.i(TAG, "从'添加新帐户'页面返回");
            b_resetview = true;
        } else if (AppGobalDef.INTRET_GIVEUP == resultCode) {
            Log.i(TAG, "从'添加新帐户'页面返回(放弃添加新帐户)");
        } else if (AppGobalDef.INTRET_USR_LOGOUT == resultCode) {
            Log.i(TAG, "注销帐户");
            b_resetview = true;

            Message m = Message.obtain(ContextUtil.getMsgHandler(),
                    AppMsgDef.MSG_USR_LOGOUT);
            m.sendToTarget();
        } else {
            Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
        }

        if (b_resetview) {
            mFGLogin.refreshUI();
        }
    }
}

