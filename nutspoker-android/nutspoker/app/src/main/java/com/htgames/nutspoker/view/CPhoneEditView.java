package com.htgames.nutspoker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.CountryEntity;
import com.netease.nim.uikit.constants.CountryCodeConstants;
import com.netease.nim.uikit.constants.CountryCodeHelper;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;

import java.util.HashMap;
import java.util.Map;

/**
 * 国家选择的手机输入框
 */
public class CPhoneEditView extends LinearLayout {
    private final static String TAG = "CPhoneEditView";
    View view;
//    EditText edt_phone_country;
    TextView tv_phone_country_code;
    ClearableEditTextWithIcon edt_phone;
    Button btn_region;
    String phoneHint = "";
    String showPrefix = "";
    Map<String, String> countryCodeMap = new HashMap<String, String>();
    private String currentCountryCode;

    public CPhoneEditView(Context context) {
        super(context);
        init(context);
    }

    public CPhoneEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init(context);
    }

    public CPhoneEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CPhoneEditView);
        phoneHint = typeArray.getString(R.styleable.CPhoneEditView_phoneHint);
        showPrefix = typeArray.getString(R.styleable.CPhoneEditView_showPrefix);
        if(TextUtils.isEmpty(showPrefix)){
            showPrefix = "";
        }
        typeArray.recycle();/*关闭资源*/
    }

    private void init(Context context) {
        currentCountryCode = CountryCodeHelper.getCurrentLocalCountryCode();
        LogUtil.i(TAG, "currentCountryCode :" + currentCountryCode);
        countryCodeMap = CountryCodeConstants.getCountryCodeMap(context);
        view = LayoutInflater.from(context).inflate(R.layout.view_cphone_edit, null);
        initView();
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void initView() {
//        edt_phone_country = (EditText) view.findViewById(R.id.edt_phone_country);
        tv_phone_country_code = (TextView) view.findViewById(R.id.tv_phone_country_code);
        edt_phone = (ClearableEditTextWithIcon) view.findViewById(R.id.edt_phone);
        edt_phone.setHint(phoneHint);
        btn_region = (Button) view.findViewById(R.id.btn_region);
//        edt_phone_country.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String code = s.toString();
//                if (countryCodeMap.containsKey(code)) {
//                    currentCountryCode = code;
//                    btn_region.setText(showPrefix + countryCodeMap.get(currentCountryCode));
//                } else {
//                    btn_region.setText(R.string.country_code_invalid);
//                }
//            }
//        });
        tv_phone_country_code.setText("+" + currentCountryCode);
        btn_region.setText(showPrefix + countryCodeMap.get(currentCountryCode));
    }

    public void setCountryClick(OnClickListener onClickListener) {
        btn_region.setOnClickListener(onClickListener);
        tv_phone_country_code.setOnClickListener(onClickListener);
    }

    public void setPhone(String phone) {
        edt_phone.setText(phone);
    }

    public void setCountry(CountryEntity countryEntity) {
        setCountryCode(countryEntity.countryCode);
    }

    public void setCountryCode(String countryCode) {
        LogUtil.i(TAG, "setCountryCode :" + currentCountryCode);
        currentCountryCode = countryCode;
        if (countryCodeMap.containsKey(currentCountryCode)) {
//            edt_phone_country.setText(currentCountryCode);
            tv_phone_country_code.setText("+" + currentCountryCode);
            btn_region.setText(showPrefix + countryCodeMap.get(currentCountryCode));
        } else {
            btn_region.setText(R.string.country_code_invalid);
        }
    }

    public String getCountryCode() {
        return currentCountryCode;
    }

    public String getPhone() {
        return edt_phone.getText().toString();
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        edt_phone.addTextChangedListener(textWatcher);
    }
}
