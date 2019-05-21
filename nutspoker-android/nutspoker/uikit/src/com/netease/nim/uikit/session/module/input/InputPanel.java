package com.netease.nim.uikit.session.module.input;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.chesscircle.interfaces.OnEditModeListener;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.interfaces.SensitiveFilter;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.emoji.EmoticonPickerView;
import com.netease.nim.uikit.session.emoji.IEmoticonSelectedListener;
import com.netease.nim.uikit.session.emoji.MoonUtil;
import com.netease.nim.uikit.session.helper.ListHelp;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import android.widget.FrameLayout;
import java.io.File;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;

import java.util.List;

/**
 * 底部文本编辑，语音等模块
 * Created by hzxuwen on 2015/6/16.
 */
public class InputPanel implements IEmoticonSelectedListener, IAudioRecordCallback {

    private static final String TAG = "MsgSendLayout";

    private static final int SHOW_LAYOUT_DELAY = 200;

    private Container container;
    private View view;
    private Handler uiHandler;

    protected View actionPanelBottomLayout; // 更多布局
    protected LinearLayout messageActivityBottomLayout;
    protected EditText messageEditText;// 文本消息编辑框
    protected Button audioRecordBtn; // 录音按钮
    protected View audioAnimLayout; // 录音动画布局
    protected FrameLayout textAudioSwitchLayout; // 切换文本，语音按钮布局
    protected View switchToTextButtonInInputBar;// 文本消息选择按钮
    protected View switchToAudioButtonInInputBar;// 语音消息选择按钮
    protected View moreFuntionButtonInInputBar;// 更多消息选择按钮
    protected View sendMessageButtonInInputBar;// 发送消息按钮
    protected View emojiButtonInInputBar;// 发送消息按钮
    protected View messageInputBar;

    private SessionCustomization customization;

    // 表情
    protected EmoticonPickerView emoticonPickerView;  // 贴图表情控件

    // 语音
    protected AudioRecorder audioMessageHelper;
    private Chronometer time;
    private TextView timerTip;
    private LinearLayout timerTipContainer;
    private boolean started = false;
    private boolean cancelled = false;
    private boolean touched = false; // 是否按着
    private boolean isKeyboardShowed = true; // 是否显示键盘

    // state
    private boolean actionPanelBottomLayoutHasSetup = false;
    private boolean isTextAudioSwitchShow = true;

    // adapter
    private List<BaseAction> actions;

    // data
    private long typingTime = 0;

    //是否是客服模式
    boolean isDealerMode = false;

    public InputPanel(ListHelp mlp,Container container, View view, List<BaseAction> actions, boolean isTextAudioSwitchShow) {
        this.mMsgListPanel = mlp;
        this.container = container;
        this.view = view;
        this.actions = actions;
        this.uiHandler = new Handler();
        this.isTextAudioSwitchShow = isTextAudioSwitchShow;
        init();
    }

    public InputPanel(ListHelp mlp,Container container, View view, List<BaseAction> actions , boolean isTextAudioSwitchShow, boolean isDealerMode) {
        this.mMsgListPanel = mlp;
        this.container = container;
        this.view = view;
        this.actions = actions;
        this.uiHandler = new Handler();
        this.isDealerMode = isDealerMode;
        this.isTextAudioSwitchShow = isTextAudioSwitchShow;
        init();
    }

    public InputPanel(ListHelp mlp,Container container, View view, List<BaseAction> actions) {
        this(mlp,container, view, actions, true);
    }

    public void onPause() {
        // 停止录音
        if (audioMessageHelper != null) {
            onEndAudioRecord(true);
        }
    }

    /**
     * 需要清除ActionsPanel
     */
    public void onDestroy() {
        ActionsPanel.onDestory();
    }

    public void setInputPanelShowHeight(int keyboardHeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)emoticonPickerView.getLayoutParams();
        if(params == null) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight);
        } else{
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = keyboardHeight;
        }
        emoticonPickerView.setLayoutParams(params);
    }

    OnEditModeListener mOnEditModeListener;

    public void setOnEditModeListener(OnEditModeListener listener) {
        mOnEditModeListener = listener;
    }

    public boolean collapse(boolean immediately) {
        boolean respond = (emoticonPickerView != null && emoticonPickerView.getVisibility() == View.VISIBLE
                || actionPanelBottomLayout != null && actionPanelBottomLayout.getVisibility() == View.VISIBLE);

        hideAllInputLayout(immediately);

        return respond;
    }

    private void init() {
        initViews();
        initInputBarListener();
        initTextEdit();
        initAudioRecordButton();
        restoreText(false);

//        for (int i = 0; i < actions.size(); ++i) {
//            actions.get(i).setIndex(i);
//            actions.get(i).setContainer(container);
//        }
        checkMoreFuntionShow();
    }

    /**
     * 判断更多按钮是否显示
     */
    public void checkMoreFuntionShow(){
        LogUtil.d(TAG, "action size: " + (actions == null ? 0 : actions.size()));
        if(actions != null && actions.size() > 0 ){
            moreFuntionButtonInInputBar.setVisibility(View.VISIBLE);
            for (int i = 0; i < actions.size(); ++i) {
                actions.get(i).setIndex(i);
                actions.get(i).setContainer(container);
            }
        }else{
            moreFuntionButtonInInputBar.setVisibility(View.GONE);
        }
    }

    public void setCustomization(SessionCustomization customization) {
        this.customization = customization;
        if (customization != null) {
            emoticonPickerView.setWithSticker(customization.withSticker);
        }
    }

    public void reload(Container container, SessionCustomization customization) {
        this.container = container;
        setCustomization(customization);
    }

    private void initViews() {
        // input bar
        messageActivityBottomLayout = (LinearLayout) view.findViewById(R.id.messageActivityBottomLayout);
        messageInputBar = view.findViewById(R.id.textMessageLayout);
        switchToTextButtonInInputBar = view.findViewById(R.id.buttonTextMessage);
        switchToAudioButtonInInputBar = view.findViewById(R.id.buttonAudioMessage);
        moreFuntionButtonInInputBar = view.findViewById(R.id.buttonMoreFuntionInText);
        emojiButtonInInputBar = view.findViewById(R.id.emoji_button);
        sendMessageButtonInInputBar = view.findViewById(R.id.buttonSendMessage);
        messageEditText = (EditText) view.findViewById(R.id.editTextMessage);

        // 语音
        audioRecordBtn = (Button) view.findViewById(R.id.audioRecord);
        audioAnimLayout = view.findViewById(R.id.layoutPlayAudio);
        time = (Chronometer) view.findViewById(R.id.timer);
        timerTip = (TextView) view.findViewById(R.id.timer_tip);
        timerTipContainer = (LinearLayout) view.findViewById(R.id.timer_tip_container);

        // 表情
        emoticonPickerView = (EmoticonPickerView) view.findViewById(R.id.emoticon_picker_view);

        //更多 -- new
        if (actionPanelBottomLayout == null) {
            View.inflate(container.activity, R.layout.nim_message_activity_actions_layout, messageActivityBottomLayout);
            actionPanelBottomLayout = view.findViewById(R.id.actionsLayout);
            actionPanelBottomLayoutHasSetup = false;
        }
        initActionPanelLayout();

        // 显示录音按钮
        switchToTextButtonInInputBar.setVisibility(View.GONE);
        switchToAudioButtonInInputBar.setVisibility(View.VISIBLE);

        // 文本录音按钮切换布局
        textAudioSwitchLayout = (FrameLayout) view.findViewById(R.id.switchLayout);
        if (isTextAudioSwitchShow) {
            textAudioSwitchLayout.setVisibility(View.VISIBLE);
        } else {
            textAudioSwitchLayout.setVisibility(View.GONE);
        }

        if(isDealerMode){
            switchToAudioButtonInInputBar.setVisibility(View.VISIBLE);//switchToAudioButtonInInputBar.setVisibility(View.GONE);// : 17/2/27  客服允许语音聊天
        }else{
            switchToAudioButtonInInputBar.setVisibility(View.VISIBLE);
        }
    }

    private void initInputBarListener() {
        switchToTextButtonInInputBar.setOnClickListener(clickListener);
        switchToAudioButtonInInputBar.setOnClickListener(clickListener);
        emojiButtonInInputBar.setOnClickListener(clickListener);
        sendMessageButtonInInputBar.setOnClickListener(clickListener);
        moreFuntionButtonInInputBar.setOnClickListener(clickListener);
    }

    private void initTextEdit() {
        messageEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        messageEditText.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //switchToTextLayout(true);
                    onTouchEditText();
                }
                return false;
            }
        });

        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                messageEditText.setHint("");
                checkSendButtonEnable(messageEditText);
            }
        });

        messageEditText.addTextChangedListener(new TextWatcher() {
            private int start;
            private int count;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.start = start;
                this.count = count;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSendButtonEnable(messageEditText);
                MoonUtil.replaceEmoticons(container.activity, s, start, count);
                int editEnd = messageEditText.getSelectionEnd();
                messageEditText.removeTextChangedListener(this);
                while (StringUtil.counterChars(s.toString()) > 5000 && editEnd > 0) {
                    s.delete(editEnd - 1, editEnd);
                    editEnd--;
                }
                messageEditText.setSelection(editEnd);
                messageEditText.addTextChangedListener(this);

                sendTypingCommand();
            }
        });
    }


    /**
     * 发送“正在输入”通知
     */
    private void sendTypingCommand() {
        if (container == null) {
            return;
        }
        if (container.account != null && container.account.equals(NimUIKit.getAccount())) {
            return;
        }

        if (container.sessionType == SessionTypeEnum.Team || container.sessionType == SessionTypeEnum.ChatRoom) {
            return;
        }

        if (System.currentTimeMillis() - typingTime > 5000L) {
            typingTime = System.currentTimeMillis();
            CustomNotification command = new CustomNotification();
            command.setSessionId(container.account);
            command.setSessionType(container.sessionType);
            CustomNotificationConfig config = new CustomNotificationConfig();
            config.enablePush = false;
            config.enableUnreadCount = false;
            command.setConfig(config);

            JSONObject json = new JSONObject();
            json.put("id", "1");
            command.setContent(json.toString());

            NIMClient.getService(MsgService.class).sendCustomNotification(command);
        }
    }

    //own
    void switchSoftInputMode(boolean isAdjustPan) {
        if (isAdjustPan)
            this.container.activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//固定
        else
            this.container.activity.getWindow()
                    .setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//可变
    }

    public void showSoftInput(boolean show) {
        if (show) {
            switchSoftInputMode(true);
            showKeyBoard(this.container.activity);
            switchSoftInputMode(false);
        } else {
            switchSoftInputMode(true);
            closeKeyBoard(this.container.activity);
            switchSoftInputMode(false);
        }
    }

    /**
     * 关闭键盘
     *
     * @param con
     */
    public static void closeKeyBoard(Activity con) {
        InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);

        try {
            View view = con.getCurrentFocus();
            if(view == null)
                return ;
            IBinder ibinder= con.getCurrentFocus().getWindowToken();
            if(ibinder != null)
                imm.hideSoftInputFromWindow(ibinder, InputMethodManager.RESULT_UNCHANGED_SHOWN);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示软键盘
     * @param con
     */
    public static void showKeyBoard(Activity con) {
        showKeyBoard(con,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    public static void showKeyBoard(Activity con,int flag) {
        try {
            InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(con.getCurrentFocus(), flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void switchFaceShow() {
        // 如果礼物界面正在展示
        if (mIsShowMore) {
            actionPanelBottomLayout.setVisibility(View.GONE);
            moreFuntionButtonInInputBar.setBackgroundResource(R.drawable.nim_message_button_bottom_add_selector);
            emoticonPickerView.setVisibility(View.VISIBLE);
            emoticonPickerView.show(this);
            emojiButtonInInputBar.setBackgroundResource(R.drawable.nim_message_button_bottom_text_selector);
            mIsShowMore = false;
            mIsShowFace = true;
            return;
        }
        else
        {
            if (mIsShowFace) {
                emoticonPickerView.setVisibility(View.GONE);
                messageEditText.requestFocus();
                showSoftInput(true);
                emojiButtonInInputBar.setBackgroundResource(R.drawable.nim_message_button_bottom_emoji_selector);
            } else {
                switchToAudioShow(false,false);
                showSoftInput(false);
                emoticonPickerView.setVisibility(View.VISIBLE);
                emoticonPickerView.show(this);
                emojiButtonInInputBar.setBackgroundResource(R.drawable.nim_message_button_bottom_text_selector);
            }
            if(mMsgListPanel!=null)
                mMsgListPanel.scrollToBottom();
        }
        mIsShowFace = !mIsShowFace;
    }

    void switchGiftShow() {
        // 如果表情界面正在展示
        if (mIsShowFace) {
            //m_rl_face_view.setVisibility(View.GONE);
            emojiButtonInInputBar.setBackgroundResource(R.drawable.nim_message_button_bottom_emoji_selector);
            emoticonPickerView.setVisibility(View.GONE);

            //m_rl_gift_view.setVisibility(View.VISIBLE);
            moreFuntionButtonInInputBar.setBackgroundResource(R.drawable.nim_message_button_bottom_text_selector);
            actionPanelBottomLayout.setVisibility(View.VISIBLE);

            mIsShowFace = false;
            mIsShowMore = true;
            return;
        } else {
            if (mIsShowMore) {
                actionPanelBottomLayout.setVisibility(View.GONE);
                messageEditText.requestFocus();
                showSoftInput(true);
                moreFuntionButtonInInputBar.setBackgroundResource(R.drawable.nim_message_button_bottom_add_selector);
            } else {
                switchToAudioShow(false,false);
                showSoftInput(false);
                actionPanelBottomLayout.setVisibility(View.VISIBLE);
                moreFuntionButtonInInputBar.setBackgroundResource(R.drawable.nim_message_button_bottom_text_selector);
            }

            if(mMsgListPanel!=null)
                mMsgListPanel.scrollToBottom();
        }
        mIsShowMore = !mIsShowMore;
    }

    // 切换成音频，收起键盘，按钮切换成键盘
    void switchToAudioShow(boolean audio,boolean keyboard) {

        if(mIsShowAudio == audio)
            return;

        messageEditText.setVisibility(audio?View.GONE:View.VISIBLE);
        audioRecordBtn.setVisibility(audio?View.VISIBLE:View.GONE);

        switchToAudioButtonInInputBar.setVisibility(audio?View.GONE:View.VISIBLE);
        switchToTextButtonInInputBar.setVisibility(audio?View.VISIBLE:View.GONE);

        if(audio)
            clearInputFaceGift();
        else
        {
            if(keyboard)
            {
                messageEditText.requestFocus();
                showSoftInput(true);

                if(mMsgListPanel!=null)
                    mMsgListPanel.scrollToBottom();
            }
        }

        mIsShowAudio = audio;
    }

    void onTouchEditText(){
        if (mIsShowFace)
            switchFaceShow();
        else if (mIsShowMore)
            switchGiftShow();
        else
        {
            messageEditText.requestFocus();
            showSoftInput(true);

            if(mMsgListPanel!=null)
                mMsgListPanel.scrollToBottom();
        }
    }

    void clearInputFaceGift() {
        switchToAudioShow(false, false);

        if (mIsShowFace) {
            emoticonPickerView.setVisibility(View.GONE);

            emojiButtonInInputBar.setBackgroundResource(R.drawable.nim_message_button_bottom_emoji_selector);
            mIsShowFace = false;
        }
        if (mIsShowMore) {
            actionPanelBottomLayout.setVisibility(View.GONE);

            moreFuntionButtonInInputBar.setBackgroundResource(R.drawable.nim_message_button_bottom_add_selector);
            mIsShowMore = false;
        }
        showSoftInput(false);
    }

    ListHelp mMsgListPanel;
    boolean mIsShowFace = false;
    boolean mIsShowMore = false;
    boolean mIsShowAudio = false;
    //own
    /**
     * ************************* 键盘布局切换 *******************************
     */

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == switchToTextButtonInInputBar) {
                //switchToTextLayout(true);// 显示文本发送的布局
                switchToAudioShow(false,true);
                onTouchEditText();
            } else if (v == sendMessageButtonInInputBar) {
                onTextMessageSendButtonPressed();
            } else if (v == switchToAudioButtonInInputBar) {
                //switchToAudioLayout();
                switchToAudioShow(true,true);
            } else if (v == moreFuntionButtonInInputBar) {
                //toggleActionPanelLayout();
                switchGiftShow();
            } else if (v == emojiButtonInInputBar) {
                //toggleEmojiLayout();
                switchFaceShow();
            }
        }
    };

    // 点击edittext，切换键盘和更多布局
    private void switchToTextLayout(boolean needShowInput) {
        hideEmojiLayout();
        hideActionPanelLayout();

        audioRecordBtn.setVisibility(View.GONE);
        messageEditText.setVisibility(View.VISIBLE);
        switchToTextButtonInInputBar.setVisibility(View.GONE);
        if(!isDealerMode){
            switchToAudioButtonInInputBar.setVisibility(View.VISIBLE);
        }

        messageInputBar.setVisibility(View.VISIBLE);

        if (needShowInput) {
            uiHandler.postDelayed(showTextRunnable, SHOW_LAYOUT_DELAY);
        } else {
            hideInputMethod();
        }
    }

    private static SensitiveFilter _filter;
    public static void setFilter(SensitiveFilter filter) {
        _filter = filter;
    }
    // 发送文本消息
    private void onTextMessageSendButtonPressed() {
        IMMessage textMessage;
        String toSendStr = messageEditText.getText().toString();
        String text = _filter == null ? toSendStr : _filter.doFilter(toSendStr);
        if (container.sessionType == SessionTypeEnum.ChatRoom) {
            textMessage = ChatRoomMessageBuilder.createChatRoomTextMessage(container.account, text);
        } else {
            textMessage = MessageBuilder.createTextMessage(container.account, container.sessionType, text);
        }

        if (container.proxy.sendMessage(textMessage)) {
            restoreText(true);
        }
    }

    // 切换成音频，收起键盘，按钮切换成键盘
    private void switchToAudioLayout() {
        messageEditText.setVisibility(View.GONE);
        audioRecordBtn.setVisibility(View.VISIBLE);
        hideInputMethod();
        hideEmojiLayout();
        hideActionPanelLayout();

        switchToAudioButtonInInputBar.setVisibility(View.GONE);
        switchToTextButtonInInputBar.setVisibility(View.VISIBLE);
    }

    // 点击“+”号按钮，切换更多布局和键盘
    private void toggleActionPanelLayout() {
        if (actionPanelBottomLayout == null || actionPanelBottomLayout.getVisibility() == View.GONE) {
            showActionPanelLayout();
        } else {
            hideActionPanelLayout();
        }
    }

    // 点击表情，切换到表情布局
    private void toggleEmojiLayout() {
        if (emoticonPickerView == null || emoticonPickerView.getVisibility() == View.GONE) {
            showEmojiLayout();
        } else {
            hideEmojiLayout();
        }
    }

    // 隐藏表情布局
    private void hideEmojiLayout() {
        uiHandler.removeCallbacks(showEmojiRunnable);
        if (emoticonPickerView != null) {
            emoticonPickerView.setVisibility(View.GONE);
        }
        onEditModeChanged(false);
    }

    boolean isEditMode;//是否是输入模式

    public void onEditModeChanged(boolean is) {
        if (isEditMode != is) {
            isEditMode = is;
            if (mOnEditModeListener != null) {
                mOnEditModeListener.onEditMode(isEditMode);
            }
        }
    }

    // 隐藏更多布局
    private void hideActionPanelLayout() {
        uiHandler.removeCallbacks(showMoreFuncRunnable);
        if (actionPanelBottomLayout != null) {
            actionPanelBottomLayout.setVisibility(View.GONE);
        }
        //
        onEditModeChanged(false);
    }

    // 隐藏键盘布局
    private void hideInputMethod() {
        isKeyboardShowed = false;
        uiHandler.removeCallbacks(showTextRunnable);
        InputMethodManager imm = (InputMethodManager) container.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
        messageEditText.clearFocus();
        onEditModeChanged(false);
    }

    // 隐藏语音布局
    private void hideAudioLayout() {
        audioRecordBtn.setVisibility(View.GONE);
        messageEditText.setVisibility(View.VISIBLE);
        switchToTextButtonInInputBar.setVisibility(View.VISIBLE);
        switchToAudioButtonInInputBar.setVisibility(View.GONE);
    }

    // 显示表情布局
    private void showEmojiLayout() {
        hideInputMethod();
        hideActionPanelLayout();
        hideAudioLayout();

        messageEditText.requestFocus();
        uiHandler.postDelayed(showEmojiRunnable, 200);
        emoticonPickerView.setVisibility(View.VISIBLE);
        emoticonPickerView.show(this);
        //
        onEditModeChanged(true);
    }

    // 初始化更多布局
    private void addActionPanelLayout() {
        if (actionPanelBottomLayout == null) {
            View.inflate(container.activity, R.layout.nim_message_activity_actions_layout, messageActivityBottomLayout);
            actionPanelBottomLayout = view.findViewById(R.id.actionsLayout);
            actionPanelBottomLayoutHasSetup = false;
        }
        initActionPanelLayout();
    }

    // 显示键盘布局
    private void showInputMethod(EditText editTextMessage) {
        editTextMessage.requestFocus();
        //如果已经显示,则继续操作时不需要把光标定位到最后
        if (!isKeyboardShowed) {
            editTextMessage.setSelection(editTextMessage.getText().length());
            isKeyboardShowed = true;
        }

        InputMethodManager imm = (InputMethodManager) container.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextMessage, 0);

        container.proxy.onInputPanelExpand();
        //
        onEditModeChanged(true);
    }

    // 显示更多布局
    private void showActionPanelLayout() {
        addActionPanelLayout();
        hideEmojiLayout();
        hideInputMethod();

        uiHandler.postDelayed(showMoreFuncRunnable, SHOW_LAYOUT_DELAY);
        container.proxy.onInputPanelExpand();
        onEditModeChanged(true);
    }

    // 初始化具体more layout中的项目
    private void initActionPanelLayout() {
        if (actionPanelBottomLayoutHasSetup) {
            return;
        }

        ActionsPanel.init(view, actions);
        actionPanelBottomLayoutHasSetup = true;
    }

    //刷新Action工具
    public void updateActionPanelLayout(List<BaseAction> baseActions) {
        this.actions = baseActions;
        if (actions != null && actions.size() > 0) {
            moreFuntionButtonInInputBar.setVisibility(View.VISIBLE);
            for (int i = 0; i < actions.size(); ++i) {
                actions.get(i).setIndex(i);
                actions.get(i).setContainer(container);
            }
        }
        LogUtil.d("ActionsPanel", "inputpanel updateActionPanelLayout");
        ActionsPanel.updateActionPanelLayout(view, actions);
    }

    private Runnable showEmojiRunnable = new Runnable() {
        @Override
        public void run() {
            emoticonPickerView.setVisibility(View.VISIBLE);
        }
    };

    private Runnable showMoreFuncRunnable = new Runnable() {
        @Override
        public void run() {
            actionPanelBottomLayout.setVisibility(View.VISIBLE);
        }
    };

    private Runnable showTextRunnable = new Runnable() {
        @Override
        public void run() {
            showInputMethod(messageEditText);
        }
    };

    private void restoreText(boolean clearText) {
        if (clearText) {
            messageEditText.setText("");
        }

        checkSendButtonEnable(messageEditText);
    }

    /**
     * 显示发送或更多
     *
     * @param editText
     */
    private void checkSendButtonEnable(EditText editText) {
        String textMessage = editText.getText().toString();
        if (!TextUtils.isEmpty(StringUtil.removeBlanks(textMessage)) && editText.hasFocus()) {
            moreFuntionButtonInInputBar.setVisibility(View.GONE);
            sendMessageButtonInInputBar.setVisibility(View.VISIBLE);
        } else {
            sendMessageButtonInInputBar.setVisibility(View.GONE);
//            moreFuntionButtonInInputBar.setVisibility(View.VISIBLE);
            checkMoreFuntionShow();
        }
    }

    /**
     * *************** IEmojiSelectedListener ***************
     */
    @Override
    public void onEmojiSelected(String key) {
        Editable mEditable = messageEditText.getText();
        if (key.equals("/DEL")) {
            messageEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            int start = messageEditText.getSelectionStart();
            int end = messageEditText.getSelectionEnd();
            start = (start < 0 ? 0 : start);
            end = (start < 0 ? 0 : end);
            mEditable.replace(start, end, key);
        }
    }

    private Runnable hideAllInputLayoutRunnable;

    @Override
    public void onStickerSelected(String category, String item) {
        LogUtil.i("InputPanel", "onStickerSelected, category =" + category + ", sticker =" + item);
        if (customization != null) {
            MsgAttachment attachment = customization.createStickerAttachment(category, item);
            IMMessage stickerMessage = MessageBuilder.createCustomMessage(container.account, container.sessionType, "贴图消息", attachment);
            container.proxy.sendMessage(stickerMessage);
        }
    }

    /**
     * 隐藏所有输入布局
     */
    private void hideAllInputLayout(boolean immediately) {
//        if (hideAllInputLayoutRunnable == null) {
//            hideAllInputLayoutRunnable = new Runnable() {
//
//                @Override
//                public void run() {
//                    hideInputMethod();
//                    hideActionPanelLayout();
//                    hideEmojiLayout();
//                }
//            };
//        }
//        long delay = immediately ? 0 : ViewConfiguration.getDoubleTapTimeout();
//        uiHandler.postDelayed(hideAllInputLayoutRunnable, delay);

        clearInputFaceGift();
    }

    /**
     * ****************************** 语音 ***********************************
     */
    private void initAudioRecordButton() {
        audioRecordBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touched = true;
                    initAudioRecord();
                    onStartAudioRecord();
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                        || event.getAction() == MotionEvent.ACTION_UP) {
                    touched = false;
                    onEndAudioRecord(isCancelled(v, event));
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    touched = false;
                    cancelAudioRecord(isCancelled(v, event));
                }

                return false;
            }
        });
    }

    // 上滑取消录音判断
    private static boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth()
                || event.getRawY() < location[1] - 40) {
            return true;
        }

        return false;
    }

    /**
     * 初始化AudioRecord
     */
    private void initAudioRecord() {
        if (audioMessageHelper == null) {
            audioMessageHelper = new AudioRecorder(container.activity, RecordType.AAC, AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND, this);
        }
    }

    /**
     * 开始语音录制
     */
    private void onStartAudioRecord() {
        container.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        started = audioMessageHelper.startRecord();
        cancelled = false;
        if (started == false) {
            Toast.makeText(container.activity, R.string.recording_init_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!touched) {
            return;
        }

        audioRecordBtn.setText(R.string.record_audio_end);
//        audioRecordBtn.setBackgroundResource(R.drawable.nim_message_input_edittext_box_pressed);
        audioRecordBtn.setBackgroundResource(R.drawable.nim_message_input_edittext_box_pressed);

        updateTimerTip(false); // 初始化语音动画状态
        playAudioRecordAnim();
    }

    /**
     * 结束语音录制
     *
     * @param cancel
     */
    private void onEndAudioRecord(boolean cancel) {
        container.activity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        audioMessageHelper.completeRecord(cancel);
        audioRecordBtn.setText(R.string.record_audio);
        audioRecordBtn.setBackgroundResource(R.drawable.nim_message_input_edittext_box);
        stopAudioRecordAnim();
    }

    /**
     * 取消语音录制
     *
     * @param cancel
     */
    private void cancelAudioRecord(boolean cancel) {
        // reject
        if (!started) {
            return;
        }
        // no change
        if (cancelled == cancel) {
            return;
        }

        cancelled = cancel;
        updateTimerTip(cancel);
    }

    /**
     * 正在进行语音录制和取消语音录制，界面展示
     *
     * @param cancel
     */
    private void updateTimerTip(boolean cancel) {
        if (cancel) {
            timerTip.setText(R.string.recording_cancel_tip);
            timerTipContainer.setBackgroundResource(R.drawable.nim_cancel_record_red_bg);
        } else {
            timerTip.setText(R.string.recording_cancel);
            timerTipContainer.setBackgroundResource(0);
        }
    }

    /**
     * 开始语音录制动画
     */
    private void playAudioRecordAnim() {
        audioAnimLayout.setVisibility(View.VISIBLE);
        time.setBase(SystemClock.elapsedRealtime());
        time.start();
    }

    /**
     * 结束语音录制动画
     */
    private void stopAudioRecordAnim() {
        audioAnimLayout.setVisibility(View.GONE);
        time.stop();
        time.setBase(SystemClock.elapsedRealtime());
    }

    // 录音状态回调
    @Override
    public void onRecordReady() {

    }

    @Override
    public void onRecordStart(File audioFile, RecordType recordType) {

    }

    @Override
    public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
        IMMessage audioMessage = MessageBuilder.createAudioMessage(container.account, container.sessionType, audioFile, audioLength);
        container.proxy.sendMessage(audioMessage);
    }

    @Override
    public void onRecordFail() {

    }

    @Override
    public void onRecordCancel() {

    }

    @Override
    public void onRecordReachedMaxTime(final int maxTime) {
        stopAudioRecordAnim();
        EasyAlertDialogHelper.createOkCancelDiolag(container.activity, "", container.activity.getString(R.string.recording_max_time), false, new EasyAlertDialogHelper.OnDialogActionListener() {
            @Override
            public void doCancelAction() {
            }

            @Override
            public void doOkAction() {
                audioMessageHelper.handleEndRecord(true, maxTime);
            }
        }).show();
    }

    public boolean isRecording() {
        return audioMessageHelper != null && audioMessageHelper.isRecording();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        int index = (requestCode << 16) >> 24;
        if (index != 0) {
            index--;
            if (index < 0 | index >= actions.size()) {
                LogUtil.i(TAG, "request code out of actions' range");
                return;
            }
            BaseAction action = actions.get(index);
            if (action != null) {
                action.onActivityResult(requestCode & 0xff, resultCode, data);
            }
        }
    }
}
