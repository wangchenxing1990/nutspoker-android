package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.base.BaseActivity;

/**
 * Created by 周智慧 on 17/5/14.
 */

public class ClubAddMgrGuide extends BaseActivity {
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, ClubAddMgrGuide.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_mgr_guide);
        setHeadTitle("收费说明");
    }
}
