package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.data.DataManager;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberAdapNew;
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberHeader;
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberItem;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.htgames.nutspoker.R.id.lv_list;

/**
 * Created by 周智慧 on 2017/5/29.
 */

public class SelectClubMgrACNew extends TActionBarActivity implements View.OnClickListener, android.support.v7.widget.SearchView.OnQueryTextListener, IClick {
    public static final String RESULT_DATA = "RESULT_DATA"; // 返回结果
    private SearchView searchView;
    private String teamId;
    private ClubMemberAdapNew contactAdapter;
    private MeRecyclerView listView;
    private ArrayList<ClubMemberItem> mDatas = new ArrayList<>();
    TextView tv_search_cancel;
    private String queryText;
    @Override
    public boolean onQueryTextSubmit(String query) {
        return onQueryTextChange(query);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        queryText = newText;
        if (contactAdapter.hasNewSearchText(newText)) {
            LogUtil.d("zzh", "onQueryTextChange newText: " + newText);
            contactAdapter.setSearchText(newText);

            // Fill and Filter mItems with your custom list and automatically animate the changes
            // - Option A: Use the internal list as original list
            contactAdapter.filterItems(300);

            // - Option B: Provide any new list to filter
            //mAdapter.filterItems(DatabaseService.getInstance().getDatabaseList(), DatabaseConfiguration.delay);
        }
        // Disable SwipeRefresh if search is active!!
        return true;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_search_cancel) {
            searchView.setQuery("", true);
            searchView.setIconified(true);
            showKeyboard(false);
        }
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
        return super.onCreateOptionsMenu(menu);
    }

    public static void startActivityForResult(Activity activity, String teamId, int requestCode) {
        Intent intent = new Intent(activity, SelectClubMgrACNew.class);
        intent.putExtra(Extras.EXTRA_TEAM_ID, teamId);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_add_club_mgr_new);
        ((TextView) findViewById(R.id.tv_head_title)).setText("添加管理员");
        initAdapter();
        initListView();
        initSearchView();
        buildLitterIdx();
    }

    private void initAdapter() {
        List<TeamMember> teamMembers = TeamDataCache.getInstance().getTeamMemberList(teamId);
        for (int i = 0; i < teamMembers.size(); i++) {
            TeamMember teamMember = teamMembers.get(i);
            if (teamMember.getType() != TeamMemberType.Normal) {
                continue;
            }
            ClubMemberItem clubMemberItem = new ClubMemberItem(teamMember);
            clubMemberItem.setId(NimUserInfoCache.getInstance().getUserDisplayName(teamMember.getAccount()));
            mDatas.add(clubMemberItem);
        }
        Collections.sort(mDatas, ClubMemberItem.comparator);
        for (int i = 0; i < mDatas.size(); i++) {//排完序后加上header
            ClubMemberItem preItem = i >= 1 ? mDatas.get(i - 1) : null;
            ClubMemberItem currentItem = mDatas.get(i);
            if (preItem == null) {
                String currentAlpha = ClubMemberItem.getAlpha(currentItem.pinyin);
                ClubMemberHeader header = new ClubMemberHeader(currentAlpha);
                header.setTitle(currentAlpha);
                currentItem.setHeader(header);
            } else {
                String preAlpha = ClubMemberItem.getAlpha(preItem.pinyin);
                String currentAlpha = ClubMemberItem.getAlpha(currentItem.pinyin);
                if (!preAlpha.equals(currentAlpha)) {
                    ClubMemberHeader header = new ClubMemberHeader(currentAlpha);
                    header.setTitle(currentAlpha);
                    currentItem.setHeader(header);
                }
            }
        }
        contactAdapter = new ClubMemberAdapNew(mDatas, this, true);
        contactAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)//Size limit = MAX_VALUE will always animate the changes
                .setNotifyMoveOfFilteredItems(false)//When true, filtering on big list is very slow!
                .setNotifyChangeOfUnfilteredItems(true)//We have highlighted text while filtering, so let's enable this feature to be consistent with the active filter
                .setAnimationInitialDelay(100L)
                .setAnimationOnScrolling(true)
                .setAnimationOnReverseScrolling(true)
                .setOnlyEntryAnimation(true);
    }

    private void initListView() {
        listView = findView(com.netease.nim.uikit.R.id.contact_list_view);
        listView.setHasFixedSize(true);
        listView.setAdapter(contactAdapter);
        listView.setItemViewCacheSize(0); //Setting ViewCache to 0 (default=2) will animate items better while scrolling down+up with LinearLayout
        contactAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(true)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                // Show Headers at startUp, 1st call, correctly executed, no warning log message!
                .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true);
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                showKeyboard(false);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    EasyAlertDialog topUpDialog;
    private void showTopUpDialog() {
        if (topUpDialog == null) {
            topUpDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    "您的钻石不足，请购买", getString(R.string.buy), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                            topUpDialog.dismiss();
                        }

                        @Override
                        public void doOkAction() {
                            ShopActivity.start(SelectClubMgrACNew.this, ShopActivity.TYPE_SHOP_DIAMOND);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            topUpDialog.show();
        }
    }

    EasyAlertDialog addMgrDialog;
    private void showAddMgrDialog(final TeamMember item) {
        String name = NimUserInfoCache.getInstance().getUserDisplayName(item.getAccount());
        int currentMgrSize = DataManager.getInstance().sMgrList.size();
        int diamondConsume = 0;//添加管理员消耗钻石
        String message = "添加3-5个管理员，\n" + "每添加一个管理员消耗800钻石";
        if (currentMgrSize <= 1) {
            message = "确定添加" +  "“" + name + "”\n" + "为俱乐部管理员?";
            diamondConsume = 0;
        } else if (currentMgrSize >= 2 && currentMgrSize <= 4) {
            message = "确定添加" +  "“" + name + "”\n" + "为俱乐部管理员?\n" + "消耗：800钻石";
            diamondConsume = 800;
        } else if (currentMgrSize >= 5 && currentMgrSize <= 9) {
            message = "确定添加" +  "“" + name + "”\n" + "为俱乐部管理员?\n" + "消耗：1000钻石";
            diamondConsume = 1000;
        } else if (currentMgrSize >= 10 && currentMgrSize <= 19) {
            message = "确定添加" +  "“" + name + "”\n" + "为俱乐部管理员?\n" + "消耗：1500钻石";
            diamondConsume = 1500;
        }
        final int finalDiamondConsume = diamondConsume;
        if (addMgrDialog == null) {
            addMgrDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    message, getString(R.string.ok), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                        }
                        @Override
                        public void doOkAction() {
                            if (UserPreferences.getInstance(ChessApp.sAppContext).getDiamond() < finalDiamondConsume) {
                                showTopUpDialog();
                                return;
                            }
                            onSelected(item);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            addMgrDialog.setMessage(message);
            addMgrDialog.show();
        }
    }

    private boolean checkMinMaxSelection(int selected) {
        int capacity = ManagerSetActivity.TotalMgrCount - DataManager.getInstance().sManagerList.size();
        if (0 > selected) {
            return showMaxMinSelectTip(true);
        } else if (capacity < selected) {
            return showMaxMinSelectTip(false);
        }
        return true;
    }
    public void onSelected(TeamMember teamMember) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_DATA, teamMember);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private boolean showMaxMinSelectTip(boolean min) {
        if (min) {
            Toast.makeText(this, String.format(getString(R.string.clubmgr_limit_mgr), ManagerSetActivity.TotalMgrCount), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, String.format(getString(R.string.clubmgr_limit_mgr), ManagerSetActivity.TotalMgrCount), Toast.LENGTH_SHORT).show();
        }
        return false;
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
        search_button.setColorFilter(Color.WHITE);//或者android:tint="@color/login_solid_color"效果一样
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

    private LivIndex litterIdx;
    ArrayList<String> letters = new ArrayList<>();
    protected final HashMap<String, Integer> indexes = new HashMap<>();
    private void buildLitterIdx() {
        getIndexes();
        LetterIndexView livIndex = (LetterIndexView) findViewById(R.id.liv_index);
        livIndex.setNormalColor(getResources().getColor(R.color.contacts_letters_color));
        TextView litterHit = (TextView) findViewById(R.id.tv_hit_letter);
        litterIdx = new LivIndex(listView, null, livIndex, litterHit, indexes);
        litterIdx.updateLetters(letters.toArray(new String[letters.size()]));
        litterIdx.show();
    }

    private int preHeaders = 0;
    public HashMap<String, Integer> getIndexes() {
        // CLEAR
        this.indexes.clear();
//        // SET
//        this.indexes.putAll(indexes);
        for (int i = 0; i < mDatas.size(); i++) {
            // 得到字母
            if (mDatas.get(i).getHeader() != null) {
                String name = ClubMemberItem.getAlpha(mDatas.get(i).sortKey);
                if (!indexes.containsKey(name)) {
                    indexes.put(name, preHeaders + i);
                    letters.add(name);
                }
                preHeaders++;
            }
        }
        return indexes;
    }

    public void showSearchView(boolean show) {
        if (show) {
            findViewById(R.id.rl_head_normal).setVisibility(View.GONE);
            tv_search_cancel.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rl_head_normal).setVisibility(View.VISIBLE);
            tv_search_cancel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDelete(int position) {

    }

    @Override
    public void onClick(int position) {
        if (!(contactAdapter.getItem(position) instanceof ClubMemberItem)) {
            return;
        }
        TeamMember member = ((ClubMemberItem) contactAdapter.getItem(position)).mTeamMember;
        if (member == null) {
            return;
        }
        showAddMgrDialog(member);
    }

    @Override
    public void onLongClick(int position) {

    }
}
