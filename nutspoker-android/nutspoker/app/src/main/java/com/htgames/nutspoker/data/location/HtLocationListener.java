package com.htgames.nutspoker.data.location;

import android.location.Location;

/**
 * Created by 20150726 on 2016/5/12.
 */
public interface HtLocationListener {
    public void onLocationChanged(Location location);

    public void onGetFailure();
}
