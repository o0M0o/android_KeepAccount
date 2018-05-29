package wxm.KeepAccount.ui.dialog

import android.Manifest.permission.READ_PHONE_STATE
import android.Manifest.permission.READ_SMS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ProgressBar
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil
import wxm.KeepAccount.utility.ToolUtil
import wxm.KeepAccount.utility.ToolUtil.callInBackground
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.util.SIMCardUtil
import wxm.androidutil.util.ThreadUtil.runInUIThread
import wxm.androidutil.util.UtilFun
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * submit usr msg
 * Created by WangXM on 2017/1/9.
 */
class DlgUsrMessage : DlgOKOrNOBase() {
    private val mSZUrlPost: String = AppBase.getString(R.string.url_post_send_message)
    private val mSZColUsr: String = AppBase.getString(R.string.col_usr)
    private val mSZColMsg: String = AppBase.getString(R.string.col_message)
    private val mSZColAppName: String = AppBase.getString(R.string.col_app_name)
    private val mSZColValAppName: String = AppBase.getString(R.string.col_val_app_name)

    private val mSZUsrMessage: String = AppBase.getString(R.string.cn_usr_message)
    private val mSZAccept: String = AppBase.getString(R.string.cn_accept)
    private val mSZGiveUp: String = AppBase.getString(R.string.cn_cancel)

    private lateinit var mETUsrMessage: TextInputEditText

    private lateinit var mPDBar: ProgressBar
    private lateinit var mTLMessage: TextInputLayout

    override fun createDlgView(savedInstanceState: Bundle?): View {
        initDlgTitle(mSZUsrMessage, mSZAccept, mSZGiveUp)
        return View.inflate(activity, R.layout.dlg_send_message, null)
    }

    override fun initDlgView(savedInstanceState: Bundle?) {
        mETUsrMessage = findDlgChildView(R.id.et_usr_message)!!
        mPDBar = findDlgChildView(R.id.pb_large)!!
        mTLMessage = findDlgChildView(R.id.tl_message)!!

        showProgress(false)
    }

    override fun checkBeforeOK(): Boolean {
        val msg = mETUsrMessage.text.toString()
        if (msg.isEmpty()) {
            DlgAlert.showAlert(context!!, R.string.dlg_warn, "消息不能为空!!")
            return false
        }

        val usr: String = ContextUtil.self.let {
            if (ContextCompat.checkSelfPermission(it, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(it, READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                SIMCardUtil(context).nativePhoneNumber
            } else "null"
        }

        val ret = sendMsgByHttpPost(usr, msg)
        if (!(ret[0] as Boolean)) {
            DlgAlert.showAlert(context!!, R.string.dlg_warn,
                    "消息发送失败!!\n原因 : ${ret[1] as String}")
            return false
        }

        DlgAlert.showAlert(context!!, R.string.dlg_info, "消息发送成功!!")
        return true
    }

    /**
     * use http send msg
     * @param usr       msg owner
     * @param msg       msg
     * @return          true if success
     */
    private fun sendMsgByHttpPost(usr: String, msg: String): Array<Any> {
        showProgress(true)
        val wrHome = WeakReference(activity as Activity?)
        return callInBackground({
            var sendResult: Boolean
            var sendExplain: String
            try {
                val body = JSONObject().apply {
                    put(mSZColUsr, usr)
                    put(mSZColMsg, msg)
                    put(mSZColAppName, "$mSZColValAppName-${AppBase.getVerName()}")
                }.let {
                    RequestBody.create(JSON, it.toString())
                }

                runInUIThread(wrHome, Runnable { mPDBar.progress = 50 })

                Request.Builder().url(mSZUrlPost).post(body).build().let {
                    OkHttpClient().newCall(it).execute()
                }
                sendResult = true
                sendExplain = "success"
            } catch (e: JSONException) {
                sendResult = false
                sendExplain = "JSONException"
                e.printStackTrace()
            } catch (e: IOException) {
                sendResult = false
                sendExplain = "IOException"
                e.printStackTrace()
            } finally {
                runInUIThread(wrHome, Runnable { showProgress(false) })
            }

            arrayOf(sendResult, sendExplain)
        }, arrayOf(false, "time out"), TimeUnit.SECONDS, 6)
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        val shortAnimTime = 200

        mTLMessage.visibility = if (show) View.GONE else View.VISIBLE
        mTLMessage.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mTLMessage.visibility = if (show) View.GONE else View.VISIBLE
            }
        })

        mPDBar.visibility = if (show) View.VISIBLE else View.GONE
        mPDBar.animate().setDuration(shortAnimTime.toLong())
                .alpha((if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        mPDBar.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    companion object {
        // for http post
        private val JSON = MediaType.parse("application/json; charset=utf-8")
    }
}
