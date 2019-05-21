package com.htgames.nutspoker.ui.adapter.channel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;

/**
 * Created by 周智慧 on 17/3/7.
 */

public class ChannelSectionVH extends RecyclerView.ViewHolder {
    TextView tv_channel_section;
    public ChannelSectionVH(View itemView) {
        super(itemView);
        tv_channel_section = (TextView) itemView.findViewById(R.id.tv_channel_section);
    }

    public void bind(ChannelSection data) {
        if (data == null) {
            return;
        }
        if (data.channelType == ChannelType.creator) {
            tv_channel_section.setText("房主");
        } else if (data.channelType == ChannelType.club) {
            tv_channel_section.setText("俱乐部");
        } else if (data.channelType == ChannelType.personal) {
            tv_channel_section.setText("管理员");
        }
    }
}
