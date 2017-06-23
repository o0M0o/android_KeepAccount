package wxm.KeepAccount.ui.data.edit.Note.webview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.util.List;

import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.define.RecordTypeItem;
import wxm.KeepAccount.ui.base.FrgWebView;
import wxm.KeepAccount.ui.data.edit.Note.ACNoteEdit;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * webview fragment for note edit
 * Created by ookoo on 2017/6/23.
 */
public class FrgWVNoteEdit extends FrgWebView {
    // for data
    private String mAction;
    private PayNoteItem mOldPayNote;
    private IncomeNoteItem mOldIncomeNote;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View vw = super.inflaterView(layoutInflater, viewGroup, bundle);
        LOG_TAG = "FrgWVNoteEdit";
        return vw;
    }

    @Override
    protected void onWVPageFinished(WebView wvPage, Object pagePara) {
        List<RecordTypeItem> payTypes =ContextUtil.getRecordTypeUtility().getAllPayItem();
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(RecordTypeItem.class,
                "id", "itemType", "type", "note");
        String para = JSON.toJSONString(payTypes, filter);

        wvPage.evaluateJavascript("setupPayTypeOptions(" + para + ")", value -> {
        });
    }

    @Override
    protected void initUiComponent(View view) {
        Bundle bd = getArguments();
        mAction = bd.getString(ACNoteEdit.PARA_ACTION);
        if (GlobalDef.STR_MODIFY.equals(mAction)) {
            int pid = bd.getInt(ACNoteEdit.PARA_NOTE_PAY, GlobalDef.INVALID_ID);
            int iid = bd.getInt(ACNoteEdit.PARA_NOTE_INCOME, GlobalDef.INVALID_ID);
            if (GlobalDef.INVALID_ID != pid) {
                mOldPayNote = ContextUtil.getPayIncomeUtility().getPayDBUtility().getData(pid);
            } else if (GlobalDef.INVALID_ID != iid) {
                mOldIncomeNote = ContextUtil.getPayIncomeUtility().getIncomeDBUtility().getData(iid);
            } else {
                Log.e(LOG_TAG, "调用intent缺少'PARA_NOTE_PAY'和'PARA_NOTE_INCOME'参数");
            }
        }
    }

    @Override
    protected void loadUI() {
        loadPage("file:///android_asset/addNote/addNote.html", null);
    }
}
