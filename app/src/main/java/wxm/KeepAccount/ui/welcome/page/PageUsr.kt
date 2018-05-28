package wxm.KeepAccount.ui.welcome.page

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.allure.lbanners.LMBanners
import com.allure.lbanners.transformer.TransitionEffect
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.define.EAction
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.data.edit.NoteCreate.ACNoteCreate
import wxm.KeepAccount.ui.data.edit.NoteEdit.ACNoteEdit
import wxm.KeepAccount.ui.data.show.calendar.ACCalendarShow
import wxm.KeepAccount.ui.data.show.note.ACNoteShow
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.ui.welcome.banner.FrgAdapter
import wxm.KeepAccount.ui.welcome.banner.FrgPara
import wxm.KeepAccount.utility.ContextUtil
import wxm.KeepAccount.utility.DGVButtonAdapter
import wxm.KeepAccount.utility.PreferencesUtil
import wxm.KeepAccount.utility.let1
import wxm.androidutil.time.CalendarUtility
import wxm.androidutil.ui.dragGrid.DragGridView
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.uilib.IconButton.IconButton
import java.util.*

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class PageUsr : FrgSupportBaseAdv(), PageBase {
    // for ui
    private val mIVUsr: ImageView by bindView(R.id.iv_usr)
    private val mTVUsrName: TextView by bindView(R.id.tv_usr_name)
    private val mIBLogout: IconButton by bindView(R.id.ib_logout)

    override fun getLayoutID(): Int = R.layout.page_usr
    override fun leavePage(): Boolean = true

    override fun initUI(savedInstanceState: Bundle?) {
        ContextUtil.curUsr?.let1 {
            mIVUsr.setImageResource(R.drawable.ic_usr_big)
            mTVUsrName.text = it.name

            mIBLogout.setOnClickListener {
                doLogout(activity)
            }
        }
    }
}
