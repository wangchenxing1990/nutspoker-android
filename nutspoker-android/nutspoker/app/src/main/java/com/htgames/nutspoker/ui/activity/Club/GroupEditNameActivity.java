package com.htgames.nutspoker.ui.activity.Club;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.tool.NameTrimTools;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter;
import com.htgames.nutspoker.ui.inputfilter.NameRuleFilter;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import java.io.UnsupportedEncodingException;

/**
 * 圈子修改名称
 */
public class GroupEditNameActivity extends BaseActivity{
    private ClearableEditTextWithIcon edt_group_name;
    private String teamId;
    private String teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        teamName = getIntent().getStringExtra(Extras.EXTRA_TEAM_NAME);
        initHead();
        initView();
    }

    private void initView() {
        edt_group_name = (ClearableEditTextWithIcon)findViewById(R.id.edt_group_name);
        TextView mHeadRightTV = (TextView) findViewById(R.id.tv_head_right);
        mHeadRightTV.setText(R.string.finish);
        mHeadRightTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = edt_group_name.getText().toString();
                String groupNameGBK = null;
                try {
                    //转换汉字编码
                    groupNameGBK = new String(groupName.getBytes("GBK"), "ISO8859_1");
                }
                catch (UnsupportedEncodingException ex) {
                }
                if (!TextUtils.isEmpty(groupNameGBK) && groupNameGBK.length() > 20) {
                    Toast.makeText(getApplicationContext(), R.string.group_name_is_long, Toast.LENGTH_SHORT).show();
                    return;
                }
                groupName = NameTrimTools.getNameTrim(groupName);
                if(TextUtils.isEmpty(groupName)){
                    groupName = " ";
                }
                updateTeamInfo(teamId , groupName , null , VerifyTypeEnum.Free ,TeamFieldEnum.Name);
            }
        });
//        edt_group_name.setFilters(new InputFilter[]{new NameLengthFilter(ClubConstant.MAX_GROUP_NAME_LENGTH) , new NoEmojiInputFilter()});
        edt_group_name.setFilters(new InputFilter[]{new NameLengthFilter(ClubConstant.MAX_GROUP_NAME_LENGTH) , new NameRuleFilter()});
        edt_group_name.setText(teamName);
    }

    private void initHead() {
        setHeadTitle(R.string.group_edit_name_head);
    }

    /**
     * 修改群组资料
     * @param teamName      群组名称
     * @param teamIntroduce 群组介绍
     * @param verifyType    群组验证方式
     * @param teamFieldEnum
     */
    public void updateTeamInfo(String teamId,final String teamName, String teamIntroduce, VerifyTypeEnum verifyType, TeamFieldEnum teamFieldEnum) {
        if (!NetworkUtil.isNetAvailable(this)) {
            com.htgames.nutspoker.widget.Toast.makeText(this, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(this, getString(R.string.club_edit_ing), false).setCanceledOnTouchOutside(false);
        // 每次仅修改群的一个属性
        // 可修改的属性包括：群名，介绍，公告，验证类型等。
        NIMClient.getService(TeamService.class).
                updateTeam(teamId, teamFieldEnum, teamName)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(getApplicationContext() , R.string.edit_group_name_success , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GroupEditNameActivity.this , GroupInfoActivity.class);
                        intent.putExtra(Extras.EXTRA_TEAM_NAME , NameTrimTools.getNameTrim(teamName));
                        setResult(GroupInfoActivity.RESULT_CODE_NAME_EDITED, intent);
                        finish();
                    }

                    @Override
                    public void onFailed(int i) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(getApplicationContext() , R.string.edit_group_name_failed, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(getApplicationContext() , R.string.edit_group_name_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
