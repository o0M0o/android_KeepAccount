package com.wxm.KeepAccount.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.wxm.KeepAccount.R;

/**
 * Created by 123 on 2016/5/5.
 */
public class payinfoDialog extends AlertDialog  {
    private ArrayAdapter info_ap;
    private View payinfo_vw;
    private EditText et_self_info;

    public String payinfo;

    public payinfoDialog(Context ct)
    {
        super(ct);
        payinfo = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payinfo_dialog);
    }
}
