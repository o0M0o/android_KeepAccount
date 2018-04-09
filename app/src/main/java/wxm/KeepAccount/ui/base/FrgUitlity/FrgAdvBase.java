package wxm.KeepAccount.ui.base.FrgUitlity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * base for fragment
 * simple to use fragment
 * @author WangXM
 * @version create：2018/4/7
 */
public abstract class FrgAdvBase extends Fragment {
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
        View v = inflater.inflate(getLayoutID(), container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        if (null != view) {
            initUI(savedInstanceState);
        }
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
     * realize this to setup layout ID
     * @return      layout ID for UI
     */
    @LayoutRes
    protected abstract int getLayoutID();

    /**
     * derive it do ui load work
     * suggest invoke it in initUI
     * @param savedInstanceState        If non-null, this fragment is being re-constructed
     *                                  from a previous saved state as given here.
     */
    protected void loadUI(Bundle savedInstanceState)    {
    }

    /**
     * derive it do ui init work
     * @param savedInstanceState        If non-null, this fragment is being re-constructed
     *                                  from a previous saved state as given here.
     */
    protected void initUI(Bundle savedInstanceState)    {
    }
}
