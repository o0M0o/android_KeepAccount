package wxm.KeepAccount.ui.welcome.page

import android.graphics.Rect
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil
import wxm.KeepAccount.utility.let1
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.uilib.IconButton.IconButton

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

            mIBLogout.setOnClickListener(::onClick)
        }

        mIBChangePwd.setColdOrHot(false)
        mCLInputPwd.visibility = View.GONE
        mIBChangePwd.setOnClickListener(::onClick)

        view!!.setOnClickListener(::onClick)

        autoScroll(R.id.te_old_pwd, mCLChangePwd)
        autoScroll(R.id.te_new_pwd, mCLChangePwd)
        autoScroll(R.id.te_repeat_new_pwd, mCLChangePwd)
    }

    fun onClick(vw:View)    {
        when(vw.id) {
            R.id.ib_logout -> doLogout(activity!!)
            R.id.ib_change_pwd ->  {
                if (mIBChangePwd.isHot) {
                    mCLInputPwd.visibility = View.GONE
                    mIBChangePwd.setColdOrHot(false)
                } else {
                    mCLInputPwd.visibility = View.VISIBLE
                    mIBChangePwd.setColdOrHot(true)
                }
            }

            else -> {
                view!!.let1 {
                    if (0 != it.scrollY) {
                        it.scrollY = 0
                    }
                }
            }
        }
    }


    /**
     * auto scroll to view [v] to top of [topVW]
     */
    private fun autoScroll(v: Any, topVW: Any) {
        val vwHome = view!!

        val getVWObj = { vw: Any ->
            when (vw) {
                is Int -> vwHome.findViewById(vw)!!
                is View -> vw
                else -> throw IllegalStateException("${vw.javaClass.name} not view!")
            }
        }

        val getTop = { vw: Any ->
            Rect().apply { getVWObj(vw).getGlobalVisibleRect(this) }.top
        }

        getVWObj(v).setOnFocusChangeListener({ _: View, hasFocus: Boolean ->
            vwHome.scrollY = if (hasFocus) { getTop(topVW) - getTop(vwHome)
            } else 0
        })
    }
}
