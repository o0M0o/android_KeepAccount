package wxm.KeepAccount.ui.welcome.page

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.theartofdev.edmodo.cropper.CropImage
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.db.UsrDBUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText
import wxm.KeepAccount.ui.welcome.base.PageBase
import wxm.KeepAccount.utility.AppUtil
import wxm.KeepAccount.utility.let1
import wxm.KeepAccount.utility.saveImage
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.util.doJudge
import wxm.uilib.IconButton.IconButton
import java.io.File

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class PageUsr : FrgSupportBaseAdv(), PageBase {
    // for ui
    private val mIVUsr: ImageView by bindView(R.id.iv_usr)
    private val mTVUsrName: TextView by bindView(R.id.tv_usr_name)
    private val mIBLogout: IconButton by bindView(R.id.ib_logout)
    private val mIBAccept: ImageButton by bindView(R.id.ib_accept)

    private val mIBChangePwd: IconButton by bindView(R.id.ib_change_pwd)
    private val mCLInputPwd: ConstraintLayout by bindView(R.id.cl_input_pwd)
    private val mCLChangePwd: ConstraintLayout by bindView(R.id.cl_change_pwd)

    private val mTENewPwd: TouchEditText by bindView(R.id.te_new_pwd)
    private val mTERepeatNewPwd: TouchEditText by bindView(R.id.te_repeat_new_pwd)

    override fun getLayoutID(): Int = R.layout.pg_usr
    //override fun isUseEventBus(): Boolean = true
    override fun leavePage(): Boolean = true

    override fun initUI(savedInstanceState: Bundle?) {
        mIBLogout.setOnClickListener(::onClick)
        mIVUsr.setOnClickListener(::onClick)

        mIBChangePwd.setColdOrHot(false)
        mCLInputPwd.visibility = View.GONE
        mIBChangePwd.setOnClickListener(::onClick)
        mIBAccept.setOnClickListener(::onClick)

        view!!.setOnClickListener(::onClick)

        autoScroll(R.id.te_old_pwd, mCLChangePwd)
        autoScroll(R.id.te_new_pwd, mCLChangePwd)
        autoScroll(R.id.te_repeat_new_pwd, mCLChangePwd)

        loadUI(savedInstanceState)
    }

    override fun loadUI(savedInstanceState: Bundle?) {
        if(AppUtil.curUsr!!.name != GlobalDef.DEF_USR_NAME) {
            mIBChangePwd.setColdOrHot(false)
            mCLInputPwd.visibility = View.GONE
        } else  {
            mCLChangePwd.visibility = View.GONE
        }
        loadUsrInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.getActivityResult(data).let1 { result ->
                if (resultCode == Activity.RESULT_OK) {
                    saveImage(result.uri).let1 {
                        if (it.isNotEmpty()) {
                            if (AppUtil.usrUtility.changeIcon(AppUtil.curUsr!!, it)) {
                                loadUsrInfo()
                            }
                        }
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    DlgAlert.showAlert(activity!!, R.string.dlg_erro, result.error.toString())
                }
            }
        }
    }

    fun onClick(vw: View) {
        when (vw.id) {
            R.id.ib_logout -> doLogout(activity!!)
            R.id.ib_change_pwd -> {
                showChangePwd(!mIBChangePwd.isHot)
            }

            R.id.iv_usr -> {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .start(context!!, this)
            }

            R.id.ib_accept -> {
                if(checkPwd())  {
                    AppUtil.usrUtility.changePwd(AppUtil.curUsr!!, mTENewPwd.text.toString())
                            .doJudge(
                                    {DlgAlert.showAlert(context!!, R.string.dlg_info, R.string.info_change_pwd_success)},
                                    {DlgAlert.showAlert(context!!, R.string.dlg_warn, R.string.info_change_pwd_success)})

                    showChangePwd(false)
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

    private fun showChangePwd(show:Boolean) {
        mCLInputPwd.visibility = show.doJudge(View.VISIBLE, View.GONE)
        mIBAccept.visibility = show.doJudge(View.VISIBLE, View.GONE)
        mIBChangePwd.setColdOrHot(show)
    }

    private fun loadUsrInfo()   {
        AppUtil.curUsr?.let1 {
            mIVUsr.setImageURI(Uri.fromFile(File(it.iconPath)))
            mTVUsrName.text = it.name
        }
    }


    private fun checkPwd(): Boolean {
        val checkEmpty = {te:TouchEditText ->
            if(te.text.isEmpty()) {
                te.error = getString(R.string.error_field_required)
                te.requestFocus()

                true
            } else  false
        }

        if(checkEmpty(mTENewPwd) || checkEmpty(mTERepeatNewPwd))
            return false

        if(mTENewPwd.text.toString() != mTERepeatNewPwd.text.toString())    {
            mTERepeatNewPwd.error = getString(R.string.error_pwd_not_match)
            mTERepeatNewPwd.requestFocus()

            return false
        }

        when(AppUtil.usrUtility.pwdValidity(mTENewPwd.text.toString())) {
            UsrDBUtility.RET_PWD_TO_SHORT -> {
                DlgAlert.showAlert(context!!, R.string.dlg_erro, R.string.error_password_to_short)
                return false
            }
        }

        return true
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
            vwHome.scrollY = if (hasFocus) {
                getTop(topVW) - getTop(vwHome)
            } else 0
        })
    }
}
