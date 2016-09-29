package wxm.KeepAccount.ui.fragment.ShowData;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.GraphView.ChartsBase;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;

/**
 * 数据显示fragment基类
 * Created by wxm on 2016/9/27.
 */
public abstract class TFShowBase extends Fragment {
    private final static String TAG = "TFShowBase";

    private final static String CHILD_HOT = "child_hot";
    protected int             CHILD_LISTVIWE  = 0;
    protected int             CHILD_GRAPHVIWE = 1;
    private ViewSwitcher    mVSSwitcher;
    protected int           mHotChild = CHILD_LISTVIWE;

    //protected ShowViewHelperBase mListViewHelper;
    //protected ChartsBase            mChartViewHelper;

    protected ShowViewHelperBase[]   mViewHelper;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mHotChild = savedInstanceState.getInt(CHILD_HOT, CHILD_LISTVIWE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHILD_HOT, mHotChild);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tf_show_base, container, false);
        if(null != v) {
            mVSSwitcher = UtilFun.cast(v.findViewById(R.id.vs_page));

            mViewHelper[CHILD_LISTVIWE].createView(inflater, container);
            mViewHelper[CHILD_GRAPHVIWE].createView(inflater, container);
            mVSSwitcher.addView(mViewHelper[CHILD_LISTVIWE].getView(), CHILD_LISTVIWE);
            mVSSwitcher.addView(mViewHelper[CHILD_GRAPHVIWE].getView(), CHILD_GRAPHVIWE);
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(null != view) {
            /*if(CHILD_GRAPHVIWE == mHotChild) {
                List<Object> ls_ret = AppModel.getPayIncomeUtility().GetAllNotes();
                mChartViewHelper.RenderChart(ls_ret);
            } else  {
                mListViewHelper.loadView();
            }*/
            mViewHelper[CHILD_LISTVIWE].loadView();
            mViewHelper[CHILD_GRAPHVIWE].loadView();
            mVSSwitcher = UtilFun.cast(view.findViewById(R.id.vs_page));
            mVSSwitcher.setDisplayedChild(mHotChild);
        }
    }


    /**
     * 在两个视图之间切换
     */
    public void switchPage() {
        View v = getView();
        if(null != v) {
            mVSSwitcher = UtilFun.cast(v.findViewById(R.id.vs_page));

            mHotChild = mVSSwitcher.getDisplayedChild() == 0 ? 1 : 0;
            mVSSwitcher.setDisplayedChild(mHotChild);
        }   else    {
            Toast.makeText(getActivity(), "getView is null", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 过滤视图
     * @param ls_tag   过滤参数 :
     *                 1. 如果为null则不过滤
     *                 2. 如果不为null, 但为空则过滤（不显示任何数据)
     */
    public void filterView(List<String> ls_tag)     {
        if(CHILD_LISTVIWE == mHotChild)
            mViewHelper[mHotChild].filterView(ls_tag);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        mViewHelper[mHotChild].onActivityResult(requestCode, resultCode, data);
    }
}
