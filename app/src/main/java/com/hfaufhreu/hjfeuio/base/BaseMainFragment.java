package com.hfaufhreu.hjfeuio.base;

import android.content.DialogInterface;

import com.hfaufhreu.hjfeuio.R;
import com.hfaufhreu.hjfeuio.app.App;
import com.hfaufhreu.hjfeuio.pay.PayUtil;
import com.hfaufhreu.hjfeuio.ui.dialog.BackOpenVipDialog;
import com.hfaufhreu.hjfeuio.ui.fragment.MainFragment;
import com.hfaufhreu.hjfeuio.util.ToastUtils;


/**
 * 懒加载
 * Created by scene on 17/3/13.
 */
public abstract class BaseMainFragment extends BaseFragment {
    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;
    private BackOpenVipDialog dialog;
    private BackOpenVipDialog.Builder builder;

    /**
     * 处理回退事件
     */
    @Override
    public boolean onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            if (App.isVip == 0) {
                if (builder == null) {
                    builder = new BackOpenVipDialog.Builder(_mActivity);
                    builder.setWeChatPayClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PayUtil.getInstance().payByWeChat(_mActivity, PayUtil.VIP_TYPE_2, 0);
                        }
                    });

                    builder.setAliPayClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PayUtil.getInstance().payByAliPay(_mActivity, PayUtil.VIP_TYPE_2, 0);
                        }
                    });
                }
                if (dialog == null) {
                    dialog = builder.create();
                }
                dialog.show();
                MainFragment.clickWantPay();
            } else {
                _mActivity.finish();
            }
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            ToastUtils.getInstance(_mActivity).showToast(getString(R.string.press_again_exit));
        }
        return true;
    }
}