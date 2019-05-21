package com.htgames.nutspoker.debug.network_monitor;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.htgames.nutspoker.debug.R;
import com.htgames.nutspoker.debug.network_monitor.data.ChuckContentProvider;
import com.htgames.nutspoker.debug.network_monitor.data.LocalCupboard;
import com.htgames.nutspoker.debug.network_monitor.support.CRequest;
import com.htgames.nutspoker.debug.network_monitor.support.NotificationHelper;
import com.htgames.nutspoker.debug.network_monitor.support.RetentionManager;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;

/**
 * Created by 周智慧 on 17/5/2.
 *  --------------------------------------这个类不要删除，在SignStringRequest.java中通过反射调用--------------------------------------
 */

public final class VolleyInterceptor {
    private static final String LOG_TAG = "VolleyInterceptor";
    private static final ChuckInterceptor.Period DEFAULT_RETENTION = ChuckInterceptor.Period.ONE_DAY;
    private static final Charset UTF8 = Charset.forName("UTF-8");
    HttpTransaction transaction;
    private final Context context;
    private final NotificationHelper notificationHelper;
    private RetentionManager retentionManager;
    private boolean showNotification;
    private long maxContentLength = 250000L;
    private long startNs;
    private Uri transactionUri;

    /**
     * @param context The current Context.
     */
    public VolleyInterceptor(Context context) {
        this.context = context.getApplicationContext();
        notificationHelper = new NotificationHelper(this.context);
        showNotification = true;
        retentionManager = new RetentionManager(this.context, DEFAULT_RETENTION);
    }

    /**
     * Control whether a notification is shown while HTTP activity is recorded.
     *
     * @param show true to show a notification, false to suppress it.
     * @return The {@link VolleyInterceptor} instance.
     */
    public VolleyInterceptor showNotification(boolean show) {
        showNotification = show;
        return this;
    }

    /**
     * Set the maximum length for request and response content before it is truncated.
     * Warning: setting this value too high may cause unexpected results.
     *
     * @param max the maximum length (in bytes) for request/response content.
     * @return The {@link VolleyInterceptor} instance.
     */
    public VolleyInterceptor maxContentLength(long max) {
        this.maxContentLength = max;
        return this;
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        return getCommAuth(getParams());
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return null;//return super.getParams();
    }

    public static Map<String, String> getCommAuth(Map<String, String> params) {
        String time = String.valueOf(System.currentTimeMillis() / 1000L);
        String randomNum = NetWork.getRandom();
        String paramsMD5 = "";
        if (params != null) {
            paramsMD5 = NetWork.getParamsMD5(params);
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(SignStringRequest.HEADER_KEY_SIGN, NetWork.getSign(randomNum, time));
        paramsMap.put(SignStringRequest.HEADER_KEY_RAND, randomNum);
        paramsMap.put(SignStringRequest.HEADER_KEY_TIME, time);
        paramsMap.put(SignStringRequest.HEADER_KEY_BODY, paramsMD5);
        paramsMap.put(SignStringRequest.HEADER_KEY_APPVER, BaseTools.getAppVersionName(DemoCache.getContext()));
        if (ApiConfig.AppVersion.isTaiwanVersion) {
            paramsMap.put(SignStringRequest.HEADER_KEY_AREA, ApiConfig.AppVersion.AREA_TW);
        }
        LogUtil.i(LOG_TAG, paramsMap.toString());
        return paramsMap;
    }

    public static String translateMethod(int method) {
        if (method == -1) {
            return "DEPRECATED_GET_OR_POST";
        } else if (method == Request.Method.GET) {
            return "GET";
        } else if (method == Request.Method.POST) {
            return "POST";
        } else if (method == Request.Method.PUT) {
            return "PUT";
        } else if (method == Request.Method.DELETE) {
            return "DELETE";
        } else if (method == Request.Method.HEAD) {
            return "HEAD";
        } else if (method == Request.Method.OPTIONS) {
            return "OPTIONS";
        } else if (method == Request.Method.TRACE) {
            return "TRACE";
        } else if (method == Request.Method.PATCH) {
            return "PATCH";
        }
        return "GET";
    }

    public static RequestBody getRequestBody(String url) {
//        return RequestBody.create(null, url);
//        String[] bodies = url.split("&");
        FormBody.Builder builder = new FormBody.Builder();
        Map<String, String> mapRequest = CRequest.URLRequest(url);
        if (mapRequest == null || mapRequest.size() <= 0) {
            return null;
        }
        for (String strRequestKey : mapRequest.keySet()) {
            String strRequestValue = mapRequest.get(strRequestKey);
            builder.add(strRequestKey, strRequestValue);
        }
        return builder.build();
    }

    /**
     * Set the retention period for HTTP transaction data captured by this interceptor.
     * The default is one week.
     *
     * @param period the peroid for which to retain HTTP transaction data.
     * @return The {@link VolleyInterceptor} instance.
     */
    public VolleyInterceptor retainDataFor(ChuckInterceptor.Period period) {
        retentionManager = new RetentionManager(context, period);
        return this;
    }

    public void onRequest(int method, final String url) throws IOException {
        transaction = new HttpTransaction();
        transaction.requestDate = (new Date());
        transaction.method = translateMethod(method);
        transaction.setUrl(url);
        transaction.setRequestHeaders(Headers.of(getCommAuth(null)));
        RequestBody requestBody = getRequestBody(url);
        boolean hasRequestBody = requestBody != null;
        if (hasRequestBody) {
            if (requestBody.contentType() != null) {
                transaction.requestContentType = (requestBody.contentType().toString());
            }
            if (requestBody.contentLength() != -1) {
                transaction.requestContentLength = (requestBody.contentLength());
            }
        }
        transaction.requestBodyIsPlainText = (!bodyHasUnsupportedEncoding(getCommAuth(null)));
        if (hasRequestBody && transaction.requestBodyIsPlainText) {
            BufferedSource source = getNativeSource(new Buffer(), bodyGzipped(Headers.of(getCommAuth(null))));
            Buffer buffer = source.buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (isPlaintext(buffer)) {
                transaction.setRequestBody(readFromBuffer(buffer, charset));
            } else {
                transaction.responseBodyIsPlainText = (false);
            }
        }
        transactionUri = create(transaction);
        startNs = System.nanoTime();
    }

    public void parseNetworkResponse(NetworkResponse response) throws IOException {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
//        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
//        transaction.setResponseHeaders(Headers.of(response.headers)); // includes headers added later in the chain
        transaction.responseDate = (new Date());
        transaction.tookMs = (response.networkTimeMs);
        transaction.protocol = getHeaderFromResponse(response.headers, "protocol");
        transaction.responseCode = (response.statusCode);
        transaction.responseMessage = (parsed);
        transaction.responseContentLength = (long) (response.data.length);
        if (getHeaderFromResponse(response.headers, "content-type") != null) {
            transaction.responseContentType = (getHeaderFromResponse(response.headers, "content-type"));
        }
        transaction.setResponseHeaders(Headers.of(response.headers));
        transaction.responseBodyIsPlainText = !bodyHasUnsupportedEncoding(response.headers);
        if (!StringUtil.isSpace(parsed) && transaction.responseBodyIsPlainText) {
            BufferedSource source = getNativeSource(response);
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            if (isPlaintext(buffer)) {
                transaction.setResponseBody(readFromBuffer(buffer.clone(), charset));
            } else {
                transaction.responseBodyIsPlainText = (false);
            }
            transaction.responseContentLength = (buffer.size());
        }
        update(transaction, transactionUri);
    }

    public void deliverError(VolleyError error) {
        NetworkResponse response = error == null ? null : error.networkResponse;
        transaction.responseDate = (new Date());
        transaction.tookMs = (error == null ? 0 : error.getNetworkTimeMs());
        transaction.protocol = getHeaderFromResponse(response == null ? null : response.headers, "protocol");
        transaction.responseCode = (response == null ? 404 : response.statusCode);
        transaction.responseMessage = error == null ? "error == null" : error.toString();
        transaction.responseContentLength = (long) (0);
        if (getHeaderFromResponse(response == null ? null : response.headers, "content-type") != null) {
            transaction.responseContentType = (getHeaderFromResponse(response == null ? null : response.headers, "content-type"));
        }
        if (response != null && response.headers != null) {
            transaction.setResponseHeaders(Headers.of(response == null ? null : response.headers));
        }
        transaction.responseBodyIsPlainText = !bodyHasUnsupportedEncoding(response == null ? null : response.headers);
//        if (!StringUtil.isSpace(parsed) && transaction.responseBodyIsPlainText) {
//            BufferedSource source = getNativeSource(response);
//            source.request(Long.MAX_VALUE);
//            Buffer buffer = source.buffer();
//            Charset charset = UTF8;
//            if (isPlaintext(buffer)) {
//                transaction.setResponseBody(readFromBuffer(buffer.clone(), charset));
//            } else {
//                transaction.responseBodyIsPlainText = (false);
//            }
//            transaction.responseContentLength = (buffer.size());
//        }
        update(transaction, transactionUri);
    }

    private Uri create(HttpTransaction transaction) {
        ContentValues values = LocalCupboard.getInstance().withEntity(HttpTransaction.class).toContentValues(transaction);
        Uri uri = context.getContentResolver().insert(ChuckContentProvider.TRANSACTION_URI, values);
        transaction._id = (Long.valueOf(uri.getLastPathSegment()));
        if (showNotification) {
            notificationHelper.show(transaction);
        }
        retentionManager.doMaintenance();
        return uri;
    }

    private int update(HttpTransaction transaction, Uri uri) {
        ContentValues values = LocalCupboard.getInstance().withEntity(HttpTransaction.class).toContentValues(transaction);
        int updated = context.getContentResolver().update(uri, values, null, null);
        if (showNotification && updated > 0) {
            notificationHelper.show(transaction);
        }
        return updated;
    }

    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyHasUnsupportedEncoding(Map<String, String> headers) {
        String contentEncoding = headers == null ? null : headers.get("Content-Encoding");
        return contentEncoding != null &&
                !contentEncoding.equalsIgnoreCase("identity") &&
                !contentEncoding.equalsIgnoreCase("gzip");
    }

    private boolean bodyGzipped(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return "gzip".equalsIgnoreCase(contentEncoding);
    }

    private String readFromBuffer(Buffer buffer, Charset charset) {
        long bufferSize = buffer.size();
        long maxBytes = Math.min(bufferSize, maxContentLength);
        String body = "";
        try {
            body = buffer.readString(maxBytes, charset);
        } catch (EOFException e) {
            body += context.getString(R.string.chuck_body_unexpected_eof);
        }
        if (bufferSize > maxContentLength) {
            body += context.getString(R.string.chuck_body_content_truncated);
        }
        return body;
    }

    private BufferedSource getNativeSource(BufferedSource input, boolean isGzipped) {
        if (isGzipped) {
            GzipSource source = new GzipSource(input);
            return Okio.buffer(source);
        } else {
            return input;
        }
    }

    private BufferedSource getNativeSource(okhttp3.Response response) throws IOException {
        if (bodyGzipped(response.headers())) {
            BufferedSource source = response.peekBody(maxContentLength).source();
            if (source.buffer().size() < maxContentLength) {
                return getNativeSource(source, true);
            } else {
                LogUtil.w(LOG_TAG, "gzip encoded response was too long");
            }
        }
        return response.body().source();
    }

    private BufferedSource getNativeSource(NetworkResponse response) throws IOException {
        BufferedSource source = Okio.buffer(Okio.source(new ByteArrayInputStream(response.data)));//response.data.source();
        if (bodyGzipped(Headers.of(response.headers))) {
            if (source.buffer().size() < maxContentLength) {
                return getNativeSource(source, true);
            } else {
                LogUtil.w(LOG_TAG, "gzip encoded response was too long");
                return source;
            }
        }
        return source;
    }

    private String getHeaderFromResponse(Map<String, String> headers, String searchKey) {
        /*
        * I/System.out: Content-Length 0
          I/System.out: Date Wed, 29 Jun 2016 17:27:59 GMT
          I/System.out: link <https://LINK_1>;rel="link-1"
          I/System.out: Server Apache-Coyote/1.1
          I/System.out: X-Android-Received-Millis 1467221322063
          I/System.out: X-Android-Response-Source NETWORK 200
          I/System.out: X-Android-Selected-Protocol http/1.1
          I/System.out: X-Android-Sent-Millis 1467221322031*/
        if (headers == null || headers.size() <= 0) {
            return searchKey + "=null";
        }
        for (String key : headers.keySet()) {
            if (key.toLowerCase().contains(searchKey)) {
                return headers.get(key);
            }
        }
        return searchKey + "=null";
    }
}
