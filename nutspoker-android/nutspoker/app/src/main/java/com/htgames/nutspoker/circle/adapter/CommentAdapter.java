package com.htgames.nutspoker.circle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.circle.bean.CommentItem;
import com.htgames.nutspoker.circle.spannable.CircleMovementMethod;
import com.htgames.nutspoker.circle.spannable.NameClickListener;
import com.htgames.nutspoker.circle.spannable.NameClickable;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;
import java.util.ArrayList;

/**
 * Created by 20150726 on 2016/1/20.
 */
public class CommentAdapter extends ListBaseAdapter<CommentItem> {
    private ICommentItemClickListener commentItemClickListener;// 评论点击事件

    public CommentAdapter(Context context, ArrayList<CommentItem> list) {
        super(context, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_circle_comment_item, null);
            holder.commentTv = (TextView) convertView.findViewById(R.id.commentTv);
            holder.circleMovementMethod = new CircleMovementMethod(R.color.circle_name_selector_color, R.color.circle_name_selector_color);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CommentItem bean = list.get(position);
        String name = bean.getUser().name;
        String cid = bean.getCid();
        String toReplyName = "";
        if (bean.getToReplyUser() != null) {
            toReplyName = bean.getToReplyUser().name;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(setClickableSpan(name, 0));

        if (!TextUtils.isEmpty(toReplyName)) {
            builder.append(" ");
            builder.append(context.getString(R.string.circle_reply));
            builder.append(" ");
            builder.append(setClickableSpan(toReplyName, 1));
        }
        builder.append(": ");
        //转换表情字符
        String contentBodyStr = bean.getContent();
        //SpannableString contentSpanText = new SpannableString(contentBodyStr);
        //contentSpanText.setSpan(new UnderlineSpan(), 0, contentSpanText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(contentBodyStr);
        holder.commentTv.setText(builder);

        final CircleMovementMethod circleMovementMethod = holder.circleMovementMethod;
        holder.commentTv.setMovementMethod(circleMovementMethod);
        holder.commentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleMovementMethod.isPassToTv() && commentItemClickListener != null) {
                    commentItemClickListener.onItemClick(position);
                }
            }
        });
        return convertView;
    }

    @NonNull
    private SpannableString setClickableSpan(String textStr, int position) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new NameClickable(new NameClickListener(subjectSpanText, ""), position), 0, subjectSpanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        subjectSpanText.setSpan(new NameClickable(new OnNameClickListener() , position), 0, subjectSpanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

//    public class OnNameClickListener implements ISpanClick {
//
//        @Override
//        public void onClick(int position) {
//            CommentItem commentItem = list.get(position);
//            Toast.makeText(context , commentItem.getUser().getName() , Toast.LENGTH_SHORT).show();
//            if(context instanceof CircleActivity){
////                UserProfileActivity.start((CircleActivity) context) , );
//            }
//        }
//    }

    class ViewHolder {
        TextView commentTv;
        CircleMovementMethod circleMovementMethod;
    }

    public void setCommentClickListener(
            ICommentItemClickListener commentItemClickListener) {
        this.commentItemClickListener = commentItemClickListener;
    }

    public interface ICommentItemClickListener {
        public void onItemClick(int position);
    }
}
