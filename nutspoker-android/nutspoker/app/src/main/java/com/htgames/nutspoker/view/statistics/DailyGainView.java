package com.htgames.nutspoker.view.statistics;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.netease.nim.uikit.bean.GameInsuranceEntity;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.WinChipEntity;
import com.netease.nim.uikit.common.util.BaseTools;
import com.htgames.nutspoker.tool.DateCalendarTools;
import com.netease.nim.uikit.common.DateTools;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.htgames.nutspoker.view.ScrollListenerHorizontalScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * 每日盈利
 */
public class DailyGainView extends LinearLayout {
    private final static String TAG = "DailyGainView";
    ScrollListenerHorizontalScrollView mDailyHorizontalScrollView;
    View view;
    LinearLayout ll_daily_gain;
    LinearLayout ll_daily_gain_item;
    List<WinChipEntity> winChipList;
    TextView tv_daily_gain_show;
    TextView tv_daily_gain_date;
    private int itemWidth = 0;
    ArrayList<TextView> tvList;
    private int lastPostion = 4;
    private long currentData;//当前日期时间戳
    TextView mUiInsurance;

    public DailyGainView(Context context) {
        super(context);
        init(context);
    }

    public DailyGainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DailyGainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        currentData = DateCalendarTools.getTodayDate();
        view = LayoutInflater.from(context).inflate(R.layout.view_daily_gain, null);
        mUiInsurance = ButterKnife.findById(view,R.id.tv_record_statistics_insurance);
        mDailyHorizontalScrollView = (ScrollListenerHorizontalScrollView) view.findViewById(R.id.mDailyHorizontalScrollView);
        ll_daily_gain = (LinearLayout) view.findViewById(R.id.ll_daily_gain);
        tv_daily_gain_show = (TextView) view.findViewById(R.id.tv_daily_gain_show);
        tv_daily_gain_date = (TextView)view.findViewById(R.id.tv_daily_gain_date);
        initDailyHorizontalScrollView();
        addView(view);
    }

    public void initDailyHorizontalScrollView(){
        mDailyHorizontalScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
        mDailyHorizontalScrollView.setHandler(new Handler());
        mDailyHorizontalScrollView.setOnScrollStateChangedListener(new ScrollListenerHorizontalScrollView.ScrollViewListener() {
            @Override
            public void onScrollTypeChanged(ScrollListenerHorizontalScrollView.ScrollType scrollType) {
                if (scrollType == ScrollListenerHorizontalScrollView.ScrollType.IDLE) {
                    int scrollX = mDailyHorizontalScrollView.getScrollX();
                    int position = scrollX / itemWidth + 4;//滑动了几个的距离
                    int distance = scrollX % itemWidth;//余多少就是多滑动的距离
                    if (distance < itemWidth / 2) {
                        //向前滑动
                        check(position , true);
                    } else {
                        //向前滑动
                        check(position + 1 , true);
                    }
                }
            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if(changed) {
            onGenerateList();
            //初始数据不算
//            if(mType == 0)
            mIsInit = false;
        }
    }

    public void setDailyGainInfo(List<WinChipEntity> list , int maxBalance, List<GameInsuranceEntity> insList) {
        if (list != null && !list.isEmpty()) {
            //计算平均刻度
            if (maxBalance > 5000) {
                calibration = maxBalance / 100;//刻度
            }else{
                calibration = 5000 / 100;
            }
            winChipList = list;

            onGenerateList();
        }

        if(insList != null){
            mMapInsurance = new HashMap<>();
            for(GameInsuranceEntity data : insList){
                mMapInsurance.put(data._id,data.buy-data.pay);
            }
        } else {
            mMapInsurance = null;
        }
    }

    public void onGenerateList(){

        if(winChipList == null || winChipList.size() == 0 || getWidth() == 0)
            return;

        itemWidth = getWidth() / 9;
        ll_daily_gain.removeAllViews();

        float calibrationHeight = BaseTools.dip2px(getContext() , 140) / (float)(2 * 100);

        tvList = new ArrayList<>();
        int size = winChipList.size();
        for(int position = 0; position < size ; position++){
            WinChipEntity winChipEntity = winChipList.get(position);
            int winChip = winChipEntity.count;
            View viewItem = LayoutInflater.from(getContext()).inflate(R.layout.view_daily_gain_item, null);
            viewItem.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout ll_daily_gain_item = (LinearLayout)viewItem.findViewById(R.id.ll_daily_gain_item);
            TextView tv_daily_gain_day = (TextView)viewItem.findViewById(R.id.tv_daily_gain_day);
            View view_daily_gain = viewItem.findViewById(R.id.view_daily_gain);//盈利部分
            View view_daily_lose = viewItem.findViewById(R.id.view_daily_lose);//亏损部分
            //
            if(mType == 0)
                tv_daily_gain_day.setText(DateTools.getDailyTime_d(winChipList.get(position)._id));
            else if(mType == 1)
                tv_daily_gain_day.setText(""+winChipList.get(position)._id);

            if(position >= 4 && position < (winChipList.size() - 4)){
                ll_daily_gain_item.setOnClickListener(new OnDailyItemClick(position));
                tv_daily_gain_day.setTextColor(getResources().getColor(R.color.daily_gain_item_normal_text_color));

                float height;
                //计算单位刻度
                if(Math.abs(winChip) < (5000 / 100) || calibrationHeight < 1){
                    //小于最小刻度，当作1%
                        height = 5;
                }else{
                    height = Math.abs(winChip) / (float)(calibration) * calibrationHeight;
                }

                if(height < 20)
                    height = 20;

                if(winChip > 0){
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view_daily_gain.getLayoutParams();
                    layoutParams.height = (int)height;
                    view_daily_gain.setLayoutParams(layoutParams);
                } else if(winChip < 0){
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view_daily_lose.getLayoutParams();
                    layoutParams.height = (int)height;
                    view_daily_lose.setLayoutParams(layoutParams);
                }
            }else{
                tv_daily_gain_day.setTextColor(getResources().getColor(R.color.daily_gain_item_invalid_text_color));
            }
            tvList.add(tv_daily_gain_day);
            ll_daily_gain.addView(viewItem);
        }

        check(winChipList.size() - 5,true);
        mIsInit = true;
    }

    public void check(int position , boolean isSmooth){
        int winChips = winChipList.get(position).count;
        WealthHelper.SetMoneyText(tv_daily_gain_show,winChips,getContext());

        int x = itemWidth * (position - 4);
        if(isSmooth){
            mDailyHorizontalScrollView.smoothScrollTo(x, 0);
        }else{
            mDailyHorizontalScrollView.scrollTo(x, 0);
        }

        long theDay = winChipList.get(position)._id;
        Integer insurance = null;
        if(mMapInsurance != null){
            insurance = mMapInsurance.get(theDay);
        }
        WealthHelper.SetMoneyText(mUiInsurance,insurance==null?0:insurance,getContext());

        if(mType == 0){//日
            tv_daily_gain_date.setText(DateTools.getDailyTime_md(theDay));
        } else if (mType == 1){
            int theYear = winChipList.get(position).year;
            tv_daily_gain_date.setText(String.format("%04d年%02d月",theYear,theDay));
        }

        tvList.get(lastPostion).setTextColor(getContext().getResources().getColor(R.color.daily_gain_item_normal_text_color));
        this.lastPostion = position;
        tvList.get(position).setTextColor(getContext().getResources().getColor(R.color.daily_gain_item_selected_text_color));
    }

    class OnDailyItemClick implements OnClickListener{
        int position;

        public OnDailyItemClick(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            check(position , true);
        }
    }

    public boolean isInit() {
        return mIsInit;
    }

    public void setInit(boolean init) {
        mIsInit = init;
    }

    public void setType(int type) {
        mType = type;
    }

    int mType = 0;
    //平均刻度
    int  calibration;
    Map<Long,Integer> mMapInsurance;
    boolean mIsInit = false;
}
