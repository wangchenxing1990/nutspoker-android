package com.htgames.nutspoker.chat.main.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.htgames.nutspoker.R
import com.htgames.nutspoker.tool.JsonResolveUtil
import com.htgames.nutspoker.ui.action.SearchAction
import com.htgames.nutspoker.ui.activity.Club.ToInviteClubUserProfileAC
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberAdapNew
import com.htgames.nutspoker.ui.base.BaseActivity
import com.htgames.nutspoker.ui.items.SearchAddClubUserItem
import com.htgames.nutspoker.view.ResultDataView
import com.htgames.nutspoker.view.TouchableRecyclerView
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.common.util.NetworkUtil
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.interfaces.IClick
import com.netease.nim.uikit.session.constant.Extras
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.model.Team
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotterknife.bindView
import java.util.HashSet
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.forEach

/*
* package com.example.butterknife.unbinder

import android.support.annotation.ColorInt
import android.view.View

import butterknife.BindView
import butterknife.BindColor
import butterknife.ButterKnife

class H(view: View) : G(view) {
    @JvmField @BindColor(android.R.color.primary_text_dark) @ColorInt
    internal var primaryTextDark: Int = 0

    @BindView(android.R.id.button3)
    lateinit var button3: View

    init {
        ButterKnife.bind(this, view)
    }
}
* */
class AddClubMember : BaseActivity(), View.OnClickListener, IClick {
    override fun onDelete(position: Int) {
    }

    override fun onClick(position: Int) {
        if (position < 0 || position >= mDatas.size || mDatas[position] !is SearchAddClubUserItem) {
            return
        }
        var item = mDatas[position]
        ToInviteClubUserProfileAC.startForResult(this@AddClubMember, item.id, ToInviteClubUserProfileAC.FROM_CLUB_OWNER, tempAlreadyInviteAccountList, mTeam.id, true)
    }

    override fun onLongClick(position: Int) {
    }

    lateinit var mTeam: Team
    var mDatas: ArrayList<SearchAddClubUserItem> = ArrayList()
    var refreshAccounts: ArrayList<String> = ArrayList()
    private lateinit var mAdapter: ClubMemberAdapNew
    private lateinit var mSearchAction: SearchAction
    val edit_search: ClearableEditTextWithIcon by bindView(R.id.edit_search)
    val tv_cancel: TextView by bindView(R.id.tv_cancel)
    val lv_user: TouchableRecyclerView by bindView(R.id.lv_user)
    val mResultDataView: ResultDataView by bindView(R.id.mResultDataView)
    companion object {
        val TAG: String = AddClubMember::class.java.simpleName
        fun start(activity: Activity, team: Team) {
            var intent: Intent = Intent(activity, AddClubMember::class.java)
            intent.putExtra(Extras.EXTRA_TEAM_DATA, team)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mTeam = intent.getSerializableExtra(Extras.EXTRA_TEAM_DATA) as Team
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_club_member)
        tv_cancel.setOnClickListener(this)
        mResultDataView.nullDataShow("暂无搜索结果!")
        mResultDataView.setReloadBtnVisibility(View.GONE)
        mSearchAction = SearchAction(this,  null)
        edit_search.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                var key = edit_search.text.toString()
                key = key.trim { it <= ' ' }
                if (StringUtil.isSpace(key)) {
                    Toast.makeText(applicationContext, R.string.not_allow_empty, Toast.LENGTH_SHORT).show()
                } else {
                    searchKey(key)
                }
                return@OnEditorActionListener true
            }
            false
        })
        initAdapter()
    }

    private fun initAdapter() {
        mAdapter = ClubMemberAdapNew(mDatas, this, true)
        mAdapter.setAnimationEntryStep(true)
                .setOnlyEntryAnimation(false)
                .setAnimationInitialDelay(500L)
                .setAnimationDelay(70L)
                .setAnimationOnScrolling(true)
                .setAnimationDuration(300L)
        lv_user.setAdapter(mAdapter)
        lv_user.setHasFixedSize(true) //Size of RV will not change
        lv_user.setItemViewCacheSize(0) //Setting ViewCache to 0 (default=2) will animate items better while scrolling down+up with LinearLayout
        mAdapter.setLongPressDragEnabled(true)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                // Show Headers at startUp, 1st call, correctly executed, no warning log message!
//                .setDisplayHeadersAtStartUp(true)
//                .setStickyHeaders(true)
    }

    fun searchKey(word: String) {
        if (!NetworkUtil.isNetAvailable(this)) {
            Toast.makeText(applicationContext, R.string.no_internet, Toast.LENGTH_SHORT).show()
            return
        }
        mSearchAction.searchKey(word, object : com.htgames.nutspoker.interfaces.RequestCallback {
            override fun onResult(code: Int, result: String, var3: Throwable?) {
                if (code == 0) {
                    mDatas.clear()
                    refreshAccounts.clear()
                    var searchBeans = JsonResolveUtil.getAdvSearchUserList(result)
                    searchBeans.forEach {
                        var item: SearchAddClubUserItem = SearchAddClubUserItem(it, null)
                        item.mTeam = mTeam
                        mDatas.add(item)
                        refreshAccounts.add(it.id)
                    }
                    NimUserInfoCache.getUserListByNeteaseEx(refreshAccounts, 0, NimUserInfoCache.IFetchCallback {
                        if (mAdapter != null) {
                            mAdapter.updateDataSet(mDatas as List<AbstractFlexibleItem<RecyclerView.ViewHolder>>?)
                        }
                    })
                    updateUI()
                } else {
                }
            }

            override fun onFailed() {
            }
        })
    }

    fun updateUI() {
        if (mResultDataView == null || lv_user == null) {
            return
        }
        if (mDatas == null || mDatas.size <= 0) {
            lv_user.visibility = View.GONE
            mResultDataView.nullDataShow("暂无搜索结果!")
        } else {
            lv_user.visibility = View.VISIBLE
            mResultDataView.successShow()
        }
    }

    var tempAlreadyInviteAccountList = HashSet<String>()//已经发送邀请加入俱乐部的用户id缓存集合, 为了保证用户id唯一不重复，用hashset而不是arraylist.
    fun inviteMembers(accounts: java.util.ArrayList<String>, position: Int) {
        NIMClient.getService(TeamService::class.java).addMembers(mTeam.id, accounts).setCallback(object : RequestCallback<Void> {
            override fun onSuccess(param: Void) {
                // 返回onSuccess，表示拉人不需要对方同意，且对方已经入群成功了
                tempAlreadyInviteAccountList.addAll(accounts)//有待改进通过注册观察者，观察team成员的改变，现在只是把俱乐部新成员加入到以发送邀请的列表tempAlreadyInviteAccountList
                mAdapter.notifyItemChanged(position)
                Toast.makeText(applicationContext, R.string.club_invite_members_success, Toast.LENGTH_SHORT).show()
            }

            override fun onFailed(code: Int) {
                if (code == 801) {
                    Toast.makeText(applicationContext, R.string.club_invite_member_count_limit, Toast.LENGTH_SHORT).show()
                } else if (code == 810) {
                    //// 返回onFailed，并且返回码为810，表示发出邀请成功了，但是还需要对方同意
                    tempAlreadyInviteAccountList.addAll(accounts)//有待改进通过注册观察者，观察team成员的改变，现在只是把俱乐部新成员加入到以发送邀请的列表tempAlreadyInviteAccountList
                    mAdapter.notifyItemChanged(position)
                    Toast.makeText(applicationContext, R.string.club_invite_members_success, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, R.string.club_invite_members_failed, Toast.LENGTH_SHORT).show()
                }
                LogUtil.e(TAG, "invite teamembers failed, code=" + code)
            }

            override fun onException(exception: Throwable) {

            }
        })
    }

    override fun onClick(v: View) {
        val viewId: Int = v.id
        if (viewId == R.id.tv_cancel) {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == ToInviteClubUserProfileAC.INVITE_MEMBER_REQUEST_CODE) {
                val accountId = data.getStringExtra(Extras.EXTRA_ACCOUNT)
                val alreadyMember = data.getBooleanExtra(ToInviteClubUserProfileAC.ALREADY_CLUB_MEMBER, false)
                if (alreadyMember) {
                    //什么都不用做，观察者teamMemberObserver的函数onUpdateTeamMember会处理掉
                } else {
                    tempAlreadyInviteAccountList.add(accountId)
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
