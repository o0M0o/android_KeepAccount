package wxm.KeepAccount.ui.welcome


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
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
import wxm.androidutil.ui.activity.ACSwitcherActivity
import wxm.androidutil.ui.dialog.DlgOKOrNOBase

/**
 * first page after login
 */
class ACWelcome : ACSwitcherActivity<FrgWelcome>()  {
    override fun setupFragment(savedInstanceState: Bundle?) {
        addFragment(FrgWelcome())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        hotFragment.reInitUI()
    }

    override fun leaveActivity() {
        setResult(GlobalDef.INTRET_USR_LOGOUT, Intent())
        finish()
    }
}
