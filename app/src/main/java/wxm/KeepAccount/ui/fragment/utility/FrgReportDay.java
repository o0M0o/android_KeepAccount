package wxm.KeepAccount.ui.fragment.utility;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.Base.utility.NotesToHtmlUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.acutility.ACReport;

/**
 * 日数据汇报
 * Created by ookoo on 2017/2/15.
 */
public class FrgReportDay extends FrgUtilityBase {
    private ArrayList<String>   mASParaLoad;
    private String              mSZHtml;

    //private SimpleDateFormat mDFFormat = new SimpleDateFormat( "yyyy-MM-dd", Locale.CHINA);

    @BindView(R.id.wv_report)
    WebView     mWVReport;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgReportDay";
        View rootView = layoutInflater.inflate(R.layout.vw_report, viewGroup, false);
        ButterKnife.bind(this, rootView);

        Bundle bd = getArguments();
        mASParaLoad = bd.getStringArrayList(ACReport.PARA_LOAD);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        if(!UtilFun.ListIsNullOrEmpty(mASParaLoad)) {
            if(2 != mASParaLoad.size())
                return;

            List<INote> ls_note = getNotesBetweenDays(mASParaLoad.get(0), mASParaLoad.get(1));
            mSZHtml = NotesToHtmlUtil.NotesToHtmlStr(ls_note);
        }
    }

    @Override
    protected void initUiInfo() {
        if(!UtilFun.StringIsNullOrEmpty(mSZHtml))   {
            Log.d(LOG_TAG, "initUiInfo html : " + mSZHtml);

            WebSettings wSet = mWVReport.getSettings();
            wSet.setDefaultTextEncodingName("utf-8");

            mWVReport.loadData(mSZHtml, "text/html; utf-8", null);
        }
    }

    private List<INote> getNotesBetweenDays(String start, String end)   {
        HashMap<String, ArrayList<INote>> hm_data = NoteShowDataHelper.getInstance().getNotesForDay();
        ArrayList<String> ls_key = new ArrayList<>(hm_data.keySet());
        Collections.sort(ls_key);

        LinkedList<INote> ls_note = new LinkedList<>();
        for(String day : ls_key)    {
            if(day.compareTo(start) >= 0)   {
                if(day.compareTo(end) > 0)
                    break;

                ls_note.addAll(hm_data.get(day));
            }
        }

        return ls_note;
    }
}
