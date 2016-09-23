package wxm.KeepAccount.ui.acinterface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

import wxm.KeepAccount.R;

public class ACNoteShowNew extends AppCompatActivity {
    private final static String TAG = "ACNoteShowNew";
    private TabHost     mTHTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_show_new);

        init_tabs();
    }

    private void init_tabs() {
        // 获取TabHost对象
        mTHTabs = (TabHost) findViewById(R.id.th_tabs);
        mTHTabs.setup();
        mTHTabs.addTab(mTHTabs.newTabSpec("tab1").setIndicator("第一个标签")
                .setContent(R.id.tab1));

        mTHTabs.addTab(mTHTabs.newTabSpec("tab2").setIndicator("第二个标签")
                .setContent(R.id.tab2));

        mTHTabs.addTab(mTHTabs.newTabSpec("tab3").setIndicator("第三个标签")
                .setContent(R.id.tab3));
    }
}
