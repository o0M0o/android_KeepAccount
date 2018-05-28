package wxm.KeepAccount.ui.welcome.page

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
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

    private val mIBChangePwd: IconButton by bindView(R.id.ib_change_pwd)
    private val mCLInputPwd: ConstraintLayout by bindView(R.id.cl_input_pwd)
    private val mCLChangePwd: ConstraintLayout by bindView(R.id.cl_change_pwd)

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

        mIBChangePwd.setColdOrHot(false)
        mCLInputPwd.visibility = View.GONE
        mIBChangePwd.setOnClickListener { vw ->
            if(mIBChangePwd.isHot)  {
                mCLInputPwd.visibility = View.GONE
                mIBChangePwd.setColdOrHot(false)
            } else  {
                mCLInputPwd.visibility = View.VISIBLE
                mIBChangePwd.setColdOrHot(true)
            }
        }

        val vwHome = view!!
        vwHome.setOnClickListener { vw ->
            if(0 != vwHome.scrollY) {
                vwHome.scrollY = 0
            }
        }

        getDisplayTop(mCLChangePwd).let1 {
            autoScroll(R.id.te_old_pwd, it)
            autoScroll(R.id.te_new_pwd, it)
            autoScroll(R.id.te_repeat_new_pwd, it)
        }
    }


    /**
     * auto scroll to view [vw] with margin to top [topMargin]
     */
    private fun autoScroll(vw: Any, top: Int) {
        val vwHome = view!!
        { v: View, hasFocus: Boolean ->
            vwHome.scrollY = if (hasFocus) {
                getDisplayTop(mCLChangePwd) - getDisplayTop(vwHome)
            } else 0
        }.apply{
            when(vw)    {
                is Int -> vwHome.findViewById<View>(vw)!!.setOnFocusChangeListener(this)
                is View -> vw.setOnFocusChangeListener(this)
                else -> throw IllegalStateException("${vw.javaClass.name} not support scroll!")
            }
        }
    }

    private fun getDisplayTop(vw:View): Int   {
        return Rect().apply { vw.getGlobalVisibleRect(this) }.top
    }
}
