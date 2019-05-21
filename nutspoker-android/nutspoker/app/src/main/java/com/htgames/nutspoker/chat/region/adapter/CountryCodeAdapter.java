package com.htgames.nutspoker.chat.region.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.CountryEntity;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 */
public class CountryCodeAdapter extends ListBaseAdapter<CountryEntity> {
    protected final HashMap<String, Integer> indexes = new HashMap<>();

    public CountryCodeAdapter(Context context, ArrayList<CountryEntity> list) {
        super(context, list);
        updateIndexes();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_country_code_item, null);
            holder.tv_country_abc = (TextView) view.findViewById(R.id.tv_country_abc);
            holder.tv_country_name = (TextView) view.findViewById(R.id.tv_country_name);
            holder.tv_country_code = (TextView) view.findViewById(R.id.tv_country_code);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        CountryEntity countryEntity = (CountryEntity) getItem(position);
        holder.tv_country_name.setText(countryEntity.countryName);
        holder.tv_country_code.setText("+" + countryEntity.countryCode);
        // 当前字母
        String currentStr = getAlpha(countryEntity.sortKey);
        // 前面的字母
        String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).sortKey) : " ";

        if (!previewStr.equals(currentStr)) {
            holder.tv_country_abc.setText(currentStr);
            holder.tv_country_abc.setVisibility(View.VISIBLE);
        } else {
            holder.tv_country_abc.setVisibility(View.GONE);
        }
        return view;
    }

    protected final class ViewHolder {
        public TextView tv_country_abc;
        public TextView tv_country_code;
        public TextView tv_country_name;
    }

    public final LivIndex createLivIndex(ListView lv, LetterIndexView liv, TextView tvHit) {
        return new LivIndex(null, lv, liv, tvHit, getIndexes());
    }

    private Map<String, Integer> getIndexes() {
        return this.indexes;
    }

    public void updateIndexes() {
        // CLEAR
        this.indexes.clear();
//        // SET
//        this.indexes.putAll(indexes);
        for (int i = 0; i < list.size(); i++) {
            // 得到字母
            String name = getAlpha(list.get(i).sortKey);
            if (!indexes.containsKey(name)) {
                indexes.put(name, i);
            }
        }
    }

    /**
     * 获取首字母
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式匹配
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase(); // 将小写字母转换为大写
        } else {
            return "#";
        }
    }
}
