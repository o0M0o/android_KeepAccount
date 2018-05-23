package wxm.KeepAccount.ui.data.report

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment

import wxm.androidutil.ui.activity.ACSwitcherActivity
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.log.TagLog

/**
 * UI for report
 * Created by WangXM on 2017/2/15.
 */
class ACReport : ACSwitcherActivity<Fragment>() {
    override fun leaveActivity() {
        setResult(GlobalDef.INTRET_GIVEUP, Intent())
        finish()
    }

    override fun setupFragment(bundle: Bundle?) {
        // check invoke intent
        val it = intent
        val szType = it.getStringExtra(PARA_TYPE)
        if (UtilFun.StringIsNullOrEmpty(szType)) {
            TagLog.e( "调用intent缺少'PARA_TYPE'参数")
            return
        }

        val alLoad = it.getStringArrayListExtra(PARA_LOAD)
        if (UtilFun.ListIsNullOrEmpty(alLoad)) {
            TagLog.e( "调用intent缺少'PARA_LOAD'参数")
            return
        }

        // for holder
        var mSelfFrg: android.support.v4.app.Fragment? = null
        when (szType) {
            PT_DAY -> {
                mSelfFrg = FrgReportDay()
            }

            PT_MONTH -> {
                mSelfFrg = FrgReportMonth()
            }

            PT_YEAR -> {
                mSelfFrg = FrgReportYear()
            }
        }

        if (null != mSelfFrg) {
            val bd = Bundle()
            bd.putStringArrayList(PARA_LOAD, alLoad)
            mSelfFrg.arguments = bd
        }

        addFragment(mSelfFrg)
    }

    companion object {
        const val PARA_TYPE = "para_type"
        const val PT_DAY = "pt_day"
        const val PT_MONTH = "pt_month"
        const val PT_YEAR = "pt_year"

        const val PARA_LOAD = "para_load"
    }
}
