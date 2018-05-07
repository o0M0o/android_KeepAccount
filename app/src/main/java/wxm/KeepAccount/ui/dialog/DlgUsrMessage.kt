package wxm.KeepAccount.ui.dialog

import android.content.pm.PackageManager
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException

import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.androidutil.util.PackageUtil
import wxm.androidutil.util.SIMCardUtil
import wxm.androidutil.util.UtilFun
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.utility.ContextUtil

import android.Manifest.permission.READ_PHONE_STATE
import android.Manifest.permission.READ_SMS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.widget.ProgressBar
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * submit usr msg
 * Created by WangXM on 2017/1/9.
 */
class DlgUsrMessage : DlgOKOrNOBase() {
    private val mSZUrlPost: String = ContextUtil.getString(R.string.url_post_send_message)
    private val mSZColUsr: String = ContextUtil.getString(R.string.col_usr)
    private val mSZColMsg: String = ContextUtil.getString(R.string.col_message)
    private val mSZColAppName: String = ContextUtil.getString(R.string.col_app_name)
    private val mSZColValAppName: String = ContextUtil.getString(R.string.col_val_app_name)

    private val mSZUsrMessage: String = ContextUtil.getString(R.string.cn_usr_message)
    private val mSZAccept: String = ContextUtil.getString(R.string.cn_accept)
    private val mSZGiveUp: String = ContextUtil.getString(R.string.cn_cancel)

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
        if (UtilFun.StringIsNullOrEmpty(msg)) {
            AlertDialog.Builder(context)
                    .setTitle("警告")
                    .setMessage("消息不能为空")
                    .create().show()
            return false
        }

        var usr: String? = null
        val ct = ContextUtil.instance
        if (null != ct) {
            if (ContextCompat.checkSelfPermission(ct, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ct, READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                val si = SIMCardUtil(context)
                usr = si.nativePhoneNumber
            }
        }

        val ret = sendMsgByHttpPost(if (UtilFun.StringIsNullOrEmpty(usr)) "null" else usr!!, msg)
        if(!(ret[0] as Boolean)) {
            AlertDialog.Builder(context)
                    .setTitle("警告")
                    .setMessage("消息发送失败!!\n" + "原因 : " + (ret[1] as String))
                    .create().show()
            return false
        }

        AlertDialog.Builder(context)
                .setTitle("信息")
                .setMessage("消息发送成功!!")
                .create().show()
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
        return ToolUtil.callInBackground({
                    var sendResult: Boolean
                    var sendExplain: String
                    try {
                        val param = JSONObject()
                        param.put(mSZColUsr, usr)
                        param.put(mSZColMsg, msg)
                        param.put(mSZColAppName,
                                mSZColValAppName + "-"
                                        + PackageUtil.getVerName(context, GlobalDef.PACKAGE_NAME))

                        val body = RequestBody.create(JSON, param.toString())
                        runInUIThread(wrHome, Runnable { mPDBar.progress = 50 })

                        val request = Request.Builder().url(mSZUrlPost).post(body).build()
                        OkHttpClient().newCall(request).execute()
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


    private fun runInUIThread(wrActivity : WeakReference<Activity?>, uiRun : Runnable)   {
        wrActivity.get()?.let {
            if(!(it.isDestroyed || it.isFinishing)) {
                it.runOnUiThread(uiRun)
            }
        }
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
