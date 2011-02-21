package com.jvk.demotivalcam;


import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "Preview";

	SurfaceHolder mHolder;
	public Camera camera;

	
	public int scuareW;
	Preview(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
	
		camera.setDisplayOrientation(90);
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
        if (camera != null) {
  //          mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
	//	camera.stopPreview();
	//	camera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		Camera.Parameters parameters = camera.getParameters();
		camera.getParameters().setJpegQuality(50);
		camera.getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
	//	parameters.setPreviewSize(w, h);
	//	parameters.setPictureSize(w, h);
		camera.setParameters(parameters);

	//	parameters.set("rotation", 270); 
	//	parameters.setRotation(270);
		camera.setParameters(parameters);
		camera.startPreview();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("camvas", "rotate!");
		canvas.rotate(90);
		super.onDraw(canvas);
	}

	@Override
	public void draw(Canvas canvas) {
		
		super.draw(canvas);
		Paint p = new Paint(Color.RED);
		Log.d(TAG, "draw");
		canvas.drawText("PREVIEW", canvas.getWidth() ,
				canvas.getHeight(), p);
	}
}