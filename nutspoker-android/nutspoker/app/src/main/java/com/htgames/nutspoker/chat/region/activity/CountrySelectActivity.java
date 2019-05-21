package com.htgames.nutspoker.chat.region.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.CountryEntity;
import com.netease.nim.uikit.constants.CountryCodeConstants;
import com.htgames.nutspoker.chat.region.adapter.CountryCodeAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.ResultDataView;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nim.uikit.session.constant.Extras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 */
public class CountrySelectActivity extends BaseActivity implements View.OnClickListener {
    ListView lv_list;
    ResultDataView mResultDataView;
    ArrayList<CountryEntity> allCountryCodeList;
    ArrayList<CountryEntity> countryCodeList;
    CountryCodeAdapter mCountryCodeAdapter;
    //
    private LivIndex litterIdx;
    //
    SearchView searchView;
    TextView tv_search_cancel;

    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, CountrySelectActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);
        setHeadTitle(R.string.country_select_head);
        initView();
        initList();
        initSearchView();
        buildLitterIdx();
    }

    private void initList() {
        allCountryCodeList = CountryCodeConstants.getCountryList(getApplicationContext());
        Collections.sort(allCountryCodeList, comparator);//排序
        countryCodeList = (ArrayList<CountryEntity>) allCountryCodeList.clone();
        //
        mCountryCodeAdapter = new CountryCodeAdapter(getApplicationContext(), countryCodeList);
        lv_list.setAdapter(mCountryCodeAdapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryEntity countryEntity = (CountryEntity) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra(Extras.EXTRA_DATA, countryEntity);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void buildLitterIdx() {
        LetterIndexView livIndex = (LetterIndexView) findViewById(R.id.liv_index);
        livIndex.setNormalColor(getResources().getColor(R.color.contacts_letters_color));
        TextView litterHit = (TextView) findViewById(R.id.tv_hit_letter);
        litterIdx = mCountryCodeAdapter.createLivIndex(lv_list, livIndex, litterHit);
        litterIdx.show();
    }

    Comparator<CountryEntity> comparator = new Comparator<CountryEntity>() {
        @Override
        public int compare(CountryEntity o1, CountryEntity o2) {
            return o1.sortKey.compareTo(o2.sortKey);
        }
    };

    private void initView() {
        lv_list = (ListView) findViewById(R.id.lv_list);
        mResultDataView = (ResultDataView) findViewById(R.id.mResultDataView);
        mResultDataView.successShow();
    }

    private void initSearchView() {
        searchView = (SearchView) findViewById(R.id.mSearchView);
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchKey(newText);
                return true;
            }
        });
        //
        tv_search_cancel = (TextView) findViewById(R.id.tv_search_cancel);
        tv_search_cancel.setOnClickListener(this);
    }

    public void searchKey(String key) {
        countryCodeList.clear();
        if (TextUtils.isEmpty(key)) {
            countryCodeList.addAll(allCountryCodeList);
            mCountryCodeAdapter.notifyDataSetChanged();
            return;
        }
//        mClubAdapter.setKeyWord(key);
        for (CountryEntity countryEntity : allCountryCodeList) {
            if (countryEntity.countryName.contains(key) || countryEntity.countryCode.contains(key)) {
                countryCodeList.add(countryEntity);
            }
        }
        mCountryCodeAdapter.notifyDataSetChanged();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                searchView.setQuery("", true);
                searchView.setIconified(true);
                showKeyboard(false);
                break;
        }
    }
}
