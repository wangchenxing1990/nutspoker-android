package com.htgames.nutspoker.chat.app_msg.helper;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.attach.AppNotify;
import com.htgames.nutspoker.chat.app_msg.attach.BuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.attach.GameOverNotify;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsResultNotify;
import com.htgames.nutspoker.chat.app_msg.contact.AppMessageConstants;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.receiver.AppMessageReceiver;
import com.htgames.nutspoker.chat.helper.UserInfoShowHelper;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.game.model.GameMatchBuyType;
import com.htgames.nutspoker.game.model.GameMatchStatus;
import com.htgames.nutspoker.game.model.GameStatus;
import com.netease.nim.uikit.session.constant.Extras;

/**
 */
public class AppMessageHelper {

    //是否是已知的消息类型
    public static boolean isKnowAppMessage(AppMessage appMessage) {
        if (appMessage == null) {
            return false;
        }
        AppMessageType type = appMessage.type;
        if (type == AppMessageType.GameBuyChips
                || type == AppMessageType.AppNotice
                || type == AppMessageType.GameOver
                || type == AppMessageType.MatchBuyChips
                || type == AppMessageType.MatchBuyChipsResult) {
            return true;
        }
        return false;
    }

    //收到新的系统消息，发送通知
    public static void sendAppMessageIncoming(Context context , AppMessage appMessage) {
        Intent intent = new Intent();
        intent.setAction(AppMessageReceiver.ACTION_APP_MESSAGE);
        intent.putExtra(Extras.EXTRA_APP_MESSAGE , appMessage);
        context.sendBroadcast(intent);
    }

    public static String getVerifyNotificationDealResult(AppMessage message) {
        String handledNickname = "";
        if (message.attachObject instanceof MatchBuyChipsNotify) {
            //这种控制中心加上处理者的nickname
            handledNickname = ((MatchBuyChipsNotify) message.attachObject).handledNickname;
//            if (TextUtils.isEmpty(handledNickname)) {
//                handledNickname = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount());
//            }
//            if (handledNickname.equals(NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()))) {
//                handledNickname = "您";
//            }
            if (!TextUtils.isEmpty(handledNickname)) {
                handledNickname = handledNickname + " ";
            }
        }
        if (message.status == AppMessageStatus.passed) {
            return handledNickname + DemoCache.getContext().getString(R.string.message_status_passed);
        } else if (message.status == AppMessageStatus.declined) {
            return handledNickname + DemoCache.getContext().getString(R.string.message_status_declined);
        } else if (message.status == AppMessageStatus.ignored) {
            return DemoCache.getContext().getString(R.string.message_status_ignored);
        } else if (message.status == AppMessageStatus.expired) {
            return DemoCache.getContext().getString(R.string.message_status_expired);
        } else {
            return DemoCache.getContext().getString(R.string.message_status_untreated);
        }
    }

    /**
     * 是否验证消息需要处理（需要有同意拒绝的操作栏）
     */
    public static boolean isVerifyMessageNeedDeal(AppMessage message) {
        if ((message.type == AppMessageType.GameBuyChips || message.type == AppMessageType.MatchBuyChips) && message.status == AppMessageStatus.init) {
            return true;
        }
        return false;
//        if (message.type == AppMessageType.AddFriend) {
//            if (message.attachObject != null) {
//                AddFriendNotify attachData = (AddFriendNotify) message.attachObject;
//                if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT ||
//                        attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND ||
//                        attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
//                    return false; // 对方直接加你为好友，对方通过你的好友请求，对方拒绝你的好友请求
//                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
//                    return true; // 好友验证请求
//                }
//            }
//            return false;
//        } else if (message.type == SystemMessageType.TeamInvite || message.type == SystemMessageType.ApplyJoinTeam) {
//            return true;
//        } else {
//            return false;
//        }
    }

    /**
     * 获取App消息显示内容
     * @param context
     * @param message
     * @return
     */
    public static String getAppMessageContent(Context context , AppMessage message) {
        String content = message.content;//“”
        if (message.attachObject instanceof BuyChipsNotify) {
            BuyChipsNotify buyChipsNotify = (BuyChipsNotify) message.attachObject;
            if (buyChipsNotify != null) {
                String nickname = buyChipsNotify.userNickname;
                String fromId = message.fromId;
                if (buyChipsNotify.gameMode == GameConstants.GAME_MODE_NORMAL) {
                    content = context.getString(R.string.app_message_buychips_recent_content, UserInfoShowHelper.getUserNickname(fromId, nickname));
                } else if (buyChipsNotify.gameMode == GameConstants.GAME_MODE_SNG) {
                    content = context.getString(R.string.app_message_buychips_recent_content, UserInfoShowHelper.getUserNickname(fromId, nickname));
                }
            }
        } else if (message.type == AppMessageType.AppNotice) {
            AppNotify appNotify = (AppNotify) message.attachObject;
            if (appNotify != null && !TextUtils.isEmpty(appNotify.content)) {
                content = appNotify.content;
            }
        } else if (message.attachObject instanceof GameOverNotify) {
            GameOverNotify gameOverNotify = (GameOverNotify) message.attachObject;
            if (gameOverNotify != null) {
                if (gameOverNotify.gameStatus == GameStatus.GAME_STATUS_START) {
                    content = context.getString(R.string.app_message_gamestatus_start_content, gameOverNotify.gameName);
                } else if (gameOverNotify.gameStatus == GameStatus.GAME_STATUS_FINISH) {
                    if (gameOverNotify.gameMode == GameConstants.GAME_MODE_NORMAL) {
                        content = context.getString(R.string.app_message_gamestatus_match_content, gameOverNotify.gameName);
                    } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_SNG) {
                        int balance = gameOverNotify.userBalance;
                        int endType = gameOverNotify.gameEndType;//结束类型
                        if (endType == AppMessageConstants.TYPE_GAMEOVER_END_NORMAL) {
                            content = context.getString(R.string.app_message_gamestatus_match_content, gameOverNotify.gameName);
                        }
                        else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
//                            content = "您参与的\\\"1115安卓6的SNG1的牌局\\\",由房主提前解散,返还220德州币";gameOverNotify.getUserBalance() = 200是报名费;
                            content = "比赛“" + gameOverNotify.gameName + "”" + "由房主提前解散，返还报名费";
                        }
                        else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_TIMEOUT) {
                            content = context.getString(R.string.app_message_gamestatus_match_end_by_timeout_content,
                                    gameOverNotify.gameName);
                        }
                    } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_MTT) {
                        int endType = gameOverNotify.gameEndType;//结束类型
                        int balance = gameOverNotify.userBalance;
                        if (endType == AppMessageConstants.TYPE_GAMEOVER_END_NORMAL) {
                            content = context.getString(R.string.app_message_gamestatus_match_content, gameOverNotify.gameName);
                        } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                            content = context.getString(R.string.app_message_gamestatus_match_end_by_creator_content,
                                    gameOverNotify.gameName);
                        } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_PLAYER_NOT_ENOUGH) {
                            //人数不够
                            content = context.getString(R.string.app_message_gamestatus_match_end_by_player_not_enough_content,
                                    gameOverNotify.gameName);
                        }
                    } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_MT_SNG) {
                        int endType = gameOverNotify.gameEndType;//结束类型
                        int balance = gameOverNotify.userBalance;
                        if (endType == AppMessageConstants.TYPE_GAMEOVER_END_NORMAL) {
                            content = context.getString(R.string.app_message_gamestatus_match_content, gameOverNotify.gameName);
                        } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                            content = context.getString(R.string.app_message_gamestatus_match_end_by_creator_content,
                                    gameOverNotify.gameName);
                        } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_PLAYER_NOT_ENOUGH) {
                            //人数不够
                            content = context.getString(R.string.app_message_gamestatus_match_end_by_player_not_enough_content,
                                    gameOverNotify.gameName);
                        }
                    }
                } else if (gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_REST_FINISH) {
                    content = context.getString(R.string.app_message_gamestatus_match_rest_finish_content, gameOverNotify.gameName);
                } else if (gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_WILL_START) {
                    content = context.getString(R.string.app_message_gamestatus_match_will_start_content, gameOverNotify.gameName);
                } else if (gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_PAUSE) {
                    content = context.getString(R.string.app_message_gamestatus_match_pause_content, gameOverNotify.gameName);
                } else if(gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_RESUME) {
                    content = message.content;
                }
            }
        } else if (message.attachObject instanceof MatchBuyChipsNotify) {
            MatchBuyChipsNotify matchBuyChipsNotify = (MatchBuyChipsNotify) message.attachObject;
            if (matchBuyChipsNotify != null) {
                String nickname = matchBuyChipsNotify.userNickname;
                String fromId = message.fromId;
                if (matchBuyChipsNotify.gameMode == GameConstants.GAME_MODE_MTT) {
                    int buyType = matchBuyChipsNotify.buyType;
                    if (buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
                        if (TextUtils.isEmpty(message.targetId)) {
                            content = context.getString(R.string.app_message_buychips_checkin_recent_content, UserInfoShowHelper.getUserNickname(fromId, nickname));
                        } else {
                            String action = message.status == AppMessageStatus.passed ? " 已同意 " : (message.status == AppMessageStatus.declined ? " 已拒绝 " : "");
                            content = matchBuyChipsNotify.handledNickname + action + UserInfoShowHelper.getUserNickname(message.targetId, nickname) + "的参赛请求";
                        }
                    } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY || buyType == GameMatchBuyType.TYPE_BUY_REBUY_WEEDOUT || buyType == GameMatchBuyType.TYPE_BUY_REBUY_REVIVAL) {
                        content = context.getString(R.string.app_message_buychips_rebuy_recent_content, UserInfoShowHelper.getUserNickname(fromId, nickname));
                    } else if (buyType == GameMatchBuyType.TYPE_BUY_ADDON || buyType == GameMatchBuyType.TYPE_BUY_ADDON_WEEDOUT) {
                        content = context.getString(R.string.app_message_buychips_addon_recent_content, UserInfoShowHelper.getUserNickname(fromId, nickname));
                    }
                }else if (matchBuyChipsNotify.gameMode == GameConstants.GAME_MODE_MT_SNG) {
                    content = context.getString(R.string.app_message_buychips_checkin_recent_content, UserInfoShowHelper.getUserNickname(fromId, nickname));
                }
            }
        } else if (message.attachObject instanceof MatchBuyChipsResultNotify) {
            MatchBuyChipsResultNotify resultNotify = (MatchBuyChipsResultNotify) message.attachObject;
            if (resultNotify != null) {
                int buyType = resultNotify.buyType;
                String buyTypeStr = context.getString(R.string.match);
                if (buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
                    buyTypeStr = context.getString(R.string.match);
                } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY || resultNotify.buyType == GameMatchBuyType.TYPE_BUY_REBUY_WEEDOUT) {
                    buyTypeStr = context.getString(R.string.rebuy);
                } else if (buyType == GameMatchBuyType.TYPE_BUY_ADDON || resultNotify.buyType == GameMatchBuyType.TYPE_BUY_ADDON_WEEDOUT) {
                    buyTypeStr = context.getString(R.string.addon);
                }
                if (message.status == AppMessageStatus.passed) {
                    content = context.getString(R.string.app_message_match_buychips_result_content_agree, buyTypeStr, resultNotify.gameName);
                } else if (message.status == AppMessageStatus.declined) {
                    content = context.getString(R.string.app_message_match_buychips_result_content_rejcet, buyTypeStr, resultNotify.gameName);
                }
            }
        }
        return content;
    }
}
