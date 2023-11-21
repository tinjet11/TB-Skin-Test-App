package com.google.ar.core.codelab.orientation;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.WindowManager;

import com.google.ar.core.codelab.common.rendering.CenterOrientationRenderer;

public class OrientationHandler {

    private final SensorManager sensorManager;
    private final Sensor rotationVectorSensor;

    //private SensorEventListener rvListener;
    public float degree;
    public float degree2;

    // System display. Need this for determining rotation.
    private final Display mDisplay;

    private CenterOrientationRenderer centerOrientationRenderer;
    public OrientationHandler(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationVectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Get the display from the window manager (for rotation).
        WindowManager windowmanager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        mDisplay = windowmanager.getDefaultDisplay();
    }

    public void onResume() {
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void onPause() {
        sensorManager.unregisterListener(rvListener);
    }

    public void setOrientationRenderer(CenterOrientationRenderer renderer) {
        this.centerOrientationRenderer = renderer;
    }

    public SensorEventListener rvListener = new SensorEventListener() {
        @Override
        public void onSensorChanged (SensorEvent event){
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, event.values);

                // Remap coordinate system (fix here)
                float[] remappedRotationMatrix = new float[16];

                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Y,
                        remappedRotationMatrix);
                // Convert to orientations
                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                //Pitch and roll orientation values
                orientations[1] = (float) (Math.toDegrees(orientations[1]));
                orientations[2] = (float) (Math.toDegrees(orientations[2]));
                degree2 = Math.round(orientations[1]);
                degree = Math.round(orientations[2]);

                // Update circle's position based on orientation values
                if (centerOrientationRenderer != null) {
                    // Adjust the offset
                    int x = mDisplay.getWidth() / 2 + (int) (degree);
                    int y = mDisplay.getHeight() / 2 + (int) (degree2);
                    //update the new circle position
                    centerOrientationRenderer.updateCirclePosition(x, y);
                }
            }
        }
        @Override
        public void onAccuracyChanged (Sensor sensor,int i){
        }
    };
    public float getdegree() {return degree;}

    public float getDegree2() {return degree2;}

}
