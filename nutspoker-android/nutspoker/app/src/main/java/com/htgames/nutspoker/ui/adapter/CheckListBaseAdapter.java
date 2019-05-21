package com.htgames.nutspoker.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 带选择框的Adapter
 */
public class CheckListBaseAdapter<T> extends ListBaseAdapter {
    /**
     * 列表中CheckBox对应的状态MAP
     */
    public Map<Integer, Boolean> map_checkbox;
    /**
     * 是否选择列表已经显示
     */
    public boolean isSelectShow = false;
    OnSelectedChangeListener mOnSelectedChangeListener;

    public CheckListBaseAdapter(Context context, ArrayList<T> list) {
        super(context, list);
        map_checkbox = new HashMap<Integer, Boolean>();
    }

    public void setSelectedMode(boolean isSelectShow) {
        this.isSelectShow = isSelectShow;
        notifyDataSetChanged();
    }

    public boolean isSelectShowMode() {
        return isSelectShow;
    }

    /**
     * 点击CheckBox，用于外部点击ITEM 来用
     */
    public void clickCheckBox(int position, boolean checked) {
        map_checkbox.put(position, checked);
        if (map_checkbox.get(position)) {
            //选中
            mOnSelectedChangeListener.onAdd(list.get(position));
        } else {
            mOnSelectedChangeListener.onRemove(list.get(position));
        }
        selectCountChanged();
        notifyDataSetChanged();
    }

    public void clickCheckBox(int position) {
        map_checkbox.put(position, !isItemChecked(position));
        if (map_checkbox.get(position)) {
            //选中
            mOnSelectedChangeListener.onAdd(list.get(position));
        } else {
            mOnSelectedChangeListener.onRemove(list.get(position));
        }
        selectCountChanged();
        notifyDataSetChanged();
    }

    public boolean isItemChecked(int position) {
        if (map_checkbox.containsKey(position)) {
            return map_checkbox.get(position);
        }
        return false;
    }

    public void resetCheckedStatus() {
        Set<Integer> mapSet = map_checkbox.keySet();
        if (mapSet != null) {
            for (int position : mapSet) {
                map_checkbox.put(position, false);
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getSelectPositionList() {
        ArrayList<Integer> positionList = new ArrayList<Integer>();
        Set<Integer> mapSet = map_checkbox.keySet();
        if (mapSet != null) {
            for (int position : mapSet) {
                if (map_checkbox.get(position)) {
                    positionList.add(position);
                }
            }
        }
        return positionList;
    }

    public ArrayList<T> getSelectList() {
        ArrayList<T> selectList = new ArrayList<T>();
        Set<Integer> mapSet = map_checkbox.keySet();
        if (mapSet != null) {
            for (int position : mapSet) {
                if (map_checkbox.get(position)) {
                    selectList.add((T) list.get(position));
                }
            }
        }
        return selectList;
    }

    public int getSelectCount() {
        int count = 0;
        Set<Integer> mapSet = map_checkbox.keySet();
        if (mapSet != null) {
            for (int position : mapSet) {
                if (map_checkbox.get(position)) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    public void selectCountChanged() {
        if (mOnSelectedChangeListener != null) {
            mOnSelectedChangeListener.onChanged(getSelectCount());
        }
    }

    /**
     * 　设置选择状态改变监听
     */
    public void setOnSelectedChangeListener(OnSelectedChangeListener mOnSelectedChangeListener) {
        this.mOnSelectedChangeListener = mOnSelectedChangeListener;
    }

    /**
     * 选择状态改变监听
     */
    public interface OnSelectedChangeListener {
        public void onChanged(int selectdCount);

        public void onAdd(Object entity);

        public void onRemove(Object entity);
    }

    public class OnCheckListener implements View.OnClickListener {
        int position;

        public OnCheckListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            clickCheckBox(position, ((CheckBox) v).isChecked());
        }
    }
}
