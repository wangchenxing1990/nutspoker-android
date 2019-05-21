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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.tool.DateCalendarTools;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.will.common.view.wheelview.ArrayWheelAdapter;
import com.will.common.view.wheelview.NumericWheelAdapter;
import com.will.common.view.wheelview.OnWheelChangedListener;
import com.will.common.view.wheelview.WheelView;

import java.util.Calendar;

/**
 */
public class DateSelectPopDialog extends Dialog {
    private final static String TAG = "WheelViewPop";
    private View rootView;
    private Context mContext;
    WheelView mDayWheelView;
    WheelView mHourWheelView;
    WheelView mMinWheelView;
    private final static int MAX_DAY = 7;
    long[] days;
    String[] dayStrs;
    OnDateSelectListener mOnDateSelectListener;
    public OnDateClearListener mOnDateClearListener;
    TextView btn_date_select_confim;
    TextView btn_date_select_clear;
    TextView tv_date_select_prompt;
    float zoomMax = 0.5f;

    public DateSelectPopDialog(Activity activity) {
        super(activity, R.style.PopupBottomDialog);
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
        rootView = LayoutInflater.from(context).inflate(R.layout.pop_game_create_date_select, null);
        btn_date_select_confim = (TextView) rootView.findViewById(R.id.btn_date_select_confim);
        btn_date_select_clear = (TextView) rootView.findViewById(R.id.btn_date_select_clear);
        tv_date_select_prompt = (TextView) rootView.findViewById(R.id.tv_date_select_prompt);
        mDayWheelView = (WheelView) rootView.findViewById(R.id.mDayWheelView);
        mHourWheelView = (WheelView) rootView.findViewById(R.id.mHourWheelView);
        mMinWheelView = (WheelView) rootView.findViewById(R.id.mMinWheelView);
        tv_date_select_prompt.setVisibility(View.INVISIBLE);
        setContentView(rootView);
        getTimeData();
        initWheel();
        btn_date_select_confim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDateSelectListener != null) {
                    long selectTime = getSelectTime();
                    long currentTime = DemoCache.getCurrentServerSecondTime();
                    if ((selectTime - currentTime) <= 0) {
                        showPrompt();
                    } else {
                        tv_date_select_prompt.setVisibility(View.INVISIBLE);
                        mOnDateSelectListener.onDateSelect(selectTime);
                        dismiss();
                    }
//                    else if(selectTime > (currentTime + 8 * 60 * 24 * 60)) {
//                        Toast.makeText(mContext, R.string.game_create_match_start_date_is_late, Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });
        btn_date_select_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnDateClearListener != null) {
                    mOnDateClearListener.onDateClear();
                }
            }
        });
        mDayWheelView.addChangingListener(onWheelChangedListener);
        mHourWheelView.addChangingListener(onWheelChangedListener);
        mMinWheelView.addChangingListener(onWheelChangedListener);
    }

    public void showPrompt() {
        tv_date_select_prompt.setVisibility(View.VISIBLE);
        showBigAnimation(tv_date_select_prompt, true);
    }

    OnWheelChangedListener onWheelChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            long selectTime = getSelectTime();
            long currentTime = DemoCache.getCurrentServerSecondTime();
            if ((selectTime - currentTime) <= 0) {
                tv_date_select_prompt.setVisibility(View.VISIBLE);
            } else {
                tv_date_select_prompt.setVisibility(View.INVISIBLE);
            }
        }
    };

    public long getSelectTime() {
        long date = days[mDayWheelView.getCurrentItem()];
        int hour = mHourWheelView.getCurrentItem();
        int min = mMinWheelView.getCurrentItem();
        Calendar calendar = DateCalendarTools.getDayCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime().getTime() / 1000L;
    }

    Calendar currentCalendar;

    public void getTimeData() {
        days = new long[MAX_DAY];
        dayStrs = new String[MAX_DAY];
        for (int i = 0; i < MAX_DAY; i++) {
            long dayTime = DateCalendarTools.getBeforeDay(i);
            days[i] = dayTime;
            Calendar calendar = DateCalendarTools.getDayCalendar(dayTime);
            if (i == 0) {
                currentCalendar = DateCalendarTools.getDayCalendar(DemoCache.getCurrentServerSecondTime() + 1 * 60);
            }
            StringBuffer dayStringBuffer = new StringBuffer();
            dayStringBuffer
                    .append(calendar.get(Calendar.MONTH) + 1)
                    .append(mContext.getString(R.string.month))
                    .append(calendar.get(Calendar.DAY_OF_MONTH))
                    .append(mContext.getString(R.string.day))
                    .append(" ");
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (i == 0) {
                dayStringBuffer.append(mContext.getString(R.string.today));
            } else {
                dayStringBuffer.append(mContext.getString(DateCalendarTools.getDayOfWeek(dayOfWeek)));
            }
            dayStrs[i] = dayStringBuffer.toString();
            LogUtil.d(TAG, "dayTime : " + dayTime + "; dayStrs :" + dayStrs[i]);
        }
    }

    public void initWheel() {
        mDayWheelView.setAdapter(new ArrayWheelAdapter<String>(dayStrs));
        mHourWheelView.setAdapter(new NumericWheelAdapter(0, 23));
        mMinWheelView.setAdapter(new NumericWheelAdapter(0, 59));
        mHourWheelView.setCurrentItem(currentCalendar.get(Calendar.HOUR_OF_DAY));
        mMinWheelView.setCurrentItem(currentCalendar.get(Calendar.MINUTE));
        LogUtil.i(TAG, currentCalendar.get(Calendar.HOUR_OF_DAY) +
                ":" + currentCalendar.get(Calendar.MINUTE));
    }

    public void setOnDateSelectListener(OnDateSelectListener listener) {
        mOnDateSelectListener = listener;
    }

    public interface OnDateSelectListener {
        public void onDateSelect(long date);
    }

    public interface OnDateClearListener {
        public void onDateClear();
    }

    public void showBigAnimation(View view, boolean isShowBig) {
//        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.5f, Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_SELF, 1f);
//        scaleAnimation.setDuration(300);
//        scaleAnimation.setZAdjustment(Animation.ZORDER_TOP);
//        scaleAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
//        scaleAnimation.setRepeatCount(1);
//        scaleAnimation.setRepeatMode(Animation.REVERSE);//必须设置setRepeatCount此设置才生效，动画执行完成之后按照逆方式动画返回

        //动画集
//        AnimationSet set = new AnimationSet(true);
//        set.addAnimation(translateAnimation);
//        set.addAnimation(alphaAnimation);

        //Sacle动画 - 渐变尺寸缩放
        Animation scaleAnimation = null;
        if (isShowBig) {
            scaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale_big_anim);
        } else {
            scaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale_small_anim);
        }
        scaleAnimation.setDuration(300);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setFillAfter(false);//动画执行完后是否停留在执行完的状态
        view.startAnimation(scaleAnimation);
    }
}