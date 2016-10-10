package wxm.KeepAccount.ui.fragment.Setting;


import android.content.Context;
import android.support.v4.app.Fragment;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.ui.acutility.ACSetting;

/**
 * 设置页面基础类
 * Created by 123 on 2016/10/10.
 */
public class TFSettingBase extends Fragment {

    /**
     * 得到ACSetting
     * @return  若成功返回结果，否则返回null
     */
    public ACSetting getRootActivity()  {
        Context ct = getContext();
        if(ct instanceof ACSetting) {
            return UtilFun.cast(ct);
        }

        return null;
    }

    /**
     * 切换回主设置页
     */
    public void toMainPage()    {
        ACSetting acs = getRootActivity();
        if(null != acs) {
            acs.change_page(ACSetting.PAGE_IDX_MAIN);
        }
    }

    /**
     * 切换页面
     * @param idx  新页面的idx
     */
    public void toPageByIdx(int idx)    {
        ACSetting acs = getRootActivity();
        if(null != acs) {
            acs.change_page(idx);
        }
    }
}
