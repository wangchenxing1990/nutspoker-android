package com.htgames.nutspoker.net.observable;

import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.common.util.log.LogUtil;

import rx.functions.Func1;

/**
 * Created by glp on 2016/8/18.
 */

//提供对 通用返回数据的转换。
public class HttpResultFunc<T> implements Func1<CommonBeanT<T>,T> {
    @Override
    public T call(CommonBeanT<T> commonBeanT) {
        LogUtil.e("HttpResultFunc","Thead is "+Thread.currentThread().toString());
        if(commonBeanT.code != 0 )
            throw new ResultException(commonBeanT.code,commonBeanT.message);
        else
            return commonBeanT.data;
    }
}
