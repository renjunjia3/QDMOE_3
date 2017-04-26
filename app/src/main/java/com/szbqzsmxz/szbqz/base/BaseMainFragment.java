package com.szbqzsmxz.szbqz.base;

import android.content.DialogInterface;

import com.szbqzsmxz.szbqz.R;
import com.szbqzsmxz.szbqz.app.App;
import com.szbqzsmxz.szbqz.pay.PayUtil;
import com.szbqzsmxz.szbqz.ui.dialog.BackOpenVipDialog;
import com.szbqzsmxz.szbqz.ui.fragment.MainFragment;
import com.szbqzsmxz.szbqz.util.ToastUtils;


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
        if (App.isVip == 0) {
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {

                if (builder == null) {
                    builder = new BackOpenVipDialog.Builder(_mActivity);
                    builder.setWeChatPayClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PayUtil.getInstance().payByWeChat(_mActivity, null, PayUtil.VIP_TYPE_2, 0, false);
                        }
                    });

                    builder.setAliPayClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PayUtil.getInstance().payByAliPay(_mActivity, null, PayUtil.VIP_TYPE_2, 0, false);
                        }
                    });
                }
                if (dialog == null) {
                    dialog = builder.create();
                }
                dialog.show();
                MainFragment.clickWantPay();

            } else {
                TOUCH_TIME = System.currentTimeMillis();
                ToastUtils.getInstance(_mActivity).showToast(getString(R.string.press_again_exit));
            }
        }
        return true;
    }
}