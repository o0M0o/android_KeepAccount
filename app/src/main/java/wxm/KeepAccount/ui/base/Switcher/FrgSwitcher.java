package wxm.KeepAccount.ui.base.Switcher;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;

/**
 * base UI for show data
 * Created by wxm on 2016/9/27.
 */
public abstract class FrgSwitcher extends FrgUtilitySupportBase {
    private final static String CHILD_HOT = "child_hot";
    protected FrgUtilitySupportBase[] mViewHelper;
    private int mHotChild = 0;

    @LayoutRes
    private int mFatherFrg;

    @IdRes
    private int mChildFrg;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mHotChild = savedInstanceState.getInt(CHILD_HOT, 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHILD_HOT, mHotChild);
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
        View v = getView();
        if (null != v) {
            mHotChild = (mHotChild + 1) % mViewHelper.length;
            loadHotFrg();
        }
    }

    /**
     * switch to child page
     * @param sb    child page want switch to
     */
    public void switchToPage(FrgUtilitySupportBase sb)  {
        for(int pos = 0; pos < mViewHelper.length; pos++)   {
            if(sb == mViewHelper[pos])  {
                if(mHotChild != pos)    {
                    mHotChild = pos;
                    loadHotFrg();
                }
            }
        }
    }

    /**
     * get current hot page
     * @return      current page
     */
    public FrgUtilitySupportBase getHotPage()    {
        return mViewHelper[mHotChild];
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

    /**
     * set child frg
     * @param childs    all child frg
     */
    protected void setChildFrg(FrgUtilitySupportBase... childs)  {
        mViewHelper = new FrgUtilitySupportBase[childs.length];
        System.arraycopy(childs, 0, mViewHelper, 0, childs.length);
    }

    //// PRIVATE START
    /**
     * load hot fragment
     */
    private void loadHotFrg() {
        android.support.v4.app.FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(mChildFrg, mViewHelper[mHotChild]);
        t.commit();
    }
    //// PRIVATE END
}
