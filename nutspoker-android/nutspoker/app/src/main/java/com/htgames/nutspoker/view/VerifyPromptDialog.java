package com.htgames.nutspoker.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

/**
 * Created by 20150726 on 2015/11/9.
 */
public class VerifyPromptDialog extends Dialog {
    View view;
    private HeadImageView iv_userhead;
    private TextView tv_nickname;
    private TextView tv_prompt;
    Button btn_close;

    public VerifyPromptDialog(Context context) {
        super(context , R.style.dialog_custom_prompt);
        init(context );
    }

    public void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_prompt_view , null);
        initView();
        setContentView(view);
    }

    private void initView() {
        iv_userhead = (HeadImageView)view.findViewById(R.id.iv_userhead);
        tv_nickname = (TextView)view.findViewById(R.id.tv_nickname);
        tv_prompt = (TextView)view.findViewById(R.id.tv_prompt);
        btn_close = (Button)view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setInfo(String account){
        iv_userhead.loadBuddyAvatar(account);
        tv_nickname.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
    }
}
