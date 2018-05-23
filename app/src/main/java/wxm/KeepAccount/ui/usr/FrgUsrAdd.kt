package wxm.KeepAccount.ui.usr

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.define.EMsgType
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.UsrItem
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.app.AppBase
import wxm.androidutil.log.TagLog
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.view.EventHelper
import wxm.androidutil.util.UtilFun
import wxm.androidutil.util.WRMsgHandler

/**
 * add user
 * Created by WangXM on 2016/11/29.
 */
class FrgUsrAdd : FrgSupportBaseAdv(), TextView.OnEditorActionListener {
    // for ui
    private val mETUsrName: EditText by bindView(R.id.et_usr_name)
    private val mETPwd: EditText by bindView(R.id.et_pwd)
    private val mETRepeatPwd: EditText by bindView(R.id.et_repeat_pwd)

    // for res
    private val mRSErrorNoUsrName: String = AppBase.getString(R.string.error_no_usr_name)
    private val mRSErrorInvalidPWD: String = AppBase.getString(R.string.error_invalid_password)
    private var mRSErrorRepeatPwdNotMatch: String = AppBase.getString(R.string.error_pwd_not_match)

    // for data
    private var mMHHandler: LocalMsgHandler = LocalMsgHandler(this)

    override fun getLayoutID(): Int {
        return R.layout.vw_usr_add
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            mETUsrName.setOnEditorActionListener(this)
            mETPwd.setOnEditorActionListener(this)
            mETRepeatPwd.setOnEditorActionListener(this)

            EventHelper.setOnClickListener(view!!,
                    intArrayOf(R.id.bt_confirm, R.id.bt_giveup),
                    View.OnClickListener { v ->
                        when (v.id) {
                            R.id.bt_confirm -> {
                                if (checkInput()) {
                                    val data = Intent().apply {
                                        putExtra(UsrItem.FIELD_NAME, mETUsrName.text.toString())
                                        putExtra(UsrItem.FIELD_PWD, mETPwd.text.toString())
                                    }

                                    Message.obtain(ContextUtil.msgHandler,
                                            EMsgType.USR_ADD.id).let {
                                        it.obj = arrayOf(data, mMHHandler)
                                        it.sendToTarget()
                                    }
                                }
                            }

                            R.id.bt_giveup -> {
                                activity.let {
                                    it.setResult(GlobalDef.INTRET_GIVEUP, Intent())
                                    it.finish()
                                }
                            }
                        }
                    })
        }
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        when (v.id) {
            R.id.et_usr_name -> {
                TagLog.d("now usr name : " + mETUsrName.text.toString())
            }

            R.id.et_pwd -> {
                TagLog.d("now pwd : " + mETPwd.text.toString())
            }

            R.id.et_repeat_pwd -> {
                TagLog.d("now repeatPWD : " + mETRepeatPwd.text.toString())
            }
        }

        return false
    }


    /// PRIVATE BEGIN
    /**
     * clean UI
     */
    private fun repeatInput() {
        mETUsrName.setText("")
        mETPwd.setText("")
        mETRepeatPwd.setText("")

        mETUsrName.requestFocus()
    }

    /**
     * check input data then give prompting message
     * @return  true if data legal else false
     */
    private fun checkInput(): Boolean {
        val usrName = mETUsrName.text.toString()
        val usrPwd = mETPwd.text.toString()
        val usrRPwd = mETRepeatPwd.text.toString()

        if (UtilFun.StringIsNullOrEmpty(usrName)) {
            mETUsrName.apply {
                error = mRSErrorNoUsrName
                requestFocus()
            }

            return false
        }

        if (4 > usrPwd.length) {
            mETPwd.apply {
                error = mRSErrorInvalidPWD
                requestFocus()
            }

            return false
        }

        if (usrPwd != usrRPwd) {
            mETRepeatPwd.apply {
                error = mRSErrorRepeatPwdNotMatch
                requestFocus()
            }

            return false
        }

        return true
    }
    /// PRIVATE END

    private class LocalMsgHandler internal constructor(ac: FrgUsrAdd) : WRMsgHandler<FrgUsrAdd>(ac) {
        override fun processMsg(m: Message, home: FrgUsrAdd) {
            when(EMsgType.getEMsgType(m.what))  {
                EMsgType.REPLAY -> {
                    EMsgType.getEMsgType(m.arg1)?.let {
                        if (EMsgType.USR_ADD === it) {
                            afterAddUsr(m, home)
                        }
                    }
                }

                else  -> {
                    TagLog.e("msg($m) can not process")
                }
            }
        }

        private fun afterAddUsr(m: Message, home: FrgUsrAdd) {
            val arr = UtilFun.cast_t<Array<Any>>(m.obj)
            if (arr[0] as Boolean) {
                home.activity.apply {
                    setResult(GlobalDef.INTRET_USR_ADD, arr[1] as Intent)
                    finish()
                }
            } else {
                val sstr = if (2 < arr.size) UtilFun.cast(arr[2])
                else "添加用户失败!"

                Toast.makeText(ContextUtil.self, sstr, Toast.LENGTH_LONG).show()
                home.repeatInput()
            }
        }
    }
}
