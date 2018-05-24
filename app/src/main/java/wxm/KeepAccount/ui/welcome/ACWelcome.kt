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
import wxm.KeepAccount.R
import wxm.KeepAccount.define.EAction
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.data.edit.NoteCreate.ACNoteCreate
import wxm.KeepAccount.ui.data.edit.NoteEdit.ACNoteEdit
import wxm.KeepAccount.ui.data.show.calendar.ACCalendarShow
import wxm.KeepAccount.ui.data.show.note.ACNoteShow
import wxm.KeepAccount.ui.dialog.DlgUsrMessage
import wxm.KeepAccount.ui.help.ACHelp
import wxm.KeepAccount.ui.setting.ACSetting
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.let1
import wxm.androidutil.time.CalendarUtility
import wxm.androidutil.ui.dialog.DlgOKOrNOBase

/**
 * first page after login
 */
class ACWelcome : AppCompatActivity(),
        View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    private val mFGWelcome: FrgWelcome = FrgWelcome()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_welcome)

        initComponent(savedInstanceState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        mFGWelcome.reInitUI()
    }


    override fun onBackPressed() {
        findViewById<DrawerLayout>(R.id.ac_welcome)!!.let {
            if (it.isDrawerOpen(GravityCompat.START)) {
                it.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onClick(v: View) {
        when (v) {
            is RelativeLayout -> {
                v.findViewById<TextView>(R.id.tv_name)?.let {
                    doClick(it.text.toString())
                }
            }

            is Button -> {
                doClick(v.text.toString())
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.nav_help -> {
                Intent(this, ACHelp::class.java).apply {
                    putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_START)
                    startActivityForResult(this, 1)
                }
            }

            R.id.nav_setting -> {
                startActivityForResult(Intent(this, ACSetting::class.java),
                        1)
            }

        /*
        R.id.nav_share_app -> {
            Toast.makeText(applicationContext, "invoke share!", Toast.LENGTH_SHORT)
                    .show()
        }
        */

            R.id.nav_contact_writer -> {
                contactWriter()
            }
        }

        findViewById<DrawerLayout>(R.id.ac_welcome)!!.closeDrawer(GravityCompat.START)
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
                Intent(this, ACNoteShow::class.java).apply {
                    putExtra(NoteDataHelper.INTENT_PARA_FIRST_TAB,
                            NoteDataHelper.TAB_TITLE_BUDGET)
                    startActivityForResult(this, 1)
                }
            }

            EAction.LOOK_DATA -> {
                startActivityForResult(Intent(this, ACNoteShow::class.java), 1)
            }

            EAction.CALENDAR_VIEW -> {
                startActivityForResult(Intent(this, ACCalendarShow::class.java), 1)
            }

            EAction.ADD_BUDGET -> {
                Intent(this, ACNoteEdit::class.java).apply {
                    putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET)
                    startActivityForResult(this, 1)
                }
            }

            EAction.ADD_DATA -> {
                Intent(this, ACNoteCreate::class.java).apply {
                    putExtra(GlobalDef.STR_RECORD_DATE,
                            CalendarUtility.SDF_YEAR_MONTH_DAY_HOUR_MINUTE.format(System.currentTimeMillis()))

                    startActivityForResult(this, 1)
                }
            }

            EAction.LOGOUT -> {
                setResult(GlobalDef.INTRET_USR_LOGOUT, Intent())
                finish()
            }
        }
    }

    /**
     * init UI component
     * @param savedInstanceState    saved component state
     */
    private fun initComponent(savedInstanceState: Bundle?) {
        // set nav view
        findViewById<Toolbar>(R.id.ac_navw_toolbar)!!.let1 { tb ->
            setSupportActionBar(tb)

            findViewById<DrawerLayout>(R.id.ac_welcome)!!.let1 {
                val toggle = ActionBarDrawerToggle(this, it, tb,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close)
                it.addDrawerListener(toggle)
                toggle.syncState()
            }
        }

        findViewById<NavigationView>(R.id.start_nav_view)!!
                .setNavigationItemSelectedListener(this)

        // load fragment
        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction().let1 {
                it.add(R.id.fl_holder, mFGWelcome)
                it.commit()
            }
        }
    }

    /**
     * invoke email app to send email
     */
    private fun contactWriter() {
        DlgUsrMessage().let1 {
            it.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                override fun onDialogPositiveResult(dialogFragment: DialogFragment) {}

                override fun onDialogNegativeResult(dialogFragment: DialogFragment) {}
            })

            it.show(supportFragmentManager, "send message")
        }
    }
}
