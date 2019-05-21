package com.htgames.nutspoker.chat.picker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.media.picker.activity.PickImageActivity;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.LinkedList;
import java.util.List;

/**
 */
public class PickImageHelper {

    public static class PickImageOption {
        /**
         * 图片选择器标题
         */
        public int titleResId = R.string.choose;

        /**
         * 是否多选
         */
        public boolean multiSelect = true;

        /**
         * 最多选多少张图（多选时有效）
         */
        public int multiSelectMaxCount = 9;

        /**
         * 是否进行图片裁剪
         */
        public boolean crop = false;

        /**
         * 图片裁剪的宽度（裁剪时有效）
         */
        public int cropOutputImageWidth = 720;

        /**
         * 图片裁剪的高度（裁剪时有效）
         */
        public int cropOutputImageHeight = 720;

        /**
         * 图片选择保存路径
         */
        public String outputPath = StorageUtil.getWritePath(StringUtil.get32UUID() + ".jpg", StorageType.TYPE_TEMP);
    }

    /**
     * 打开图片选择器
     */
    public static void pickImage(final Context context, final int requestCode, final PickImageOption option) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialog_default_style);
        final AlertDialog dialogTest = builder.create();

        final int listMarginTop = ScreenUtil.dp2px(context, 10);
        final int textPadding = ScreenUtil.dp2px(context, 10);
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.dialog_easy_alert_with_listview, null, false);
        rootView.setBackgroundColor(context.getResources().getColor(R.color.register_page_bg_color));
        View titleViewBg = rootView.findViewById(R.id.easy_dialog_title_view);
        titleViewBg.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.common_column_bg));
        TextView titleTV = (TextView) rootView.findViewById(R.id.easy_dialog_title_text_view);
        titleTV.setText(option.titleResId);
        titleTV.setTextColor(context.getResources().getColor(R.color.login_grey_color));
        titleTV.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.common_column_bg));
        titleTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        ListView listView = (ListView) rootView.findViewById(R.id.easy_dialog_list_view);
        final List<Pair<String, Integer>> itemTextList = new LinkedList<Pair<String, Integer>>();
        itemTextList.add(new Pair<String, Integer>(context.getString(R.string.input_panel_take), R.color.black));
        itemTextList.add(new Pair<String, Integer>(context.getString(R.string.choose_from_photo_album), R.color.black));
        BaseAdapter listAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return itemTextList.size();
            }
            @Override
            public Object getItem(int position) {
                return itemTextList.get(position);
            }
            @Override
            public long getItemId(int position) {
                return position;
            }
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                TextView textView = new TextView(context);
                Pair<String, Integer> pair = (Pair<String, Integer>) itemTextList.get(position);
                textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.common_column_bg));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                textView.setTextColor(Color.BLACK);
                textView.setPadding(textPadding, textPadding, textPadding, textPadding);
                textView.setText(pair.first);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int from = position == 0 ? PickImageActivity.FROM_CAMERA : PickImageActivity.FROM_LOCAL;
                        if (!option.crop) {
                            PickImageActivity.start((Activity) context, requestCode, from, option.outputPath, option.multiSelect, option.multiSelectMaxCount, false, false, 0, 0);
                        } else {
                            PickImageActivity.start((Activity) context, requestCode, from, option.outputPath, false, 1, false, true, option.cropOutputImageWidth, option.cropOutputImageHeight);
                        }
                        if (dialogTest.isShowing()) {
                            dialogTest.dismiss();
                        }
                    }
                });
                return textView;
            }
        };
        ((LinearLayout.LayoutParams) listView.getLayoutParams()).setMargins(0, listMarginTop, 0, listMarginTop);
        listView.setDividerHeight(ScreenUtil.dp2px(context, 1));
        listView.setAdapter(listAdapter);
        dialogTest.show();
        Window windowTest = dialogTest.getWindow();
        android.view.WindowManager.LayoutParams lp = windowTest.getAttributes();
        lp.width = ScreenUtil.getScreenWidth(context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowTest.setWindowAnimations(R.style.PopupAnimation);
        windowTest.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        dialogTest.setContentView(rootView);
    }
}
