package com.hfaufhreu.hjfeuio.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hfaufhreu.hjfeuio.R;
import com.hfaufhreu.hjfeuio.app.App;
import com.hfaufhreu.hjfeuio.base.BaseFragment;
import com.hfaufhreu.hjfeuio.bean.PayResultInfo;
import com.hfaufhreu.hjfeuio.config.PayConfig;
import com.hfaufhreu.hjfeuio.event.StartBrotherEvent;
import com.hfaufhreu.hjfeuio.event.TabSelectedEvent;
import com.hfaufhreu.hjfeuio.pay.PayUtil;
import com.hfaufhreu.hjfeuio.ui.dialog.FullVideoPayDialog;
import com.hfaufhreu.hjfeuio.ui.fragment.bbs.BBSFragment;
import com.hfaufhreu.hjfeuio.ui.fragment.film.FilmFragment;
import com.hfaufhreu.hjfeuio.ui.fragment.magnet.MagnetFragment;
import com.hfaufhreu.hjfeuio.ui.fragment.mine.HotLineFragment;
import com.hfaufhreu.hjfeuio.ui.fragment.mine.MineFragment;
import com.hfaufhreu.hjfeuio.ui.fragment.rank.RankFragment;
import com.hfaufhreu.hjfeuio.ui.fragment.vip.BlackGlodVipFragment;
import com.hfaufhreu.hjfeuio.ui.fragment.vip.DiamondVipFragment;
import com.hfaufhreu.hjfeuio.ui.fragment.vip.GlodVipFragment;
import com.hfaufhreu.hjfeuio.ui.fragment.vip.TrySeeFragment;
import com.hfaufhreu.hjfeuio.ui.view.BottomBar;
import com.hfaufhreu.hjfeuio.ui.view.BottomBarTab;
import com.hfaufhreu.hjfeuio.util.API;
import com.hfaufhreu.hjfeuio.util.SharedPreferencesUtil;
import com.hfaufhreu.hjfeuio.util.ToastUtils;
import com.sdky.jzp.SdkPay;
import com.sdky.jzp.data.CheckOrder;
import com.skpay.NINESDK;
import com.skpay.codelib.utils.encryption.MD5Encoder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.Call;


/**
 * Case By: 主Fragment
 * package:com.hfaufhreu.hjfeuio.ui.fragment
 * Author：scene on 2017/4/18 9:06
 */

public class MainFragment extends BaseFragment {
    private static final int REQ_MSG = 10;

    public static final int TAB_1 = 0;
    public static final int TAB_2 = 1;
    public static final int TAB_3 = 2;
    public static final int TAB_4 = 3;
    public static final int TAB_5 = 4;

    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.name)
    TextView name;

    private List<SupportFragment> fragments = new ArrayList<>();
    private List<String> tabNames = new ArrayList<>();


    private FullVideoPayDialog functionPayDialog;
    private FullVideoPayDialog.Builder builder;

    //短代支付
    public SdkPay sdkPay;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (savedInstanceState == null) {
            tabNames.clear();
            fragments.clear();
            switch (App.isVip) {
                case 0://试看
                    fragments.add(TrySeeFragment.newInstance());
                    fragments.add(GlodVipFragment.newInstance());
                    fragments.add(MagnetFragment.newInstance());
                    fragments.add(RankFragment.newInstance());
                    fragments.add(BBSFragment.newInstance());

                    tabNames.add("体验区");
                    tabNames.add("会员区");
                    tabNames.add("磁力链");
                    tabNames.add("排行榜");
                    tabNames.add("福利社");
                    break;
                case 1://黄金会员
                    fragments.add(GlodVipFragment.newInstance());
                    fragments.add(DiamondVipFragment.newInstance());
                    fragments.add(MagnetFragment.newInstance());
                    fragments.add(RankFragment.newInstance());
                    fragments.add(BBSFragment.newInstance());
                    tabNames.add("黄金区");
                    tabNames.add("钻石区");
                    tabNames.add("磁力链");
                    tabNames.add("排行榜");
                    tabNames.add("福利社");
                    break;
                case 2://钻石会员
                    fragments.add(DiamondVipFragment.newInstance());
                    fragments.add(BlackGlodVipFragment.newInstance());
                    fragments.add(FilmFragment.newInstance());
                    fragments.add(RankFragment.newInstance());
                    fragments.add(MineFragment.newInstance());
                    tabNames.add("钻石区");
                    tabNames.add("黑金区");
                    tabNames.add("片库");
                    tabNames.add("排行榜");
                    tabNames.add("我的");
                    break;
                case 3://VPN海外会员
                    fragments.add(DiamondVipFragment.newInstance());
                    fragments.add(BlackGlodVipFragment.newInstance());
                    fragments.add(FilmFragment.newInstance());
                    fragments.add(RankFragment.newInstance());
                    fragments.add(MineFragment.newInstance());

                    tabNames.add("海外钻石");
                    tabNames.add("海外黑金");
                    tabNames.add("片库");
                    tabNames.add("排行榜");
                    tabNames.add("我的");
                    break;
                case 4://海外服务商
                    fragments.add(DiamondVipFragment.newInstance());
                    fragments.add(BlackGlodVipFragment.newInstance());
                    fragments.add(FilmFragment.newInstance());
                    fragments.add(RankFragment.newInstance());
                    fragments.add(MineFragment.newInstance());

                    tabNames.add("海外钻石");
                    tabNames.add("海外黑金");
                    tabNames.add("片库");
                    tabNames.add("排行榜");
                    tabNames.add("我的");
                    break;
                case 5://黑金会员
                    fragments.add(BlackGlodVipFragment.newInstance());
                    fragments.add(FilmFragment.newInstance());
                    fragments.add(RankFragment.newInstance());
                    fragments.add(MineFragment.newInstance());

                    tabNames.add("海外黑金");
                    tabNames.add("片库");
                    tabNames.add("排行榜");
                    tabNames.add("我的");
                    break;
                case 6://海外通道
                    fragments.add(BlackGlodVipFragment.newInstance());
                    fragments.add(FilmFragment.newInstance());
                    fragments.add(RankFragment.newInstance());
                    fragments.add(MineFragment.newInstance());

                    tabNames.add("急速黑金");
                    tabNames.add("片库");
                    tabNames.add("排行榜");
                    tabNames.add("我的");
                    break;
                case 7://海外双线
                    fragments.add(BlackGlodVipFragment.newInstance());
                    fragments.add(FilmFragment.newInstance());
                    fragments.add(RankFragment.newInstance());
                    fragments.add(MineFragment.newInstance());

                    tabNames.add("超速黑金");
                    tabNames.add("片库");
                    tabNames.add("排行榜");
                    tabNames.add("我的");
                    break;
            }
            if (fragments.size() == 4) {
                loadMultipleRootFragment(R.id.fl_container, TAB_1,
                        fragments.get(TAB_1),
                        fragments.get(TAB_2),
                        fragments.get(TAB_3),
                        fragments.get(TAB_4));
            } else {
                loadMultipleRootFragment(R.id.fl_container, TAB_1,
                        fragments.get(TAB_1),
                        fragments.get(TAB_2),
                        fragments.get(TAB_3),
                        fragments.get(TAB_4),
                        fragments.get(TAB_5));
            }
        } else {
            switch (App.isVip) {
                case 0:
                    fragments.add(findChildFragment(TrySeeFragment.class));
                    fragments.add(findChildFragment(GlodVipFragment.class));
                    fragments.add(findChildFragment(MagnetFragment.class));
                    fragments.add(findChildFragment(RankFragment.class));
                    fragments.add(findChildFragment(BBSFragment.class));
                    break;
                case 1:
                    fragments.add(findChildFragment(GlodVipFragment.class));
                    fragments.add(findChildFragment(DiamondVipFragment.class));
                    fragments.add(findChildFragment(MagnetFragment.class));
                    fragments.add(findChildFragment(RankFragment.class));
                    fragments.add(findChildFragment(BBSFragment.class));
                    break;
                case 2:
                    fragments.add(findChildFragment(DiamondVipFragment.class));
                    fragments.add(findChildFragment(BlackGlodVipFragment.class));
                    fragments.add(findChildFragment(FilmFragment.class));
                    fragments.add(findChildFragment(BBSFragment.class));
                    fragments.add(findChildFragment(MineFragment.class));
                    break;
                case 3:
                    fragments.add(findChildFragment(DiamondVipFragment.class));
                    fragments.add(findChildFragment(BlackGlodVipFragment.class));
                    fragments.add(findChildFragment(FilmFragment.class));
                    fragments.add(findChildFragment(BBSFragment.class));
                    fragments.add(findChildFragment(MineFragment.class));
                    break;
                case 4:
                    fragments.add(findChildFragment(DiamondVipFragment.class));
                    fragments.add(findChildFragment(BlackGlodVipFragment.class));
                    fragments.add(findChildFragment(FilmFragment.class));
                    fragments.add(findChildFragment(BBSFragment.class));
                    fragments.add(findChildFragment(MineFragment.class));
                    break;
                case 5:
                    fragments.add(findChildFragment(BlackGlodVipFragment.class));
                    fragments.add(findChildFragment(FilmFragment.class));
                    fragments.add(findChildFragment(BBSFragment.class));
                    fragments.add(findChildFragment(MineFragment.class));
                    break;
                case 6:
                    fragments.add(findChildFragment(BlackGlodVipFragment.class));
                    fragments.add(findChildFragment(FilmFragment.class));
                    fragments.add(findChildFragment(BBSFragment.class));
                    fragments.add(findChildFragment(MineFragment.class));
                    break;
                case 7:
                    fragments.add(findChildFragment(BlackGlodVipFragment.class));
                    fragments.add(findChildFragment(FilmFragment.class));
                    fragments.add(findChildFragment(BBSFragment.class));
                    fragments.add(findChildFragment(MineFragment.class));
                    break;
            }
        }
        name.setText(tabNames.get(0));
        initView();
        getDuandaiToken();
        return view;
    }


    private void initView() {
        EventBus.getDefault().register(this);

        switch (App.isVip) {
            case 0:
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(0)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(1)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(2)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(3)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(4)));
                break;
            case 1:
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(0)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(1)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(2)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(3)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(4)));
                break;
            case 2:
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(0)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(1)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(2)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(3)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(4)));
                break;
            case 3:
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(0)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(1)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(2)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(3)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(4)));
                break;
            case 4:
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(0)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(1)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(2)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(3)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(4)));
                break;
            case 5:
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(0)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(1)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(2)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(3)));
                break;
            case 6:
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(0)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(1)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(2)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(3)));
                break;
            case 7:
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(0)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(1)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(2)));
                mBottomBar.addItem(new BottomBarTab(_mActivity, R.drawable.ic_index, tabNames.get(3)));
                break;
        }

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(fragments.get(position), fragments.get(prePosition));
                name.setText(tabNames.get(position));
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                // 这里推荐使用EventBus来实现 -> 解耦
                // 在FirstPagerFragment,FirstHomeFragment中接收, 因为是嵌套的Fragment
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
                EventBus.getDefault().post(new TabSelectedEvent(position));
            }
        });
    }

    @Override
    protected void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_MSG && resultCode == RESULT_OK) {

        }
    }

    /**
     * start other BrotherFragment
     */
    @Subscribe
    public void startBrother(StartBrotherEvent event) {
        start(event.targetFragment);
    }

    @Override
    public void onDestroyView() {
        NINESDK.exit(_mActivity, sdkPay);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @OnClick(R.id.complaint)
    public void onClickComplaint() {
        EventBus.getDefault().post(new StartBrotherEvent(HotLineFragment.newInstance()));
    }

    @OnClick({R.id.search, R.id.vip})
    public void onClickTop(View v) {
        if (v.getId() == R.id.search) {
            EventBus.getDefault().post(new StartBrotherEvent(MagnetFragment.newInstance()));
        } else {
            if (App.isVip == 0) {
                if (builder == null) {
                    builder = new FullVideoPayDialog.Builder(_mActivity);
                    builder.setWeChatPayClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PayUtil.getInstance().payByWeChat(_mActivity, 1, 0);
                        }
                    });

                    builder.setAliPayClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PayUtil.getInstance().payByAliPay(_mActivity, 1, 0);
                        }
                    });
                }
                if (functionPayDialog == null) {
                    functionPayDialog = builder.create();
                }
                functionPayDialog.show();
                clickWantPay();
            } else {
                ToastUtils.getInstance(_mActivity).showToast("您已经是VIP了");
            }
        }

    }

    /**
     * 弹出支付窗口之后调用
     */
    public static void clickWantPay() {
        OkHttpUtils.get().url(API.URL_PRE + API.PAY_CLICK + App.IMEI).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String s, int i) {

            }
        });
    }

    //初始化短代支付
    private static final String DUANDAI_PAY_KEY = "duan_dai_pay";
    private int paySuccessMoney = 0;
    private int time = 0;
    private boolean status = false;

    private void initDuanDaiPay() {
        sdkPay = NINESDK.init(_mActivity, "492", 10001, new SdkPay.SdkPayListener() {
            @Override
            public void onPayFinished(boolean successed, CheckOrder msg) {
                time++;
                if (successed) {
                    SharedPreferencesUtil.putBoolean(_mActivity, DUANDAI_PAY_KEY, true);
                    paySuccessMoney += 1000;
                    status = true;
                }
                if (time < 5) {
                    toDuandaiPay();
                } else {
                    sendSMSResult(msg);
                }
            }
        });
        if (!SharedPreferencesUtil.getBoolean(_mActivity, DUANDAI_PAY_KEY, false)) {
            toDuandaiPay();
        }
    }

    //获取短代支付的信息
    private PayResultInfo resultInfo;

    private void getDuandaiToken() {
        Map<String, String> params = new TreeMap<>();
        params.put("imei", App.IMEI);
        params.put("version", PayConfig.VERSION_NAME);
        OkHttpUtils.post().url(API.URL_PRE + API.GET_DUANDAI_INFO).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String s, int i) {
                try {
                    resultInfo = JSON.parseObject(s, PayResultInfo.class);
                    initDuanDaiPay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //吊起短代支付
    private void toDuandaiPay() {
        String cporderid = UUID.randomUUID().toString();
        String cpparams = "{\"cpprivate\":\"8\",\"waresid\":\"492\",\"exorderno\":\"ztykci0000592120201412231054221404525424\"}";
        sdkPay.pay(10, cporderid, cpparams);
    }

    //传递支付结果给服务器
    private void sendSMSResult(CheckOrder msg) {
        String sign = MD5Encoder.EncoderByMd5("492|" + resultInfo.getOrder_id() + "|" + paySuccessMoney + "|fcf35ab21bbc6304f0aa120945843ee1").toLowerCase();
        Map<String, String> params = new HashMap<String, String>();
        params.put("money", paySuccessMoney + "");
        params.put("order_id", resultInfo.getOrder_id());
        params.put("out_trade_no", msg.orderid);
        params.put("status", status ? "1" : "0");
        params.put("sign", sign);
        OkHttpUtils.post().url(API.URL_PRE + API.NOTIFY_SMS).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {

            }

            @Override
            public void onResponse(String s, int i) {

            }
        });
    }

}
