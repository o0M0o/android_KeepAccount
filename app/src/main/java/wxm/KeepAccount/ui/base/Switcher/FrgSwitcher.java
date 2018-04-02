package wxm.KeepAccount.ui.base.Switcher;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * base UI for show data
 * Created by WangXM on2016/9/27.
 */
public abstract class FrgSwitcher<T>
        extends FrgUtilitySupportBase   {
    private final static String CHILD_HOT = "child_hot";
    protected ArrayList<T>  mFrgArr = new ArrayList<>();
    protected int           mHotFrgIdx  = 0;

    @LayoutRes
    private int mFatherFrg;

    @IdRes
    private int mChildFrg;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mHotFrgIdx = savedInstanceState.getInt(CHILD_HOT, 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHILD_HOT, mHotFrgIdx);
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(mFatherFrg, viewGroup, false);
    }

    @Override
    protected void initUiComponent(View view) {
    }

    @Override
    protected void loadUI() {
        loadHotFrg();
    }

    /**
     * switch in pages
     * will loop switch between all child
     */
    public void switchPage() {
        if(null != getView()) {
            mHotFrgIdx = (mHotFrgIdx + 1) % mFrgArr.size();
            loadHotFrg();
        }
    }

    /**
     * switch to child page
     * @param sb    child page want switch to
     */
    public void switchToPage(T sb)  {
        if(null != getView()) {
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
     * get current hot page
     * @return      current page
     */
    public T getHotPage()    {
        return mFrgArr.size() > 0 ? mFrgArr.get(mHotFrgIdx) : null;
    }

    /**
     * set child frg
     * @param child    all child frg
     */
    protected void addChildFrg(T child)  {
        mFrgArr.add(child);
    }

    /**
     * set main resource id
     * @param father        mainly frg
     * @param child         container frg for child
     */
    protected void setFrgID(@LayoutRes int father, @IdRes int child) {
        mFatherFrg = father;
        mChildFrg = child;
    }


    //// PRIVATE START
    /**
     * load hot fragment
     */
    protected void loadHotFrg() {
        if(null != getView() && mHotFrgIdx >= 0  && mHotFrgIdx < mFrgArr.size()) {
            T cur = mFrgArr.get(mHotFrgIdx);
            if(cur instanceof android.support.v4.app.Fragment) {
                android.support.v4.app.FragmentTransaction t =
                        getChildFragmentManager().beginTransaction();
                t.replace(mChildFrg, (android.support.v4.app.Fragment)cur);
                t.commit();
            }
        }
    }
    //// PRIVATE END
}
