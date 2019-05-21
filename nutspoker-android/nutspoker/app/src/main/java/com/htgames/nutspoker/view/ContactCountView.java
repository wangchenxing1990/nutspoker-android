package com.htgames.nutspoker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;

/**
 */
public class ContactCountView extends LinearLayout {
    TextView tv_contacts_num;
    
    public ContactCountView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View countView = LayoutInflater.from(context).inflate(R.layout.layout_contact_count, null);
        tv_contacts_num = (TextView) countView.findViewById(R.id.tv_contacts_num);
        addView(countView , new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setCount(String num){
        tv_contacts_num.setText(num);
    }
}
