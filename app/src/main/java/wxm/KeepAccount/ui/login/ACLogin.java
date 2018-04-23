package wxm.KeepAccount.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import wxm.KeepAccount.R;
import wxm.KeepAccount.define.EMsgType;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.help.ACHelp;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * A login screen that offers login via email/password.
 */
public class ACLogin extends AppCompatActivity {
    private static final String TAG = "ACLogin";
    private final FrgLogin mFGLogin = new FrgLogin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_login);

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
        if (null == savedInstanceState) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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
        switch (resultCode) {
            case GlobalDef.INTRET_USR_ADD:
                Log.i(TAG, "从'添加新帐户'页面返回");
                b_resetview = true;
                break;

            case GlobalDef.INTRET_GIVEUP:
                Log.i(TAG, "从'添加新帐户'页面返回(放弃添加新帐户)");
                break;

            case GlobalDef.INTRET_USR_LOGOUT:
                Log.i(TAG, "注销帐户");
                b_resetview = true;

                Message m = Message.obtain(ContextUtil.Companion.getMsgHandler(),
                        EMsgType.USR_LOGOUT.getId());
                m.sendToTarget();
                break;

            default:
                Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
                break;
        }

        if (b_resetview) {
            mFGLogin.reInitUI();
        }
    }
}

