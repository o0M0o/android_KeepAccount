package wxm.KeepAccount.ui.base.ExtendActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * activity extend
 * Created by wxm on 2018/03/30.
 */
public abstract class ACSwitcherActivity<T>
        extends AppCompatActivity {
    private final static String CHILD_HOT = "child_hot";
    protected String LOG_TAG = "ACSwitcherActivity";

    private int       mDIDBack = wxm.androidutil.R.drawable.ic_back;

    protected ArrayList<T>  mFrgArr;
    protected int           mHotFrgIdx  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(wxm.androidutil.R.layout.ac_base);

        mFrgArr = new ArrayList<>();
        if (savedInstanceState != null) {
            mHotFrgIdx = savedInstanceState.getInt(CHILD_HOT, 0);
        }

        ButterKnife.bind(this);
        initUi(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHILD_HOT, mHotFrgIdx);
    }

    /**
     * 需要在调用此函数前赋值view holder和LOG_TAG
     * 优先使用当前view holder，然后再使用兼容view holder
     * @param savedInstanceState 视图参数
     */
    protected void initUi(Bundle savedInstanceState) {
        // for left menu(go back)
        Toolbar toolbar = ButterKnife.findById(this, wxm.androidutil.R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getBackIconRID());
        toolbar.setNavigationOnClickListener(v -> leaveActivity());

        if(null == savedInstanceState)  {
            loadHotFrg();
        }
    }

    /**
     * leave activity
     */
    protected void leaveActivity()   {
        finish();
    }

    /**
     * get back icon ID
     * @return      ID
     */
    public int getBackIconRID() {
        return mDIDBack;
    }

    /**
     * set back icon id
     * @param mDIDBack      ID
     */
    public void setBackIconRID(int mDIDBack) {
        this.mDIDBack = mDIDBack;
    }

    /**
     * set child frg
     * @param child    all child frg
     */
    protected void addChildFrg(T child)  {
        mFrgArr.add(child);
    }

    /**
     * switch in pages
     * will loop switch between all child
     */
    public void switchFrg() {
        if(!(isFinishing() || isDestroyed())) {
            mHotFrgIdx = (mHotFrgIdx + 1) % mFrgArr.size();
            loadHotFrg();
        }
    }

    /**
     * switch to child fragment
     * @param sb    child fragment want switch to
     */
    public void switchToFrg(T sb)  {
        if(!(isFinishing() || isDestroyed())) {
            for (T frg : mFrgArr) {
                if (frg == sb) {
                    if (frg != mFrgArr.get(mHotFrgIdx)) {
                        mHotFrgIdx = mFrgArr.indexOf(frg);
                        loadHotFrg();
                    }
                    break;
                }
            }
        }
    }

    /**
     * get current hot fragment
     * @return      current page
     */
    public T getHotFrg()    {
        return mFrgArr.size() > 0 ? mFrgArr.get(mHotFrgIdx) : null;
    }

    /**
     * load hot fragment
     */
    protected void loadHotFrg() {
        if(mHotFrgIdx >= 0  && mHotFrgIdx < mFrgArr.size()) {
            T cur = mFrgArr.get(mHotFrgIdx);
            if(cur instanceof Fragment) {
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.replace(wxm.androidutil.R.id.fl_holder, (Fragment)cur);
                t.commit();
            } else  {
                if(cur instanceof  android.support.v4.app.Fragment) {
                    android.support.v4.app.FragmentTransaction t =
                            getSupportFragmentManager().beginTransaction();
                    t.replace(wxm.androidutil.R.id.fl_holder, (android.support.v4.app.Fragment)cur);
                    t.commit();
                }
            }
        }
    }
}
