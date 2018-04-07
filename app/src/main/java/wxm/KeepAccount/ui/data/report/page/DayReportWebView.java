package wxm.KeepAccount.ui.data.report.page;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import org.greenrobot.eventbus.EventBus;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

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
    protected void onWVPageFinished(WebView wvPage, Object para) {
        wvPage.evaluateJavascript("onLoadData(" + para + ")", value -> {
        });
    }

    protected void initUI(Bundle bundle)    {
        Bundle bd = getArguments();
        mASParaLoad = bd.getStringArrayList(ACReport.PARA_LOAD);
    }

    @Override
    protected void loadUI(Bundle savedInstanceState) {
        if (!UtilFun.ListIsNullOrEmpty(mASParaLoad)) {
            if (2 != mASParaLoad.size())
                return;

            final String[] param = new String[1];
            showProgress(true);
            ToolUtil.runInBackground(this.getActivity(),
                    () -> {
                        String d_s = mASParaLoad.get(0);
                        String d_e = mASParaLoad.get(1);
                        HashMap<String, ArrayList<INote>> hmData = NoteDataHelper
                                .getInstance().getNotesBetweenDays(d_s, d_e);

                        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(INote.class,
                                "info", "ts", "val", "payNote");
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
