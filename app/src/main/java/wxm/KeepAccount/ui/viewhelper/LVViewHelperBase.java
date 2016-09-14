package wxm.KeepAccount.ui.viewhelper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.LinkedList;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;

/**
 * viewhelper基础类
 * Created by 123 on 2016/9/14.
 */
public class LVViewHelperBase {
    protected View mSelfView;
    protected LinkedList<HashMap<String, String>> mMainPara;
    protected HashMap<String, LinkedList<HashMap<String, String>>>    mHMSubPara;
    protected LinkedList<String>                                      mFilterPara;

    public LVViewHelperBase()   {
        mSelfView   = null;
        mMainPara   = new LinkedList<>();
        mHMSubPara  = new HashMap<>();
        mFilterPara = new LinkedList<>();
    }

    protected void setAttachLayoutVisible(int visible)   {
        RelativeLayout rl = UtilFun.cast(mSelfView.findViewById(R.id.rl_attach_button));
        assert null != rl;
        setLayoutVisible(rl, visible);
    }


    protected void setFilterLayoutVisible(int visible)   {
        if(View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        RelativeLayout rl = UtilFun.cast(mSelfView.findViewById(R.id.rl_filter));
        assert null != rl;
        setLayoutVisible(rl, visible);
    }


    protected void setAccpetGiveupLayoutVisible(int visible)   {
        if(View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        RelativeLayout rl = UtilFun.cast(mSelfView.findViewById(R.id.rl_accpet_giveup));
        assert null != rl;
        setLayoutVisible(rl, visible);
    }

    private void setLayoutVisible(RelativeLayout rl, int visible)    {
        int w = RelativeLayout.LayoutParams.MATCH_PARENT;
        int h = 0;
        if(View.INVISIBLE != visible)
            h = RelativeLayout.LayoutParams.WRAP_CONTENT;

        ViewGroup.LayoutParams param = rl.getLayoutParams();
        param.width = w;
        param.height = h;
        rl.setLayoutParams(param);
    }
}
