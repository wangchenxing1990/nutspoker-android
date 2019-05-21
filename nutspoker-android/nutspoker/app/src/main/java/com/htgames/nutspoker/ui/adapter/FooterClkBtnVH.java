package com.htgames.nutspoker.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.model.CheckInStatus;
import com.htgames.nutspoker.game.model.GameStatus;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.interfaces.IClick;

/**
 * Created by 周智慧 on 17/2/23.
 */

public class FooterClkBtnVH extends RecyclerView.ViewHolder {
    public boolean hasClick = false;
    public IClick.IOnlyClick clickListener;
    TextView btn_more;
    private static int width;
    public FooterClkBtnVH(View itemView, boolean hasClick) {
        super(itemView);
        this.hasClick = hasClick;
        LogUtil.i("hasClick: create viewholder: " + hasClick);
        btn_more = (TextView) itemView.findViewById(R.id.btn_more);
        itemView.setVisibility(View.GONE);
    }

    public static FooterClkBtnVH createViewHolder(Context context, ViewGroup parent, boolean hasClick) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_btn_more, null);
        width = parent.getMeasuredWidth();
        int itemHeight = ScreenUtil.dp2px(context, 35);
        view.setLayoutParams(new RecyclerView.LayoutParams(width, itemHeight));
        return new FooterClkBtnVH(view, hasClick);
    }

    public void bind(final int position, int playerCount, int checkInCount, int gameStatus, int myCheckInStatus) {
//        boolean showAllBeforeClick = false;//点击"更多"之前是否已经显示完
//        if (gameStatus == GameStatus.GAME_STATUS_START && myCheckInStatus == CheckInStatus.CHECKIN_STATUES_ED) {
//            showAllBeforeClick = (playerCount - 1 >= checkInCount);//游戏开始后且已经报名会多返回一个"我"在第一行
//        } else {
//            showAllBeforeClick = (playerCount >= checkInCount);
//        }
//        LogUtil.i("bind viewholder  hasClick: " + hasClick + "   showAllBeforeClick： " + showAllBeforeClick + "  checkInCount:" + checkInCount + "  playerCount：" + playerCount);
        int playerCountAfterFilter = playerCount;
        if (gameStatus == GameStatus.GAME_STATUS_START && myCheckInStatus == CheckInStatus.CHECKIN_STATUES_ED) {
            playerCountAfterFilter = playerCount - 1;//减去自己，这种情况自己重复
        } else {
            playerCountAfterFilter = playerCount;
        }
        if (playerCountAfterFilter < 200) {
            itemView.setLayoutParams(new RecyclerView.LayoutParams(width, 0));
            return;
        }
        int itemHeight = ScreenUtil.dp2px(itemView.getContext(), 35);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(width, itemHeight));
        itemView.setVisibility(View.VISIBLE);
        btn_more.setText("已显示前" + /*playerCountAfterFilter*/200 + "名玩家");
    }
}
