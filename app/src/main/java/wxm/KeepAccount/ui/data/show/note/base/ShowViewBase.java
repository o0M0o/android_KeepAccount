package wxm.KeepAccount.ui.data.show.note.base;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.LinkedList;

import butterknife.BindView;
import wxm.KeepAccount.ui.base.FrgUitlity.FrgAdvBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;

/**
 * base for show record
 * Created by xiaoming wang on 2016/9/14.
 */
public abstract class ShowViewBase extends FrgAdvBase {
    // filter for view
    protected boolean mBFilter;
    protected final LinkedList<String> mFilterPara;

    @BindView(R.id.rl_attach_button)
    RelativeLayout mRLAttachButton;

    @BindView(R.id.rl_accpet_giveup)
    RelativeLayout mRLAccept;

    @BindView(R.id.rl_filter)
    RelativeLayout mRLFilter;

    protected ShowViewBase() {
        super();
        mBFilter = false;
        mFilterPara = new LinkedList<>();
    }

    /**
     * get root activity
     * @return      activity if success else null
     */
    protected ACNoteShow getRootActivity() {
        Context ct = getContext();
        if (ct instanceof ACNoteShow) {
            return UtilFun.cast(ct);
        }

        return null;
    }

    /**
     * set layout visibility
     * @param visible can be :
     *                1. {@code View.GONE}
     *                2. {@code View.VISIBLE}
     */
    protected void setAttachLayoutVisible(int visible) {
        mRLAttachButton.setVisibility(visible);
    }


    /**
     * set filter layout visibility
     * @param visible can be :
     *                1. {@code View.GONE}
     *                2. {@code View.VISIBLE}
     */
    protected void setFilterLayoutVisible(int visible) {
        if (View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        mRLFilter.setVisibility(visible);
    }


    /**
     * set accept/give-up layout visibility
     * @param visible can be :
     *                1. {@code View.GONE}
     *                2. {@code View.VISIBLE}
     */
    protected void setAccpetGiveupLayoutVisible(int visible) {
        if (View.VISIBLE == visible)
            setAttachLayoutVisible(View.VISIBLE);

        mRLAccept.setVisibility(visible);
    }
}
