package com.htgames.nutspoker.ui.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class ListBaseAdapter<T> extends BaseAdapter {
	public Context context;
	public List<T> list;
	public LayoutInflater inflater;
	public String keyWord;

	public ListBaseAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public ListBaseAdapter(Context context, List<T> list) {
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		if (list != null && list.size() != 0 && position < list.size()) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	/**
	 * 设置关键字，用于显示成红色
	 */
	public void setKeyWord(String keyWord){
		this.keyWord = keyWord;
		notifyDataSetChanged();
	}

	//显示关键字
	public void setKeywordText(TextView tv , String text){
		if(!TextUtils.isEmpty(keyWord) && !TextUtils.isEmpty(text)){
			if(text.contains(keyWord)){
				//如果包含这个字，特殊处理
				int start = text.indexOf(keyWord);
				int end = start + keyWord.length();
				String newMessageInfo = text.substring(0 , start) + "<font color='#b79e5e'><b>" + text.substring(start , end)  + "</b></font>" + text.substring(end , text.length());
				tv.setText(Html.fromHtml(newMessageInfo));
//                tv.setText(text);
			}else{
				tv.setText(text);
			}
		} else{
			tv.setText(text);
		}
	}

	public String getString(int id) {
		return context.getString(id);
	}

	public String getString(int id , Object... formatArgs) {
		return context.getString(id, formatArgs);
	}
}