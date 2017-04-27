package com.ofgvyiss.ofgvyi.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.ofgvyiss.ofgvyi.R;
import com.ofgvyiss.ofgvyi.util.ScreenUtils;
import com.ofgvyiss.ofgvyi.util.ViewUtils;


public class UpdateDialog extends Dialog {

    public UpdateDialog(Context context) {
        super(context);
    }

    public UpdateDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public UpdateDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final UpdateDialog dialog = new UpdateDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_update, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            ViewUtils.setViewHeightByViewGroup(layout, (int) (ScreenUtils.instance(context).getScreenWidth() * 0.9f));

            layout.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }
}  