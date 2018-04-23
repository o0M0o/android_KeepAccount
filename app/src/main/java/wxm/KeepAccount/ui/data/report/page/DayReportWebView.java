package wxm.KeepAccount.ui.data.report.page;

import android.os.Bundle;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.data.report.ACReport;
import wxm.KeepAccount.ui.data.report.base.EventSelectDays;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.androidutil.FrgWebView.FrgSupportWebView;
import wxm.androidutil.util.UtilFun;

/**
 * day data report(webview)
 * Created by WangXM on 2017/3/4.
 */
public class DayReportWebView extends FrgSupportWebView {
    private ArrayList<String> mASParaLoad;

    /**
     * handler for change data range
     * @param event     param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectDaysEvent(EventSelectDays event) {
        mASParaLoad.set(0, event.mSZStartDay);
        mASParaLoad.set(1, event.mSZEndDay);
        loadUI(null);
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    protected void onWVPageFinished(WebView wvPage, Object para) {
        wvPage.evaluateJavascript("onLoadData(" + para + ")", value -> {
        });
    }


    @Override
    protected void loadUI(Bundle savedInstanceState) {
        Bundle bd = getArguments();
        mASParaLoad = bd.getStringArrayList(ACReport.PARA_LOAD);

        if (!UtilFun.ListIsNullOrEmpty(mASParaLoad)) {
            if (2 != mASParaLoad.size())
                return;

            final String[] param = new String[1];
            showProgress(true);
            ToolUtil.INSTANCE.runInBackground(this.getActivity(),
                    () -> {
                        String d_s = mASParaLoad.get(0);
                        String d_e = mASParaLoad.get(1);
                        HashMap<String, ArrayList<INote>> hmData = NoteDataHelper.Companion
                                .getInstance().getNotesBetweenDays(d_s, d_e);

                        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(INote.class,
                                "info", "ts", "amount", "payNote");
                        param[0] = JSON.toJSONString(hmData, filter);
                    },
                    () -> {
                        showProgress(false);
                        if (!UtilFun.StringIsNullOrEmpty(param[0])) {
                            loadPage("file:///android_asset/report/report_day.html", param[0]);
                        }
                    });
        }
    }
}
