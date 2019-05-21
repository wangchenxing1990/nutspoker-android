package com.htgames.nutspoker.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.view.hands.HandCardView;
import com.htgames.nutspoker.widget.Toast;

import java.util.List;
import com.htgames.nutspoker.data.common.CircleConstant;

/**
 * 手牌记录更多按钮
 */
public class PaipuMoreView extends PopupWindow implements View.OnClickListener {
    private final static String TAG = "PaipuMoreView";
    private Context context;
    View view;
    TextView tv_paipu_send;
    TextView tv_paipu_share;
    TextView tv_paipu_collect;
    TextView tv_paipu_cancel;
    //
    TextView btn_share_wechat;
    TextView btn_share_moments;
    private boolean isShowCollect = true;
    int position = 0;
    boolean isCollected = false;//是否已经收藏
    private boolean isOldPaipu = false;//是否是旧的牌谱
    private int type = CircleConstant.TYPE_PAIJU;
    //
    HandCardView mHandCardView;

    public PaipuMoreView(Context context , int type) {
        super(context);
        this.type = type;
        init(context);
    }

    public PaipuMoreView(Context context , int type ,boolean isShowCollect) {
        super(context);
        this.type = type;
        this.isShowCollect = isShowCollect;
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.view_paipu_more, null);
        tv_paipu_send = (TextView) view.findViewById(R.id.tv_paipu_send);
        tv_paipu_share = (TextView) view.findViewById(R.id.tv_paipu_share);
        tv_paipu_collect = (TextView) view.findViewById(R.id.tv_paipu_collect);
        tv_paipu_cancel = (TextView) view.findViewById(R.id.tv_paipu_cancel);
        //
        btn_share_wechat = (TextView) view.findViewById(R.id.btn_share_wechat);
        btn_share_moments = (TextView) view.findViewById(R.id.btn_share_moments);
        //
        mHandCardView = (HandCardView) view.findViewById(R.id.mHandCardView);
        //
        tv_paipu_send.setOnClickListener(this);
        tv_paipu_share.setOnClickListener(this);
        tv_paipu_collect.setOnClickListener(this);
        tv_paipu_cancel.setOnClickListener(this);
        btn_share_wechat.setOnClickListener(this);
        btn_share_moments.setOnClickListener(this);
        //
        setAnimationStyle(R.style.PopupAnimation);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setTouchable(true);
//        setBackgroundDrawable(null);
        setBackgroundDrawable(new BitmapDrawable());
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                    callOverback.showShadow(false);
                    dismiss();
                }
                return false;
            }
        });
        if (type == CircleConstant.TYPE_PAIJU) {
            tv_paipu_share.setVisibility(View.VISIBLE);
            tv_paipu_collect.setVisibility(View.GONE);
        } else if (type == CircleConstant.TYPE_PAIPU) {
            tv_paipu_share.setVisibility(View.GONE);
            if (isShowCollect) {
                tv_paipu_collect.setVisibility(View.VISIBLE);
            } else {
                tv_paipu_collect.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置Position，
     * @param position
     * @param isOldPaipu 是否是旧的牌谱
     * @param isCollected 是否已经收藏
     */
    public void setPosition(int position , boolean isOldPaipu , List<Integer> handCards, boolean isCollected) {
        LogUtil.i(TAG, "position :" + position + ";isCollected :" + isCollected);
        this.position = position;
        this.isCollected = isCollected;
        this.isOldPaipu = isOldPaipu;
        if (isCollected) {
            tv_paipu_send.setVisibility(View.VISIBLE);
//            tv_paipu_share.setVisibility(View.VISIBLE);
            tv_paipu_share.setVisibility(View.GONE);
            tv_paipu_collect.setText(R.string.cancel_collect);
            if (ApiConfig.AppVersion.isTaiwanVersion) {
                //台湾版本，不能分享
                btn_share_wechat.setVisibility(View.GONE);
                btn_share_moments.setVisibility(View.GONE);
            } else {
                btn_share_wechat.setVisibility(View.VISIBLE);
                btn_share_moments.setVisibility(View.VISIBLE);
            }
        } else {
            //没有收藏，不能分享
            tv_paipu_send.setVisibility(View.GONE);
            tv_paipu_share.setVisibility(View.GONE);
            tv_paipu_collect.setText(R.string.collect);
            btn_share_wechat.setVisibility(View.GONE);
            btn_share_moments.setVisibility(View.GONE);
        }
        //
        mHandCardView.setHandCard(handCards);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (mOnCallOverback != null) {
            mOnCallOverback.showShadow(true);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(mOnCallOverback != null){
            mOnCallOverback.showShadow(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_paipu_send:
                this.dismiss();
                if(mOnMoreListener != null){
                    mOnMoreListener.onSend(position);
                }
                break;
            case R.id.tv_paipu_share:
                this.dismiss();
                if(mOnMoreListener != null){
                    mOnMoreListener.onShare(position);
                }
                break;
            case R.id.tv_paipu_collect:
                this.dismiss();
                if(mOnMoreListener != null){
                    mOnMoreListener.onCollect(position);
                }
                break;
            case R.id.tv_paipu_cancel:
                this.dismiss();
                break;
            case R.id.btn_share_wechat:
                this.dismiss();
                if (isOldPaipu) {
                    //旧的牌谱
                    Toast.makeText(context, R.string.share_hands_is_old, Toast.LENGTH_SHORT).show();
                } else {
                    if (mOnMoreListener != null) {
                        mOnMoreListener.onShareWechat(position, mHandCardView);
                    }
                }
                break;
            case R.id.btn_share_moments:
                this.dismiss();
                if (isOldPaipu) {
                    //旧的牌谱
                    Toast.makeText(context, R.string.share_hands_is_old, Toast.LENGTH_SHORT).show();
                } else {
                    if (mOnMoreListener != null) {
                        mOnMoreListener.onShareMoments(position, mHandCardView);
                    }
                }
                break;
//            case R.id.btn_share_sinaweibo:
//                this.dismiss();
//                if(mOnMoreListener != null) {
//                    mOnMoreListener.onShareSinaweibo(position , mHandCardView);
//                }
//                break;
        }
    }

    public OnMoreListener mOnMoreListener;
    OnCallOverback mOnCallOverback;

    public void setOnMoreListener(OnMoreListener listener){
        this.mOnMoreListener = listener;
    }

    public interface OnMoreListener {
        public void onSend(int position);

        public void onShare(int position);

        public void onCollect(int position);

        public void onShareWechat(int position, View shareView);

        public void onShareMoments(int position, View shareView);

        public void onShareSinaweibo(int position, View shareView);
    }

    public void setOnCallOverback(OnCallOverback listener){
        this.mOnCallOverback = listener;
    }

    public interface OnCallOverback{
        void showShadow(boolean show);
    }
}
