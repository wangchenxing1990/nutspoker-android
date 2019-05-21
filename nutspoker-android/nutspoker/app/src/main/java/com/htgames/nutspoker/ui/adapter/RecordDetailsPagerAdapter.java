package com.htgames.nutspoker.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.htgames.nutspoker.view.RecordDetailsInfoView;
import com.htgames.nutspoker.view.RecordDetailsUserView;

import java.util.ArrayList;

/**
 */
public class RecordDetailsPagerAdapter extends PagerAdapter {
    GameBillEntity gameBillEntity;
    Context context;
    RecordDetailsUserView recordDetailsUserView;
    ArrayList<GameMemberEntity> gameMemberList = new ArrayList<>();

    public RecordDetailsPagerAdapter(Context context , GameBillEntity gameBillEntity, ArrayList<GameMemberEntity> gameMemberList) {
        this.context = context;
        this.gameBillEntity = gameBillEntity;
        this.gameMemberList = gameMemberList;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
//        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (position == 0) {
            RecordDetailsInfoView recordDetailsInfoView = new RecordDetailsInfoView(context);
            recordDetailsInfoView.setInfo(gameBillEntity);
            container.addView(recordDetailsInfoView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return recordDetailsInfoView;
        } else {
            recordDetailsUserView = new RecordDetailsUserView(context);
            if (gameMemberList != null && gameMemberList.size() > 0) {
                recordDetailsUserView.setUserHandInfo(gameMemberList.get(0), gameBillEntity.gameInfo, gameBillEntity.endSblindsIndex);
            } else {
                //成员为空，设置为空数据
                recordDetailsUserView.setUserHandInfoNull();
            }
            container.addView(recordDetailsUserView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return recordDetailsUserView;
        }
    }

    public void setRecordDetailsUserInfo(GameMemberEntity gameMemberEntity , GameEntity gameEntity) {
        if (recordDetailsUserView != null) {
            recordDetailsUserView.setUserHandInfo(gameMemberEntity, gameEntity, gameBillEntity.endSblindsIndex);
        }
    }

    public void setFirstGameMember(GameMemberEntity gameMember) {
        if (recordDetailsUserView != null) {
            recordDetailsUserView.setUserHandInfo(gameMember, gameBillEntity.gameInfo, gameBillEntity.endSblindsIndex);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
