package com.htgames.nutspoker.ui.recycler;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.dealer.DealerHelper;
import com.htgames.nutspoker.ui.activity.System.SettingsActivity;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.activity.System.WebViewActivity;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.session.constant.Extras;

/**
 * Created by 周智慧 on 16/11/23.
 */

public class MeVH<T> extends RecyclerView.ViewHolder implements View.OnClickListener {
    private MeRecycAdapter.MeItemData itemData;
    private ImageView icon;
    private TextView name;
    private ImageView reddot;
    private ImageView rightArrow;
    private Activity mContext;
    public MeVH(Activity contxt, View itemView) {
        super(itemView);
        mContext = contxt;
        icon = (ImageView) itemView.findViewById(R.id.me_recycler_item_icon);
        name = (TextView) itemView.findViewById(R.id.me_recycler_item_text);
        reddot = (ImageView) itemView.findViewById(R.id.me_recycler_item_reddot);
        rightArrow = (ImageView) itemView.findViewById(R.id.me_recycler_item_arrow);
    }

    public void bind(int position, MeRecycAdapter.MeItemData data) {
        if (data == null || data.type == MeRecycAdapter.ITEM_TYPE_EMPTY_DIVIDER) {
            return;
        }
        icon.setImageResource(data.drawableId);
        name.setText(mContext.getResources().getString(data.nameId));
        itemData = data;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (itemData == null) {
            return;
        }
        switch (itemData.nameId) {
            case R.string.me_column_shop:
                Intent intent = new Intent(mContext , ShopActivity.class);
                intent.putExtra(Extras.EXTRA_SHOP_TYPE, ShopActivity.TYPE_SHOP_COIN);
                mContext.startActivity(intent);
                break;
            case R.string.me_column_settings:
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                break;
            case R.string.settings_column_aboutus:
                WebViewActivity.start(mContext, WebViewActivity.TYPE_ABOUT_US, ApiConstants.URL_ABOUT_US);
                break;
            case R.string.me_column_share:
                if (mShareInterface != null) {
                    mShareInterface.share();
                }
                break;
            case R.string.dealer:
                DealerHelper.startDealerChatting(mContext, DealerConstant.dealer123456Uid);
                break;
            case R.string.protocol:
                WebViewActivity.start(mContext, WebViewActivity.TYPE_POKERCLANS_PROTOCOL, ApiConstants.URL_PROTOCOL_REGISTER);
                break;
//            case R.id.idll_me_friend:
//                startActivity(new Intent(getActivity(), FriendsListActivity.class));
//                break;
//            case R.id.ll_me_group:
//                startActivity(new Intent(getActivity(), GroupListActivity.class));
//                break;
//            case R.id.ll_me_dealer:
//                DealerHelper.startDealerChatting(getActivity());
//                break;
        }
    }

    private IShareInMe mShareInterface;
    public void setShareInterface(IShareInMe shareInterface) {
        mShareInterface = shareInterface;
    }
    public static interface IShareInMe {
        void share();
    }
}
