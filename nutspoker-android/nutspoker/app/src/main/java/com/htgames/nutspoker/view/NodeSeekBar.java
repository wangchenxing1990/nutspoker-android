package com.htgames.nutspoker.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.view.dotseekbar.DotSeekBar;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.ArrayList;

/**
 */
public class NodeSeekBar extends RelativeLayout {
    private final static String TAG = "NodeSeekBar";
    LinearLayout ll_time;
    public DotSeekBar mSeekBar;
    ArrayList<TextView> timeTextViewList;
    public int currentPosition = 0;
    public int lastPosition = -1;
    public int[] datas;
    public int timeSize = 0;
    int maxProgres = 0;
    boolean isShowTip;
    boolean isTime = false;
    public boolean showCustomTips = false;//顶部自定义文案显示，比如大菠萝的"摆牌时间"的"快速"和"慢速"显示
    String strUnit;

    public NodeSeekBar(Context context) {
        this(context, null);
    }

    public NodeSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NodeSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DotSeekBar, defStyleAttr, 0);
        boolean shouldDrawDot = ta.getBoolean(R.styleable.DotSeekBar_shouldDrawDot, true);
        this.isShowTip = ta.getBoolean(R.styleable.DotSeekBar_showTip, true);
        LayoutInflater.from(context).inflate(R.layout.view_node_seekbar, this, true);
        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        ll_time.setVisibility(isShowTip ? VISIBLE : GONE);
        mSeekBar = (DotSeekBar) findViewById(R.id.mSeekBar);
        mSeekBar.shouldDrawDot = shouldDrawDot;
    }

    public int getDataItem(int position) {
        return this.datas == null || position >= this.datas.length ? 0 : this.datas[position];
    }

    public void setThumbId(int thumbId) {
        Drawable drawable = getResources().getDrawable(thumbId);
        int iconWidth = drawable.getMinimumWidth();
        int iconHeight = drawable.getMinimumHeight();
        drawable.setBounds(0, 0, iconWidth, iconHeight);
        if(mSeekBar != null){
            mSeekBar.setThumb(drawable);
            mSeekBar.setMax(maxProgres);
            mSeekBar.setDotCount(timeSize);
            mSeekBar.setOnSeekBarChangeListener(mOnSeekListener);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(mSeekBar != null) {
            mSeekBar.setEnabled(enabled);
        }
        super.setEnabled(enabled);
        invalidate();
    }

    public void setData(int[] datas, boolean showTip, boolean isTime, int thumbId, String strUnit) {
        currentPosition = currentPosition >= datas.length ? datas.length - 1 : currentPosition;
        lastPosition = -1;
        this.datas = datas;
        this.strUnit = strUnit;
        this.isTime = isTime;
        this.isShowTip = showTip;
        Drawable drawable = getResources().getDrawable(thumbId);
        int iconWidth = drawable.getMinimumWidth();
        int iconHeight = drawable.getMinimumHeight();
        drawable.setBounds(0, 0, iconWidth, iconHeight);
        timeSize = datas.length;
        maxProgres = timeSize - 1;
        if(ll_time != null){
            ViewGroup.LayoutParams params = ll_time.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            ll_time.setLayoutParams(params);
        }
        timeTextViewList = new ArrayList<>(timeSize);
        initTimeView();
        if(mSeekBar != null){
            mSeekBar.setThumb(drawable);
            mSeekBar.setMax(maxProgres);
            mSeekBar.setDotCount(timeSize);
            mSeekBar.setOnSeekBarChangeListener(mOnSeekListener);
        }
        updateProgressUI();
    }

    /**
     * 只更新数据，拖动条上面的textview不显示也不更新(更新上部textview会导致很卡)    这样执行上部textview动画时可能index out of bounds，之行动画前判断boolean变量this.isShowTip
     */
    public void onlyUpdateData(int[] newDatas) {
        if (newDatas == null) {
            return;
        }
        this.datas = newDatas;
        timeSize = datas.length;
        maxProgres = timeSize - 1;
        mSeekBar.setMax(maxProgres);
        mSeekBar.setDotCount(timeSize);
    }

    public void updateAnteData(int[] newAnteDatas) {//更新ante数据
        this.datas = newAnteDatas;
        for (int i = 0; i < newAnteDatas.length; i++) {
            timeTextViewList.get(i).setText(newAnteDatas[i] + "");
        }
    }

    public String[] customStrs;
    public void initTimeView() {
        timeTextViewList.clear();
        ll_time.removeAllViews();
        for (int i = 0; i < timeSize; i++) {
            int num = datas[i];
            TextView tv = new TextView(getContext());
            if (showCustomTips && customStrs != null && customStrs.length > i) {
                tv.setText(customStrs[i]);
                tv.setTextSize(10);
            } else {
                if (!TextUtils.isEmpty(strUnit)) {
                    tv.setText(num + strUnit);
                    tv.setTextSize(9);
                } else if (isTime) {
                    tv.setText(GameConstants.getGameDurationShow(datas[i] * 60));
                    tv.setTextSize(10);
                } else {
                    tv.setText(num + "");
                    tv.setTextSize(10);
                }
            }
            ColorStateList csl = getResources().getColorStateList(R.color.seekbar_time_tv_color);
            tv.setTextColor(csl);
            LinearLayout.LayoutParams paramsFram = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            FrameLayout tvParent = new FrameLayout(getContext());

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (datas.length == 2) {
                if (i == 0) {
                    tv.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                    tv.setPadding(ScreenUtil.dp2px(getContext(), 25), 0, 0, 0);
                    params.gravity = Gravity.LEFT | Gravity.BOTTOM;
                } else if (i == (timeSize - 1)) {
                    tv.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                    tv.setPadding(0, 0, ScreenUtil.dp2px(getContext(), 25), 0);
                    params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                } else {
                    tv.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                }
            } else {
                tv.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            }
            tvParent.addView(tv, params);
            if(ll_time != null) {
                ll_time.addView(tvParent, paramsFram);
            }
            timeTextViewList.add(tv);
        }
        if (ll_time != null) {
            ll_time.setVisibility(isShowTip ? VISIBLE : GONE);
        }
    }

    public void setProgress(int progress) {
        currentPosition = progress >= datas.length ? datas.length - 1 : progress;
        if(mSeekBar != null)
            mSeekBar.setProgress(progress);
    }

    /**
     * 获取时间节点
     *
     * @return
     */
    public int getCurrentTimeProgress() {
        return mSeekBar == null ? 0 : mSeekBar.getProgress();
    }


    SeekBar.OnSeekBarChangeListener mOnSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            currentPosition = progress >= datas.length ? datas.length - 1 : progress;
            updateProgressUI();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            LogUtil.i(TAG, "onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            LogUtil.i(TAG, "onStopTrackingTouch");
            int progress = seekBar.getProgress();
            if(mSeekBar != null)
                mSeekBar.setProgress(progress);
        }
    };

    public void updateProgressUI() {
        LogUtil.i(TAG, "currentPosition :" + currentPosition + ";lastPosition:" + lastPosition);
        if (currentPosition != lastPosition) {
            showBigAnimation(currentPosition, true);
            LogUtil.i(TAG, "onProgressChanged : " + currentPosition);
            if (lastPosition != -1) {
                showBigAnimation(lastPosition, false);
            }
            if (mOnNodeChangeListener != null) {
                mOnNodeChangeListener.onNodeChanged(currentPosition);
            }
        }
        lastPosition = currentPosition;
    }

    public void showBigAnimation(int progress, boolean isShowBig) {
        if (!isShowTip) {
            return;//如果只更新了数据，拖动条上面的textview没有不更新(更新上部textview会导致很卡)    这时执行上部textview动画时可能index out of bounds，之行动画前判断boolean变量this.isShowTip
        }
        //Sacle动画 - 渐变尺寸缩放
        Animation scaleAnimation = null;
        if (timeTextViewList != null && timeTextViewList.size() > 0) {
            timeTextViewList.get(progress >= timeTextViewList.size() ? timeTextViewList.size() - 1 : progress).setSelected(isShowBig);
        }
        if (isShowBig) {
            scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_big_anim);
        } else {
            scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_small_anim);
        }
        scaleAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        if (timeTextViewList != null && timeTextViewList.size() > 0) {
            timeTextViewList.get(progress >= timeTextViewList.size() ? timeTextViewList.size() - 1 : progress).startAnimation(scaleAnimation);
        }
    }

    public interface OnNodeChangeListener {
        void onNodeChanged(int progress);
    }

    OnNodeChangeListener mOnNodeChangeListener;

    public void setOnNodeChangeListener(OnNodeChangeListener listener) {
        mOnNodeChangeListener = listener;
    }
}
