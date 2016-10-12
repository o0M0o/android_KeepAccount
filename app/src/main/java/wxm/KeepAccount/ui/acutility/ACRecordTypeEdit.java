package wxm.KeepAccount.ui.acutility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.RecordTypeItem;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;

/**
 * 记录类型编辑界面
 */
public class ACRecordTypeEdit extends AppCompatActivity
        implements  AdapterView.OnItemClickListener {
    private static final String TAG = "ACRecordTypeEdit";
    private static final String NEWITEM_PAY     = "新支出类型-新支出类型说明";
    private static final String NEWITEM_INCOME  = "新收入类型-新收入类型说明";

    private static final String ID             = "ID";
    private static final String TITLE          = "TITLE";
    private static final String EXPLAIN        = "EXPLAIN";
    private static final String EDIT           = "EDIT";
    private static final String PARA_CHANGED        = "CHANGED";
    private static final String PARA_NOCHANGED      = "NOCHANGED";

    private final ArrayList<HashMap<String, String>> mLHData = new ArrayList<>();
    private ListView                mLVRecordType;
    private MySimpleAdapter         mMAAdapter;

    private FloatingActionButton    mFABAddition;
    private MenuItem                mMISure;
    private MenuItem                mMIEdit;
    private int                     mHotChildPos = ListView.INVALID_POSITION;

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

                    String[] lns = mType.equals(AppGobalDef.STR_RECORD_PAY) ?
                                        NEWITEM_PAY.split("-") : NEWITEM_INCOME.split("-");
                    HashMap<String, String> hm = new HashMap<>();
                    hm.put(TITLE, lns[0]);
                    hm.put(EXPLAIN, lns[1]);
                    hm.put(EDIT, PARA_CHANGED);
                    mLHData.add(hm);

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

            load_type();
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
                if(mBEditModel) {
                    mBEditModel = false;

                    mMIEdit.setVisible(false);
                    mMISure.setVisible(false);
                    mFABAddition.setVisibility(View.INVISIBLE);

                    update_type();
                    mMAAdapter.notifyDataSetChanged();
                } else {
                    if (ListView.INVALID_POSITION != mHotChildPos) {
                        String ty = mLHData.get(mHotChildPos).get(TITLE);
                        Intent data = new Intent();
                        if (!UtilFun.StringIsNullOrEmpty(ty)) {
                            data.putExtra(AppGobalDef.STR_RECORD_TYPE, ty);
                            setResult(AppGobalDef.INTRET_SURE, data);
                        } else {
                            setResult(AppGobalDef.INTRET_GIVEUP, data);
                        }
                        finish();
                    }
                }
            }
            break;

            case R.id.recordtype_menu_giveup: {
                if(mBEditModel) {
                    mBEditModel = false;

                    load_type();
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
                    mMISure.setVisible(true);

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
     * 加载类型数据
     */
    private void load_type()   {
        List<RecordTypeItem> ls;
        if(mType.equals(AppGobalDef.STR_RECORD_PAY))   {
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
            hm = new HashMap<>();
            hm.put(TITLE, ln.getType());
            hm.put(EXPLAIN, ln.getNote());
            hm.put(EDIT, PARA_NOCHANGED);
            hm.put(ID, String.valueOf(ln.get_id()));
            mLHData.add(hm);
        }

        mMAAdapter.notifyDataSetChanged();
    }

    /**
     * 更新类型数据
     * 把当前的数据添加/更新到数据库中，然后再加载回来
     */
    private void update_type() {
        String rt = mType.equals(AppGobalDef.STR_RECORD_PAY) ? RecordTypeItem.DEF_PAY
                                    : RecordTypeItem.DEF_INCOME;
        for(HashMap<String, String> i : mLHData)    {
            String id = i.get(ID);
            if(UtilFun.StringIsNullOrEmpty(id)) {
                RecordTypeItem ri = new RecordTypeItem();
                ri.setType(i.get(TITLE));
                ri.setNote(i.get(EXPLAIN));
                ri.setItemType(rt);

                AppModel.getRecordTypeUtility().addItem(ri);
            } else  {
                if(i.get(EDIT).equals(PARA_CHANGED))    {
                    RecordTypeItem ri = new RecordTypeItem();
                    ri.setType(i.get(TITLE));
                    ri.setNote(i.get(EXPLAIN));
                    ri.setItemType(rt);
                    ri.set_id(Integer.parseInt(i.get(ID)));

                    AppModel.getRecordTypeUtility().modifyItem(ri);
                }
            }
        }

        load_type();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == mHotChildPos)   {
            mHotChildPos = ListView.INVALID_POSITION;

            mMISure.setVisible(false);
            mMIEdit.setVisible(true);
        }   else    {
            mHotChildPos = position;

            mMISure.setVisible(true);
            mMIEdit.setVisible(false);
        }

        int n_c = getResources().getColor(R.color.white);
        int y_c = getResources().getColor(R.color.powderblue);
        int cc = mLVRecordType.getChildCount();
        for(int i = 0; i < cc; i++) {
            View vo = mLVRecordType.getChildAt(i);
            vo.setBackgroundColor(mHotChildPos == i ? y_c : n_c);
        }
    }


    /**
     * 此adapter混合显示'textview'和'edittext'
     */
    public class MySimpleAdapter
            extends SimpleAdapter {
        private final ACRecordTypeEdit mHome;
        private final List<? extends Map<String, ?>> mSelfData;

        MySimpleAdapter(ACRecordTypeEdit home,
                        Context context, List<? extends Map<String, ?>> data,
                        String[] from,
                        int[] to) {
            super(context, data, R.layout.li_record_type, from, to);
            mHome = home;
            mSelfData = data;
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            View v = super.getView(position, view, arg2);
            if (null != v) {
                ViewSwitcher vs = (ViewSwitcher)v.findViewById(R.id.lvvs_switcher);
                assert vs != null;

                final Map<String, String> hm = UtilFun.cast(mSelfData.get(position));
                if(!mBEditModel) {
                    vs.setDisplayedChild(0);
                } else {
                    vs.setDisplayedChild(1);
                    String info = UtilFun.cast(hm.get(TITLE));
                    EditText et = (EditText)vs.getCurrentView().findViewById(R.id.lvet_title);
                    et.setText(info);
                    et.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            //Log.i(TAG, "changed to : " + s.toString());
                            hm.put(EDIT, PARA_CHANGED);
                            hm.put(TITLE, s.toString());
                        }
                    });

                    String explain = UtilFun.cast(hm.get(EXPLAIN));
                    EditText exet = (EditText)vs.getCurrentView().findViewById(R.id.lvet_explain);
                    exet.setText(explain);
                    exet.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            hm.put(EDIT, PARA_CHANGED);
                            hm.put(EXPLAIN, s.toString());
                        }
                    });
                }
            }

            return v;
        }
    }

}
