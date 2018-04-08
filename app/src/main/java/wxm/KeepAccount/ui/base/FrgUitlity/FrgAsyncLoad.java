package wxm.KeepAccount.ui.base.FrgUitlity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import wxm.KeepAccount.utility.ToolUtil;

/**
 * @author WangXM
 * @version create：2018/4/7
 */
public abstract class FrgAsyncLoad extends Fragment {
    protected final String LOG_TAG = getClass().getSimpleName();

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

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflaterView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, v);

        initUI(savedInstanceState);
        return v;
    }

    /**
     * refresh UI
     * used by outer totally reinitialize UI
     */
    public final void reInitUI()    {
        if(isVisible()) {
            initUI(null);
        }
    }

    /**
     * reload UI
     * used by outer only reload UI
     */
    public final void reloadUI()    {
        if(isVisible()) {
            loadUI(null);
        }
    }

    /**
     * realize this in derived class
     * @param inflater                  inflater for view
     * @param container                 view holder
     * @param savedInstanceState        If non-null, this fragment is being re-constructed
     *                                  from a previous saved state as given here.
     * @return                          inflated view
     */
    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container,
                                         Bundle savedInstanceState);

    /**
     * load ui
     * @param savedInstanceState        If non-null, this fragment is being re-constructed
     *                                  from a previous saved state as given here.
     */
    protected void loadUI(Bundle savedInstanceState)    {
    }

    /**
     * init ui
     * @param savedInstanceState        If non-null, this fragment is being re-constructed
     *                                  from a previous saved state as given here.
     */
    protected void initUI(Bundle savedInstanceState)    {
    }
}
