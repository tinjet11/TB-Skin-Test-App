package com.google.ar.core.codelab.imagecapture;

import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.ar.core.codelab.depth.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class ImageCaptureActivity extends AppCompatActivity  implements View.OnClickListener{
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    /*
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_image_capture);
        }
        @Override
        public void onClick(View v) {
    
        }
    
         */
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private PreviewView previewView;

    private Button bCapture;

    private ImageCapture imageCapture;

    private static final long CAPTURE_DELAY_MS = 5000; // 5 seconds delay

    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        previewView = findViewById(R.id.previewView);

        bCapture = findViewById(R.id.bCapture);

        bCapture.setOnClickListener(this);

        // Camera Provider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(()->{
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
                capturePhoto();
            }catch(ExecutionException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            },getExecutor());
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }


    private void startCameraX(ProcessCameraProvider cameraProvider) {
        //cameraProvider.unbindAll();

        //Camera Selector use case
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        //Preview Use Case
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());


        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();

        try{
            // Unbind use cases before rebinding
            cameraProvider.unbindAll();

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture);

        } catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bCapture:
                capturePhoto();
                break;

        }
    }

    // Image Capture function
    private void capturePhoto() {
        /*
        // Set Image directory and the filename
        File storageDir  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String filename = "IMG_" + timeStamp +".png";

        File imageFile = new File(storageDir + "/CImages",filename);

         */
        ImageCapture imageCapture = this.imageCapture;
        if (imageCapture == null) {
            return;
        }

        // Create time-stamped name and MediaStore entry
        SimpleDateFormat dateFormat = new SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault());
        String name = dateFormat.format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }

        // Create output options object which contains file + metadata
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();

        /*
        // Create a file in the external storage in the device /depthcodelab
        final File imageFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/CImages", "IMG" +
                Long.toHexString(System.currentTimeMillis()) + ".png");


        // Make sure the directory exists
        if (!imageFile.getParentFile().exists()) {
            imageFile.getParentFile().mkdirs();
        }

        //Image Capture Use Case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

         */
        //new ImageCapture.OutputFileOptions.Builder(imageFile).build()

        imageCapture.takePicture(outputOptions , ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Toast.makeText(ImageCaptureActivity.this, "Error for saving photo"+ exc.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Toast.makeText(ImageCaptureActivity.this, "Photo has been saved successfully"+ output.getSavedUri(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }


}