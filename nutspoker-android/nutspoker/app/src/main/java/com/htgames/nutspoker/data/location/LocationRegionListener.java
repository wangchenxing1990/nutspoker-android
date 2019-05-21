package com.htgames.nutspoker.data.location;

/**
 */
public interface LocationRegionListener {
    public void onLocationRegionSuccess(AddressEntity address);
    /** 定位没有打开 */
    public void onLocationRegionFailure(boolean isLocationNotOpen);

}
