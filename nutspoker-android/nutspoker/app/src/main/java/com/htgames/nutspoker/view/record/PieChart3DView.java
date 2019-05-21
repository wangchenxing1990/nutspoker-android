package com.htgames.nutspoker.view.record;

/**
 * 3D饼图
 */

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.htgames.nutspoker.R;

public class PieChart3DView extends View {
    private int[] data;             //数据
    private String[] info;          //信息
    private float[] percent;        //比例
    private float depth;            //深度
    private int[] colors =          //颜色
            new int[]{getResources().getColor(R.color.statistics_chart_analysis_blue_color) ,
                    getResources().getColor(R.color.statistics_chart_analysis_green_color) ,
                    getResources().getColor(R.color.statistics_chart_analysis_orange_color)};
    private int[] colorsShape =          //阴影颜色
            new int[]{getResources().getColor(R.color.statistics_chart_analysis_blue_shape_color) ,
                    getResources().getColor(R.color.statistics_chart_analysis_green_shape_color) ,
                    getResources().getColor(R.color.statistics_chart_analysis_orange_shape_color)};
    private String[] dataContents =          //显示的内容
            new String[]{getResources().getString(R.string.data_analysis_chart_analysis_fanpai) ,
                    getResources().getString(R.string.data_analysis_chart_analysis_tanpai)  ,
                    getResources().getString(R.string.data_analysis_chart_analysis_other) };
    private Paint mainPaint;        //轮廓画笔
    private int ScrHeight;          //屏幕高度
    private int ScrWidth;           //屏幕宽度
    private Paint arrPaint;         //填充画笔
    private Paint textPaint = null; //文本画笔

    public PieChart3DView(Context context) {
        super(context);
        init(context);
    }

    public PieChart3DView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PieChart3DView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
    }

    //构造函数
    public PieChart3DView(Context context, int[] data, String[] info) {
        super(context);
        this.data = data;
        this.info = info;
        initData();
        initPaint();
    }

    public void setData(int[] data, String[] info){
        this.data = data;
        this.info = info;
        initData();
        initPaint();
    }

    //初始化画笔
    private void initPaint() {
        //线的颜色
        mainPaint = new Paint();
//        mainPaint.setAntiAlias(true);//抗锯齿
        mainPaint.setColor(getResources().getColor(R.color.statistics_chart_analysis_line_shape_color));
//        mainPaint.setColor(Color.TRANSPARENT);
//        mainPaint.setColor(Color.GRAY);
        mainPaint.setStyle(Style.STROKE);
        mainPaint.setStrokeWidth(3);

        arrPaint = new Paint();
        arrPaint.setStyle(Style.FILL);
        BlurMaskFilter PaintBGBlur = new BlurMaskFilter(2, BlurMaskFilter.Blur.SOLID);
        arrPaint.setMaskFilter(PaintBGBlur);

        textPaint = new Paint();
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(13);
        textPaint.setTypeface(Typeface.DEFAULT);
    }

    //数据转化比例
    private void initData() {
        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        percent = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            percent[i] = (float) data[i] / sum * 360;
        }
    }

    //绘图
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        ScrHeight = getHeight();
        ScrWidth = getWidth();

        float cirX = ScrWidth / 2;
        float cirY = ScrHeight / 2;
        float radius = ScrHeight / 2;
        depth = radius / 10;

        final float p = 0.5f;
        float arcLeft = cirX - radius;
        float arcTop = cirY - radius * p;
        float arcRight = cirX + radius;
        float arcBottom = cirY + radius * p;
        RectF arcRF0;
        for (int j = (int) depth; j >= 0; j--) {
            arcRF0 = new RectF(arcLeft, arcTop + j, arcRight, arcBottom + j);
            float CurrPer = -20;
            for (int i = 0; i < percent.length; i++) {
                arrPaint.setColor(colors[i]);
                canvas.drawArc(arcRF0, CurrPer, percent[i], true, arrPaint);
                if (j == 1 && CurrPer > 0 && CurrPer < 180)
//                    mainPaint.setColor(colorsShape[i]);
                    canvas.drawLine(cirX + (float) Math.cos(CurrPer / 180 * Math.PI) * radius,
                            cirY + (float) Math.sin(CurrPer / 180 * Math.PI) * radius * p,
                            cirX + (float) Math.cos(CurrPer / 180 * Math.PI) * radius,
                            cirY + (float) Math.sin(CurrPer / 180 * Math.PI) * radius * p + 1f + depth,
                            mainPaint);
                if (j == 0 || j == depth)
                    canvas.drawArc(arcRF0, CurrPer, percent[i], true, mainPaint);
                CurrPer += percent[i];
            }
        }
        for(int i = 0; i < percent.length; i++){
            textPaint.setColor(colors[i]);
            float CurrPer = -20;
            float x = cirX + (float) Math.cos(CurrPer / 180 * Math.PI) * radius;
            float y = cirY + (float) Math.sin(CurrPer / 180 * Math.PI) * radius * p;
//            canvas.drawText(dataContents[i] , x , y + 10, textPaint);
            //如果是饼图下方的，高度要减
        }
    }
}