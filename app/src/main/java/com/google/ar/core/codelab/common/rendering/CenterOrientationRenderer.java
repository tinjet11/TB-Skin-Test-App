package com.google.ar.core.codelab.common.rendering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.google.ar.core.codelab.orientation.OrientationHandler;


/**
 * This is the CenterOrientationRenderer is display a filled circle
 * that represent the orientation value of the phone with the roll and pitch
 * CenterOrientationRenderer moves into the it when both roll and pitch = 0' and 0'
 */
public class CenterOrientationRenderer extends View {

    private final Paint paint = new Paint();
    private int x0;
    private int y0;
    private int radius;

    private int ratio = 9;
    private OrientationHandler orientationHandler; // Add this variable

    public CenterOrientationRenderer(Context context, OrientationHandler orientationHandler) {
        super(context);
        this.orientationHandler = orientationHandler;
    }


    @Override
    protected void onDraw(Canvas canvas) { // Override the onDraw() Method
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);

        // Calculate the center point of the circle based on orientation values
        float pitch = orientationHandler.getDegree2(); // Get pitch angle from OrientationHandler
        float roll = orientationHandler.getdegree();   // Get roll angle from OrientationHandler

        // Adjust with scaling of the circle  && pitch and roll * 5 = offset
        x0 = canvas.getWidth() / 2 + (int) (pitch * 5);
        y0 = canvas.getHeight() / 2 + (int) (roll * 5);
        radius = canvas.getHeight() / ratio;

        // Draw the circle
        canvas.drawCircle(x0, y0, radius, paint);

    }

    // Update the circle's position
    public void updateCirclePosition(int x, int y) {
        x0 = x;
        y0 = y;
        // request to redrawn the circle -> onDraw() method is called afterwards with the new x0 and y0 value from orientation Handler
        invalidate();
    }

    public void updateCircleSize(int r){
        ratio = r;
        invalidate();
    }
}