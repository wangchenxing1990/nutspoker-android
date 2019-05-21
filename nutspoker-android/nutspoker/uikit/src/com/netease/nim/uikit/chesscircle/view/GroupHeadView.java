package com.netease.nim.uikit.chesscircle.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 20150726 on 2016/2/18.
 */
public class GroupHeadView extends LinearLayout {
    private final static String TAG = "GroupHeadView";
    Context context;
    View view;
    CircularImageView circularImageView;
    ImageView mDefaultHead;

    public GroupHeadView(Context context) {
        super(context);
        init(context);
    }

    public GroupHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.layout_grouphead_view , null);
        initView();
        addView(view);
    }

    private void initView() {
        circularImageView = (CircularImageView)view.findViewById(R.id.circularImageView);
        mDefaultHead = (ImageView)view.findViewById(R.id.mDefaultHead);
    }

    public void setGroupId(String tid) {
        List<TeamMember> members = TeamDataCache.getInstance().getTeamMemberList(tid);
        ArrayList<Bitmap> mBmpsList = new ArrayList<Bitmap>();
        if (members != null && members.size() > 1) {
            for(int i = 0 ; i < members.size() ; i ++) {
                if(i == 5){
                    break;
                }
                TeamMember teamMember = members.get(i);
                Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.default_male_head);
                if(NimUserInfoCache.getInstance().hasUser(teamMember.getAccount())) {
                    NimUserInfo nimUserInfo = NimUserInfoCache.getInstance().getUserInfo(teamMember.getAccount());
                    if(!TextUtils.isEmpty(nimUserInfo.getAvatar())){
//                        Bitmap userAvatar = ImageLoaderKit.getMemoryCachedAvatarBitmap(nimUserInfo);
                        Bitmap userAvatar = ImageLoaderKit.getBitmapFromMemoryCache(nimUserInfo, 60 , 60);
//                        Bitmap userAvatar = ImageLoader.getInstance().loadImageSync(nimUserInfo.getAvatar() , ImageLoaderKit.headImageOption);
                        if(userAvatar != null){
                            avatar = userAvatar;
                        } else{
                            LogUtil.d(TAG , teamMember.getAccount() + ":头像不存在，使用默认头像");
                        }
                    }
                }
                mBmpsList.add(avatar);
            }
            mDefaultHead.setVisibility(GONE);
            circularImageView.setVisibility(VISIBLE);
            circularImageView.setImageBitmaps(mBmpsList);
        }else{
            mDefaultHead.setVisibility(VISIBLE);
            circularImageView.setVisibility(GONE);
        }
    }
}
