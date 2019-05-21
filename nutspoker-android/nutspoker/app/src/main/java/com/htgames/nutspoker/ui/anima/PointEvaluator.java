package com.htgames.nutspoker.ui.anima;

import android.graphics.Point;

import com.nineoldandroids.animation.TypeEvaluator;

/**
 */
public class PointEvaluator implements TypeEvaluator {

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        Point startPoint = (Point) startValue;
        Point endPoint = (Point) endValue;
        float x = startPoint.x + fraction * (endPoint.x - startPoint.x);
        float y = startPoint.y + fraction * (endPoint.y - startPoint.y);
        Point point = new Point((int)x, (int)y);
        return point;
    }

}