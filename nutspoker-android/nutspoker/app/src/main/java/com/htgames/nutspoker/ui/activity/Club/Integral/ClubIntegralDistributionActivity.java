package com.htgames.nutspoker.ui.activity.Club.Integral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.activity.Club.ClubInfoActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.ResultDataView;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nim.uikit.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.contact.core.item.ContactItem;
import com.netease.nim.uikit.contact.core.item.ItemTypes;
import com.netease.nim.uikit.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.contact.core.model.ContactGroupStrategy;
import com.netease.nim.uikit.contact.core.model.TeamMemberContact;
import com.netease.nim.uikit.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.contact.core.query.TextQuery;
import com.netease.nim.uikit.contact.core.viewholder.ContactHolder;
import com.netease.nim.uikit.contact.core.viewholder.LabelHolder;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 分配积分-选择俱乐部成员
 */
public class ClubIntegralDistributionActivity extends BaseActivity {

    @BindView(R.id.contact_list_view)
    ListView mUiList;
    @BindView(R.id.tv_hit_letter)
    TextView mUiTextLetter;
    @BindView(R.id.liv_index)
    LetterIndexView mUiLetterIndex;
    @BindView(R.id.friends_loading_view)
    ResultDataView mUiResult;

    public static void StartActivity(Activity activity) {
        Intent intent = new Intent(activity, ClubIntegralDistributionActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mUnbinder = ButterKnife.bind(this);

        setHeadTitle(R.string.club_integral_distribution);

        mUiResult.setVisibility(View.GONE);
        mAdapter = new ContactDataAdapter(this
                , new ClubIntegralDistributionActivity.ContactsGroupStrategy()
                , new ContactDataProvider(ItemTypes.TEAM_MEMBER){//这里其实不是朋友，而是俱乐部成员
                    @Override
                    public List<AbsContactItem> provide(TextQuery query) {

                        //检索俱乐部成员,搜索先忽略
                        List<AbsContactItem> items = new ArrayList<>();
                        for(TeamMember tm : ClubInfoActivity.teamMembers){
                            items.add(new ContactItem(new TeamMemberContact(tm),ItemTypes.TEAM_MEMBER));
                        }
                        return items;
                    }
                }
        )
        {
            @Override
            protected List<AbsContactItem> onNonDataItems() {
                return new ArrayList<>();
            }
            @Override
            protected void onPreReady() {
            }

            @Override
            protected void onPostLoad(boolean empty, String queryText, boolean all) {
                // 开始加载
                if (!mAdapter.load(false)) {
                    // 如果不需要加载，则直接当完成处理
                    //onReloadCompleted();
                }
            }
        };
        mAdapter.addViewHolder(ItemTypes.LABEL, LabelHolder.class);
        mAdapter.addViewHolder(ItemTypes.TEAM_MEMBER, ContactHolder.class);
        mAdapter.load(true);
        mLitterIdx = mAdapter.createLivIndex(mUiList, mUiLetterIndex, mUiTextLetter);
        mLitterIdx.show();
        mUiList.setAdapter(mAdapter);
        mUiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactItem item = (ContactItem) mAdapter.getItem(position - mUiList.getHeaderViewsCount());
                if (item == null) {
                    return;
                }

                ClubIntegralOperation.StartActivity(ClubIntegralDistributionActivity.this
                        ,item.getContact().getContactId(),ClubIntegralOperation.Type_Dispatch,null);
            }
        });
    }

    //提供分类列表
    static final class ContactsGroupStrategy extends ContactGroupStrategy {
        public ContactsGroupStrategy() {
            add(ContactGroupStrategy.GROUP_NULL, -1, "");
            addABC(0);
        }
    }

    ContactDataAdapter mAdapter;
    LivIndex mLitterIdx;
}
