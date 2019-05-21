package com.htgames.nutspoker.game.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.htgames.nutspoker.R;
import com.will.common.view.wheelview.WheelView;

/**
 */
public class WheelViewPop extends Dialog {
    private final static String TAG = "WheelViewPop";
    private View rootView;
    private Activity mActivity;
    private Context mContext;
    WheelView mWheelView;
//    WheelView.OnWheelViewListener mOnWheelViewListener;

    public WheelViewPop(Activity activity) {
        super(activity, R.style.PopupBottomDialog);
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        init();
    }

    public void init() {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        setAnimationStyle(R.style.PopupAnimation);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        //
        Window win = this.getWindow();
        win.setGravity(Gravity.BOTTOM);//从下方弹出
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = width;   //宽度填满
        lp.height = height;  //高度自适应
        win.setAttributes(lp);
        initView(mContext);
        setContentView(rootView);
    }

    private void initView(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.pop_game_create_wheelview, null);
        mWheelView = (WheelView)rootView.findViewById(R.id.mWheelView);
        setContentView(rootView);
        initWheel();
    }

    public void initWheel() {
//        mWheelView.setOffset(1);
//        mWheelView.setItems(Arrays.asList(GameConfigConstants.SNG_MULTIPLE_TABLE_PLAYERS));
//        mWheelView.setOnWheelViewListener(new Wheel11View.OnWheelViewListener() {
//            @Override
//            public void onSelected(int selectedIndex, String item) {
//                Log.d(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
//                if (mOnWheelViewListener != null) {
//                    mOnWheelViewListener.onSelected(selectedIndex, item);
//                }
//            }
//        });
    }

//    public void setOnWheelViewListener(Wheel11View.OnWheelViewListener listener) {
//        this.mOnWheelViewListener = listener;
//    }
}
