package wxm.KeepAccount.ui.data.show.note.ShowData;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;

/**
 * 数据显示fragment基类
 * Created by wxm on 2016/9/27.
 */
public abstract class TFShowBase extends FrgUtilitySupportBase {
    private final static String CHILD_HOT = "child_hot";
    protected ShowViewBase[] mViewHelper;
    private int mHotChild = 0;

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
        return layoutInflater.inflate(R.layout.tf_show_base, viewGroup, false);
    }

    @Override
    protected void initUiComponent(View view) {
    }

    @Override
    protected void loadUI() {
        loadHotFrg();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        View cur_v = getView();
        Log.i(LOG_TAG, "setUserVisibleHint, visible = "  + (isVisibleToUser ? "true" : "false")
                + ", view = " + (cur_v == null ? "false" : "true"));
    }

    /**
     * switch in pages
     */
    public void switchPage() {
        View v = getView();
        if (null != v) {
            mHotChild = (mHotChild + 1) % mViewHelper.length;
            loadHotFrg();
        }
    }


    /**
     * update view
     * @param bForce    if true it will reload data
     */
    public void loadView(boolean bForce) {
        View cur_v = getView();
        if (null != cur_v) {
            if (bForce)
                mViewHelper[mHotChild].loadView();
        }
    }

    //// PRIVATE START
    /**
     * load hot fragment
     */
    private void loadHotFrg() {
        android.support.v4.app.FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(R.id.fl_holder, mViewHelper[mHotChild]);
        t.commit();
    }
    //// PRIVATE END
}
