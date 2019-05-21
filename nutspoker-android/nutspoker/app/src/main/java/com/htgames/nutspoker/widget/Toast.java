package com.htgames.nutspoker.widget;

import android.content.Context;

public abstract class Toast {
    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;
    private static android.widget.Toast toast;
    //private static Handler handler = new Handler();

    private static Runnable run = new Runnable() {
        public void run() {
            if (toast != null) {
                toast.cancel();
            }
        }
    };

    public static android.widget.Toast makeText(Context context, CharSequence text, int duration)  throws NullPointerException {
        if (null == context) {
            throw new NullPointerException("The ctx is null!");
        }
        //handler.removeCallbacks(run);
        if (null != toast) {
            toast.setText(text);
        } else {
            if (duration == LENGTH_SHORT) {
                toast = android.widget.Toast.makeText(context, text, LENGTH_SHORT);
            }else{
                toast = android.widget.Toast.makeText(context, text, LENGTH_LONG);
            }
        }
        return toast;
    }

    public static android.widget.Toast makeText(Context context, int resId, int duration)  throws NullPointerException {
        return  makeText(context , context.getString(resId) , duration);
    }

    /**
     * 弹出Toast
     */
//    public static void show() {
//        int duration = 1000;
//        // handler的duration不能直接对应Toast的常量时长，在此针对Toast的常量相应定义时长
//        switch (duration) {
//            case LENGTH_SHORT:// Toast.LENGTH_SHORT值为0，对应的持续时间大概为1s
//                duration = 1000;
//                break;
//            case LENGTH_LONG:// Toast.LENGTH_LONG值为1，对应的持续时间大概为3s
//                duration = 3000;
//                break;
//            default:
//                break;
//        }
//        //handler.postDelayed(run, duration);
//        if(toast != null){
//            toast.show();
//        }
//    }
}
