package com.htgames.nutspoker.data.location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;
import java.util.List;
import java.util.Locale;

/**
 */
public class HtLocationManager {
    private final static String TAG = "HtLocationManager";
    private Context mContext;
    Criteria criteria; // onResume 重新激活 if mProvider == null
    private Geocoder mGeocoder;
    LocationManager mLocationManager;
    HtLocationListener mHtLocationListener;
    String locationProvider;

    public HtLocationManager(Context context, HtLocationListener listener) {
        mContext = context;
        mGeocoder = new Geocoder(mContext, Locale.getDefault());
        mHtLocationListener = listener;
        //获取地理位置管理器
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        initLocation();
    }

    public static boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria cri = new Criteria();
        cri.setAccuracy(Criteria.ACCURACY_COARSE);
        cri.setAltitudeRequired(false);
        cri.setBearingRequired(false);
        cri.setCostAllowed(false);
        String bestProvider = locationManager.getBestProvider(cri, true);
        return !TextUtils.isEmpty(bestProvider);
    }

    private void initLocation() {
//        //获取所有可用的位置提供器
        List<String> providers = mLocationManager.getProviders(true);
        boolean network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (network) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            LogUtil.i(TAG, "Network" + locationProvider);
        } else if (gps) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            LogUtil.i(TAG, "GPS" + locationProvider);
        }
//        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
//            //如果是Network
//            locationProvider = LocationManager.NETWORK_PROVIDER;
//            Log.d(TAG, "Network" + locationProvider);
//        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
//            //如果是GPS
//            locationProvider = LocationManager.GPS_PROVIDER;
//            Log.d(TAG, "GPS" + locationProvider);
//        }
//        Criteria criteria = new Criteria();
////        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//粗糙的查询
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);//粗糙的查询
//        locationProvider = mLocationManager.getBestProvider(criteria, true);

//        if (locationProvider != null) {
//            //获取Location
//            mlocation = mLocationManager.getLastKnownLocation(locationProvider);
//            mLocationManager.requestLocationUpdates(locationProvider, 5000, 1, mLocationListener);
//        }
    }

    public void requestSingleUpdate() {
        if (locationProvider != null) {
            LogUtil.i(TAG, "locationProvider" + locationProvider);
            mLocationManager.requestSingleUpdate(locationProvider, mLocationListener, null);
        } else {
            initLocation();
            if (locationProvider != null) {
                mLocationManager.requestSingleUpdate(locationProvider, mLocationListener, null);
            }
        }
    }

    private void updateToNewLocation(Location location) {
        if (location != null) {
            LogUtil.i(TAG, "updateToNewLocation:" + location);
            if (mHtLocationListener != null) {
                mHtLocationListener.onLocationChanged(location);
            }
//            try {
//                List<Address> addresses = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                for (Address address : addresses) {
//                    Log.d(TAG, "address:" + address.getCountryCode());
//                    Log.d(TAG, "address:" + address.getCountryName());
//                    Log.d(TAG, "address:" + address.getLocality());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            removeUpdates();
        }
    }

    public final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            LogUtil.i(TAG, "onLocationChanged:" + location);
            updateToNewLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            LogUtil.i(TAG , "onProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            LogUtil.i(TAG , "onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            LogUtil.i(TAG , "onStatusChanged");
        }
    };

    protected void removeUpdates() {
        if (mLocationManager != null && mLocationListener != null) {
            //移除监听器
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isLocationOpen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
