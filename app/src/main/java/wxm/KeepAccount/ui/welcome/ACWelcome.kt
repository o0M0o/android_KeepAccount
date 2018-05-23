package wxm.KeepAccount.ui.welcome

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import java.util.Calendar
import java.util.Locale

import wxm.KeepAccount.define.EAction
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.data.edit.NoteCreate.ACNoteCreate
import wxm.KeepAccount.ui.data.edit.NoteEdit.ACNoteEdit
import wxm.KeepAccount.ui.data.show.calendar.ACCalendarShow
import wxm.KeepAccount.ui.data.show.note.ACNoteShow
import wxm.KeepAccount.ui.dialog.DlgUsrMessage
import wxm.KeepAccount.ui.help.ACHelp
import wxm.KeepAccount.ui.setting.ACSetting
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.R

/**
 * first page after login
 */
class ACWelcome : AppCompatActivity(), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private var mFGWelcome: FrgWelcome? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_welcome)

        initComponent(savedInstanceState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        mFGWelcome!!.reInitUI()
    }


    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.ac_welcome)!!
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View) {
        if (v is RelativeLayout) {
            val tv = UtilFun.cast<TextView>(v.findViewById(R.id.tv_name))
            if (null != tv) {
                doClick(tv.text.toString())
            }
        } else if (v is Button) {
            val bt = UtilFun.cast<Button>(v)
            doClick(bt.text.toString())
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.nav_help -> {
                val intent = Intent(this, ACHelp::class.java)
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_START)

                startActivityForResult(intent, 1)
            }

            R.id.nav_setting -> {
                val intent = Intent(this, ACSetting::class.java)
                startActivityForResult(intent, 1)
            }

            R.id.nav_share_app -> {
                Toast.makeText(applicationContext,
                        "invoke share!",
                        Toast.LENGTH_SHORT).show()
            }

            R.id.nav_contact_writer -> {
                contactWriter()
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.ac_welcome)!!
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * click handler
     * @param act   name for action
     */
    private fun doClick(act: String) {
        val ea = EAction.getEAction(act)!!
        when (ea) {
            EAction.LOOK_BUDGET -> {
                val intent = Intent(this, ACNoteShow::class.java)
                intent.putExtra(NoteDataHelper.INTENT_PARA_FIRST_TAB,
                        NoteDataHelper.TAB_TITLE_BUDGET)
                startActivityForResult(intent, 1)
            }

            EAction.LOOK_DATA -> {
                val intent = Intent(this, ACNoteShow::class.java)
                startActivityForResult(intent, 1)
            }

            EAction.CALENDAR_VIEW -> {
                val intent = Intent(this, ACCalendarShow::class.java)
                startActivityForResult(intent, 1)
            }

            EAction.ADD_BUDGET -> {
                val intent = Intent(this, ACNoteEdit::class.java)
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET)
                startActivityForResult(intent, 1)
            }

            EAction.ADD_DATA -> {
                val intent = Intent(this, ACNoteCreate::class.java)
                val cal = Calendar.getInstance()
                cal.timeInMillis = System.currentTimeMillis()
                intent.putExtra(GlobalDef.STR_RECORD_DATE,
                        String.format(Locale.CHINA, "%d-%02d-%02d %02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)))

                startActivityForResult(intent, 1)
            }

            EAction.LOGOUT -> {
                setResult(GlobalDef.INTRET_USR_LOGOUT, Intent())
                finish()
            }
        }/*
            case ActionHelper.ACT_ADD_REMIND: {
                Intent intent = new Intent(this, ACRemindEdit.class);
                startActivityForResult(intent, 1);
            }
            break;
            */
    }

    /**
     * init UI component
     * @param savedInstanceState    saved component state
     */
    private fun initComponent(savedInstanceState: Bundle?) {
        // set nav view
        val tb = UtilFun.cast<Toolbar>(findViewById(R.id.ac_navw_toolbar))
        setSupportActionBar(tb)

        val drawer = UtilFun.cast<DrawerLayout>(findViewById(R.id.ac_welcome))!!
        val toggle = ActionBarDrawerToggle(
                this, drawer, tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val nv = UtilFun.cast<NavigationView>(findViewById(R.id.start_nav_view))!!
        nv.setNavigationItemSelectedListener(this)

        // load fragment
        if (null == savedInstanceState) {
            mFGWelcome = FrgWelcome()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.fl_holder, mFGWelcome)
            ft.commit()
        }
    }

    /**
     * invoke email app to send email
     */
    private fun contactWriter() {
        val dlg = DlgUsrMessage()
        dlg.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
            override fun onDialogPositiveResult(dialogFragment: DialogFragment) {}

            override fun onDialogNegativeResult(dialogFragment: DialogFragment) {}
        })

        dlg.show(supportFragmentManager, "send message")
    }
}
