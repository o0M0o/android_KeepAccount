package wxm.KeepAccount.ui.data.show.note.ShowData;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilitySupportBase;
import wxm.KeepAccount.R;

/**
 * 数据显示fragment基类
 * Created by wxm on 2016/9/27.
 */
public abstract class TFShowBase extends FrgUtilitySupportBase {
    private final static String CHILD_HOT = "child_hot";
    private int   mHotChild = 0;

    protected ShowViewHelperBase[]   mViewHelper;

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
        View rootView = layoutInflater.inflate(R.layout.tf_show_base, viewGroup, false);
        ButterKnife.bind(this, rootView);

        return rootView;
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
        Log.i(LOG_TAG, "setUserVisibleHint, visible = "
                        + (isVisibleToUser ? "true" : "false")
                        + ", view = " + (cur_v == null ? "false" : "true"));
    }

    /**
     * 在两个视图之间切换
     */
    public void switchPage() {
        View v = getView();
        if(null != v) {
            mHotChild = mHotChild >= mViewHelper.length - 1 ? 0 : mHotChild + 1;
            loadHotFrg();
        }
    }


    /**
     * 数据变化后调用
     * @param bForce   若为true则刷新数据
     */
    public void loadView(boolean bForce)  {
        View cur_v = getView();
        if(null != cur_v) {
            if(bForce)
                mViewHelper[mHotChild].refreshData();
        }
    }

    //// PRIVATE START

    /**
     * 加载热fragment
     */
    private void loadHotFrg()   {
        android.support.v4.app.FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(R.id.fl_holder, mViewHelper[mHotChild]);
        t.commit();
    }
    //// PRIVATE END
}
