package wxm.KeepAccount.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.os.Process
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import wxm.KeepAccount.R
import wxm.KeepAccount.define.EMsgType
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.help.ACHelp
import wxm.KeepAccount.utility.AppUtil
import wxm.androidutil.improve.let1
import wxm.androidutil.log.TagLog
import wxm.androidutil.ui.activity.ACSwitcherActivity

/**
 * A login screen that offers login via email/password.
 */
class ACLogin : ACSwitcherActivity<FrgLogin>() {
    override fun setupFragment(savedInstanceState: Bundle?) {
        addFragment(FrgLogin())
        findViewById<Toolbar>(R.id.toolbar).let1 {
            it.setTitleTextAppearance(this, R.style.AppTheme_ActionBar_Text)
            it.overflowIcon?.setTint(getColor(R.color.white))
        }
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
     * 其它activity返回结果
     *
     * @param requestCode request param
     * @param resultCode  result param
     * @param data        data param
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            GlobalDef.INTRET_USR_LOGOUT -> {
                TagLog.i("usr logout")
                Message.obtain(AppUtil.msgHandler, EMsgType.USR_LOGOUT.id).sendToTarget()
            }

            else -> TagLog.d("do nothing for resultCode = $resultCode!")
        }
    }
}

