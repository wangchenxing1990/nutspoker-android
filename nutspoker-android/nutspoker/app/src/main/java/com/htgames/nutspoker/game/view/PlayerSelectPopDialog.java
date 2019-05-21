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
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.constants.GameConstants;
import com.will.common.view.wheelview.ArrayWheelAdapter;
import com.will.common.view.wheelview.OnWheelChangedListener;
import com.will.common.view.wheelview.WheelView;

/**
 * Created by 20150726 on 2016/6/3.
 */
public class PlayerSelectPopDialog extends Dialog {
    private final static String TAG = "WheelViewPop";
    private View rootView;
    private Context mContext;
    WheelView mPlayerWheelView;
    String[] players;
    OnSelectListener mOnSelectListener;
    TextView btn_date_select_confim;
    TextView tv_table_mode;

    public PlayerSelectPopDialog(Activity activity) {
        super(activity, R.style.PopupBottomDialog);
        this.mContext = activity.getApplicationContext();
        initData();
        init();
    }

    private void initData() {
        int size = GameConstants.sngMatchPlayers.length;
        players = new String[size];
        for (int i = 0; i < size; i++) {
            players[i] = "" + GameConstants.sngMatchPlayers[i];
        }
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
        rootView = LayoutInflater.from(context).inflate(R.layout.pop_game_create_player_select, null);
        btn_date_select_confim = (TextView) rootView.findViewById(R.id.btn_date_select_confim);
        tv_table_mode = (TextView) rootView.findViewById(R.id.tv_table_mode);
        mPlayerWheelView = (WheelView) rootView.findViewById(R.id.mPlayerWheelView);
//        mPlayerWheelView.setLabel(context.getString(R.string.person));
        mPlayerWheelView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (GameConstants.sngMatchPlayers[newValue] > 9) {
                    tv_table_mode.setText(R.string.game_create_table_multi);
                } else {
                    tv_table_mode.setText(R.string.game_create_table_single);
                }
            }
        });
        setContentView(rootView);
        initWheel();
        btn_date_select_confim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSelectListener != null) {
                    String player = players[mPlayerWheelView.getCurrentItem()];
                    mOnSelectListener.onSelect(Integer.valueOf(player));
                }
                dismiss();
            }
        });
    }

    public void initWheel() {
        mPlayerWheelView.setAdapter(new ArrayWheelAdapter<String>(players));
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
    }

    public interface OnSelectListener {
        public void onSelect(int player);
    }
}