package com.wxm.KeepAccount.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.ViewSwitcher;

import com.wxm.KeepAccount.Base.data.AppGobalDef;
import com.wxm.KeepAccount.Base.data.AppModel;
import com.wxm.KeepAccount.Base.db.RecordTypeItem;
import com.wxm.KeepAccount.Base.utility.ContextUtil;
import com.wxm.KeepAccount.BuildConfig;
import com.wxm.KeepAccount.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;

/**
 * 记录类型编辑界面
 */
public class ACRecordTypeEdit extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "ACRecordTypeEdit";
    private static final String NEWITEM_PAY     = "新支出类型-tv-新支出类型说明";
    private static final String NEWITEM_INCOME  = "新收入类型-tv-新收入类型说明";

    private static final String TEXTVIEW_CHILD = "TEXTVIEW_CHILD";
    private static final String EDITTEXT_CHILD = "EDITTEXT_CHILD";
    private static final String CHILD_TYPE     = "CHILD_TYPE";
    private static final String TITLE          = "TITLE";
    private static final String EXPLAIN        = "EXPLAIN";

    private ArrayList<HashMap<String, String>> mLHData = new ArrayList<>();
    private ListView                mLVRecordType;
    private MySimpleAdapter         mMAAdapter;

    private FloatingActionButton    mFABAddition;
    private MenuItem                mMISure;
    private MenuItem                mMIEdit;

    private String                  mType;
    private boolean                 mBEditModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check intent
        Intent it = getIntent();
        if(null == it)  {
            Log.e(TAG, "没有intent");

            Intent data = new Intent();
            setResult(AppGobalDef.INTRET_ERROR, data);
            finish();
        }   else {
            mType = it.getStringExtra(AppGobalDef.STR_RECORD_TYPE);
            if (UtilFun.StringIsNullOrEmpty(mType) ||
                    (!mType.equals(AppGobalDef.STR_RECORD_INCOME)
                            && !mType.equals(AppGobalDef.STR_RECORD_PAY))) {
                Log.e(TAG, "intent参数不正确");

                Intent data = new Intent();
                setResult(AppGobalDef.INTRET_ERROR, data);
                finish();
            }

            // init component
            setContentView(R.layout.ac_record_type_edit);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mBEditModel = false;
            mFABAddition = (FloatingActionButton) findViewById(R.id.fab);
            assert null != mFABAddition;
            mFABAddition.setVisibility(View.INVISIBLE);
            mFABAddition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "新添加类型", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    if(mType.equals(AppGobalDef.STR_RECORD_PAY))   {
                        mLHData.add(line2hm(NEWITEM_PAY));
                    } else  {
                        mLHData.add(line2hm(NEWITEM_INCOME));
                    }

                    mMAAdapter.notifyDataSetChanged();
                }
            });

            mLVRecordType = (ListView)findViewById(R.id.aclv_record_type_edit);
            mMAAdapter = new MySimpleAdapter(this,
                    ContextUtil.getInstance(),
                    mLHData,
                    new String[] {TITLE, EXPLAIN},
                    new int[] {R.id.lvtv_title, R.id.lvtv_explain});
            mLVRecordType.setAdapter(mMAAdapter);
            mLVRecordType.setOnItemClickListener(this);

            load_type(mType);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_record_type, menu);

        mMISure = menu.findItem(R.id.recordtype_menu_sure);
        mMIEdit = menu.findItem(R.id.recordtype_menu_edit);
        mMISure.setVisible(false);
        mMIEdit.setVisible(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recordtype_menu_sure: {
                String ty = "";
                int cc = mLVRecordType.getChildCount();
                for(int i = 0; i < cc; i++) {
                    View vo = mLVRecordType.getChildAt(i);
                    CheckBox cb = (CheckBox)vo.findViewById(R.id.lvcb_selected);
                    assert null != cb;

                    if(cb.isChecked())  {
                        ty = mLHData.get(i).get(TITLE);
                    }
                }

                Intent data = new Intent();
                if(!UtilFun.StringIsNullOrEmpty(ty)) {
                    data.putExtra(AppGobalDef.STR_RECORD_TYPE, ty);
                    setResult(AppGobalDef.INTRET_SURE, data);
                }
                else    {
                    setResult(AppGobalDef.INTRET_GIVEUP, data);
                }
                finish();
            }
            break;

            case R.id.recordtype_menu_giveup: {
                if(mBEditModel) {
                    mBEditModel = false;

                    mMIEdit.setVisible(true);
                    mFABAddition.setVisibility(View.INVISIBLE);

                    mMAAdapter.notifyDataSetChanged();
                } else  {
                    Intent data = new Intent();
                    setResult(AppGobalDef.INTRET_GIVEUP, data);
                    finish();
                }
            }
            break;

            case R.id.recordtype_menu_edit :    {
                if(!mBEditModel) {
                    mBEditModel = true;
                    mFABAddition.setVisibility(View.VISIBLE);

                    mMIEdit.setVisible(false);

                    mMAAdapter.notifyDataSetChanged();
                }
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }


    /**
     * 加载数据
     * @param ty 数据类型
     */
    private void load_type(String ty)   {
        List<RecordTypeItem> ls;
        if(ty.equals(AppGobalDef.STR_RECORD_PAY))   {
            ls = AppModel.getRecordTypeUtility().getAllPayItem();
            this.setTitle(R.string.title_acrt_pay);
        }
        else    {
            ls = AppModel.getRecordTypeUtility().getAllIncomeItem();
            this.setTitle(R.string.title_acrt_income);
        }

        mLHData.clear();
        HashMap<String, String> hm;
        for(RecordTypeItem ln : ls)   {
            hm = line2hm(ln.getType());
            mLHData.add(hm);
        }

        mMAAdapter.notifyDataSetChanged();
    }


    /**
     * 把字符串(生活费-tv-生活开销)转换为item
     * @param ln 待转换字符串
     * @return item信息
     */
    private HashMap<String, String> line2hm(String ln)  {
        String[] sln =  ln.split("-", 5);
        if(BuildConfig.DEBUG && (3 != sln.length)) {
            throw new AssertionError();
        }

        HashMap<String, String> hm = new HashMap<>();
        hm.put(TITLE, sln[0]);
        hm.put(EXPLAIN, sln[2]);

        if(sln[1].equals("tv"))
            hm.put(CHILD_TYPE, TEXTVIEW_CHILD);
        else
            hm.put(CHILD_TYPE, EDITTEXT_CHILD);

        return hm;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.lvcb_selected){
            int pos = mLVRecordType.getPositionForView(v);
            activeItem(pos);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        activeItem(position);
    }


    /**
     * 选中指定位置的节点
     * @param pos 节点的位置
     */
    private void activeItem(int pos)    {
        int cc = mLVRecordType.getChildCount();
        for(int i = 0; i < cc; i++) {
            View vo = mLVRecordType.getChildAt(i);
            CheckBox cb = (CheckBox)vo.findViewById(R.id.lvcb_selected);
            assert null != cb;

            if(i != pos) {
                if (cb.isChecked()) {
                    cb.setChecked(false);
                }
            } else  {
                boolean bc = cb.isChecked();
                mMISure.setVisible(bc);
                mMIEdit.setVisible(!bc);
            }
        }
    }



    /**
     * 此adapter混合显示'textview'和'edittext'
     */
    public class MySimpleAdapter
            extends SimpleAdapter {
        private ACRecordTypeEdit mHome;
        private List<? extends Map<String, ?>> mSelfData;

        public MySimpleAdapter(ACRecordTypeEdit home,
                               Context context, List<? extends Map<String, ?>> data,
                               String[] from,
                               int[] to) {
            super(context, data, R.layout.li_record_type, from, to);
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
                String tp = UtilFun.cast(hm.get(CHILD_TYPE));
                if(tp.equals(TEXTVIEW_CHILD) && (!mBEditModel)) {
                    vs.setDisplayedChild(0);
                } else {
                    vs.setDisplayedChild(1);
                    String info = UtilFun.cast(hm.get(TITLE));
                    EditText et = (EditText)vs.getCurrentView().findViewById(R.id.lvet_title);
                    et.setText(info);

                    String explain = UtilFun.cast(hm.get(EXPLAIN));
                    EditText exet = (EditText)vs.getCurrentView().findViewById(R.id.lvet_explain);
                    exet.setText(explain);
                }

                CheckBox cb = (CheckBox)v.findViewById(R.id.lvcb_selected);
                assert null != cb;

                if(!mBEditModel) {
                    cb.setOnClickListener(mHome);
                    cb.setVisibility(View.VISIBLE);
                } else    {
                    cb.setVisibility(View.INVISIBLE);
                }
            }

            return v;
        }
    }

}
