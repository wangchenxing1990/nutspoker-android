package com.htgames.nutspoker.thirdPart.aliyun;

import android.content.Context;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSConstants;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.netease.nim.uikit.api.ApiConstants;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * Created by 20150726 on 2016/4/20.
 */
public class AliyunHelper {
    private final static String TAG = "AliyunHelper";
    private final static String END_POINT = "http://oss-cn-hangzhou.aliyuncs.com";
    public final static String BUCKET_NAME = "texasclub-hand";
    //测试服
//    private final static String ACCESS_KEY_ID = "BSkgRd6ypz8u6Miw";
//    private final static String ACCESS_KEY_SECRET = "WvGu2Ex46J7rvQK7uSjLzWbrzOnoKI";
//    public final static String BUCKET_NAME = "rafuture";
    static OSS oss;

    //初始化OSSClient
    public static OSS getOSSClient(Context context) {
        if (oss == null) {
            String endpoint = END_POINT;
            // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
//            String accessKeyId = ACCESS_KEY_ID;
//            String accessKeySecret = ACCESS_KEY_SECRET;
//            OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
            oss = new OSSClient(context, endpoint, credetialProvider);
        }
        return oss;
    }

    public static OSSCredentialProvider credetialProvider = new OSSFederationCredentialProvider() {
        @Override
        public OSSFederationToken getFederationToken() {
            try {
                URL stsUrl = new URL(getHost() + ApiConstants.URL_OSS);
                HttpURLConnection conn = (HttpURLConnection) stsUrl.openConnection();
                InputStream input = conn.getInputStream();
                String jsonText = IOUtils.readStreamAsString(input, OSSConstants.DEFAULT_CHARSET_NAME);
                JSONObject jsonObjs = new JSONObject(jsonText);
                JSONObject credentialsJson = jsonObjs.getJSONObject("Credentials");
                if(credentialsJson != null){
                    String ak = credentialsJson.getString("AccessKeyId");
                    String sk = credentialsJson.getString("AccessKeySecret");
                    String token = credentialsJson.getString("SecurityToken");
                    String expiration = credentialsJson.getString("Expiration");
                    return new OSSFederationToken(ak, sk, token, expiration);
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    };

//    public static void uploadFile(Context context , String fileName ,String filePath) {
//        String bucketName = BUCKET_NAME;
//        String objectKey = fileName;
//        String uploadFilePath = filePath;
//        // 构造上传请求
//        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, uploadFilePath);
//        // 异步上传时可以设置进度回调
//        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//            @Override
//            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d(TAG, "currentSize: " + currentSize + " totalSize: " + totalSize);
//            }
//        });
//        OSSAsyncTask task = getOSSClient(context).asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//            @Override
//            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                Log.d(TAG, "UploadSuccess");
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                // 请求异常
//                if (clientExcepion != null) {
//                    // 本地异常如网络异常等
//                    clientExcepion.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // 服务异常
//                    Log.e("ErrorCode", serviceException.getErrorCode());
//                    Log.e("RequestId", serviceException.getRequestId());
//                    Log.e("HostId", serviceException.getHostId());
//                    Log.e("RawMessage", serviceException.getRawMessage());
//                }
//            }
//        });
//        // task.cancel(); // 可以取消任务
//        // task.waitUntilFinished(); // 可以等待直到任务完成
//    }
//
//    public static void downloadFile(Context context , String fileName) {
//        String bucketName = BUCKET_NAME;
//        String objectKey = fileName;
//        // 构造下载文件请求
//        GetObjectRequest get = new GetObjectRequest(bucketName , objectKey);
//        OSSAsyncTask task = getOSSClient(context).asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
//            @Override
//            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
//                // 请求成功
//                Log.d("Content-Length", "" + result.getContentLength());
//                InputStream inputStream = result.getObjectContent();
//                byte[] buffer = new byte[2048];
//                int len;
//                try {
//                    while ((len = inputStream.read(buffer)) != -1) {
//                        // 处理下载的数据
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                // 请求异常
//                if (clientExcepion != null) {
//                    // 本地异常如网络异常等
//                    clientExcepion.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // 服务异常
//                    Log.e("ErrorCode", serviceException.getErrorCode());
//                    Log.e("RequestId", serviceException.getRequestId());
//                    Log.e("HostId", serviceException.getHostId());
//                    Log.e("RawMessage", serviceException.getRawMessage());
//                }
//            }
//        });
//        // task.cancel(); // 可以取消任务
//        // task.waitUntilFinished(); // 如果需要等待任务完成
//    }
}
