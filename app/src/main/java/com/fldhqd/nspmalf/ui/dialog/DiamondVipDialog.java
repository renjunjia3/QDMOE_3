package com.fldhqd.nspmalf.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fldhqd.nspmalf.R;
import com.fldhqd.nspmalf.pay.PayUtil;
import com.fldhqd.nspmalf.util.ScreenUtils;


/**
 * Case By:升级砖石会员
 * package:
 * Author：scene on 2017/4/18 13:53
 */
public class DiamondVipDialog extends Dialog {
    public DiamondVipDialog(@NonNull Context context) {
        super(context);
    }

    public DiamondVipDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected DiamondVipDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context context;
        private int videoId;
        private boolean isVideoDetailPage;

        private int type = 1;

        public Builder(Context context, int videoId, boolean isVideoDetailPage) {
            this.context = context;
            this.videoId = videoId;
            this.isVideoDetailPage = isVideoDetailPage;
        }

        public DiamondVipDialog create() {
            LayoutInflater inflater = LayoutInflater.from(context);
            final DiamondVipDialog dialog = new DiamondVipDialog(context, R.style.Dialog);

            View layout = inflater.inflate(R.layout.dialog_diamond_vip, null);

            ((TextView) layout.findViewById(R.id.diamond_old_price)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ImageView image = (ImageView) layout.findViewById(R.id.image);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.height = (int) ((ScreenUtils.instance(context).getScreenWidth() - ScreenUtils.instance(context).dip2px(50)) * 3f / 5f);
            image.setLayoutParams(layoutParams);
            layout.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            RadioGroup radioGroup = (RadioGroup) layout.findViewById(R.id.radio_group);
            if (radioGroup.getCheckedRadioButtonId() == R.id.type_wechat) {
                type = 1;
            } else {
                type = 2;
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                    if (checkedId == R.id.type_wechat) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                }
            });
            layout.findViewById(R.id.open_vip).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 1) {
                        PayUtil.getInstance().payByWeChat(context, dialog, PayUtil.VIP_TYPE_4, videoId, isVideoDetailPage);
                    } else {
                        PayUtil.getInstance().payByAliPay(context, dialog, PayUtil.VIP_TYPE_4, videoId, isVideoDetailPage);
                    }

                }
            });
            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }

}
