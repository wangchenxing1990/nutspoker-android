package com.htgames.nutspoker.ui.activity.Club;

import android.os.Bundle;
import android.widget.EditText;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.view.AreaView;
import com.htgames.nutspoker.ui.base.BaseActivity;

/**
 * 区域选择
 */
public class ClubAreaActivity extends BaseActivity {
    public final static String KEY_AREA = "KEY_AREA";
    public final static int RESULT_AREA_CLICK = 1;
    EditText edit_search_club;
    AreaView mAreaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_join);
        setHeadTitle(R.string.club_area_head);
        initView();
    }

    private void initView() {
//        mAreaView = (AreaView)findViewById(R.id.mAreaView);
//        edit_search_club = (EditText)findViewById(R.id.edit_search_club);
//        edit_search_club.setVisibility(View.GONE);
//        mAreaView.setAreaClickListener(new AreaClickListener() {
//            @Override
//            public void onAreaClick(String area) {
//                Intent intent = new Intent(ClubAreaActivity.this , ClubCreateActivity.class);
//                intent.putExtra(KEY_AREA , area);
//                setResult(RESULT_AREA_CLICK , intent);
//                finish();
//            }
//        });
    }
}
