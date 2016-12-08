package wxm.KeepAccount.ui.fragment.utility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.DragGrid.DragGridView;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.utility.DGVButtonAdapter;
import wxm.KeepAccount.Base.utility.PreferencesUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACSetting;
import wxm.KeepAccount.ui.dialog.DlgSelectChannel;

/**
 * for welcome
 * Created by ookoo on 2016/12/7.
 */
public class FrgWelcome extends FrgUtilityBase {
    // for ui
    @BindView(R.id.dgv_buttons)
    DragGridView mDGVActions;

    @BindView(R.id.rl_channel)
    RelativeLayout  mRLChannel;

    @BindView(R.id.rl_setting)
    RelativeLayout  mRLSetting;

    // for data
    private List<HashMap<String, Object>> mLSData = new ArrayList<>();

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgWelcome";
        View rootView = layoutInflater.inflate(R.layout.vw_welcome, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        mLSData.clear();
        for(String i : PreferencesUtil.loadHotAction())  {
            HashMap<String, Object> ihm = new HashMap<>();
            ihm.put(DGVButtonAdapter.HKEY_ACT_NAME, i);
            mLSData.add(ihm);
        }

        final DGVButtonAdapter apt = new DGVButtonAdapter(getActivity(), mLSData,
                new String[] {}, new int[] { });

        mDGVActions.setAdapter(apt);
        mDGVActions.setOnChangeListener((from, to) -> {
            HashMap<String, Object> temp = mLSData.get(from);
            if(from < to){
                for(int i=from; i<to; i++){
                    Collections.swap(mLSData, i, i+1);
                }
            }else if(from > to){
                for(int i=from; i>to; i--){
                    Collections.swap(mLSData, i, i-1);
                }
            }

            mLSData.set(to, temp);

            ArrayList<String> hot_name = new ArrayList<>();
            for(HashMap<String, Object> hi : mLSData)   {
                String an = UtilFun.cast_t(hi.get(DGVButtonAdapter.HKEY_ACT_NAME));
                hot_name.add(an);
            }
            PreferencesUtil.saveHotAction(hot_name);

            apt.notifyDataSetChanged();
        });
        apt.notifyDataSetChanged();


        mRLChannel.setOnClickListener(v -> {
            DGVButtonAdapter dapt = UtilFun.cast(mDGVActions.getAdapter());
            DlgSelectChannel dlg = new DlgSelectChannel();
            dlg.setHotChannel(dapt.getCurAction());
            dlg.setDialogListener(new DlgOKOrNOBase.DialogResultListener() {
                @Override
                public void onDialogPositiveResult(DialogFragment dialog) {
                    DlgSelectChannel dsc = UtilFun.cast(dialog);
                    PreferencesUtil.saveHotAction(dsc.getHotChannel());

                    mLSData.clear();
                    for(String i : PreferencesUtil.loadHotAction())     {
                        HashMap<String, Object> ihm = new HashMap<>();
                        ihm.put(DGVButtonAdapter.HKEY_ACT_NAME, i);
                        mLSData.add(ihm);
                    }

                    DGVButtonAdapter dapt = UtilFun.cast(mDGVActions.getAdapter());
                    dapt.notifyDataSetChanged();
                }

                @Override
                public void onDialogNegativeResult(DialogFragment dialog) {
                }
            });

            dlg.show(((AppCompatActivity)getActivity()).getSupportFragmentManager(),
                    "选择频道");
        });

        mRLSetting.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ACSetting.class);
            startActivity(intent);
        });
    }

    @Override
    protected void initUiInfo() {
    }
}
