package wxm.KeepAccount.ui.acutility;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;
import wxm.KeepAccount.ui.fragment.EditNoteSlidingTabsFragment;

public class ACNoteEdit extends AppCompatActivity {
    private SlidingTabsColorsFragment mTabFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_edit);

        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        // set fragment for tab
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //mTabFragment = new ListViewSlidingTabsFragment();
            mTabFragment = new EditNoteSlidingTabsFragment();
            transaction.replace(R.id.tabfl_note_edit_content, mTabFragment);
            transaction.commit();
        }
    }
}
