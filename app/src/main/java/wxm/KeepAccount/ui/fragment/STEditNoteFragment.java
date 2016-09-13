package wxm.KeepAccount.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;
import wxm.KeepAccount.ui.viewhelper.EditIncomeNoteViewHelper;
import wxm.KeepAccount.ui.viewhelper.EditPayNoteViewHelper;
import wxm.KeepAccount.ui.viewhelper.IEditNoteViewHelper;

/**
 * 编辑收支数据的视图
 * Created by 123 on 2016/9/6.
 */
public class STEditNoteFragment extends SlidingTabsColorsFragment {
    private static final String TAG = "EditNoteSTFragment ";

    private static String          mAction;
    private static PayNoteItem     mPayNote;
    private static IncomeNoteItem  mIncomeNote;

    protected static class ListViewPagerItem extends SamplePagerItem {
        protected NoteContentFragment  mContent;

        public ListViewPagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            super(title, indicatorColor, dividerColor);
        }

        @Override
        protected Fragment createFragment() {
            mContent =  NoteContentFragment.newInstance(mTitle, mIndicatorColor,
                            mDividerColor, new Object[]{mAction, mPayNote, mIncomeNote});
            return mContent;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get intent para
        Intent it = getActivity().getIntent();
        assert null != it;

        mAction = it.getStringExtra(ACNoteEdit.PARA_ACTION);
        if(UtilFun.StringIsNullOrEmpty(mAction)) {
            Log.e(TAG, "调用intent缺少'PARA_ACTION'参数");
            return ;
        }

        if(!mAction.equals(ACNoteEdit.LOAD_NOTE_ADD)
                && !mAction.equals(ACNoteEdit.LOAD_NOTE_MODIFY))   {
            Log.e(TAG, "调用intent中'PARA_ACTION'参数不正确, cur_val = " + mAction);
            return ;
        }

        if(mAction.equals(ACNoteEdit.LOAD_NOTE_MODIFY)) {
            int pid = it.getIntExtra(ACNoteEdit.PARA_NOTE_PAY, AppGobalDef.INVALID_ID);
            int iid = it.getIntExtra(ACNoteEdit.PARA_NOTE_INCOME, AppGobalDef.INVALID_ID);

            if(AppGobalDef.INVALID_ID != pid)
                mPayNote = AppModel.getPayIncomeUtility().GetPayNoteById(pid);
            else if(AppGobalDef.INVALID_ID != iid)
                mIncomeNote = AppModel.getPayIncomeUtility().GetIncomeNoteById(iid);

            if(null == mPayNote && null == mIncomeNote)   {
                Log.e(TAG, "调用intent缺少'PARA_NOTE_PAY'和'PARA_NOTE_INCOME'参数");
                return ;
            }
        } else  {
            mIncomeNote = null;
            mPayNote = null;
        }

        // BEGIN_INCLUDE (populate_tabs)
        if(mTabs.isEmpty()) {
            if(mAction.equals(ACNoteEdit.LOAD_NOTE_ADD)) {
                mTabs.add(new ListViewPagerItem(
                        AppGobalDef.CNSTR_RECORD_PAY, // Title
                        Color.GREEN, // Indicator color
                        Color.GRAY// Divider color
                ));

                mTabs.add(new ListViewPagerItem(
                        AppGobalDef.CNSTR_RECORD_INCOME, // Title
                        Color.GREEN, // Indicator color
                        Color.GRAY// Divider color
                ));
            }   else    {
                if(null != mPayNote)    {
                    mTabs.add(new ListViewPagerItem(
                            AppGobalDef.CNSTR_RECORD_PAY, // Title
                            Color.GREEN, // Indicator color
                            Color.GRAY// Divider color
                    ));
                } else  {
                    mTabs.add(new ListViewPagerItem(
                            AppGobalDef.CNSTR_RECORD_INCOME, // Title
                            Color.GREEN, // Indicator color
                            Color.GRAY// Divider color
                    ));
                }
            }
        }
        // END_INCLUDE (populate_tabs)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vw = inflater.inflate(R.layout.fm_main, container, false);
        return vw;
    }

    public boolean onAccpet()    {
        if(AppGobalDef.INVALID_ID == mCurTabPos || null == mCurView)
            return false;

        NoteContentFragment nf = ((ListViewPagerItem)mTabs.get(mCurTabPos)).mContent;
        return nf.onAccept();
    }


    /**
     * 记录内容编辑块
     * Created by 123 on 2016/9/6.
     */
    public static class NoteContentFragment extends Fragment  {
        private static final String TAG = "NoteContentFragment";

        private String          mAction;
        private String          mNoteType;
        private PayNoteItem     mOldPayNote;
        private IncomeNoteItem  mOldIncomeNote;

        private IEditNoteViewHelper mViewHelper;

        public NoteContentFragment()    {
            super();
        }

        /**
         * @return a new instance of {@link NoteContentFragment}, adding the parameters into a bundle and
         * setting them as arguments.
         */
        public static NoteContentFragment newInstance(CharSequence title, int indicatorColor,
                                                      int dividerColor, Object[] para_arr) {
            Bundle bundle = new Bundle();
            bundle.putCharSequence(KEY_TITLE, title);
            bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
            bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);

            NoteContentFragment fragment = new NoteContentFragment();
            fragment.setArguments(bundle);
            fragment.mAction         = UtilFun.cast(para_arr[0]);
            fragment.mOldPayNote     = UtilFun.cast(para_arr[1]);
            fragment.mOldIncomeNote  = UtilFun.cast(para_arr[2]);

            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle args = getArguments();
            CharSequence cs = args.getCharSequence(KEY_TITLE);
            assert null != cs;

            String title = cs.toString();
            Log.i(TAG, "onCreateView, cur title = " + cs.toString());
            if(title.equals(AppGobalDef.CNSTR_RECORD_PAY))  {
                mViewHelper = new EditPayNoteViewHelper();
                mViewHelper.setPara(mAction, mOldPayNote);
            }   else    {
                mViewHelper = new EditIncomeNoteViewHelper();
                mViewHelper.setPara(mAction, mOldIncomeNote);
            }

            mNoteType = title;
            return mViewHelper.createView(inflater, container);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mViewHelper.loadView();
            /*if(null != savedInstanceState) {
            }

            Bundle args = getArguments();
            if (args != null) {
            }*/
        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data)   {
            super.onActivityResult(requestCode, resultCode, data);
            mViewHelper.onActivityResult(requestCode, resultCode, data);
        }

        public boolean onAccept()   {
            return mViewHelper.onAccept();
        }
    }
}
