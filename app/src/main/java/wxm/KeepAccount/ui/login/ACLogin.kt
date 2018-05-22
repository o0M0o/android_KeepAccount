package wxm.KeepAccount.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.os.Process
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import wxm.KeepAccount.R
import wxm.KeepAccount.define.EMsgType
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.help.ACHelp
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.log.TagLog

/**
 * A login screen that offers login via email/password.
 */
class ACLogin : AppCompatActivity() {
    private val mFGLogin = FrgLogin()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_login)

        init_ui(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater = menuInflater
        inflater.inflate(R.menu.mu_login, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.acm_mi_leave -> {
                Toast.makeText(applicationContext,
                        "退出应用!",
                        Toast.LENGTH_SHORT).show()

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    return false
                }

                //finish();
                Process.killProcess(Process.myPid())
            }

            R.id.acm_mi_help -> {
                val intent = Intent(this, ACHelp::class.java)
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_MAIN)

                startActivityForResult(intent, 1)
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    /**
     * 初始化UI组件
     */
    private fun init_ui(savedInstanceState: Bundle?) {
        // for frg
        if (null == savedInstanceState) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fl_login, mFGLogin)
            transaction.commit()
        }
    }


    /**
     * 其它activity返回结果
     *
     * @param requestCode request param
     * @param resultCode  result param
     * @param data        data param
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        var bResetView = false
        when (resultCode) {
            GlobalDef.INTRET_USR_ADD -> {
                TagLog.i("从'添加新帐户'页面返回")
                bResetView = true
            }

            GlobalDef.INTRET_GIVEUP -> TagLog.i("从'添加新帐户'页面返回(放弃添加新帐户)")

            GlobalDef.INTRET_USR_LOGOUT -> {
                TagLog.i("注销帐户")
                bResetView = true

                Message.obtain(ContextUtil.msgHandler, EMsgType.USR_LOGOUT.id).sendToTarget()
            }

            else -> TagLog.d(String.format("不处理的resultCode(%d)!", resultCode))
        }

        if (bResetView) {
            mFGLogin.reInitUI()
        }
    }
}

