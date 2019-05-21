package com.htgames.nutspoker.game.match.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.game.audio.GameAudioAction;
import com.htgames.nutspoker.game.audio.GameAudioListener;
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity;
import com.htgames.nutspoker.game.match.constants.MatchConstants;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.customview.EmojiFilter;
import com.netease.nimlib.sdk.media.record.RecordType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 比赛大厅高级菜单
 */
public class AdvancedFunctionView extends Dialog implements View.OnClickListener {
    private final static String TAG = "AdvancedFunctionView";

    public AdvancedFunctionView(MatchRoomActivity activity) {
        super(activity, R.style.MyDialog);
        this.activity = activity;
        this.context = activity.getApplicationContext();
        init();
        initVeiw();
    }

    private void init() {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        Window win = this.getWindow();
        win.setGravity(Gravity.CENTER);//从下方弹出
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = width;   //宽度填满
        lp.height = height;  //高度自适应
        win.setAttributes(lp);
    }

    public void setInfo(String sessionId, String gameCode, int gameMode) {
        mGameAudioAction = new GameAudioAction(activity, view, sessionId, gameCode, gameMode);
        mGameAudioAction.setGameAudioListener(new GameAudioListener() {
            @Override
            public void onAudioRecordStart() {
                LogUtil.i(TAG, "onAudioRecordStart");
                rl_match_voice_success.setVisibility(View.GONE);
                showVoiceView(true);
            }

            @Override
            public void onAudioRecordFail() {
                LogUtil.i(TAG, "onAudioRecordFail");
                rl_match_voice_success.setVisibility(View.GONE);
                showVoiceView(false);
            }

            @Override
            public void onAudioRecordCancel() {
                LogUtil.i(TAG, "onAudioRecordCancel");
                rl_match_voice_success.setVisibility(View.GONE);
                showVoiceView(false);
            }

            @Override
            public void onAudioRecordIng(int time) {
                LogUtil.i(TAG, "onAudioRecordIng");
            }

            @Override
            public void onAudioRecordSuccess(final File audioFile, final long audioLength, final RecordType recordType) {
                LogUtil.i(TAG, "audioFile :" + audioFile.getAbsolutePath());
                rl_match_voice_success.setVisibility(View.VISIBLE);
                showVoiceView(false);
                LogUtil.i(TAG, "发送成功！");
                mGameAudioAction.sendAudioMessage(audioFile, audioLength, recordType);
                audioCount = audioCount + 1;
                tv_match_voice_count.setText("" + audioCount);
            }
        });
    }

    public void initVeiw() {
        view = LayoutInflater.from(context).inflate(R.layout.view_match_advance_function, null);
        rl_match_advance_voice = (RelativeLayout) view.findViewById(R.id.rl_match_advance_voice);
        btn_match_menu_voice = (TextView) view.findViewById(R.id.btn_match_menu_voice);
        btn_match_menu_close = (ImageView) view.findViewById(R.id.btn_match_menu_close);
        //
        rl_match_voice_success = (RelativeLayout) view.findViewById(R.id.rl_match_voice_success);
        tv_match_voice_count = (TextView) view.findViewById(R.id.tv_match_voice_count);
        rl_match_voice_success.setVisibility(View.GONE);
//        rl_match_voice_success.setOnClickListener(this);
        ll_match_advance_voice_ing = view.findViewById(R.id.ll_match_advance_voice_ing);
        probar_voice_timer = (ProgressBar) view.findViewById(R.id.probar_voice_timer);
        tv_voice_timer = (Chronometer) view.findViewById(R.id.tv_voice_timer);
        tv_voice_timer_tip = (TextView) view.findViewById(R.id.tv_voice_timer_tip);
//        btn_match_menu_voice.setOnClickListener(this);
        btn_match_menu_close.setOnClickListener(this);
        //发送文本公告
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setCornerRadius(ScreenUtil.dp2px(activity, 6));
        normalDrawable.setColor(activity.getResources().getColor(R.color.list_item_bg_press));
        mtt_send_text_notice_container = view.findViewById(R.id.mtt_send_text_notice_container);
        mtt_send_text_notice_et = (ClearableEditTextWithIcon) view.findViewById(R.id.mtt_send_text_notice_et);
        mtt_send_text_notice_et.isShowDeleteBtn = false;
        mtt_send_text_notice_et.removeClearButton();
        mtt_send_text_notice_et.setBackgroundDrawable(normalDrawable);
        EmojiFilter emojiFilter = new EmojiFilter();
        InputFilter[] nf = new InputFilter[1];nf[0] = emojiFilter;
//        mtt_send_text_notice_et.setFilters(nf);//禁止使用表情
        mtt_send_text_notice_btn = (TextView) view.findViewById(R.id.mtt_send_text_notice_btn);
        mtt_send_text_notice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity instanceof MatchRoomActivity && activity.mGameAction != null) {
                    if (StringUtil.isSpace(mtt_send_text_notice_et.getText().toString())) {
                        Toast.makeText(activity, "文本内容不能为空", Toast.LENGTH_SHORT).show();
                    }
                    activity.mGameAction.sendTextNotice("", activity.getGameInfo().code, mtt_send_text_notice_et.getText().toString(), new VolleyCallback() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int code = jsonObject.optInt("code");
                                if (code == 0) {
                                    Toast.makeText(ChessApp.sAppContext, "发送成功", Toast.LENGTH_SHORT).show();
                                    mtt_send_text_notice_et.setText("");
                                    rl_match_voice_success.setVisibility(View.VISIBLE);
                                    audioCount = audioCount + 1;
                                    tv_match_voice_count.setText("" + audioCount);
                                } else if (code == ApiCode.CODE_SEND_GAME_NOTICE) {
                                    Toast.makeText(ChessApp.sAppContext, "发送过于频繁，请稍后再试", Toast.LENGTH_SHORT).show();
                                } else {//3064未知错误
                                    Toast.makeText(ChessApp.sAppContext, "发送失败", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ChessApp.sAppContext, "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        rl_match_advance_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeybord();
            }
        });
        setContentView(view);
        showVoiceView(false);
    }

    private void hideKeybord() {
        if (activity instanceof MatchRoomActivity) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_match_menu_close:
                hideKeybord();
                this.dismiss();
                break;
//            case R.id.btn_match_menu_voice:
//                //发送语音
//                boolean isShow = rl_match_advance_voice.getVisibility() == View.VISIBLE;
//                showVoiceView(!isShow);
//                break;
//            case R.id.rl_match_voice_success:
//                mGameAudioAction.sendAudioMessage();
//                break;
        }
    }

    public void showVoiceView(boolean show) {
        if (show) {
            ll_match_advance_voice_ing.setVisibility(View.VISIBLE);
        } else {
            ll_match_advance_voice_ing.setVisibility(View.GONE);
        }
    }

    @Override
    public void show() {

        if(activity.matchStatus != null){

            if(activity.matchStatus.matchInRest == MatchConstants.MATCH_IN_REST)
                mIsPause = true;
            if(mIsPause) {
            } else {
            }
        }

        super.show();
    }

    public void setPause(boolean pause) {
        mIsPause = pause;
    }

    MatchRoomActivity activity;
    Context context;
    View view;
    TextView btn_match_menu_voice;
    ImageView btn_match_menu_close;
    View ll_match_advance_voice_ing;
    ProgressBar probar_voice_timer;
    Chronometer tv_voice_timer;
    TextView tv_voice_timer_tip;
    RelativeLayout rl_match_advance_voice;
    RelativeLayout rl_match_voice_success;
    TextView tv_match_voice_count;
    GameAudioAction mGameAudioAction;
    String sessionId = "";
    //发送文本公告
    View mtt_send_text_notice_container;
    ClearableEditTextWithIcon mtt_send_text_notice_et;
    TextView mtt_send_text_notice_btn;
    private int audioCount = 0;

    boolean mIsPause;
}
