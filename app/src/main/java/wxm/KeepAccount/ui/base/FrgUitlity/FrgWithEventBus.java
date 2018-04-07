package wxm.KeepAccount.ui.base.FrgUitlity;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * @author WangXM
 * @version create：2018/4/7
 */
public abstract class FrgWithEventBus extends FrgUtilitySupportBase {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }
}
