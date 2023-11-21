package com.google.ar.core.codelab.common.rendering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * This is the CircleOrientation is display a empty circle that set at the center of the screen
 * to make sure the CenterOrientationRenderer to move into the it when both roll and pitch = 0' and 0'
 */
public class CircleOrientationRenderer extends View {
    private final Paint paint = new Paint();
    public CircleOrientationRenderer(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) { // Override the onDraw() Method
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(20);

        //center
        int x0 = canvas.getWidth()/2;
        int y0 = canvas.getHeight()/2;
        int radius  = canvas.getHeight()/9;
        //draw guide circle
        canvas.drawCircle(x0, y0, radius, paint);
    }
}
