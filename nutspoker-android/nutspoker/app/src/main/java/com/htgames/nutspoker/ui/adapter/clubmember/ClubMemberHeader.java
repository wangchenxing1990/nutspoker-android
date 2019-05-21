package com.htgames.nutspoker.ui.adapter.clubmember;

import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by 周智慧 on 2017/5/26.
 */

public class ClubMemberHeader extends AbstractHeaderItem<ClubMemberHeader.HeaderViewHolder> implements IFilterable {
    private String id;
    private String title;

    public ClubMemberHeader(String id) {
        super();
        this.id = id;
        setDraggable(true);
    }

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof ClubMemberHeader) {
            ClubMemberHeader inItem = (ClubMemberHeader) inObject;
            return this.getId().equals(inItem.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getSpanSize(int spanCount, int position) {
        return spanCount;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.layout_channel_section;
    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new HeaderViewHolder(view, adapter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void bindViewHolder(FlexibleAdapter adapter, HeaderViewHolder holder, int position, List payloads) {
        if (payloads.size() > 0) {
            Log.d(this.getClass().getSimpleName(), "ClubMemberHeader " + id + " Payload " + payloads);
        } else {
            holder.mTitle.setText(getTitle());
        }
        List<ISectionable> sectionableList = adapter.getSectionItems(this);
        String subTitle = (sectionableList.isEmpty() ? "Empty section" :
                sectionableList.size() + " section items");
    }

    @Override
    public boolean filter(String constraint) {
        return getTitle() != null && getTitle().toLowerCase().trim().contains(constraint);
    }

    static class HeaderViewHolder extends FlexibleViewHolder {

        TextView mTitle;

        HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);//True for sticky
            mTitle = (TextView) view.findViewById(R.id.tv_channel_section);
            mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HeaderTitle", "Registered internal click on Header TitleTextView! " + mTitle.getText() + " position=" + getFlexibleAdapterPosition());
                }
            });

            //Support for StaggeredGridLayoutManager
            if (itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams()).setFullSpan(true);
            }
        }
    }

    @Override
    public String toString() {
        return "ClubMemberHeader[id=" + id +
                ", title=" + title + "]";
    }
}
