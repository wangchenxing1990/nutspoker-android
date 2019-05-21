package com.htgames.nutspoker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;

/**
 * Created by 周智慧 on 2017/5/25.
 */

public class RecordGameBg extends View {
    int bgHeight = 165;//165dp
    int bottomSpace = 28;//
    public RecordGameBg(Context context) {
        this(context, null);
    }

    public RecordGameBg(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordGameBg(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        bgHeight = getContext().getResources().getDimensionPixelOffset(R.dimen.game_record_details_content_height);
        bottomSpace = ScreenUtil.dp2px(getContext(), bottomSpace);
        normalDrawable = new GradientDrawable();
        float radius = ScreenUtil.dp2px(getContext(), 145) / 2f;
        normalDrawable.setCornerRadii(new float[]{0, 0,
                0, 0,
                radius, radius,
                radius, radius});
        normalDrawable.setColor(getContext().getResources().getColor(R.color.bg_club_game_bottom));

        height = bgHeight - bottomSpace;
        starY = -height;
    }

    public void setInfo(GameEntity gameEntity) {
        int play_mode = gameEntity == null ? GameConstants.PLAY_MODE_TEXAS_HOLDEM : gameEntity.play_mode;
        int gameMode = gameEntity == null ? GameConstants.GAME_MODE_NORMAL : gameEntity.gameMode;
        int myStartColor = 0;
        int myEndColor = 0;
        if (play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM || play_mode == GameConstants.PLAY_MODE_OMAHA) {
            myStartColor = ContextCompat.getColor(getContext(), gameMode == GameConstants.GAME_MODE_NORMAL ? R.color.record_bg_normal_start : (gameMode == GameConstants.GAME_MODE_SNG ? R.color.record_bg_sng_start : R.color.record_bg_mtt_start));
            myEndColor = ContextCompat.getColor(getContext(), gameMode == GameConstants.GAME_MODE_NORMAL ? R.color.record_bg_normal_end : (gameMode == GameConstants.GAME_MODE_SNG ? R.color.record_bg_sng_end : R.color.record_bg_mtt_end));
        } else if (play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
            myStartColor = ContextCompat.getColor(getContext(), R.color.record_bg_pineapple_start);
            myEndColor = ContextCompat.getColor(getContext(), R.color.record_bg_pineapple_end);
        }
        GradientDrawable myBg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] { myStartColor, myEndColor});
        myBg.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        setBackgroundDrawable(myBg);
    }

    GradientDrawable normalDrawable;
    float starY = 0;
    float height = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if (starY > 0) {
            starY = 0;
        }
        float progress = (height + starY) / height;
        canvas.scale(1, progress);
        normalDrawable.setBounds(0, (int) starY, getWidth(), (int) (starY + height));
        normalDrawable.draw(canvas);
        starY += 20;
        canvas.restore();

        if (starY < 0) {
            invalidate();
        }
    }
}
