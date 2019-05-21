package com.htgames.nutspoker.view.record;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.java4less.rchart.Chart;
import com.java4less.rchart.FillStyle;
import com.java4less.rchart.LineStyle;
import com.java4less.rchart.PieDataSerie;
import com.java4less.rchart.PiePlotter;
import com.java4less.rchart.android.ChartPanel;
import com.java4less.rchart.gc.ChartColor;
import com.java4less.rchart.gc.ChartFont;
import com.java4less.rchart.gc.GraphicsProvider;

/**
 * 3D饼图，使用j4lchartlibrary构建
 */
public class PieChartView extends LinearLayout {
    private final String TAG = "PieChartView";
    private static int width = 720;
    private static int height = 600;
    ChartPanel chartPanel1;
    static double[] values = new double[]{30, 60, 10};//存放数值
    static boolean[] groupValues;//是否分组的标识
    static FillStyle[] fillStyleColor;//存放颜色
    static ChartColor[] color = new ChartColor[]{//颜色表
            GraphicsProvider.getColor("0xaccf65"),
            GraphicsProvider.getColor("0xd9a569"),
            GraphicsProvider.getColor("0x49bec9")};

//    static ChartColor[] color = new ChartColor[]{//颜色表
//            GraphicsProvider.getColor(ChartColor.AQUAMARINE),
//            GraphicsProvider.getColor(ChartColor.VIOLET),
//            GraphicsProvider.getColor(ChartColor.BLUEVIOLET),
//            GraphicsProvider.getColor(ChartColor.CORAL),
//            GraphicsProvider.getColor(ChartColor.GREENYELLOW),
//            GraphicsProvider.getColor(ChartColor.SKYBLUE),
//            GraphicsProvider.getColor(ChartColor.PINK),
//            GraphicsProvider.getColor(ChartColor.BORLYWOOD),
//            GraphicsProvider.getColor(ChartColor.FORESTGREEN)};

    public PieChartView(Context context) {
        super(context);
        init(context);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        groupValues = new boolean[values.length];
        fillStyleColor = new FillStyle[values.length];
        for (int i = 0; i < values.length; i++) {
            fillStyleColor[i] = new FillStyle(color[i]);
            if (i < values.length - 1) {
                groupValues[i] = false;//除了最后一个外都赋值为true
            }
        }
        chartPanel1 = new ChartPanel(context);
        chartPanel1.setChart(TheChart());
        chartPanel1.setLayoutParams(new ViewGroup.LayoutParams(720 , ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(chartPanel1);
    }

    public static Chart TheChart() {
        //new出来一个图饼的对象， 参数简介1.所占的比例 2.颜色 3.是否分组（是否种其他的块是分开的） 4.文字介绍
        PieDataSerie pds = new PieDataSerie(values, fillStyleColor, groupValues, null);
        //设置label的样式
        pds.valueFont = GraphicsProvider.getFont("Arial", ChartFont.PLAIN, 18);
        //设置label到中心的距离
        pds.textDistanceToCenter = 1.4;

        //设置图饼的标题
//        Title title = new Title("理财账户");
        //开始绘图
        PiePlotter pp = new PiePlotter();
        //设置3D效果为true
        pp.effect3D = true;
        //设置边框
        pp.border = new LineStyle(1, GraphicsProvider.getColor(ChartColor.BLACK), LineStyle.LINE_NORMAL);
        //设置label的格式（#PERCENTAGE#，#VALUE#,#LABEL#）什么也不想显示的话直接“ ”里面有一个空格就可以了
//        pp.labelFormat = "#PERCENTAGE#";
        pp.labelFormat = " ";
        //设置半径
        pp.radiusModifier = 1.5;
        //设置块与块之间的间隔
        pp.space = 10;
        pp.drawRadius = true;
        //设置label到块之间的线的样式
        pp.labelLine = new LineStyle(1, GraphicsProvider.getColor(ChartColor.BLACK), LineStyle.LINE_NORMAL);

        //生成一个对象
//        Legend legend = new Legend();
//        //因为我这里不需要解说，所以设置它为" ",中间有空格，没有空格的话，会出现多余的文字
//        legend.legendLabel = "";
//        for (int i = 0; i < values.length; i++) {
//            legend.addItem(lableName[i], new FillStyle(color[i]));
//        }
        //new一个图表对象，用来存放生成的图饼
//        com.java4less.rchart.Chart chart = new com.java4less.rchart.Chart(title, pp, null, null);
        Chart chart = new Chart(null, pp, null, null);
        chart.layout = com.java4less.rchart.Chart.LAYOUT_LEGEND_TOP;
//        chart.back = new FillStyle(GraphicsProvider.getColor(ChartColor.WHITE));//设置背景
        chart.topMargin = 0.2;
        chart.bottomMargin = 0.2;
        chart.leftMargin = 0.2;
//        chart.legend = legend;
        chart.setHeight(width / 3 * 2);
        chart.setWidth(height / 3 * 2);
        chart.addSerie(pds);
        return chart;
    }

    public void onDestroy() {
        if (chartPanel1 != null)
            if (chartPanel1.getChart() != null)
                chartPanel1.getChart().stopUpdater();
    }
}
