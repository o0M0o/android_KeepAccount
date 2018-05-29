package wxm.KeepAccount.ui.data.report.page

import android.os.Bundle
import android.webkit.WebView

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.ArrayList

import wxm.KeepAccount.item.INote
import wxm.KeepAccount.ui.data.report.ACReport
import wxm.KeepAccount.ui.data.report.base.EventSelectDays
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.ui.frg.FrgSupportWebView
import wxm.androidutil.log.TagLog
import wxm.androidutil.util.UtilFun

/**
 * day data report(webview)
 * Created by WangXM on 2017/3/4.
 */
class DayReportWebView : FrgSupportWebView() {
    private var mASParaLoad: ArrayList<String>? = null

    /**
     * handler for change data range
     * @param event     param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectDaysEvent(event: EventSelectDays) {
        mASParaLoad!![0] = event.mSZStartDay
        mASParaLoad!![1] = event.mSZEndDay
        loadUI(null)
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    override fun onWVPageFinished(wvPage: WebView?, para: Any?) {
        wvPage!!.evaluateJavascript("onLoadData($para)") { _ -> TagLog.i( "page finished")}
    }


    override fun loadUI(savedInstanceState: Bundle?) {
        mASParaLoad = arguments!!.getStringArrayList(ACReport.PARA_LOAD)

        if (!UtilFun.ListIsNullOrEmpty(mASParaLoad)) {
            if (2 != mASParaLoad!!.size)
                return

            val param = arrayOfNulls<String>(1)
            showProgress(true)
            ToolUtil.runInBackground(this.activity!!,
                    {
                        val dStart = mASParaLoad!![0]
                        val dEnd = mASParaLoad!![1]
                        val hmData = NoteDataHelper.getNotesBetweenDays(dStart, dEnd)

                        val filter = SimplePropertyPreFilter(INote::class.java,
                                "info", "ts", "amount", "payNote")
                        param[0] = JSON.toJSONString(hmData, filter)
                    },
                    {
                        showProgress(false)
                        if (!UtilFun.StringIsNullOrEmpty(param[0])) {
                            loadPage("file:///android_asset/report/report_day.html", param[0])
                        }
                    })
        }
    }
}
