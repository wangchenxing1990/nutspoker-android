package com.htgames.nutspoker.ui.adapter.team;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.session.SessionHelper;
import com.htgames.nutspoker.chat.session.activity.TeamMessageAC;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.ui.action.GameAction;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 周智慧 on 17/2/28.
 */

public class ClubAdapterRecyc extends RecyclerView.Adapter<ClubAdapterRecyc.ClubVH> {
    public static final int ITEM_TYPE_ITEM = 0;
    public static final int ITEM_TYPE_TEXT_VIEW = 1;
    private final LayoutInflater mLayoutInflater;
    private final Activity activity;
    private RecyclerView.LayoutParams rootLayoutParams;
    public static HashMap<String, Integer> mAllGameList = new HashMap<String, Integer>();//<teamId, 牌局个数>
    private GameAction mGameAction;
    private GradientDrawable myBg;
    private GradientDrawable othersBg;
    private Drawable muteDrawable;
    private Drawable paijuDrawable;
    public ClubAdapterRecyc(Activity context) {
        activity = context;
        mLayoutInflater = LayoutInflater.from(context);
        initBg();
    }

    private void initBg() {
        //背景还是programmically设置吧，不要读取xml老的背景在bg_list_club_my和bg_list_club_others两个文件，会出现crash-->java.lang.IllegalArgumentException: radius must be > 0
        //android:gradientRadius="280dp"的设置有坑
        //https://issuetracker.google.com/issues/37007621
        int myStartColor = activity.getResources().getColor(R.color.bg_club_my_start);//Color.parseColor("#E1C481");
        int myEndColor = activity.getResources().getColor(R.color.bg_club_my_end);//Color.parseColor("#9D7B3C");
        myBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] { myStartColor, myEndColor});
        myBg.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        myBg.setGradientCenter(0.7f, 0.22f);
        myBg.setGradientRadius(ScreenUtil.dp2px(activity, 280));
        myBg.setCornerRadius(activity.getResources().getDimension(R.dimen.bg_club_common_radius));
        int otherStartColor = activity.getResources().getColor(R.color.bg_club_other_start);//Color.parseColor("#4CB5CE");
        int otherEndColor = activity.getResources().getColor(R.color.bg_club_other_end);//Color.parseColor("#2F6071");
        othersBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] { otherStartColor, otherEndColor});
        othersBg.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        othersBg.setGradientCenter(0.7f, 0.22f);
        othersBg.setGradientRadius(ScreenUtil.dp2px(activity, 280));
        othersBg.setCornerRadius(activity.getResources().getDimension(R.dimen.bg_club_common_radius));
        muteDrawable = activity.getResources().getDrawable(R.drawable.icon_chatroom_mute);
        muteDrawable.setBounds(0, 0, muteDrawable.getIntrinsicWidth(), muteDrawable.getIntrinsicHeight());
        paijuDrawable = activity.getResources().getDrawable(R.mipmap.tags_caln_list_game);
        paijuDrawable.setBounds(0, 0, paijuDrawable.getIntrinsicWidth(), paijuDrawable.getIntrinsicHeight());
    }

    private void calculateBgSize(View rootView) {
        //bg_club_me图片比例是360 * 120，上部深色补丁的比例是360 * 36
        float marginLeftRight = ScreenUtil.dp2px(activity, 8);
        float screenWidth = ScreenUtil.getScreenWidth(activity);
        float bgWidth = (int) screenWidth - marginLeftRight * 2;
        float height = ScreenUtil.dp2px(activity, 110);
        rootLayoutParams = new RecyclerView.LayoutParams((int) bgWidth, (int) height);
        rootLayoutParams.setMargins((int) marginLeftRight, (int) marginLeftRight, 0, 0);
        rootView.setLayoutParams(rootLayoutParams);
    }

    @Override
    public ClubAdapterRecyc.ClubVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == ITEM_TYPE_ITEM) {
            view = mLayoutInflater.inflate(R.layout.list_club_item_recyc, parent, false);
            calculateBgSize(view);//同比例缩放背景图片
        } else if (viewType == ITEM_TYPE_TEXT_VIEW) {
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view = new TextView(activity);
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            ((TextView) view).setTextColor(activity.getResources().getColor(R.color.gray_auxiliary_text_color));
            ((TextView) view).setPadding(0, ScreenUtil.dp2px(activity, 10), 0, ScreenUtil.dp2px(activity, 5));
            ((TextView) view).setGravity(Gravity.CENTER);
            ((TextView) view).setText(activity.getResources().getString(R.string.club_num, ChessApp.teamList.size()));
            ((TextView) view).setLayoutParams(lp);
        }
        return new ClubAdapterRecyc.ClubVH(activity, view);
    }

    @Override
    public void onBindViewHolder(ClubAdapterRecyc.ClubVH holder, int position) {
        holder.bind(position < ChessApp.teamList.size() ? ChessApp.teamList.get(position) : null, position);
        holder.setTextNum(ChessApp.teamList.size());
    }

    @Override
    public int getItemCount() {
        return ChessApp.teamList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position < ChessApp.teamList.size() ? ITEM_TYPE_ITEM : ITEM_TYPE_TEXT_VIEW;
    }

    public void setData(ArrayList<Team> list) {
        notifyDataSetChanged();
        getPaijuDataByTeamId();
    }

    public void onDestroy() {
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
    }

    public void getPaijuDataByTeamId() {
        if (mGameAction == null) {
            mGameAction = new GameAction(activity, null);
        }
        if (ChessApp.teamList.isEmpty() || !NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
        long currentTime = DemoCache.getCurrentServerSecondTime();
        long lastGetTime = RequestTimeLimit.lastGetGamePlayingTime;
        if ((currentTime - lastGetTime) < RequestTimeLimit.GET_GAME_PLAYING_TIME_LIMIT) {
            LogUtil.i(GameAction.TAG, "获取数据时间未到");
            return;
        }
        //{"code":0,"message":"ok","data":[{"tid":"13325223","count":"0"},{"tid":"13108800","count":1}]}
        mGameAction.getGamePlayingList(ChessApp.teamList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.i(GameAction.TAG, response);
                        try {
                            JSONObject json = new org.json.JSONObject(response);
                            int code = json.getInt("code");
                            if (code == 0) {
                                RequestTimeLimit.lastGetGamePlayingTime = DemoCache.getCurrentServerSecondTime();
                                JSONArray gameArray = json.getJSONArray("data");
                                int size = gameArray.length();
                                for (int i = 0; i < size; i++) {
                                    JSONObject teamJson = gameArray.getJSONObject(i);
                                    String teamId = teamJson.getString("tid");
                                    int paijuNum = Integer.parseInt("" + teamJson.get("count"));
                                    mAllGameList.put(teamId, paijuNum);
                                }
                                notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
    }




    public class ClubVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_club_info_name;
        public TextView tv_club_info_member_count;
        public HeadImageView iv_club_head;
        public TextView club_list_has_paiju_tv;
        public TextView club_list_bottom_msg_num_tv;
        public ImageView club_list_bottom_msg_num_iv;
        public TextView club_list_bottom_newest_msg_tv;
        public TextView clubNumTV;
        Team team;
        int unreadNum;
        private final WeakReference<Activity> weakActivity;
        public ClubVH(Activity context, View itemView) {
            super(itemView);
            weakActivity = new WeakReference<Activity>(context);
            if (itemView instanceof TextView) {
                clubNumTV = (TextView) itemView;
                clubNumTV.setOnClickListener(this);
            } else {
                itemView.setOnClickListener(this);
                tv_club_info_name = (TextView) itemView.findViewById(R.id.tv_club_info_name);
                tv_club_info_member_count = (TextView) itemView.findViewById(R.id.tv_club_info_member_count);
                iv_club_head = (HeadImageView) itemView.findViewById(R.id.iv_club_head);
                club_list_has_paiju_tv = (TextView) itemView.findViewById(R.id.club_list_has_paiju_tv);
                club_list_bottom_msg_num_tv = (TextView) itemView.findViewById(R.id.club_list_bottom_msg_num_tv);
                club_list_bottom_msg_num_iv = (ImageView) itemView.findViewById(R.id.club_list_bottom_msg_num_iv);
                club_list_bottom_newest_msg_tv = (TextView) itemView.findViewById(R.id.club_list_bottom_newest_msg_tv);
            }
        }

        public void setTextNum(int num) {
            if (clubNumTV != null) {
                clubNumTV.setText(num + "个俱乐部"); //activity.getResources().getString(R.string.club_num, num)
            }
        }

        public void bind(Team data, int position) {
            if (data == null) {
                return;
            }
            team = data;
            int paijuNum = 0;
            for (Map.Entry<String, Integer> entry : mAllGameList.entrySet()) {
                if (team.getId().equals(entry.getKey())) {
                    paijuNum = entry.getValue();
                    break;
                }
            }
            String extServer = team.getExtServer();
            itemView.setBackgroundDrawable(team.getCreator().equals(DemoCache.getAccount()) ? myBg : othersBg);
            tv_club_info_name.setCompoundDrawables(null, null, team.mute() ? muteDrawable : null, null);
            tv_club_info_name.setText(team.getName());
            tv_club_info_member_count.setText(team.getMemberCount() + "/" + ClubConstant.getClubMemberLimit(team));
            iv_club_head.loadClubAvatarByUrl(team.getId() , ClubConstant.getClubExtAvatar(extServer), HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
            if (paijuNum <= 0) {
                club_list_has_paiju_tv.setText("暂无牌局");
                club_list_has_paiju_tv.setCompoundDrawables(null, null, null, null);
            } else {
                club_list_has_paiju_tv.setText(paijuNum + " 进行中");
                club_list_has_paiju_tv.setCompoundDrawables(paijuDrawable, null, null, null);
            }
            unreadNum = ChessApp.unreadChatNumPerTeam.get(team.getId()) == null ? 0 : ChessApp.unreadChatNumPerTeam.get(team.getId()).intValue();
            if (unreadNum <= 0) {
                club_list_bottom_msg_num_tv.setVisibility(View.GONE);
                club_list_bottom_msg_num_iv.setVisibility(View.GONE);
            } else {
                if (unreadNum > 99) {
                    club_list_bottom_msg_num_tv.setText(R.string.new_message_count_max);
                } else if (unreadNum > 1) {
                    club_list_bottom_msg_num_tv.setText("" + unreadNum);
                }
                club_list_bottom_msg_num_tv.setVisibility(unreadNum > 1 ? View.VISIBLE : View.GONE);
                club_list_bottom_msg_num_iv.setVisibility(unreadNum == 1 ? View.VISIBLE : View.GONE);
            }
            club_list_bottom_newest_msg_tv.setText(ChessApp.newestMsgContentPerTeam.get(team.getId()));
        }
        @Override
        public void onClick(View v) {
            if (v == itemView) {
                if (weakActivity != null && weakActivity.get() != null && team != null) {
                    if (unreadNum > 0) {
                        TeamMessageAC.start(weakActivity.get(), team.getId(), SessionHelper.getTeamCustomization(team.getId()), TeamMessageAC.PAGE_TYPE_CHAT);
                    } else {
                        SessionHelper.startTeamSession(weakActivity.get(), team.getId());
                    }
                }
            }
        }
    }
}
