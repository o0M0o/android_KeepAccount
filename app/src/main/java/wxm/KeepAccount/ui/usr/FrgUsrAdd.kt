package wxm.KeepAccount.ui.usr

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import butterknife.BindString
import butterknife.BindView
import butterknife.OnClick
import wxm.KeepAccount.define.EMsgType
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.androidutil.util.WRMsgHandler
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.UsrItem
import wxm.KeepAccount.utility.ContextUtil

/**
 * add user
 * Created by WangXM on 2016/11/29.
 */
class FrgUsrAdd : FrgSupportBaseAdv(), TextView.OnEditorActionListener {
    // for ui
    @BindView(R.id.et_usr_name)
    private var mETUsrName: EditText? = null
    @BindView(R.id.et_pwd)
    private var mETPwd: EditText? = null
    @BindView(R.id.et_repeat_pwd)
    internal var mETRepeatPwd: EditText? = null
    // fro res
    @BindString(R.string.error_no_usrname)
    internal var mRSErrorNoUsrName: String? = null

    @BindString(R.string.error_invalid_password)
    internal var mRSErrorInvalidPWD: String? = null
    @BindString(R.string.error_repeatpwd_notmatch)
    internal var mRSErrorRepeatPwdNotMatch: String? = null
    // for data
    private var mMHHandler: LocalMsgHandler = LocalMsgHandler(this)

    override fun getLayoutID(): Int {
        return R.layout.vw_usr_add
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(bundle: Bundle?) {
        mETUsrName!!.setOnEditorActionListener(this)
        mETPwd!!.setOnEditorActionListener(this)
        mETRepeatPwd!!.setOnEditorActionListener(this)
    }


    @OnClick(R.id.bt_confirm, R.id.bt_giveup)
    internal fun onSelfClick(v: View) {
        when (v.id) {
            R.id.bt_confirm -> {
                if (checkInput()) {
                    val data = Intent()
                    data.putExtra(UsrItem.FIELD_NAME, mETUsrName!!.text.toString())
                    data.putExtra(UsrItem.FIELD_PWD, mETPwd!!.text.toString())

                    val m = Message.obtain(ContextUtil.msgHandler,
                            EMsgType.USR_ADD.id)
                    m.obj = arrayOf<Any>(data, mMHHandler)
                    m.sendToTarget()
                }
            }

            R.id.bt_giveup -> {
                val ret_data = GlobalDef.INTRET_GIVEUP

                val ac = activity
                val data = Intent()
                ac.setResult(ret_data, data)
                ac.finish()
            }
        }
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        when (v.id) {
            R.id.et_usr_name -> {
                Log.d(LOG_TAG, "now usr name : " + mETUsrName!!.text.toString())
            }

            R.id.et_pwd -> {
                Log.d(LOG_TAG, "now pwd : " + mETPwd!!.text.toString())
            }

            R.id.et_repeat_pwd -> {
                Log.d(LOG_TAG, "now repeatPWD : " + mETRepeatPwd!!.text.toString())
            }
        }

        return false
    }


    /// PRIVATE BEGIN
    /**
     * clean UI
     */
    private fun repeatInput() {
        mETUsrName!!.setText("")
        mETPwd!!.setText("")
        mETRepeatPwd!!.setText("")

        mETUsrName!!.requestFocus()
    }

    /**
     * check input data then give prompting message
     * @return  true if data legal else false
     */
    private fun checkInput(): Boolean {
        val usr_name = mETUsrName!!.text.toString()
        val usr_pwd = mETPwd!!.text.toString()
        val usr_r_pwd = mETRepeatPwd!!.text.toString()

        var bret = true
        if (UtilFun.StringIsNullOrEmpty(usr_name)) {
            mETUsrName!!.error = mRSErrorNoUsrName
            mETUsrName!!.requestFocus()
            bret = false
        }

        if (bret && 4 > usr_pwd.length) {
            mETPwd!!.error = mRSErrorInvalidPWD
            mETPwd!!.requestFocus()
            bret = false
        }

        if (bret && usr_pwd != usr_r_pwd) {
            mETRepeatPwd!!.error = mRSErrorRepeatPwdNotMatch
            mETRepeatPwd!!.requestFocus()
            bret = false
        }

        return bret
    }
    /// PRIVATE END

    private class LocalMsgHandler internal constructor(ac: FrgUsrAdd) : WRMsgHandler<FrgUsrAdd>(ac) {
        init {
            TAG = "LocalMsgHandler"
        }

        override fun processMsg(m: Message, home: FrgUsrAdd) {
            val et = EMsgType.getEMsgType(m.what) ?: return

            if (EMsgType.REPLAY === et) {
                val et_inner = EMsgType.getEMsgType(m.arg1)
                if (null != et_inner) {
                    if (EMsgType.USR_ADD === et_inner) {
                        afterAddUsr(m, home)
                    }
                }
            } else {
                Log.e(TAG, String.format("msg(%s) can not process", m.toString()))
            }
        }

        private fun afterAddUsr(m: Message, home: FrgUsrAdd) {
            val ac = home.activity
            val arr = UtilFun.cast_t<Array<Any>>(m.obj)
            val ret = arr[0] as Boolean
            if (ret) {
                val data = UtilFun.cast_t<Intent>(arr[1])

                ac.setResult(GlobalDef.INTRET_USR_ADD, data)
                ac.finish()
            } else {
                var sstr = "添加用户失败!"
                if (2 < arr.size)
                    sstr = UtilFun.cast(arr[2])

                Toast.makeText(ContextUtil.instance, sstr,
                        Toast.LENGTH_LONG).show()
                home.repeatInput()
            }
        }
    }
}
