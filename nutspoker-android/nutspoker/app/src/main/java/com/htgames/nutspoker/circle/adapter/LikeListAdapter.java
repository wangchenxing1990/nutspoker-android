package com.htgames.nutspoker.circle.adapter;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.circle.bean.LikeItem;
import com.htgames.nutspoker.circle.spannable.CircleMovementMethod;
import com.htgames.nutspoker.circle.spannable.NameClickable;
import com.htgames.nutspoker.circle.view.LikeListView;
import java.util.List;

/**
 *
 */
public class LikeListAdapter {
    private LikeListView mLikeListView;
    private List<LikeItem> likes;

    public LikeListAdapter(List<LikeItem> likes){
        this.likes = likes;
    }

    public List<LikeItem> getDatas() {
        return likes;
    }

    @NonNull
    public void bindListView(LikeListView listview) {
        mLikeListView = listview;
    }


    public int getCount() {
        if (likes != null && likes.size() > 0) {
            return likes.size();
        }
        return 0;
    }

    public Object getItem(int position) {
        if (likes != null && likes.size() > position) {
            return likes.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public void notifyDataSetChanged() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (likes != null && likes.size() > 0) {
            //添加点赞图标
            builder.append(setImageSpan());
            //builder.append("  ");
            LikeItem item = null;
            for (int i = 0; i < likes.size(); i++) {
                item = likes.get(i);
                if (item != null) {
                    builder.append(setClickableSpan(item.getUser().name, i));
                    if (i != likes.size() - 1) {
                        builder.append(", ");
                    }
                }
            }

        }
        mLikeListView.setText(builder);
        mLikeListView.setMovementMethod(new CircleMovementMethod(R.color.circle_name_selector_color));
    }

    @NonNull
    private SpannableString setClickableSpan(String textStr, int position) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new NameClickable(mLikeListView.getSpanClickListener(), position), 0, subjectSpanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

    private SpannableString setImageSpan() {
        String text = "  ";
        SpannableString imgSpanText = new SpannableString(text);
        imgSpanText.setSpan(new ImageSpan(DemoCache.getContext(), R.mipmap.message_system/*icon_circle_like_unchecked*/, DynamicDrawableSpan.ALIGN_BASELINE),  0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return imgSpanText;
    }

}
