package wxm.KeepAccount.ui.base.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.R;

/**
 * 记录内容编辑块
 * Created by 123 on 2016/9/6.
 */
public class NoteContentFragment extends Fragment {
    private static final String TAG = "NoteContentFragment";

    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";

    public NoteContentFragment()    {
        super();
    }

    /**
     * @return a new instance of {@link LVContentFragment}, adding the parameters into a bundle and
     * setting them as arguments.
     */
    public static NoteContentFragment newInstance(CharSequence title, int indicatorColor,
                                                int dividerColor) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);

        NoteContentFragment fragment = new NoteContentFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        CharSequence cs = args.getCharSequence(KEY_TITLE);
        assert null != cs;

        View cur_view;
        String title = cs.toString();
        if(title.equals(AppGobalDef.CNSTR_RECORD_PAY))  {
            cur_view = inflater.inflate(R.layout.vw_edit_pay, container, false);
        }   else    {
            cur_view = inflater.inflate(R.layout.vw_edit_income, container, false);
        }

        return cur_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        Bundle args = getArguments();
        if (args != null) {
        }
        */
    }
}
