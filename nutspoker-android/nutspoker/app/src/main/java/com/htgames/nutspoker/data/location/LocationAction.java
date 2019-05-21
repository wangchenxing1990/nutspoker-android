package com.htgames.nutspoker.data.location;

import android.app.Activity;
import android.location.Location;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.ui.base.BaseAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 定位
 */
public class LocationAction extends BaseAction {
    private final static String TAG = "LocationAction";
    String requestLocationUrl;
    LocationRegionListener mLocationRegionListener;
    Location currentLocation;
    HtLocationManager htLocationManager;

    public LocationAction(Activity activity, View baseView) {
        super(activity, baseView);
        currentLocation = DemoCache.getLocation();
        htLocationManager = new HtLocationManager(ChessApp.sAppContext, new HtLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    currentLocation = location;
                    DemoCache.setLocation(currentLocation);
                    getLocationRegion(location);
                }
            }

            @Override
            public void onGetFailure() {
                if (mLocationRegionListener != null) {
                    mLocationRegionListener.onLocationRegionFailure(false);
                }
            }
        });
    }

    public void setLocationRegionListener(LocationRegionListener listener) {
        this.mLocationRegionListener = listener;
    }

    public void getLocation() {
        if (htLocationManager.isLocationOpen(ChessApp.sAppContext)) {
            if (currentLocation == null) {
                htLocationManager.requestSingleUpdate();
            } else {
                getLocationRegion(currentLocation);
            }
        } else {
            if (mLocationRegionListener != null) {
                mLocationRegionListener.onLocationRegionFailure(true);
            }
        }
    }

    public void getLocationRegion(final Location location) {
        requestLocationUrl = getHost() + ApiConstants.URL_LOCATION;
        LogUtil.i(TAG, requestLocationUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestLocationUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        JSONObject dataObj = json.getJSONObject("data");
                        String country = dataObj.optString("country");
                        String province = dataObj.optString("province");
                        String city = dataObj.optString("city");
                        AddressEntity addressEntity = new AddressEntity();
                        addressEntity.setLongitude(location.getLongitude());
                        addressEntity.setLatitude(location.getLatitude());
                        addressEntity.setCountryName(country);
                        addressEntity.setProvinceName(province);
                        addressEntity.setCityName(city);
                        if (mLocationRegionListener != null) {
                            mLocationRegionListener.onLocationRegionSuccess(addressEntity);
                        }
                    } else {
                        if (mLocationRegionListener != null) {
                            mLocationRegionListener.onLocationRegionFailure(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mLocationRegionListener != null) {
                        mLocationRegionListener.onLocationRegionFailure(false);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mLocationRegionListener != null) {
                    mLocationRegionListener.onLocationRegionFailure(false);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("longitude", String.valueOf(location.getLongitude()));
                paramsMap.put("latitude", String.valueOf(location.getLatitude()));
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestLocationUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestLocationUrl);
        if (htLocationManager != null) {
            htLocationManager.removeUpdates();
            htLocationManager = null;
        }
    }
}
