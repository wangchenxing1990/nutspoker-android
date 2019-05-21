package com.htgames.nutspoker.view.record;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;

/**
 * Created by 周智慧 on 17/2/6.
 * 排名View 垂直的，可能显示空间不够，新加一个垂直的排名组件RankViewV
 */

public class RankViewV extends LinearLayout {
    TextView tv_record_match_rank_tag_vertical;
    ImageView iv_rank;
    public RankViewV(Context context) {
        this(context, null);
    }

    public RankViewV(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RankViewV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RankViewV(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RankView, defStyleAttr, 0);
        LayoutInflater.from(context).inflate(R.layout.view_rankview_vertical, this, true);
        tv_record_match_rank_tag_vertical = (TextView) findViewById(R.id.tv_record_match_rank_tag_vertical);
        iv_rank = (ImageView) findViewById(R.id.iv_rank);
    }

    /**
     * 设置SNG排名View
     *
     * @param rank
     */
    public void setRankTagView(int rank) {
//        tv_record_match_rank_tag_vertical.setBackgroundResource(RecordHelper.getRecordSngRankImage(rank, true));
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
            tv_record_match_rank_tag_vertical.setVisibility(View.VISIBLE);
            iv_rank.setVisibility(GONE);
            tv_record_match_rank_tag_vertical.setText("" + rank);
        }
        setVisibility(VISIBLE);
    }

    public void setOnlyNum(int rank) {//不显示奖杯图片
        if (rank <= 0) {
            tv_record_match_rank_tag_vertical.setVisibility(View.GONE);
            setVisibility(GONE);
            return;
        } else {
            tv_record_match_rank_tag_vertical.setVisibility(View.VISIBLE);
            iv_rank.setVisibility(GONE);
            tv_record_match_rank_tag_vertical.setText("" + rank);
        }
        setVisibility(VISIBLE);
    }
}
