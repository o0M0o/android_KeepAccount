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

import kotterknife.bindView
import wxm.KeepAccount.define.EMsgType
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.androidutil.util.WRMsgHandler
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.UsrItem
import wxm.KeepAccount.utility.ContextUtil
import wxm.KeepAccount.utility.EventHelper

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
    private val mRSErrorNoUsrName: String = ContextUtil.getString(R.string.error_no_usrname)
    private val mRSErrorInvalidPWD: String = ContextUtil.getString(R.string.error_invalid_password)
    private var mRSErrorRepeatPwdNotMatch: String = ContextUtil.getString(R.string.error_repeatpwd_notmatch)

    // for data
    private var mMHHandler: LocalMsgHandler = LocalMsgHandler(this)

    override fun getLayoutID(): Int {
        return R.layout.vw_usr_add
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(bundle: Bundle?) {
        if(null == bundle) {
            mETUsrName.setOnEditorActionListener(this)
            mETPwd.setOnEditorActionListener(this)
            mETRepeatPwd.setOnEditorActionListener(this)

            EventHelper.setOnClickListener(view!!,
                    intArrayOf(R.id.bt_confirm, R.id.bt_giveup),
                    View.OnClickListener { v ->
                        when (v.id) {
                            R.id.bt_confirm -> {
                                if (checkInput()) {
                                    val data = Intent()
                                    data.putExtra(UsrItem.FIELD_NAME, mETUsrName.text.toString())
                                    data.putExtra(UsrItem.FIELD_PWD, mETPwd.text.toString())

                                    val m = Message.obtain(ContextUtil.msgHandler,
                                            EMsgType.USR_ADD.id)
                                    m.obj = arrayOf(data, mMHHandler)
                                    m.sendToTarget()
                                }
                            }

                            R.id.bt_giveup -> {
                                val ac = activity
                                val data = Intent()
                                ac.setResult(GlobalDef.INTRET_GIVEUP, data)
                                ac.finish()
                            }
                        }
                     })
        }
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        when (v.id) {
            R.id.et_usr_name -> {
                Log.d(LOG_TAG, "now usr name : " + mETUsrName.text.toString())
            }

            R.id.et_pwd -> {
                Log.d(LOG_TAG, "now pwd : " + mETPwd.text.toString())
            }

            R.id.et_repeat_pwd -> {
                Log.d(LOG_TAG, "now repeatPWD : " + mETRepeatPwd.text.toString())
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

        var bret = true
        if (UtilFun.StringIsNullOrEmpty(usrName)) {
            mETUsrName.error = mRSErrorNoUsrName
            mETUsrName.requestFocus()
            bret = false
        }

        if (bret && 4 > usrPwd.length) {
            mETPwd.error = mRSErrorInvalidPWD
            mETPwd.requestFocus()
            bret = false
        }

        if (bret && usrPwd != usrRPwd) {
            mETRepeatPwd.error = mRSErrorRepeatPwdNotMatch
            mETRepeatPwd.requestFocus()
            bret = false
        }

        return bret
    }
    /// PRIVATE END

    private class LocalMsgHandler internal constructor(ac: FrgUsrAdd) : WRMsgHandler<FrgUsrAdd>(ac) {
        init {
            TAG = ::LocalMsgHandler.javaClass.simpleName
        }

        override fun processMsg(m: Message, home: FrgUsrAdd) {
            val et = EMsgType.getEMsgType(m.what) ?: return

            if (EMsgType.REPLAY === et) {
                val etInner = EMsgType.getEMsgType(m.arg1)
                if (null != etInner) {
                    if (EMsgType.USR_ADD === etInner) {
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
