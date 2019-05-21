package com.htgames.nutspoker.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.interfaces.DownloadCallback;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import okhttp3.OkHttpClient;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BaseAction {
	public static String TAG = "BaseAction";

	protected Activity mActivity;
	protected View mBaseView;

	protected HashSet<String> mListRequestUrl = new HashSet<>();

	public BaseAction(Activity activity, View baseView){
		mActivity = activity;
		mBaseView = baseView;
		mListRequestUrl.clear();
	}

	protected void onCreate(){

	}
	
	protected void onResume(){
		
	}
	
	protected void onPause(){
		
	}
	
	protected void onStop(){
		
	}
	
	public void onDestroy() {
		for (String requestUrl : mListRequestUrl)
			cancelAll(requestUrl);
		mListRequestUrl.clear();
		mListRequestUrl = null;
		mActivity = null;
	}

	public HashMap<String,String> getRequestCommonMap(){
		return NetWork.getRequestCommonParams(ChessApp.sAppContext);
	}

	protected void addRequestGet(String url,HashMap<String, String> paramsMap,RequestCallback requestCallback){
		addRequestGet(true,url,paramsMap,requestCallback);
	}

	protected void addRequestPost(String url,HashMap<String, String> paramsMap,RequestCallback requestCallback){
		addRequest(Request.Method.POST,true,url,paramsMap,requestCallback);
	}

	protected void addRequestGet(boolean isShow,String url,HashMap<String, String> paramsMap,RequestCallback requestCallback){
		addRequest(Request.Method.GET,isShow,url,paramsMap,requestCallback);
	}

	protected void addRequestDown(String url, HashMap<String, String> paramsMap, File downFile, DownloadCallback downloadCallback){
		addRequestDown(true,url,paramsMap,downFile,downloadCallback);
	}

	protected void addRequestDown(boolean isShow, String url,final HashMap<String, String> paramsMap,final File downFile, final DownloadCallback downloadCallback){
		if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
			if(isShow) {
				Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
			}
			if (downloadCallback != null) {
				downloadCallback.onFailed();
			}
			return;
		}
		String requestUrl = url + NetWork.getRequestParams(paramsMap);
		Observable.just(requestUrl)
				.observeOn(Schedulers.io())
				.map(new Func1<String, String>() {
					@Override
					public String call(String s) {
						//同步 OKHttp Get
						OkHttpClient client = new OkHttpClient();
						okhttp3.Request request = new okhttp3.Request.Builder()
								.url(s)
								.build();

						okhttp3.Response response;
						try {
							response  = client.newCall(request).execute();
							byte[] bytes = response.body().bytes();
							if (!downFile.getParentFile().exists()) {
								downFile.getParentFile().mkdirs();
							}
							if(!downFile.exists() || downFile.isDirectory())
								downFile.createNewFile();
							FileOutputStream fos = new FileOutputStream(downFile);
							fos.write(bytes);
							fos.flush();
							fos.close();

							return downFile.getPath();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<String>() {
					@Override
					public void call(String s) {
						if(s == null)
							downloadCallback.onFailed();
						else
							downloadCallback.onDownload(s);
					}
				});
	}

	protected void addRequest(int httpMethod,boolean isShow,String url,final HashMap<String, String> paramsMap,final RequestCallback requestCallback){
		if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
			if(isShow)
				Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
			if (requestCallback != null) {
				requestCallback.onFailed();
			}
			return;
		}
		String requestUrl = url + (httpMethod == Request.Method.GET ? NetWork.getRequestParams(paramsMap) : "");
		LogUtil.i(TAG , requestUrl);
		SignStringRequest signRequest = new SignStringRequest(httpMethod, requestUrl, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				LogUtil.i(TAG , response);
				try {
					JSONObject json = new JSONObject(response);
					int code = json.getInt("code");

					if(requestCallback != null)
						requestCallback.onResult(code, response, null);
				} catch (JSONException e) {
					e.printStackTrace();

					if(requestCallback != null){
						requestCallback.onFailed();
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (!TextUtils.isEmpty(error.getMessage())) {
					LogUtil.e(TAG, error.getMessage());
				}
				if(requestCallback != null){
					requestCallback.onFailed();
				}
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return paramsMap;
			}
		};
		addRequest(signRequest,requestUrl);
	}

	protected  void addRequest(SignStringRequest signRequest,String requestUrl){
		signRequest.setTag(requestUrl);
		mListRequestUrl.add(requestUrl);
		ChessApp.sRequestQueue.add(signRequest);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data){

	}

	public void onConfigurationChanged(Configuration newConfig) {

	}

	protected View findViewById(int id) {
		return mActivity.findViewById(id);
	}
	
	protected Drawable getDrawable(int id) {
		return mActivity.getResources().getDrawable(id);
	}
	
	protected String getString(int resId) {
		return ChessApp.sAppContext.getString(resId);
	}
	
	protected String getString(int resId, Object... formatArgs) {
		return ChessApp.sAppContext.getString(resId, formatArgs);
	}

	/**
	 * 取消网络请求
	 * @param requestUrl
	 */
	public void cancelAll(String requestUrl){
		if(!TextUtils.isEmpty(requestUrl)){
			ChessApp.sRequestQueue.cancelAll(requestUrl);
		}
	}

	public boolean checkNetWork(){
		return checkNetWork(true);
	}

	public boolean checkNetWork(boolean isShow){
		if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
			if(isShow)
				Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
}
