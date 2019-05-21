package com.htgames.nutspoker.chat.app_msg.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.activity.AppMsgInfoAC;
import com.htgames.nutspoker.chat.app_msg.adapter.NoticeAdapter;
import com.htgames.nutspoker.chat.app_msg.interfaces.AppMessageListener;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.view.ResultDataView;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.session.constant.Extras;

import java.util.ArrayList;

/**
 * 通知消息
 */
public class NoticeFragment extends BaseFragment implements AppMessageListener {
    public final static String TAG = NoticeFragment.class.getSimpleName();
    NoticeAdapter adapter;
    public ArrayList<AppMessage> noticeList = new ArrayList<>();
    public ResultDataView mResultDataView;
    public View view;
    RecyclerView mRecyclerView;
    LinearLayoutManager layoutManager;
    private boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_notice, container, false);
        initView();
        initAdapter();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            onPause();
        } else {
            onResume();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
    }

    public static NoticeFragment newInstance() {
        NoticeFragment mInstance = new NoticeFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Extras.EXTRA_SHOW_DIVIDER, true);
        mInstance.setArguments(bundle);
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentName("NoticeFragment");
    }

    private void initAdapter() {
        adapter = new NoticeAdapter(getContext(), noticeList);
        adapter.setAppMessageListener(this);
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LogUtil.i("" + newState);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int firstVisibleItemPos = linearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPos = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && totalItemCount > 5 && lastVisibleItemPos + 5 >= totalItemCount && (lastVisibleItemPos - firstVisibleItemPos) != totalItemCount) {
                    if (noticeList == null || noticeList.size() <= 0 || isLoading) {
                        return;
                    }
                    isLoading = true;
                    getAppMessageList(noticeList.get(noticeList.size() - 1).time);//数据库分页查询的第一页的时间参数是最大值
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int firstVisibleItemPos = linearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPos = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if (totalItemCount > 5 && lastVisibleItemPos + 2 >= totalItemCount && (lastVisibleItemPos - firstVisibleItemPos) != totalItemCount) {
//                    if (noticeList == null || noticeList.size() <= 0) {
//                        return;
//                    }
//                    getAppMessageList(noticeList.get(noticeList.size() - 1).time);//数据库分页查询的第一页的时间参数是最大值
                }
            }
        });
    }

    private void initView() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mResultDataView = (ResultDataView) view.findViewById(R.id.mResultDataView);
        mResultDataView.nullDataShow(R.string.app_smg_control_null_data_text, R.mipmap.img_message_null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mResultDataView.showLoading();
        new Thread() {
            @Override
            public void run() {
                super.run();
                getAppMessageList(Long.MAX_VALUE);//数据库分页查询的第一页的时间参数是最大值
            }
        }.start();
    }

    private void getAppMessageList(long time) {
        ArrayList<AppMessage> loaclAppMessageList = AppMsgDBHelper.queryAppMessageByPage(getContext(), AppMsgDBHelper.TYPE_NOTICE, time);
        if (loaclAppMessageList == null || loaclAppMessageList.size() <= 0) {
            if (noticeList == null || noticeList.size() <= 0) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mResultDataView.nullDataShow(R.string.app_smg_control_null_data_text, R.mipmap.img_message_null);
                        }
                    });
                }
            }
            return;
        }
//            noticeList.clear();
        noticeList.addAll(loaclAppMessageList);
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLocalList();
                    isLoading = false;
                }
            });
        }
    }

    public void setLocalList() {
        if (noticeList != null && noticeList.size() != 0) {
            mResultDataView.successShow();
            adapter.setData(noticeList);
            notifyAdapterDataSetChanged();
        } else {
            mResultDataView.nullDataShow(R.string.app_smg_control_null_data_text, R.mipmap.img_message_null);
        }
    }

    /**
     * 新消息接收
     * @param appMessage
     */
    public void onImcomingMessage(AppMessage appMessage) {
        for (AppMessage item : noticeList) {
            if (!TextUtils.isEmpty(item.key) && item.key.equals(appMessage.key) && item.type == appMessage.type && item.checkinPlayerId.equals(appMessage.checkinPlayerId)) {
                noticeList.remove(item);
                break;
            }
        }
        appMessage.unread = (false);
        noticeList.add(0, appMessage);
        notifyAdapterDataSetChanged();
    }

    public synchronized void notifyAdapterDataSetChanged() {
        if (adapter != null) {
            adapter.setData(noticeList);
        }
        if (noticeList != null && noticeList.size() != 0) {
            mResultDataView.successShow();
        } else {
            mResultDataView.nullDataShow(R.string.app_smg_control_null_data_text, R.mipmap.img_message_null);
        }
    }

    public void clearNotice(int deleteType) {
        int size = (noticeList == null ? 0 : noticeList.size());
        for (int i = (size - 1); i >= 0; i--) {
            if (deleteType == AppMsgInfoAC.DELETE_ALL) {
                noticeList.remove(i);
            } else if (deleteType == AppMsgInfoAC.DELETE_GAMEOVER && noticeList.get(i).type == AppMessageType.GameOver) {
                noticeList.remove(i);
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyAdapterDataSetChanged();
                DialogMaker.dismissProgressDialog();
                Toast.makeText(getContext(), R.string.clear_all_success, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAgree(AppMessage message, int position) {

    }

    @Override
    public void onReject(AppMessage message, int position) {

    }

    @Override
    public void onClick(AppMessage message, int position) {
        ((AppMsgInfoAC) getActivity()).clickMessage(message);
    }

    @Override
    public void onLongPressed(AppMessage message, int position) {
        showLongClickMenu(message);
    }

    private void showLongClickMenu(final AppMessage message) {
        CustomAlertDialog alertDialog = new CustomAlertDialog(getActivity());
        alertDialog.setTitle(R.string.delete_tip);
        String title = getString(R.string.delete_system_message);
        alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                deleteAppMessage(message);
            }
        });
        alertDialog.show();
    }

    /**
     * 删除系统消息
     */
    public void deleteAppMessage(final AppMessage message) {
        AppMsgDBHelper.deleteAppMessage(getContext(), message);
        if (noticeList.contains(message)) {
            noticeList.remove(message);
            notifyAdapterDataSetChanged();
            Toast.makeText(getContext(), R.string.delete_success, Toast.LENGTH_SHORT).show();
        }
    }
}

