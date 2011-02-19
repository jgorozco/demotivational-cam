package com.jvk.demotivalcam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.R.drawable;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

import com.admob.android.ads.AdManager;
import com.admob.android.ads.AdView;

public class demotivalcam extends Activity  {
	private static final String SDCARD_TEMP_JPG = "/mnt/sdcard/DCIM/Camera/temp_demotival.jpg";
	private static final String FINAL_IMA_PATH = "/mnt/sdcard/DCIM/Camera/last_demotival.png";
	private static final String SDCARD_DIR = "/mnt/sdcard/DCIM/Camera";
	/** Called when the activity is first created. */
	Camera camera;
	public Preview preview;
	private ImageView   previewimg;
	private ImageButton capture;
	private ImageButton share;
	private ImageButton captureAgain;
	private ImageButton save;
	private LinearLayout linear_capture;
	private LinearLayout linear_again;
	private RelativeLayout allView;
	private FrameLayout flay;
	private EditText title;
	private EditText subtitle;
	private ProgressDialog waitingDialog;

	private OnClickListener listenerCapture;
	private OnClickListener listenerSave;
	private OnClickListener listenerShare;
	private OnClickListener listenerCaptureAgain;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.main);
		assignXML();
		AdManager.setTestDevices( new String[] {
				AdManager.TEST_EMULATOR
				} );
		AdView add=(AdView) findViewById(R.id.ad);
		add.requestFreshAd();
		initListeners();
		File folder=new File(SDCARD_DIR);
		if (!folder.exists())folder.mkdir();
		linear_capture.setVisibility(View.VISIBLE);
		linear_again.setVisibility(View.GONE);
		ShowPreview();
		preview = new Preview(this);
		flay.addView(preview);
		

	}

	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		 waitingDialog = ProgressDialog.show(demotivalcam.this, "", 
					"Loading. Please wait...", true);
		return waitingDialog;
	}



	public void ShowImage()
	{
		
		title.setBackgroundColor(0x00ff00);
		title.setFocusable(true);
		title.setFocusableInTouchMode(true);
		title.setInputType(InputType.TYPE_CLASS_TEXT);
		subtitle.setInputType(InputType.TYPE_CLASS_TEXT);
		subtitle.setFocusable(true);
		subtitle.setFocusableInTouchMode(true);
		subtitle.setText(R.string.fail_context);
		previewimg.setVisibility(View.VISIBLE);
		flay.setVisibility(View.GONE);
		
		Bitmap bm=BitmapFactory.decodeFile(SDCARD_TEMP_JPG);
		Matrix m=new Matrix();
	    float scaleWidth = ((float) flay.getWidth()) / bm.getWidth();
	    float scaleHeight = ((float) flay.getHeight()) / bm.getHeight();

	    Matrix matrix = new Matrix();

	    matrix.postScale(Math.max(scaleWidth, scaleHeight),Math.max(scaleWidth, scaleHeight));
	    // rotate the Bitmap
	    matrix.postRotate(90);

	    // recreate the new Bitmap
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0,
	    		bm.getWidth(),bm.getHeight() , matrix, true);
	    previewimg.setScaleType(ScaleType.CENTER);
		previewimg.setImageBitmap(resizedBitmap);
		bm=null;
		if (waitingDialog!=null)waitingDialog.dismiss();
		
	}
	public void ShowPreview()
	{
		title.setBackgroundColor(0x000000);
		previewimg.setVisibility(View.GONE);
		flay.setVisibility(View.VISIBLE);
		title.setFocusable(false);
		subtitle.setFocusable(false);
		title.setFocusableInTouchMode(false);
		subtitle.setFocusableInTouchMode(false);
		subtitle.setText(R.string.fail_instructions);
		
	}
		
	
	public Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        Matrix m=new Matrix();
        m.postRotate(90);
 //       m.postScale(0.17f, 0.175f);
 //       m.postTranslate(290.0f, 32.0f);
        int min=Math.min(bmp2.getWidth(), bmp2.getHeight());
        int x=0;
        int y=0;
        if (bmp2.getWidth()>bmp2.getHeight())
        {
        	x=bmp2.getWidth()-bmp2.getHeight();
        }else
        {
        	y=bmp2.getHeight()-bmp2.getWidth();
        }
        Bitmap newbm=Bitmap.createBitmap(bmp2, x, y, min, min);
        canvas.drawBitmap(newbm, m, null);
        return bmOverlay;
    }
	
	private void initListeners() {
		listenerCapture=new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(0);
				camera.stopPreview();
				ShutterCallback shutterCallback = new ShutterCallback() {
					public void onShutter() {
					//	Log.d(TAG, "onShutter'd");
					}
				};

				/** Handles data for raw picture */
				PictureCallback rawCallback = new PictureCallback() {
					public void onPictureTaken(byte[] data, Camera camera) {
					//	Log.d(TAG, "onPictureTaken - raw");
					}
				};

				/** Handles data for jpeg picture */
				PictureCallback jpegCallback = new PictureCallback() {
					public void onPictureTaken(byte[] data, Camera camera) {
						FileOutputStream outStream = null;
						try {
							
							File mfile=new File(SDCARD_TEMP_JPG);
							if (mfile.exists())
							{
								previewimg.setImageBitmap(null);
								mfile.delete();
							}
							outStream = new FileOutputStream(SDCARD_TEMP_JPG);
							outStream.write(data);
							outStream.flush();
							outStream.close();
							previewimg.setVisibility(View.VISIBLE);
							flay.setVisibility(View.GONE);
							ShowImage();
							
						//	Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
						}
					//	Log.d(TAG, "onPictureTaken - jpeg");
					}
				};
				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				linear_capture.setVisibility(View.GONE);
				linear_again.setVisibility(View.VISIBLE);

				

			}
		};
		listenerShare=new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				try {
					generateImage();
					shareImage();
					linear_capture.setVisibility(View.VISIBLE);
					linear_again.setVisibility(View.GONE);
				} catch (FileNotFoundException e) {
					waitingDialog.dismiss();
					e.printStackTrace();
				}

			}


		};
		listenerCaptureAgain=new OnClickListener() {

			@Override
			public void onClick(View v) {
				camera.startPreview();
				ShowPreview();
				linear_capture.setVisibility(View.VISIBLE);
				linear_again.setVisibility(View.GONE);

			}
		};
		listenerSave=new OnClickListener() {

			@Override
			public void onClick(View v) {
				//save image, ask for url
				//camera.startPreview();
				ShowImage();
				//linear_capture.setVisibility(View.VISIBLE);
				//slinear_again.setVisibility(View.GONE);

			}
		};
		capture.setOnClickListener(listenerCapture);
		captureAgain.setOnClickListener(listenerCaptureAgain);
		share.setOnClickListener(listenerShare);
		save.setOnClickListener(listenerSave);
		

	}

	private void shareImage() {
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		Uri screenshotUri = Uri.parse("file://"+FINAL_IMA_PATH);
		sharingIntent.setType("image/png");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
		startActivity(Intent.createChooser(sharingIntent, "Share image using"));
		
	}
	private void assignXML() {
		title=(EditText) findViewById(R.id.txt_title);
		subtitle=(EditText) findViewById(R.id.txt_details);
		flay=(FrameLayout) findViewById(R.id.FrameLayout01);
		previewimg=(ImageView) findViewById(R.id.image_preview);
		allView=(RelativeLayout) findViewById(R.id.complete_photo);
		capture=(ImageButton) findViewById(R.id.button_capture);
		capture.setImageResource(drawable.ic_menu_camera);
		share=(ImageButton) findViewById(R.id.button_share);
		share.setImageResource(drawable.ic_menu_share);
		save=(ImageButton) findViewById(R.id.button_save);
		save.setImageResource(drawable.ic_menu_save);
		captureAgain=(ImageButton) findViewById(R.id.button_capture_again);
		captureAgain.setImageResource(drawable.ic_menu_camera);
		linear_capture=(LinearLayout) findViewById(R.id.la_boton_capt);
		linear_again=(LinearLayout) findViewById(R.id.la_boton_share);

	}

	@Override
	protected void onResume() {
		super.onResume();
		ShowPreview();
		if (waitingDialog!=null)waitingDialog.dismiss();
		camera = Camera.open();
		preview.setCamera(camera);
	}

	@Override
	protected void onPause() {
		super.onPause();
		waitingDialog.dismiss();
		if (camera != null) {
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
	}


	private void generateImage() throws FileNotFoundException {
//		Bitmap myBitmap = BitmapFactory.decodeFile(SDCARD_TEMP_JPG);
		allView.setDrawingCacheEnabled(true);
		Bitmap bm=allView.getDrawingCache();
	//	Bitmap ov=overlay(bm, myBitmap);
		bm.compress(CompressFormat.PNG, 100, new FileOutputStream(String.format(FINAL_IMA_PATH)));
	}


}