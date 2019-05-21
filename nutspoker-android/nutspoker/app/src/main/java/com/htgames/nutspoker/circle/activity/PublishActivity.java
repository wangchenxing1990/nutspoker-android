package com.htgames.nutspoker.circle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.htgames.nutspoker.circle.bean.CircleItem;
import com.htgames.nutspoker.circle.view.CirclePaijuView;
import com.htgames.nutspoker.circle.view.CirclePaipuView;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.htgames.nutspoker.ui.action.CircleAction;
import com.htgames.nutspoker.ui.activity.Record.RecordDetailsActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 德州圈内容的发布
 */
public class PublishActivity extends BaseActivity {
    private final static String TAG = "PublishActivity";
    ViewStub mViewStub;
    int type = CircleConstant.TYPE_PAIJU;
    GameBillEntity mGameBillEntity;
    PaipuEntity mPaipuEntity;

    CirclePaijuView mCirclePaijuView;
    CirclePaipuView mCirclePaipuView;
    CircleAction mCircleAction;
    EditText edt_publish;
    public int form = RecordDetailsActivity.FROM_NORMAL;

    public static void start(Activity activity , int type , Object data){
        Intent intent = new Intent(activity , PublishActivity.class);
        intent.putExtra(Extras.EXTRA_TYPE , type);
        intent.putExtra(Extras.EXTRA_DATA, (Serializable)data);
        activity.startActivity(intent);
    }

    public static void start(Activity activity , int type , GameBillEntity data , int from){
        Intent intent = new Intent(activity , PublishActivity.class);
        intent.putExtra(Extras.EXTRA_TYPE , type);
        intent.putExtra(Extras.EXTRA_DATA, data);
        intent.putExtra(Extras.EXTRA_FROM, from);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aircle_publish);
        type = getIntent().getIntExtra(Extras.EXTRA_TYPE , CircleConstant.TYPE_PAIJU);
        form =  getIntent().getIntExtra(Extras.EXTRA_FROM, RecordDetailsActivity.FROM_NORMAL);
        Object data = getIntent().getSerializableExtra(Extras.EXTRA_DATA);
        if(type == CircleConstant.TYPE_PAIJU && data instanceof GameBillEntity){
            mGameBillEntity = (GameBillEntity)data;
        } else if(type == CircleConstant.TYPE_PAIPU && data instanceof PaipuEntity){
            mPaipuEntity = (PaipuEntity)data;
        }
//        feedBackAction = getIntent().getIntExtra(Extras.EXTRA_FEEDBACK_ACTION , 0);
        mCircleAction = new CircleAction(this , null);
        initHeadView();
        initView();
    }

    private void initHeadView() {
        setHeadTitle(R.string.main_tab_discovery);
        setHeadRightButton(R.string.send, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = edt_publish.getText().toString();
                if (type == CircleConstant.TYPE_PAIJU && mGameBillEntity != null) {
                    publishPaijuToCircle(content);
                }
            }
        });
    }

    public void publishPaijuToCircle(String content) {
        mCircleAction.doShareToCircle(content, CircleConstant.TYPE_PAIJU, mGameBillEntity.gameInfo.gid, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    Toast.makeText(getApplicationContext(), R.string.circle_share_success, android.widget.Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject json = new JSONObject(result);
                        publishSuccess(JsonResolveUtil.getCircleItem(json, mGameBillEntity));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.circle_share_failure, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed() {

            }
        });
//        Map<String, String> paramsMap = new HashMap<String, String>();
//        paramsMap.put("uid", UserPreferences.getInstance(getApplicationContext()).getUserId());
//        if (!TextUtils.isEmpty(content)) {
//            paramsMap.put("content", content);
//        }
//        paramsMap.put("pid", "0");
//        paramsMap.put("type", String.valueOf(type));
//        if (type == CircleConstant.TYPE_PAIJU) {
//            paramsMap.put("gid", mGameBillEntity.getGameInfo().getGid());
//        }
//        DialogMaker.showProgressDialog(PublishActivity.this, getString(R.string.circle_share_ing), false);
//        TexasClient.getInstance().getCircleService()
//                .shareToCircle(paramsMap)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseBody>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        DialogMaker.dismissProgressDialog();
//                        Toast.makeText(getApplicationContext(), R.string.circle_share_failure, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody response) {
//                        DialogMaker.dismissProgressDialog();
//                        try {
//                            String responseStr = response.string();
//                            Log.d(TAG, responseStr);
//                            JSONObject json = new JSONObject(responseStr);
//                            int code = json.getInt("code");
//                            if (code == 0) {
//                                Toast.makeText(getApplicationContext(), R.string.circle_share_success, android.widget.Toast.LENGTH_SHORT).show();
//                                if (feedBackAction == 0) {
//                                    publishSuccess(JsonResolveUtil.getCircleItem(json, mGameBillEntity));
//                                }
//                                finish();
//                            } else {
//                                Toast.makeText(getApplicationContext(), R.string.circle_share_failure, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
    }

    public void publishSuccess(CircleItem circleItem){
        if(form == RecordDetailsActivity.FROM_CIRCLE){
            Intent intent = new Intent(this, CircleActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Extras.EXTRA_DATA, circleItem);
            startActivity(intent);
            finish();
        } else if(form == RecordDetailsActivity.FROM_CHAT || form == RecordDetailsActivity.FROM_NORMAL){
            finish();
        }

    }

    private void initView() {
        mViewStub = (ViewStub)findViewById(R.id.mViewStub);
        edt_publish = (EditText)findViewById(R.id.edt_publish);
        mCirclePaijuView = (CirclePaijuView)findViewById(R.id.mCirclePaijuView);
        mCirclePaipuView = (CirclePaipuView)findViewById(R.id.mCirclePaipuView);
        if(type == CircleConstant.TYPE_PAIJU){
            mViewStub.setLayoutResource(R.layout.layout_circle_paiju_item);
            mViewStub.inflate();
            mViewStub.setVisibility(View.GONE);
            mCirclePaijuView.setData(mGameBillEntity);
            mCirclePaijuView.setVisibility(View.VISIBLE);
        }else if(type == CircleConstant.TYPE_PAIPU){
            mCirclePaipuView.setData(mPaipuEntity);
            mCirclePaipuView.setVisibility(View.VISIBLE);
        }
    }
}
