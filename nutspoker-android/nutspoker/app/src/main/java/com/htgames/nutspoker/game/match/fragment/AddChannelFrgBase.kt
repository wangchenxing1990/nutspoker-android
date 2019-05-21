package com.htgames.nutspoker.game.match.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.google.gson.reflect.TypeToken
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.R
import com.htgames.nutspoker.game.match.activity.AddChannelAC
import com.htgames.nutspoker.game.match.activity.ChannelListAC
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.tool.JsonResolveUtil
import com.htgames.nutspoker.ui.action.GameAction
import com.htgames.nutspoker.ui.action.SearchAction
import com.htgames.nutspoker.ui.base.BaseFragment
import com.netease.nim.uikit.api.ApiCode
import com.netease.nim.uikit.api.ApiConstants
import com.netease.nim.uikit.api.NetWork
import com.netease.nim.uikit.api.SignStringRequest
import com.netease.nim.uikit.bean.GameEntity
import com.netease.nim.uikit.bean.SearchUserBean
import com.netease.nim.uikit.common.gson.GsonUtils
import com.netease.nim.uikit.common.preference.GameMgrPref
import com.netease.nim.uikit.common.ui.dialog.DialogMaker
import com.netease.nim.uikit.common.util.NetworkUtil
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.interfaces.IOperationClick
import com.netease.nim.uikit.nav.UrlConstants
import kotlinx.android.synthetic.main.fragment_add_channel.*
import org.json.JSONException
import org.json.JSONObject

open class AddChannelFrgBase : BaseFragment(), IOperationClick {
    companion object {
        fun newInstance(channelType: Int): AddChannelFrgBase {
            val mInstance = AddChannelFrgBase()
            val bundle = Bundle()
            bundle.putInt(AddChannelAC.KEY_CHANNEL_TYPE, channelType)
            mInstance.arguments = bundle
            return mInstance
        }
    }
    override fun onAgree(position: Int, payload: Any?) {
        if (payload is SearchUserBean) {
            if (channelType == AddChannelAC.CHANNEL_TYPE_PERSONAL) {
                addMttMgrPersonalChannel(payload.id, position)
            } else {
                addMttMgrClubChannel(payload.tid, position)
            }
        }
    }
    override fun onReject(position: Int, payload: Any?) {
        deleteHistory(position, payload as? SearchUserBean)
    }
    override fun onOtherOperation(position: Int, payload: Any?) {
    }

    var channelType = AddChannelAC.CHANNEL_TYPE_PERSONAL
    var mSearchAction: SearchAction? = null
    var mGameAction: GameAction? = null
    var showHistory = true//默认看是历史记录
    var gameInfo: GameEntity? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        channelType = arguments.getInt(AddChannelAC.KEY_CHANNEL_TYPE)
        return LayoutInflater.from(activity).inflate(R.layout.fragment_add_channel, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSearchAction = SearchAction(activity, null)
        mGameAction = GameAction(activity, null)
        gameInfo = activity.intent.getSerializableExtra(UrlConstants.ADD_GAME_MANAGER_GAME_INFO) as GameEntity?
        initHistoryRelated()
        changeIsShowHistory(showHistory)
        tv_add_channel_head_right.setOnClickListener {
            if (!ChessApp.gameManagers.containsKey(gameInfo?.gid)) return@setOnClickListener
            val originalData = if (showHistory) mHistoryMgrList?.list else mSearchDatas
            val addChannelListParamsStr: StringBuilder = StringBuilder()//请求接口的参数
            var varargPos = ArrayList<Int>()
            run run@ {//已经是渠道的item过滤掉
                for (i in 0 until (originalData?.size ?: -999)) {//originalData?.forEach outer@ {//the label 'outer' does not reference to a context from which we can return
                    var hasSame = false
                    val outerItem = originalData?.get(i)!!
                    run outer@ {
                        ChessApp.gameManagers.get(gameInfo?.gid)?.forEach dangerous@ {
                            if (it.account == outerItem.id) {
                                hasSame = true
                                return@outer
                            }
                        }
                    }
                    if (!hasSame) {
                        addChannelListParamsStr.append("${if (channelType == AddChannelAC.CHANNEL_TYPE_PERSONAL) outerItem.id else outerItem.tid},")//批量添加以逗号分割
                        varargPos.add(i)
                    }
                }
            }
            if (addChannelListParamsStr.isEmpty()) return@setOnClickListener
            if (channelType == AddChannelAC.CHANNEL_TYPE_PERSONAL) {
                addMttMgrPersonalChannel(addChannelListParamsStr.substring(0, addChannelListParamsStr.length - 1), *(varargPos.toIntArray()))
            } else {
                addMttMgrClubChannel(addChannelListParamsStr.substring(0, addChannelListParamsStr.length - 1), *(varargPos.toIntArray()))
            }
        }
    }

    override fun onDestroy() {
        mSearchAction?.onDestroy()
        mGameAction?.onDestroy()
        writeHistory()//onDestroy()
        super.onDestroy()
    }

    var mHistoryMgrList: AddChannelAC.HistoryMgrList? = null
    lateinit var mHistoryAdapter: AddChannelAC.AddChannelAdap
    open fun initHistoryRelated() {//初始化管理员历史记录相关
        val historyListStr = if (channelType == AddChannelAC.CHANNEL_TYPE_PERSONAL) GameMgrPref.getInstance(activity).mgrListStringPersonal else GameMgrPref.getInstance(activity).mgrListStringClub
        val type = object : TypeToken<AddChannelAC.HistoryMgrList>() {
        }.type
        if (!StringUtil.isSpace(historyListStr)) {
            mHistoryMgrList = GsonUtils.getGson().fromJson<AddChannelAC.HistoryMgrList>(historyListStr, type)
        }
        if (mHistoryMgrList == null) {
            mHistoryMgrList = AddChannelAC.HistoryMgrList()
        }
        if (mHistoryMgrList?.list == null) {
            mHistoryMgrList?.list = ArrayList()
        }
        mHistoryAdapter = AddChannelAC.AddChannelAdap(mHistoryMgrList?.list, this)
        mHistoryAdapter.channelType = channelType
        mHistoryAdapter.showHistory = showHistory
        mHistoryAdapter.gameInfo = gameInfo
        recycler_view_add_channel.adapter = mHistoryAdapter
    }

    private fun writeHistory() {
        val historyMgrListStr = GsonUtils.getGson().toJson(mHistoryMgrList)
        if (channelType == AddChannelAC.CHANNEL_TYPE_PERSONAL) {
            GameMgrPref.getInstance(activity).mgrListStringPersonal = historyMgrListStr//把管理员历史记录写入到sharedpreference
        } else if (channelType == AddChannelAC.CHANNEL_TYPE_CLUB) {
            GameMgrPref.getInstance(activity).mgrListStringClub = historyMgrListStr//把管理员历史记录写入到sharedpreference
        }
    }

    fun deleteHistory(position: Int, deleteItem: SearchUserBean?) {
        if (position < (mHistoryMgrList?.list?.size ?: -999) && mHistoryMgrList?.list?.removeAt(position) != null) {
            if (showHistory) {
                mHistoryAdapter.mDatas.removeAt(position)
                mHistoryAdapter.notifyItemRemoved(position)
                judgeEmptyData()//deleteHistory
            }
        }
    }

    private fun clearHistory() {
        mHistoryMgrList?.list?.clear()
        mHistoryAdapter.mDatas.clear()
        mHistoryAdapter.notifyDataSetChanged()
        judgeEmptyData()//clearHistory
    }

    //下面是搜索相关的逻辑下面是搜索相关的逻辑下面是搜索相关的逻辑下面是搜索相关的逻辑下面是搜索相关的逻辑下面是搜索相关的逻辑下面是搜索相关的逻辑下面是搜索相关的逻辑
    internal var mSearchDatas: ArrayList<SearchUserBean>? = null
    fun search(word: String) {
        if (channelType == AddChannelAC.CHANNEL_TYPE_PERSONAL) {
            searchPersonalChannel(word)
        } else if (channelType == AddChannelAC.CHANNEL_TYPE_CLUB) {
            searchClubChannel(word)
        }
    }

    fun searchPersonalChannel(word: String) {
        if (!NetworkUtil.isNetAvailable(activity)) {
            mResultDataView.showError(activity, R.string.network_is_not_available)
            return
        }
        DialogMaker.showProgressDialog(activity, getString(com.netease.nim.uikit.R.string.empty), true)
        val paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext)
        paramsMap.put("word", word)
        //        paramsMap.put("type", "0"); 传0表示搜索用户，传1表示搜索俱乐部
        val requestSearchUrl = com.netease.nim.uikit.api.HostManager.getHost() + ApiConstants.URL_SEARCH + NetWork.getRequestParams(paramsMap)
        val signRequest = object : SignStringRequest(Request.Method.GET, requestSearchUrl, Response.Listener { response ->
            LogUtil.i("SearchAction", response)
            DialogMaker.dismissProgressDialog()
            try {
                val json = JSONObject(response)
                val code = json.getInt("code")
                if (code == 0) {
                    mSearchDatas = JsonResolveUtil.getAdvSearchUserList(response)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            changeIsShowHistory(false)
        }, Response.ErrorListener {
            DialogMaker.dismissProgressDialog()
            changeIsShowHistory(false)
        }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return paramsMap
            }
        }
        signRequest.tag = requestSearchUrl
        ChessApp.sRequestQueue.add(signRequest)
    }

    private fun searchClubChannel(word: String) {
        mSearchAction?.searchTeamByVid(word, object : GameRequestCallback {
            override fun onSuccess(response: JSONObject?) {
                mSearchDatas = JsonResolveUtil.getSearchClubChannelList(response)
                changeIsShowHistory(false)
            }
            override fun onFailed(code: Int, response: JSONObject?) {
                changeIsShowHistory(false)
            }
        })
    }

    //下面是添加渠道的逻辑下面是添加渠道的逻辑下面是添加渠道的逻辑下面是添加渠道的逻辑下面是添加渠道的逻辑下面是添加渠道的逻辑下面是添加渠道的逻辑下面是添加渠道的逻辑下面是添加渠道的逻辑
    var requestAddMttMgrUrl: String = ""
    private fun addMttMgrPersonalChannel(ids: String, vararg positions: Int) {//批量添加私人渠道
        val creatorId = gameInfo?.creatorInfo?.account
        val gameId = gameInfo?.gid
        val gameCode = gameInfo?.code
        if (StringUtil.isEmpty(creatorId) || StringUtil.isSpace(gameCode)) {
            Toast.makeText(ChessApp.sAppContext, "信息不全", Toast.LENGTH_LONG).show()
            return
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show()
            return
        }
        DialogMaker.showProgressDialog(activity, "", false)
        val paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext)
        paramsMap.put("uid", creatorId)
        paramsMap.put("code", gameCode)
        paramsMap.put("admin_id", ids)
        requestAddMttMgrUrl = com.netease.nim.uikit.api.HostManager.getHost() + ApiConstants.URL_MTT_ADD_MGR + NetWork.getRequestParams(paramsMap)
        LogUtil.i("GameAction", requestAddMttMgrUrl)
        val signRequest = object : SignStringRequest(Request.Method.POST, requestAddMttMgrUrl, Response.Listener { response ->
            LogUtil.i("GameAction", response)
            try {
                val json = JSONObject(response)
                val code = json.getInt("code")
                if (code == 0) {
                    Toast.makeText(ChessApp.sAppContext, "添加赛事管理员成功", Toast.LENGTH_LONG).show()
                    mGameAction?.getMgrList(gameId, creatorId, gameCode, object : GameRequestCallback {
                        override fun onSuccess(response: JSONObject?) {
                            afterAddChannelSuccess(*positions)
                            ChannelListAC.dataTrigger = true
                            DialogMaker.dismissProgressDialog()
                        }
                        override fun onFailed(code: Int, response: JSONObject?) {
                            DialogMaker.dismissProgressDialog()
                        }
                    })
                } else {
                    val failMsg = ApiCode.SwitchCode(code, response)
                    Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                DialogMaker.dismissProgressDialog()
                val failMsg = ApiCode.SwitchCode(ApiCode.CODE_JSON_ERROR, null)
                Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener {
            DialogMaker.dismissProgressDialog()
            val failMsg = ApiCode.SwitchCode(ApiCode.CODE_NETWORD_ERROR, null)
            Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show()
        }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return paramsMap
            }
        }
        signRequest.tag = requestAddMttMgrUrl
        ChessApp.sRequestQueue.add(signRequest)
    }

    private fun addMttMgrClubChannel(tids: String, vararg positions: Int) {//批量添加俱乐部渠道
        val creatorId = gameInfo?.creatorInfo?.account
        val gameId = gameInfo?.gid
        val gameCode = gameInfo?.code
        mGameAction?.addClubChannel(tids, gameCode, object : GameRequestCallback {
            override fun onSuccess(response: JSONObject) {
                Toast.makeText(ChessApp.sAppContext, "添加赛事管理员成功", Toast.LENGTH_LONG).show()
                mGameAction?.getMgrList(gameId, creatorId, gameCode, object : GameRequestCallback {
                    override fun onSuccess(response: JSONObject) {
                        afterAddChannelSuccess(*positions)
                        ChannelListAC.dataTrigger = true
                        DialogMaker.dismissProgressDialog()
                    }
                    override fun onFailed(code: Int, response: JSONObject) {
                        DialogMaker.dismissProgressDialog()
                    }
                })
            }
            override fun onFailed(code: Int, response: JSONObject?) {
                DialogMaker.dismissProgressDialog()
                val failMsg = ApiCode.SwitchCode(code, response?.toString() ?: "")
                Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun afterAddChannelSuccess(vararg positions: Int) {
        var handledData = ArrayList<SearchUserBean>()//刚刚成功添加的渠道
        positions.forEach {
            mHistoryAdapter.notifyItemChanged(it)
            if (it < (mSearchDatas?.size ?: -999)) {
                handledData.add(mSearchDatas!![it])
            }
        }
        if (!showHistory) {
            ////无重复并集
            mHistoryMgrList?.list?.removeAll(handledData)//与hashCode和equails这两个api有关
            mHistoryMgrList?.list?.addAll(handledData)
        }
    }

    fun changeIsShowHistory(isSHowHistory: Boolean) {
        showHistory = isSHowHistory
        tv_add_channel_head_left?.setText(if (showHistory) R.string.history_log else R.string.club_search_head)
        mHistoryAdapter.updateData(if (showHistory) mHistoryMgrList?.list else mSearchDatas)
        mHistoryAdapter.showHistory = showHistory
        mHistoryAdapter.notifyDataSetChanged()
        judgeEmptyData()
    }

    fun judgeEmptyData() {
        if (mHistoryAdapter.itemCount <= 0) {
            val strId = if (showHistory) R.string.data_null else R.string.search_null
            mResultDataView?.nullDataShow(strId)
        } else {
            mResultDataView?.successShow()
        }
    }
}