/*
 * Copyright (C) 2017 Jeff Gilfelt.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htgames.nutspoker.debug.network_monitor;

import android.net.Uri;

import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.debug.network_monitor.data.HttpHeader;
import com.htgames.nutspoker.debug.network_monitor.support.FormatUtils;
import com.netease.nim.uikit.common.gson.GsonUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nl.qbusict.cupboard.annotation.Index;
import okhttp3.Headers;

public class HttpTransaction {

    public enum Status {
        Requested,
        Complete,
        Failed
    }

    public static final String[] PARTIAL_PROJECTION = new String[] {
            "_id",
            "requestDate",
            "tookMs",
            "method",
            "host",
            "path",
            "scheme",
            "requestContentLength",
            "responseCode",
            "error",
            "responseContentLength"
    };

    private static final SimpleDateFormat TIME_ONLY_FMT = new SimpleDateFormat("HH:mm:ss", Locale.US);

    public Long _id;
    @Index public Date requestDate;
    public Date responseDate;
    public Long tookMs;

    public String protocol;
    public String method;
    private String url;
    public String host;
    public String path;
    public String scheme;

    public Long requestContentLength;
    public String requestContentType;
    private String requestHeaders;
    private String requestBody;
    public boolean requestBodyIsPlainText = true;

    public Integer responseCode;
    public String responseMessage;
    public String error;

    public Long responseContentLength;
    public String responseContentType;
    private String responseHeaders;
    private String responseBody;
    public boolean responseBodyIsPlainText = true;

    public String getRequestBody() {
        return requestBody;
    }

    public String getFormattedRequestBody() {
        return formatBody(requestBody, requestContentType);
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getFormattedResponseBody() {
        return formatBody(responseBody, responseContentType);
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        Uri uri = Uri.parse(url);
        host = uri.getHost();
        path = uri.getPath() + ((uri.getQuery() != null) ? "?" + uri.getQuery() : "");
        scheme = uri.getScheme();
    }

    public void setRequestHeaders(Headers headers) {
        setRequestHeaders(toHttpHeaderList(headers));
    }

    public void setRequestHeaders(List<HttpHeader> headers) {
        requestHeaders = GsonUtils.getGson().toJson(headers);
    }

    public List<HttpHeader> getRequestHeaders() {
        return GsonUtils.getGson().fromJson(requestHeaders, new TypeToken<List<HttpHeader>>(){}.getType());
    }

    public String getRequestHeadersString(boolean withMarkup) {
        return FormatUtils.formatHeaders(getRequestHeaders(), withMarkup);
    }

    public void setResponseHeaders(Headers headers) {
        setResponseHeaders(toHttpHeaderList(headers));
    }

    public void setResponseHeaders(List<HttpHeader> headers) {
        responseHeaders = GsonUtils.getGson().toJson(headers);
    }

    public List<HttpHeader> getResponseHeaders() {
        return GsonUtils.getGson().fromJson(responseHeaders, new TypeToken<List<HttpHeader>>(){}.getType());
    }

    public String getResponseHeadersString(boolean withMarkup) {
        return FormatUtils.formatHeaders(getResponseHeaders(), withMarkup);
    }

    public Status getStatus() {
        if (error != null) {
            return Status.Failed;
        } else if (responseCode == null) {
            return Status.Requested;
        } else {
            return Status.Complete;
        }
    }

    public String getRequestStartTimeString() {
        return (requestDate != null) ? TIME_ONLY_FMT.format(requestDate) : null;
    }

    public String getRequestDateString() {
        return (requestDate != null) ? requestDate.toString() : null;
    }

    public String getResponseDateString() {
        return (responseDate != null) ? responseDate.toString() : null;
    }

    public String getDurationString() {
        return (tookMs != null) ? + tookMs + " ms" : null;
    }

    public String getRequestSizeString() {
        return formatBytes((requestContentLength != null) ? requestContentLength : 0);
    }
    public String getResponseSizeString() {
        return (responseContentLength != null) ? formatBytes(responseContentLength) : null;
    }

    public String getTotalSizeString() {
        long reqBytes = (requestContentLength != null) ? requestContentLength : 0;
        long resBytes = (responseContentLength != null) ? responseContentLength : 0;
        return formatBytes(reqBytes + resBytes);
    }

    public String getResponseSummaryText() {
        switch (getStatus()) {
            case Failed:
                return error;
            case Requested:
                return null;
            default:
                return String.valueOf(responseCode) + " " + responseMessage;
        }
    }

    public String getNotificationText() {
        switch (getStatus()) {
            case Failed:
                return " ! ! !  " + path;
            case Requested:
                return " . . .  " + path;
            default:
                return String.valueOf(responseCode) + " " + path;
        }
    }

    public boolean isSsl() {
        return scheme.toLowerCase().equals("https");
    }

    private List<HttpHeader> toHttpHeaderList(Headers headers) {
        List<HttpHeader> httpHeaders = new ArrayList<>();
        for (int i = 0, count = headers.size(); i < count; i++) {
            httpHeaders.add(new HttpHeader(headers.name(i), headers.value(i)));
        }
        return httpHeaders;
    }

    private String formatBody(String body, String contentType) {
        if (contentType != null && contentType.toLowerCase().contains("json")) {
            return FormatUtils.formatJson(body);
        } else if (contentType != null && contentType.toLowerCase().contains("xml")) {
            return FormatUtils.formatXml(body);
        } else {
            return body;
        }
    }

    private String formatBytes(long bytes) {
        return FormatUtils.formatByteCount(bytes, true);
    }
}
