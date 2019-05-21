package com.htgames.nutspoker.view.record;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.log.LogUtil;

/**
 * 排名View 水平的，可能显示空间不够，新加一个垂直的排名组件RankViewV
 * where used:
 * 1: CirclePaijuView
 * 2: list_game_record_item_new牌局信息adapter的xml里面(用横的)
 * 3: mtt大厅玩家页面的PlayerViewHolder的list_match_player_item(用竖的)
 * 4: 战绩-牌局详情-RecordMemberListAdapter的list_record_member_item(用竖的)
 * 4: 战绩-牌局详情-RecordMemberViewHolder的viewholder_record_member(用竖的)
 * 5: GameOverView的view_app_gameover_item暂时横的
 * 6: GameStatusVH的viewholder_app_game_status_new 暂时横的
 * 7: RecordMemberListAdapter的list_record_member_item废弃掉了，也用竖的
 * 8:
 * 9:
 * 10:
 */
public class RankView extends LinearLayout {
    private final static String TAG = "RankView";
    float textSize = 9;
    int width = 0;
    int height = 0;
    public TextView tv_record_match_rank_tag_vertical;
    View rl_rankview;
    ImageView iv_rank;

    public RankView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RankView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RankView, defStyleAttr, 0);
        try {
            width = typedArray.getDimensionPixelOffset(R.styleable.RankView_rankWidth, 0);
            textSize = typedArray.getDimensionPixelSize(R.styleable.RankView_rankTextSize, -1);
        } finally {
            typedArray.recycle();
        }
        LogUtil.i(TAG, "width :" + width);
        LayoutInflater.from(context).inflate(R.layout.view_rankview_vertical, this, true);
        tv_record_match_rank_tag_vertical = (TextView) findViewById(R.id.tv_record_match_rank_tag_vertical);
        rl_rankview = findViewById(R.id.rl_rankview);
        iv_rank = (ImageView) findViewById(R.id.iv_rank);
//        tv_record_match_rank_tag.setMinHeight(width);
//        tv_record_match_rank_tag.setMinWidth(width);
//        //
//        if (textSize != -1) {
//            this.textSize = ScreenUtil.px2sp(context, textSize);
//            tv_record_match_rank_tag.setTextSize(textSize);
//        }
//        //
//        ViewGroup.LayoutParams params = rl_rankview.getLayoutParams();
//        if (params == null) {
//            params = new ViewGroup.LayoutParams(width, width * 72 / 58);
//        } else {
//            params.width = width;
//            params.height = width * 72 / 58;
//        }
//        rl_rankview.setLayoutParams(params);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.rank_view_width), ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.gravity = Gravity.CENTER;
//        addView(view, lp);
    }

    /**
     * 设置SNG排名View
     *
     * @param rank
     */
    public void setRankTagView(int rank) {
//        tv_record_match_rank_tag.setBackgroundResource(RecordHelper.getRecordSngRankImage(rank, true));
        if (rank <= 0) {
            tv_record_match_rank_tag_vertical.setVisibility(View.GONE);
            setVisibility(GONE);
            return;
        } else if (rank == 1) {
            tv_record_match_rank_tag_vertical.setVisibility(View.GONE);
            iv_rank.setVisibility(VISIBLE);
            iv_rank.setImageResource(R.mipmap.mtt_room_rank_1);
        } else if (rank == 2) {
            tv_record_match_rank_tag_vertical.setVisibility(View.GONE);
            iv_rank.setVisibility(VISIBLE);
            iv_rank.setImageResource(R.mipmap.mtt_room_rank_2);
        } else if (rank == 3) {
            tv_record_match_rank_tag_vertical.setVisibility(View.GONE);
            iv_rank.setVisibility(VISIBLE);
            iv_rank.setImageResource(R.mipmap.mtt_room_rank_3);
        } else if (rank > 3) {
            int strokeWidth = BaseTools.dip2px(getContext(), 1); // 3dp 边框宽度
            int roundRadius = width / 2; // 7dp 圆角半径
            int strokeColor = getContext().getResources().getColor(R.color.match_rank_common_stroke_color);//边框颜色
            int fillColor = getContext().getResources().getColor(android.R.color.transparent);//边框颜色
            GradientDrawable gd = new GradientDrawable();//创建drawable
            gd.setColor(fillColor);
            gd.setCornerRadius(roundRadius);
            gd.setStroke(strokeWidth, strokeColor);
            tv_record_match_rank_tag_vertical.setVisibility(View.VISIBLE);
            iv_rank.setVisibility(GONE);
            ViewGroup.LayoutParams layoutParams = tv_record_match_rank_tag_vertical.getLayoutParams();
            if (rank <= 3) {
                tv_record_match_rank_tag_vertical.setText("");
                layoutParams.height = width * 72 / 58;
            } else {
                tv_record_match_rank_tag_vertical.setText("" + rank);
                layoutParams.height = width;
            }
        }
        setVisibility(VISIBLE);
        tv_record_match_rank_tag_vertical.setGravity(Gravity.CENTER);
        tv_record_match_rank_tag_vertical.setText("" + rank);
    }
}
