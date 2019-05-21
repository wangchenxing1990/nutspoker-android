package com.netease.nim.uikit.common.ui.liv;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.uikit.common.ui.liv.LetterIndexView.OnTouchingLetterChangedListener;

import java.util.Map;

/**
 * 字母导航，点击字母，列表滑动到指定字母集合上。
 *
 * @author huangjun
 */
public class LivIndex {
    public int scrollOffset = 0;
    private final ListView lvContacts;
    private final RecyclerView recyclerView;

    private final LetterIndexView livIndex;

    private final TextView lblLetterHit;

    private final Map<String, Integer> mapABC; // 字母:所在的行的index

    public LivIndex(RecyclerView recyclerView, ListView contactsListView, LetterIndexView letterIndexView, TextView letterHit, Map<String, Integer> abcMap) {
        this.recyclerView = recyclerView;
        this.lvContacts = contactsListView;
        this.livIndex = letterIndexView;
        this.lblLetterHit = letterHit;
        this.mapABC = abcMap;
        this.livIndex.setOnTouchingLetterChangedListener(new LetterChangedListener());
    }

    /**
     * 更新索引表
     * @param letters
     */
    public void updateLetters(String[] letters){
        livIndex.setLetters(letters);
    }
    /**
     * 显示
     */
    public void show() {
        this.livIndex.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏
     */
    public void hide() {
        this.livIndex.setVisibility(View.GONE);
    }

    private class LetterChangedListener implements OnTouchingLetterChangedListener {

        @Override
        public void onHit(String letter) {
            lblLetterHit.setVisibility(View.VISIBLE);
            lblLetterHit.setText(letter);
            int index = -1;
            if ("↑".equals(letter)) {
                index = 0;
            } else if (mapABC.containsKey(letter)) {
                index = mapABC.get(letter);
            }
            if (index < 0) {
                return;
            }
            if (lvContacts != null) {
                index += lvContacts.getHeaderViewsCount();
                if (index < lvContacts.getCount()) {
                    lvContacts.setSelectionFromTop(index, 0);
                }
            }
            if (recyclerView != null) {
                if (index + scrollOffset < recyclerView.getAdapter().getItemCount() && index + scrollOffset < recyclerView.getLayoutManager().getItemCount()) {
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, index + scrollOffset);
                }
            }
        }

        @Override
        public void onCancel() {
            lblLetterHit.setVisibility(View.INVISIBLE);
        }
    }

}
