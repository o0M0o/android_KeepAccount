package wxm.KeepAccount.ui.base.SwitcherActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * activity extend
 * T can be --
 *      android.app.Fragment  or android.support.v4.app.Fragment
 * Created by WangXM on2018/03/30.
 */
public abstract class ACSwitcherActivity<T>
        extends AppCompatActivity {
    private final static String CHILD_HOT = "child_hot";
    protected String LOG_TAG = "ACSwitcherActivity";

    private int       mDIDBack = wxm.androidutil.R.drawable.ic_back;

    protected ArrayList<T>  mALFrg;
    protected int           mHotFrgIdx  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(wxm.androidutil.R.layout.ac_base);

        LOG_TAG = this.getClass().getSimpleName();

        ButterKnife.bind(this);
        initUi(savedInstanceState);

        if(null == savedInstanceState)  {
            loadHotFragment();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHILD_HOT, mHotFrgIdx);
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
     * switch in pages
     * will loop switch between all child
     */
    public void switchFragment() {
        if(!(isFinishing() || isDestroyed())) {
            swapToFragmentByIdx((mHotFrgIdx + 1) % mALFrg.size());
        }
    }

    /**
     * switch to child fragment
     * @param sb    child fragment want switch to
     */
    public void switchToFragment(T sb)  {
        if(!(isFinishing() || isDestroyed())) {
            for (T frg : mALFrg) {
                if (frg == sb && frg != mALFrg.get(mHotFrgIdx)) {
                    swapToFragmentByIdx(mALFrg.indexOf(frg));
                    break;
                }
            }
        }
    }

    /**
     * get current hot fragment
     * @return      current page
     */
    public T getHotFragment()    {
        return mALFrg.size() > 0 ? mALFrg.get(mHotFrgIdx) : null;
    }


    protected void removeAllFragment()  {
        mALFrg.clear();
        mHotFrgIdx = 0;
    }

    /**
     * add child frg
     * @param child    all child frg
     */
    protected void addFragment(T child)  {
        mALFrg.add(child);
    }


    /**
     * invoke this to setup ui
     * @param savedInstanceState    param for ui
     */
    protected void initUi(Bundle savedInstanceState) {
        // for left menu(go back)
        Toolbar toolbar = ButterKnife.findById(this, wxm.androidutil.R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getBackIconRID());
        toolbar.setNavigationOnClickListener(v -> leaveActivity());

        // for Fragment
        if (null != mALFrg && savedInstanceState != null) {
            mHotFrgIdx = savedInstanceState.getInt(CHILD_HOT, 0);
        } else  {
            mALFrg = new ArrayList<>();
            mHotFrgIdx = 0;
        }
    }

    /**
     * leave activity
     */
    protected void leaveActivity()   {
        finish();
    }

    /**
     * load hot fragment
     */
    private void loadHotFragment() {
        if(mHotFrgIdx >= 0  && mHotFrgIdx < mALFrg.size()) {
            T cur = mALFrg.get(mHotFrgIdx);
            if(cur instanceof Fragment) {
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.replace(wxm.androidutil.R.id.fl_holder, (Fragment)cur);
                t.commit();
            } else  {
                if(cur instanceof android.support.v4.app.Fragment) {
                    android.support.v4.app.FragmentTransaction t =
                            getSupportFragmentManager().beginTransaction();
                    t.replace(wxm.androidutil.R.id.fl_holder, (android.support.v4.app.Fragment)cur);
                    t.commit();
                }
            }
        }
    }

    /**
     * swap to fragment by idx
     * @param idx       swap in fragment idx
     */
    private void swapToFragmentByIdx(int idx)    {
        if(idx >= 0  && idx < mALFrg.size()) {
            mHotFrgIdx = idx;
            loadHotFragment();
        }
    }
}
