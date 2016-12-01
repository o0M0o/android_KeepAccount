package wxm.KeepAccount.Base.define;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;
import wxm.KeepAccount.R;

/**
 * for activity base
 * Created by wxm on 2016/12/1.
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {
    protected String LOG_TAG = "BaseAppCompatActivity";
    protected Fragment  mFGHolder;
    protected int       mDIDBack = R.drawable.ic_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_base);

        ButterKnife.bind(this);
        initUi(savedInstanceState);
    }

    /**
     * 需要在调用此函数前赋值view holder和LOG_TAG
     * @param savedInstanceState 视图参数
     */
    protected void initUi(Bundle savedInstanceState) {
        if(null == mFGHolder)   {
            Log.e(LOG_TAG, "需要先赋值View Holder");
            return;
        }

        // for left menu(go back)
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(mDIDBack);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveActivity();
            }
        });

        if(null == savedInstanceState)  {
            FragmentTransaction t =  getFragmentManager().beginTransaction();
            t.add(R.id.fl_holder, mFGHolder);
            t.commit();
        }
    }


    /**
     * 离开当前activity
     */
    protected abstract void leaveActivity();
}
