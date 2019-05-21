package com.htgames.nutspoker.api.customexception;

/**
 * Created by glp on 2016/8/12.
 */

public class HttpCustomException extends RuntimeException {

    public int mCode;

    public HttpCustomException(int code,String msg){
        super(msg);
        mCode = code;
    }

    public int getCode(){
        return mCode;
    }
}
