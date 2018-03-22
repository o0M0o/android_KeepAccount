package wxm.KeepAccount.ui.data.report.page;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;

import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.data.report.ACReport;
import wxm.KeepAccount.ui.data.report.EventSelectDays;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.androidutil.FrgWebView.FrgWebView;
import wxm.androidutil.util.UtilFun;

/**
 * day data report(webview)
 * Created by ookoo on 2017/3/4.
 */
public class DayReportWebView extends FrgWebView {
    private ArrayList<String> mASParaLoad;

    @Override
    protected void enterActivity() {
        Log.d(LOG_TAG, "in enterActivity");
        super.enterActivity();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void leaveActivity() {
        Log.d(LOG_TAG, "in leaveActivity");
        EventBus.getDefault().unregister(this);

        super.leaveActivity();
    }

    /**
     * handler for change data range
     * @param event     param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectDaysEvent(EventSelectDays event) {
        mASParaLoad.set(0, event.mSZStartDay);
        mASParaLoad.set(1, event.mSZEndDay);
        loadUI();
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View vw = super.inflaterView(layoutInflater, viewGroup, bundle);
        LOG_TAG = "DayReportWebView";
        return vw;
    }

    @Override
    protected void onWVPageFinished(WebView wvPage, Object para) {
        wvPage.evaluateJavascript("onLoadData(" + para + ")", value -> {
        });
    }

    @Override
    protected void initUiComponent(View view) {
        super.initUiComponent(view);

        Bundle bd = getArguments();
        mASParaLoad = bd.getStringArrayList(ACReport.PARA_LOAD);
    }

    @Override
    protected void loadUI() {
        showProgress(true);

        Activity h = this.getActivity();
        Executors.newCachedThreadPool().submit(() -> {
            String mSzPara = null;
            if (!UtilFun.ListIsNullOrEmpty(mASParaLoad)) {
                if (2 != mASParaLoad.size())
                    return;

                String d_s = mASParaLoad.get(0);
                String d_e = mASParaLoad.get(1);
                HashMap<String, ArrayList<INote>> hmData = NoteDataHelper
                        .getInstance().getNotesBetweenDays(d_s, d_e);

                SimplePropertyPreFilter filter = new SimplePropertyPreFilter(INote.class,
                        "info", "ts", "val", "payNote");
                mSzPara = JSON.toJSONString(hmData, filter);
            }

            final String para = mSzPara;
            if(!(h.isDestroyed() || h.isFinishing()))   {
                h.runOnUiThread(() ->   {
                    showProgress(false);

                    if (!UtilFun.StringIsNullOrEmpty(para)) {
                        loadPage("file:///android_asset/report/report_day.html", para);
                    }
                });
            }
        });
    }
}
