package wxm.KeepAccount.ui.data.show.note.ShowData;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilitySupportBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.FrgNoteShow;
import wxm.KeepAccount.ui.utility.NoteShowDataHelper;

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
     * 过滤视图
     * @param ls_tag   过滤参数 :
     *                 1. 如果为null则不过滤
     *                 2. 如果不为null, 但为空则过滤（不显示任何数据)
     */
    public void filterView(List<String> ls_tag)     {
        for(ShowViewHelperBase sb : mViewHelper)    {
            sb.filterView(ls_tag);
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
                mViewHelper[mHotChild].refreshUI();
        }
    }

    //// PRIVATE START

    /**
     * 加载热fragment
     */
    private void loadHotFrg()   {
        android.support.v4.app.FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(cn.wxm.andriodutillib.R.id.fl_holder, mViewHelper[mHotChild]);
        t.commit();
    }
    //// PRIVATE END
}
