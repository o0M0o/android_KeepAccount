package wxm.KeepAccount.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.transformer.TransitionEffect;

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
import wxm.KeepAccount.utility.DGVButtonAdapter;
import wxm.KeepAccount.utility.PreferencesUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.utility.FrgAdapter;
import wxm.KeepAccount.ui.utility.FrgPara;
import wxm.KeepAccount.ui.setting.ACSetting;
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

    @BindView(R.id.banners)
    LMBanners mLBanners;

    // for data
    private List<HashMap<String, Object>> mLSData = new ArrayList<>();
    private ArrayList<FrgPara> mALFrgs = new ArrayList<>();

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgWelcome";
        initFrgs();

        View rootView = layoutInflater.inflate(R.layout.vw_welcome, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        initBanner();

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
            dlg.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
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

    private void initFrgs()  {
        FrgPara fp = new FrgPara();
        fp.mFPViewId = R.layout.banner_month;
        mALFrgs.add(fp);

        fp = new FrgPara();
        fp.mFPViewId = R.layout.banner_year;
        mALFrgs.add(fp);

//        fp = new FrgPara();
//        fp.mFPViewId = R.layout.banner_three;
//        mALFrgs.add(fp);
    }

    private void initBanner()   {
        //设置Banners高度
        //mLBanners.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(this, 200)));
        //本地用法
        mLBanners.setAdapter(new FrgAdapter(getActivity()), mALFrgs);

        //网络图片
//        mLBanners.setAdapter(new UrlImgAdapter(MainActivity.this), networkImages);
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
