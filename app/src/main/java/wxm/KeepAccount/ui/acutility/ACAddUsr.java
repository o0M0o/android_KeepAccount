package wxm.KeepAccount.ui.acutility;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.utility.FrgUsrAdd;

/**
 * 添加用户
 */
public class ACAddUsr extends AppCompatActivity   {
    private static final String TAG = "ACAddUsr";
    private FrgUsrAdd mFGUsrAdd = new FrgUsrAdd();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_usr_add);

        ButterKnife.bind(this);
        init_ui(savedInstanceState);
    }

    /**
     * 初始化UI组件
     */
    private void init_ui(Bundle savedInstanceState) {
        // for frg
        if(null == savedInstanceState) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fl_holder, mFGUsrAdd);
            transaction.commit();
        }
    }
}
