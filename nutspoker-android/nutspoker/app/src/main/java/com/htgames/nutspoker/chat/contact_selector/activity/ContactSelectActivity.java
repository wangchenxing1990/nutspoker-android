package com.htgames.nutspoker.chat.contact_selector.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.activity.Club.GroupListActivity;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.htgames.nutspoker.ui.action.ClubAction;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.view.ShareToSessionDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nim.uikit.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.contact.core.item.ContactItem;
import com.netease.nim.uikit.contact.core.item.ContactItemFilter;
import com.netease.nim.uikit.contact.core.item.ItemTypes;
import com.netease.nim.uikit.contact.core.model.IContact;
import com.netease.nim.uikit.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.contact.core.provider.TeamMemberDataProvider;
import com.netease.nim.uikit.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.contact.core.query.TextQuery;
import com.netease.nim.uikit.contact.core.viewholder.LabelHolder;
import com.netease.nim.uikit.contact_selector.adapter.ContactSelectAdapter;
import com.netease.nim.uikit.contact_selector.adapter.ContactSelectAvatarAdapter;
import com.netease.nim.uikit.contact_selector.viewholder.ContactsMultiSelectHolder;
import com.netease.nim.uikit.contact_selector.viewholder.ContactsSelectHolder;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 联系人选择器
 */
public class ContactSelectActivity extends TActionBarActivity implements View.OnClickListener, android.support.v7.widget.SearchView.OnQueryTextListener {
    public static final String EXTRA_DATA = "EXTRA_DATA"; // 请求数据：Option
    public static final String RESULT_DATA = "RESULT_DATA"; // 返回结果
    public static final int FROM_NORMAL = 0; //
    public final static int FROM_PAIJU_DETAILS = 1;
    public final static int FROM_PAIPU_RECORD = 2;
    public final static int FROM_PAIPU_COLLECT = 3;
    // adapter
    private ContactSelectAdapter contactAdapter;
    private ContactSelectAvatarAdapter contactSelectedAdapter;
    // view
    private ListView listView;
    private LivIndex livIndex;
    private RelativeLayout bottomPanel;
    private HorizontalScrollView scrollViewSelected;
    private GridView imageSelectedGridView;
    private Button btnSelect;
    private SearchView searchView;
    // other
    private String queryText;
    private Option option;
    ShareToSessionDialog mShareToSessionDialog;
    // class
    int from = FROM_NORMAL;
    Object shareObject;
    int shareType;

    ClubAction mClubAction;

    private static class ContactsSelectGroupStrategy extends com.netease.nim.uikit.contact.core.model.ContactGroupStrategy {
        public ContactsSelectGroupStrategy() {
            add(com.netease.nim.uikit.contact.core.model.ContactGroupStrategy.GROUP_NULL, -1, "");
            addABC(0);
        }
    }

    /**
     * 联系人选择器配置可选项
     */
    public enum ContactSelectType {
        BUDDY,
//        TEAM_MEMBER,
        TEAM
    }

    //
    RelativeLayout rl_search;
    RelativeLayout rl_select_club;
    RelativeLayout rl_select_group;
    TextView tv_search_cancel;

    public static class Option implements Serializable {

        /**
         * 联系人选择器中数据源类型：好友（默认）、群、群成员（需要设置teamId）
         */
        public ContactSelectType type = ContactSelectType.BUDDY;

        /**
         * 联系人选择器数据源类型为群成员时，需要设置群号
         */
        public String teamId = null;

        /**
         * 联系人选择器标题
         */
        public String title = "联系人选择器";

        /**
         * 联系人单选/多选（默认）
         */
        public boolean multi = true;

        /**
         * 至少选择人数
         */
        public int minSelectNum = 1;

        /**
         * 低于最少选择人数的提示
         */
        public String minSelectedTip = null;

        /**
         * 最大可选人数
         */
        public int maxSelectNum = 2000;

        /**
         * 超过最大可选人数的提示
         */
        public String maxSelectedTip = null;

        /**
         * 是否显示已选头像区域
         */
        public boolean showContactSelectArea = true;

        /**
         * 默认勾选（且可操作）的联系人项
         */
        public ArrayList<String> alreadySelectedAccounts = null;

        /**
         * 需要过滤（不显示）的联系人项
         */
        public ContactItemFilter itemFilter = null;

        /**
         * 需要disable(可见但不可操作）的联系人项
         */
        public ContactItemFilter itemDisableFilter = null;

        /**
         * 是否支持搜索
         */
        public boolean searchVisible = true;

        /**
         * 允许不选任何人点击确定
         */
        public boolean allowSelectEmpty = false;

        //管理员添加界面
        public boolean isMgrAdd = false;
    }

    public static void startActivityForResult(Context context, Option option, int requestCode) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, option);
        intent.setClass(context, ContactSelectActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void start(Context context, Option option, int shareType, Object shareContent, int from) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, option);
        intent.putExtra(Extras.EXTRA_SHARE_DATA, (Serializable) shareContent);
        intent.putExtra(Extras.EXTRA_SHARE_TYPE, shareType);
        intent.putExtra(Extras.EXTRA_FROM, from);
        intent.setClass(context, ContactSelectActivity.class);
        ((Activity) context).startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setQuery("", true);
            searchView.setIconified(true);
            showKeyboard(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // search view
        getMenuInflater().inflate(com.netease.nim.uikit.R.menu.contacts_search_menu, menu);
        MenuItem item = menu.findItem(com.netease.nim.uikit.R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                finish();
                return false;
            }
        });
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//        this.searchView = searchView;
//        this.searchView.setVisibility(option.searchVisible ? View.VISIBLE : View.GONE);
//        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_contacts_select);
        parseIntentData();
        initAdapter();
        initListView();
        initSearchView();
        initContactSelectArea();
        loadData();

        if (option.isMgrAdd) {
            mClubAction = new ClubAction(this, null);
            bottomPanel.setVisibility(View.GONE);
        }
        setResult(RESULT_CANCELED);
    }

    private void initSearchView() {
        tv_search_cancel = (TextView) findViewById(R.id.tv_search_cancel);
        searchView = (SearchView) findViewById(R.id.mSearchView);
        tv_search_cancel.setOnClickListener(this);
//        searchView.setIconified(false);//是否一开始就处于显示SearchView的状态
        searchView.setIconifiedByDefault(true);//是否可以隐藏
        searchView.setQueryHint(getString(R.string.contact_search));
//        searchView.set
//        final int closeImgId = getResources().getIdentifier("search_close_btn", "id", getPackageName());
        ImageView search_button = (ImageView) searchView.findViewById(R.id.search_button);
//        search_button.setImageResource(R.drawable.btn_head_search);
        //
        ImageView search_mag_icon = (ImageView) searchView.findViewById(R.id.search_mag_icon);
//        search_mag_icon.setImageResource(R.drawable.btn_head_search);
        search_mag_icon.setVisibility(View.GONE);
        //删除按钮
        ImageView closeImg = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeImg.setImageResource(R.mipmap.icon_edit_delete);
        //输入框
        SearchView.SearchAutoComplete mEditSearchView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        mEditSearchView.setTextColor(Color.WHITE);
        mEditSearchView.setHintTextColor(Color.GRAY);
        //提示
        LinearLayout tipLayout = (LinearLayout) searchView.findViewById(R.id.search_edit_frame);
        //搜索按钮
        ImageView icTip = (ImageView) searchView.findViewById(R.id.search_mag_icon);
//        icTip.setImageResource(R.drawable.btn_head_search);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                showSearchView(false);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchView(true);
            }
        });
        //文本内容监听
        searchView.setOnQueryTextListener(this);
    }

    public void showSearchView(boolean show) {
        if (show) {
            findViewById(R.id.rl_head_normal).setVisibility(View.GONE);
            tv_search_cancel.setVisibility(View.VISIBLE);
            if (headerView != null && listView.getHeaderViewsCount() != 0) {
                listView.removeHeaderView(headerView);
            }
        } else {
            findViewById(R.id.rl_head_normal).setVisibility(View.VISIBLE);
            tv_search_cancel.setVisibility(View.GONE);
            if (headerView != null && listView.getHeaderViewsCount() == 0) {
                listView.addHeaderView(headerView);
            }
        }
    }

    View headerView;

    public void initNewContactHeaderView() {
        headerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_newcontact_select, null);
        rl_select_club = (RelativeLayout) headerView.findViewById(R.id.rl_select_club);
        rl_select_group = (RelativeLayout) headerView.findViewById(R.id.rl_select_group);
        rl_select_club.setOnClickListener(this);
        rl_select_group.setOnClickListener(this);
        rl_select_group.setVisibility(View.GONE);
        listView.addHeaderView(headerView);
    }

    private void parseIntentData() {
        this.option = (Option) getIntent().getSerializableExtra(EXTRA_DATA);
        if (TextUtils.isEmpty(option.maxSelectedTip)) {
//            option.maxSelectedTip = "" + option.maxSelectNum + "";
            option.maxSelectedTip = getString(R.string.contact_select_max, option.maxSelectNum);
        }
        if (TextUtils.isEmpty(option.minSelectedTip)) {
//            option.minSelectedTip = "至少选择" + option.minSelectNum + "人";
            option.minSelectedTip = getString(R.string.contact_select_min, option.minSelectNum);
        }
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, FROM_NORMAL);
        shareObject = getIntent().getSerializableExtra(Extras.EXTRA_SHARE_DATA);
        shareType = getIntent().getIntExtra(Extras.EXTRA_SHARE_TYPE, CircleConstant.TYPE_PAIJU);
//        setTitle(option.title);
        setHeadTitle(option.title);
    }

    private void initAdapter() {
        IContactDataProvider dataProvider;
        if (option.type == ContactSelectType.TEAM) {
            option.showContactSelectArea = false;
            dataProvider = new ContactDataProvider(ItemTypes.TEAM);
        } else {
            dataProvider = new ContactDataProvider(ItemTypes.FRIEND);
        }

        // contact adapter
        contactAdapter = new com.netease.nim.uikit.contact_selector.adapter.ContactSelectAdapter(ContactSelectActivity.this, new ContactsSelectGroupStrategy(), dataProvider) {
            boolean isEmptyContacts = false;

            @Override
            protected List<AbsContactItem> onNonDataItems() {
                return null;
            }

            @Override
            protected void onPostLoad(boolean empty, String queryText, boolean all) {
                if (empty) {
                    if (TextUtils.isEmpty(queryText)) {
                        isEmptyContacts = true;
                    }
                    updateEmptyView(queryText);
                } else {
                    setSearchViewVisible(true);
                }
            }

            private void updateEmptyView(String queryText) {
                if (!isEmptyContacts && !TextUtils.isEmpty(queryText)) {
                    setSearchViewVisible(true);
                } else {
                    setSearchViewVisible(false);
                }
            }

            private void setSearchViewVisible(boolean visible) {
                option.searchVisible = visible;
                if (searchView != null) {
                    searchView.setVisibility(option.searchVisible ? View.VISIBLE : View.GONE);
                }
            }
        };

        Class c = option.multi ? ContactsMultiSelectHolder.class : ContactsSelectHolder.class;
        contactAdapter.addViewHolder(ItemTypes.LABEL, LabelHolder.class);
        contactAdapter.addViewHolder(ItemTypes.FRIEND, c);
        contactAdapter.addViewHolder(ItemTypes.TEAM_MEMBER, c);
        contactAdapter.addViewHolder(ItemTypes.TEAM, c);

        contactAdapter.setFilter(option.itemFilter);
        contactAdapter.setDisableFilter(option.itemDisableFilter);

        // contact select adapter
        contactSelectedAdapter = new com.netease.nim.uikit.contact_selector.adapter.ContactSelectAvatarAdapter(this);
    }

    private void initListView() {
        listView = findView(com.netease.nim.uikit.R.id.contact_list_view);
        if (from != FROM_NORMAL) {
            initNewContactHeaderView();
        }
        listView.setAdapter(contactAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                showKeyboard(false);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                AbsContactItem item = (AbsContactItem) contactAdapter.getItem(position);

                if (item == null) {
                    return;
                }

                if (option.isMgrAdd) {
                    IContact contact = null;
                    if (item instanceof ContactItem) {
                        contact = ((ContactItem) item).getContact();

                        final String selected = contact.getContactId();

                        if (!NetworkUtil.isNetAvailable(ContactSelectActivity.this)) {
                            com.htgames.nutspoker.widget.Toast.makeText(getApplicationContext(), R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_SHORT).show();
                            return;
                        }
                        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(getApplicationContext(), null,
                                getString(R.string.text_set_mgr_format, NimUserInfoCache.getInstance().getUserDisplayName(selected)), true,
                                new EasyAlertDialogHelper.OnDialogActionListener() {

                                    @Override
                                    public void doCancelAction() {

                                    }

                                    @Override
                                    public void doOkAction() {
                                        //addMgr(selected);
                                    }
                                });
                        if (!isFinishing() && !isDestroyedCompatible()) {
                            dialog.show();
                        }
                    }
                } else {
                    if (option.multi) {
                        if (!contactAdapter.isEnabled(position)) {
                            return;
                        }
                        IContact contact = null;
                        if (item instanceof ContactItem) {
                            contact = ((ContactItem) item).getContact();
                        }
                        if (contactAdapter.isSelected(position)) {
                            contactAdapter.cancelItem(position);
                            if (contact != null) {
                                contactSelectedAdapter.removeContact(contact);
                            }
                        } else {
                            if (contactSelectedAdapter.getCount() <= option.maxSelectNum) {
                                contactAdapter.selectItem(position);
                                if (contact != null) {
                                    contactSelectedAdapter.addContact(contact);
                                }
                            } else {
                                Toast.makeText(ContactSelectActivity.this, option.maxSelectedTip, Toast.LENGTH_SHORT).show();
                            }

                            if (!TextUtils.isEmpty(queryText) && searchView != null) {
                                searchView.setQuery("", true);
                                searchView.setIconified(true);
                                showKeyboard(false);
                            }
                        }
                        arrangeSelected();
                    } else {
                        if (item instanceof ContactItem) {
                            final IContact contact = ((ContactItem) item).getContact();
                            ArrayList<String> selectedIds = new ArrayList<>();
                            selectedIds.add(contact.getContactId());
                            onSelected(selectedIds);
                        }

                        arrangeSelected();
                    }
                }
            }
        });

        // 字母导航
        TextView letterHit = (TextView) findViewById(com.netease.nim.uikit.R.id.tv_hit_letter);
        com.netease.nim.uikit.common.ui.liv.LetterIndexView idxView = (com.netease.nim.uikit.common.ui.liv.LetterIndexView) findViewById(com.netease.nim.uikit.R.id.liv_index);
        idxView.setLetters(getResources().getStringArray(com.netease.nim.uikit.R.array.letter_list2));
        if (option.type != ContactSelectType.TEAM) {
            livIndex = contactAdapter.createLivIndex(listView, idxView, letterHit);
            livIndex.show();
        } else {
            idxView.setVisibility(View.GONE);
        }
    }

    private void initContactSelectArea() {
        btnSelect = (Button) findViewById(com.netease.nim.uikit.R.id.btnSelect);
        if (!option.allowSelectEmpty) {
            btnSelect.setEnabled(false);
        } else {
            btnSelect.setEnabled(true);
        }
        btnSelect.setOnClickListener(this);
        bottomPanel = (RelativeLayout) findViewById(com.netease.nim.uikit.R.id.rlCtrl);
        scrollViewSelected = (HorizontalScrollView) findViewById(com.netease.nim.uikit.R.id.contact_select_area);
        if (option.multi) {
            bottomPanel.setVisibility(View.VISIBLE);
            if (option.showContactSelectArea) {
                scrollViewSelected.setVisibility(View.VISIBLE);
                btnSelect.setVisibility(View.VISIBLE);
            } else {
                scrollViewSelected.setVisibility(View.GONE);
                btnSelect.setVisibility(View.GONE);
            }
            btnSelect.setText(getOKBtnText(0));
        } else {
            bottomPanel.setVisibility(View.GONE);
        }

        // selected contact image banner
        imageSelectedGridView = (GridView) findViewById(com.netease.nim.uikit.R.id.contact_select_area_grid);
        imageSelectedGridView.setAdapter(contactSelectedAdapter);
        notifySelectAreaDataSetChanged();
        imageSelectedGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (contactSelectedAdapter.getItem(position) == null) {
                        return;
                    }

                    IContact iContact = contactSelectedAdapter.remove(position);
                    if (iContact != null) {
                        contactAdapter.cancelItem(iContact);
                    }
                    arrangeSelected();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // init already selected items
        List<String> selectedUids = option.alreadySelectedAccounts;
        if (selectedUids != null && !selectedUids.isEmpty()) {
            contactAdapter.setAlreadySelectedAccounts(selectedUids);
            List<ContactItem> selectedItems = contactAdapter.getSelectedItem();
            for (ContactItem item : selectedItems) {
                contactSelectedAdapter.addContact(item.getContact());
            }
            arrangeSelected();
        }
    }

    private void loadData() {
        contactAdapter.load(true);
    }

    private void arrangeSelected() {
        this.contactAdapter.notifyDataSetChanged();
        if (option.multi) {
            int count = contactSelectedAdapter.getCount();
            if (!option.allowSelectEmpty) {
                btnSelect.setEnabled(count > 1);
            } else {
                btnSelect.setEnabled(true);
            }
            btnSelect.setText(getOKBtnText(count));
            notifySelectAreaDataSetChanged();
        }
    }

    private void notifySelectAreaDataSetChanged() {
        int converViewWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46, this.getResources()
                .getDisplayMetrics()));
        ViewGroup.LayoutParams layoutParams = imageSelectedGridView.getLayoutParams();
        layoutParams.width = converViewWidth * contactSelectedAdapter.getCount();
        layoutParams.height = converViewWidth;
        imageSelectedGridView.setLayoutParams(layoutParams);
        imageSelectedGridView.setNumColumns(contactSelectedAdapter.getCount());

        try {
            final int x = layoutParams.width;
            final int y = layoutParams.height;
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    scrollViewSelected.scrollTo(x, y);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        contactSelectedAdapter.notifyDataSetChanged();
    }

    private String getOKBtnText(int count) {
        String caption = getString(com.netease.nim.uikit.R.string.ok);
        return caption + " (" + (count < 1 ? 0 : (count - 1)) + ")";
    }

    /**
     * ************************** select ************************
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_select_club:
//                ClubListActivity.start(ContactSelectActivity.this, from, shareType, shareObject);
                break;
            case R.id.rl_select_group:
                GroupListActivity.start(ContactSelectActivity.this, from, shareType, shareObject);
                break;
            case R.id.btnSelect:
                List<IContact> contacts = contactSelectedAdapter.getSelectedContacts();
                if (from == FROM_NORMAL) {
                    if (option.allowSelectEmpty || checkMinMaxSelection(contacts.size())) {
                        ArrayList<String> selectedAccounts = new ArrayList<>();
                        for (IContact c : contacts) {
                            selectedAccounts.add(c.getContactId());
                        }
                        onSelected(selectedAccounts);
                    }
                } else {
                    ArrayList<String> contactUidList = new ArrayList<String>();
                    for (IContact c : contacts) {
                        contactUidList.add(c.getContactId());
                    }
                    if (mShareToSessionDialog == null) {
                        mShareToSessionDialog = new ShareToSessionDialog(this, shareType, shareObject, from);
                    }
                    mShareToSessionDialog.show(contactUidList, SessionTypeEnum.P2P);
                }
                break;
            case R.id.tv_search_cancel:
                searchView.setQuery("", true);
                searchView.setIconified(true);
                showKeyboard(false);
                break;
        }
    }

    private boolean checkMinMaxSelection(int selected) {
        if (option.minSelectNum > selected) {
            return showMaxMinSelectTip(true);
        } else if (option.maxSelectNum < selected) {
            return showMaxMinSelectTip(false);
        }
        return true;
    }

    private boolean showMaxMinSelectTip(boolean min) {
        if (min) {
            Toast.makeText(this, option.minSelectedTip, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, option.maxSelectedTip, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void onSelected(ArrayList<String> selects) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(RESULT_DATA, selects);
        setResult(Activity.RESULT_OK, intent);
        this.finish();
    }

    /**
     * ************************* search ******************************
     */

    @Override
    public boolean onQueryTextChange(String query) {
        queryText = query;
        if (TextUtils.isEmpty(query)) {
            this.contactAdapter.load(true);
        } else {
            this.contactAdapter.query(query);
        }
        return true;
    }

    public void setHeadTitle(int resId) {
        ((TextView) findViewById(R.id.tv_head_title)).setText(resId);
    }

    public void setHeadTitle(String title) {
        ((TextView) findViewById(R.id.tv_head_title)).setText(title);
    }

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String arg0) {
        return false;
    }

    @Override
    public void finish() {
        showKeyboard(false);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClubAction != null)
            mClubAction.onDestroy();
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }


}
