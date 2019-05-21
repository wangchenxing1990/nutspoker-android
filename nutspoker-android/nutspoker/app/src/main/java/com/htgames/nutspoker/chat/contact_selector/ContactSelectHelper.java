package com.htgames.nutspoker.chat.contact_selector;

import android.content.Context;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact_selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.contact.core.item.ContactIdFilter;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ContactSelectHelper {
    /**
     * 打开联系人选择器
     * @param context     上下文（Activity）
     * @param option      联系人选择器可选配置项
     * @param requestCode startActivityForResult使用的请求码
     */
    public static void startContactSelect(Context context, ContactSelectActivity.Option option, int requestCode) {
        ContactSelectActivity.startActivityForResult(context, option, requestCode);
    }

    /**
     * 获取创建群通讯录选择器option
     * @return
     */
    public static ContactSelectActivity.Option getCreateContactSelectOption(ArrayList<String> memberAccounts, int teamCapacity) {
        // 限制群成员数量在群容量范围内
        int capacity = teamCapacity - (memberAccounts == null ? 0 : memberAccounts.size());
        ContactSelectActivity.Option option = getContactSelectOption(memberAccounts);
        option.maxSelectNum = capacity;
        option.maxSelectedTip = NimUIKit.getContext().getString(R.string.reach_team_member_capacity, teamCapacity);
        option.allowSelectEmpty = false;//是否允许为空
        option.alreadySelectedAccounts = memberAccounts;
        return option;
    }

    /**
     * 获取通讯录选择器option
     *
     * @param memberAccounts
     * @return
     */
    public static ContactSelectActivity.Option getContactSelectOption(List<String> memberAccounts) {
        ContactSelectActivity.Option option = new ContactSelectActivity.Option();
        option.title = NimUIKit.getContext().getString(R.string.invite_member);
        if (memberAccounts != null) {
            ArrayList<String> disableAccounts = new ArrayList<>();
            disableAccounts.addAll(memberAccounts);
            option.itemDisableFilter = new ContactIdFilter(disableAccounts);
        }
        return option;
    }

    public static ContactSelectActivity.Option getContactSelectOption(List<String> memberAccounts , int teamCapacity) {
        // 限制群成员数量在群容量范围内
        int capacity = teamCapacity - (memberAccounts == null ? 0 : memberAccounts.size());
        ContactSelectActivity.Option option = new ContactSelectActivity.Option();
        option.title = NimUIKit.getContext().getString(R.string.invite_member);
        option.maxSelectNum = capacity;
        option.maxSelectedTip = NimUIKit.getContext().getString(R.string.reach_team_member_capacity, teamCapacity);
        if (memberAccounts != null) {
            ArrayList<String> disableAccounts = new ArrayList<>();
            disableAccounts.addAll(memberAccounts);
            option.itemDisableFilter = new ContactIdFilter(disableAccounts);
        }
        return option;
    }
}
