package com.htgames.nutspoker.ui.adapter.channel;

/**
 * Created by 周智慧 on 17/3/7.
 */

public class ChannelSection {
    public int channelType;
    public boolean isExpanded = true;
    public int size;
    String description;
    public ChannelSection(int channelType) {
        this.channelType = channelType;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChannelSection) {
            ChannelSection other = (ChannelSection) o;
            return (channelType == (other.channelType));
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return channelType;
    }
}
