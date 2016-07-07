package com.wxm.KeepAccount.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.ViewSwitcher;

import com.wxm.KeepAccount.R;
import com.wxm.KeepAccount.Base.utility.ContextUtil;
import com.wxm.KeepAccount.Base.utility.ToolUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ACRecordType extends AppCompatActivity {
    private static final String TEXTVIEW_CHILD = "TEXTVIEW_CHILD";
    private static final String EDITTEXT_CHILD = "EDITTEXT_CHILD";
    private static final String CHILD_TYPE     = "CHILD_TYPE";
    private static final String TITLE          = "TITLE";
    private static final String EXPLAIN        = "EXPLAIN";

    private ArrayList<HashMap<String, String>> mLHData = new ArrayList<>();
    private ListView                        mLVRecordType;
    private MySimpleAdapter                 mMAAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_record_type);

        mLVRecordType = (ListView)findViewById(R.id.aclv_record_type);
        mMAAdapter = new MySimpleAdapter(this,
                                    ContextUtil.getInstance(),
                                    mLHData,
                                    new String[] {TITLE, EXPLAIN},
                                    new int[] {R.id.lvtv_title, R.id.lvtv_explain});
        mLVRecordType.setAdapter(mMAAdapter);
        forTest();
    }


    private void forTest()  {
        mLHData.clear();

        HashMap<String, String> hm = new HashMap<>();
        hm.put(TITLE, "test 1");
        hm.put(EXPLAIN, "for test 1");
        hm.put(CHILD_TYPE, TEXTVIEW_CHILD);
        mLHData.add(hm);

        hm = new HashMap<>();
        hm.put(TITLE, "test 2");
        hm.put(EXPLAIN, "for test 2");
        hm.put(CHILD_TYPE, TEXTVIEW_CHILD);
        mLHData.add(hm);

        hm = new HashMap<>();
        hm.put(TITLE, "test 3");
        hm.put(EXPLAIN, "其它类型（可以编辑此项）");
        hm.put(CHILD_TYPE, EDITTEXT_CHILD);
        mLHData.add(hm);

        mMAAdapter.notifyDataSetChanged();
    }


    /**
     * 此adapter混合显示'textview'和'edittext'
     */
    public class MySimpleAdapter extends SimpleAdapter {
        private ACRecordType mHome;
        private List<? extends Map<String, ?>> mSelfData;

        public MySimpleAdapter(ACRecordType home,
                               Context context, List<? extends Map<String, ?>> data,
                               String[] from,
                               int[] to) {
            super(context, data, R.layout.lv_record_type, from, to);
            mHome = home;
            mSelfData = data;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            View v;
            v = super.getView(position, view, arg2);
            if (null != v) {
                ViewSwitcher vs = (ViewSwitcher)v.findViewById(R.id.lvvs_switcher);
                assert vs != null;

                Map<String, ?> hm = mSelfData.get(position);
                String tp = ToolUtil.cast(hm.get(CHILD_TYPE));
                if(tp.equals(TEXTVIEW_CHILD)) {
                    vs.setDisplayedChild(0);
                } else {
                    vs.setDisplayedChild(1);
                    String info = ToolUtil.cast(hm.get(TITLE));
                    EditText et = (EditText)vs.getCurrentView().findViewById(R.id.lvet_title);
                    et.setText(info);
                }
            }

            return v;
        }
    }
}
