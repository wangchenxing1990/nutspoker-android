package com.netease.nim.uikit.bean;

import com.netease.nim.uikit.chesscircle.DealerConstant;

import java.io.Serializable;

/**
 * Created by 周智慧 on 17/1/3.
 */

public class SearchUserBean implements Serializable {
    public int channelType;//是私人渠道0还是俱乐部渠道1

    //私人渠道
    public String id;//用户id------uid
    public String uuid;//用户站鱼id----uuid

    //俱乐部渠道
    public String tid;
    public String vid;

    @Override
    public int hashCode() {
        if (DealerConstant.isNumeric(id + tid)) {
            return Integer.parseInt(id + tid);
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        SearchUserBean e = (SearchUserBean) o;
        return (this.hashCode() == (e.hashCode()));
    }
}
