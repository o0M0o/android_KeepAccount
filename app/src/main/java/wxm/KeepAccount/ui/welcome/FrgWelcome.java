package wxm.KeepAccount.ui.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.transformer.TransitionEffect;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import wxm.KeepAccount.db.DBDataChangeEvent;
import wxm.KeepAccount.ui.base.FrgUitlity.FrgWithEventBus;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.DragGrid.DragGridView;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.dialog.DlgSelectChannel;
import wxm.KeepAccount.ui.setting.ACSetting;
import wxm.KeepAccount.ui.welcome.banner.FrgAdapter;
import wxm.KeepAccount.ui.welcome.banner.FrgPara;
import wxm.KeepAccount.utility.DGVButtonAdapter;
import wxm.KeepAccount.utility.PreferencesUtil;

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
public class FrgWelcome extends FrgWithEventBus {
    // for ui
    @BindView(R.id.dgv_buttons)
    DragGridView mDGVActions;

    @BindView(R.id.banners)
    LMBanners mLBanners;

    // for data
    private List<HashMap<String, Object>> mLSData = new ArrayList<>();
    private ArrayList<FrgPara> mALFrgs = new ArrayList<>();

    /**
     * handler for DB data change
     * @param event     for event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBEvent(DBDataChangeEvent event) {
        //noinspection unchecked
        mLBanners.setAdapter(new FrgAdapter(getActivity(), null), mALFrgs);
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        initFrgs();
        return layoutInflater.inflate(R.layout.vw_welcome, viewGroup, false);
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {
        initBanner();

        mLSData.clear();
        for (String i : PreferencesUtil.loadHotAction()) {
            HashMap<String, Object> ihm = new HashMap<>();
            ihm.put(DGVButtonAdapter.HKEY_ACT_NAME, i);
            mLSData.add(ihm);
        }

        final DGVButtonAdapter apt = new DGVButtonAdapter(getActivity(), mLSData,
                new String[]{}, new int[]{});

        mDGVActions.setAdapter(apt);
        mDGVActions.setOnChangeListener((from, to) -> {
            HashMap<String, Object> temp = mLSData.get(from);
            if (from < to) {
                for (int i = from; i < to; i++) {
                    Collections.swap(mLSData, i, i + 1);
                }
            } else if (from > to) {
                for (int i = from; i > to; i--) {
                    Collections.swap(mLSData, i, i - 1);
                }
            }

            mLSData.set(to, temp);

            ArrayList<String> hot_name = new ArrayList<>();
            for (HashMap<String, Object> hi : mLSData) {
                String an = UtilFun.cast_t(hi.get(DGVButtonAdapter.HKEY_ACT_NAME));
                hot_name.add(an);
            }
            PreferencesUtil.saveHotAction(hot_name);

            apt.notifyDataSetChanged();
        });
        apt.notifyDataSetChanged();
    }

    private void initFrgs() {
        FrgPara fp = new FrgPara();
        fp.mFPViewId = R.layout.banner_month;
        mALFrgs.add(fp);

        fp = new FrgPara();
        fp.mFPViewId = R.layout.banner_year;
        mALFrgs.add(fp);
    }

    @OnClick({R.id.ib_channel, R.id.ib_setting})
    public void OnActClick(View v) {
        switch (v.getId()) {
            case R.id.ib_channel: {
                DGVButtonAdapter dapt = UtilFun.cast(mDGVActions.getAdapter());
                DlgSelectChannel dlg = new DlgSelectChannel();
                dlg.setHotChannel(dapt.getCurAction());
                dlg.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
                    @Override
                    public void onDialogPositiveResult(DialogFragment dialog) {
                        DlgSelectChannel dsc = UtilFun.cast(dialog);
                        PreferencesUtil.saveHotAction(dsc.getHotChannel());

                        mLSData.clear();
                        for (String i : PreferencesUtil.loadHotAction()) {
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

                dlg.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(),
                        "选择频道");
            }
            break;

            case R.id.ib_setting: {
                Intent intent = new Intent(getActivity(), ACSetting.class);
                startActivity(intent);
            }
            break;
        }
    }

    /**
     * banner is show in head of welcome page
     */
    private void initBanner() {
        //noinspection unchecked
        mLBanners.setAdapter(new FrgAdapter(getActivity(), null), mALFrgs);

        //参数设置
        mLBanners.setAutoPlay(false);//自动播放
        mLBanners.setVertical(false);//是否可以垂直
        mLBanners.setScrollDurtion(222);//两页切换时间
        mLBanners.setCanLoop(true);//循环播放
        mLBanners.setSelectIndicatorRes(R.drawable.page_indicator_select);//选中的原点
        mLBanners.setUnSelectUnIndicatorRes(R.drawable.page_indicator_unselect);//未选中的原点
        mLBanners.setIndicatorWidth(5);//默认为5dp
        mLBanners.setHoriZontalTransitionEffect(TransitionEffect.Default);//选中喜欢的样式
        //mLBanners.setHoriZontalCustomTransformer(new ParallaxTransformer(R.id.id_image));//自定义样式
        mLBanners.setDurtion(5000);//切换时间
        mLBanners.hideIndicatorLayout();//隐藏原点
        mLBanners.showIndicatorLayout();//显示原点
        mLBanners.setIndicatorPosition(LMBanners.IndicaTorPosition.BOTTOM_MID);//设置原点显示位置
    }
}
