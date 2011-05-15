package com.jvk.demotivalcam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.R.drawable;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class demotivalcam extends Activity  {
	private static final String SDCARD_TEMP_JPG = "/mnt/sdcard/DCIM/temp_demotival.jpg";
	private static final String FINAL_IMA_PATH = "/mnt/sdcard/DCIM/last_demotival.png";
	private static final String SDCARD_DIR = "/mnt/sdcard/DCIM";
	/** Called when the activity is first created. */
	Camera camera=null;
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
	private ProgressDialog waitingDialog=null;

	private OnClickListener listenerCapture;
	private OnClickListener listenerSave;
	private OnClickListener listenerShare;
	private OnClickListener listenerCaptureAgain;
	private boolean textState;
	private boolean isSend;
	private AdView add;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		textState=false;
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.main);
		assignXML();
	/*	AdManager.setTestDevices( new String[] {
				AdManager.TEST_EMULATOR
				} );*/
		add=(AdView) findViewById(R.id.ad);
		initListeners();
		File folder=new File(SDCARD_DIR);
		if (!folder.exists())folder.mkdir();
		linear_capture.setVisibility(View.VISIBLE);
		linear_again.setVisibility(View.GONE);
		ShowPreview();
		preview = new Preview(this);
		flay.addView(preview);
		swichTextState(textState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		 waitingDialog = ProgressDialog.show(demotivalcam.this, "", 
					"Loading. Please wait...", true);
		return waitingDialog;
	}


    public void myClickHandler(View target) {
        EditText m=(EditText) target;
        m.setText("");
    }
	
	public void ShowImage()
	{
		swichTextState(true);
		previewimg.setVisibility(View.VISIBLE);
		flay.setVisibility(View.GONE);
		Bitmap bm=BitmapFactory.decodeFile(SDCARD_TEMP_JPG);
	    float scaleWidth = ((float) flay.getWidth()) / bm.getWidth();
	    float scaleHeight = ((float) flay.getHeight()) / bm.getHeight();

	    Matrix matrix = new Matrix();

	    matrix.postScale(Math.max(scaleWidth, scaleHeight),Math.max(scaleWidth, scaleHeight));
	    // rotate the Bitmap
	    Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		if (display.getRotation()==0){
			matrix.postRotate(90);
		}
		if (display.getRotation()==270){
			matrix.postRotate(180);
		}	    

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
		swichTextState(false);
		previewimg.setVisibility(View.GONE);
		flay.setVisibility(View.VISIBLE);
		linear_capture.setVisibility(View.VISIBLE);
		linear_again.setVisibility(View.GONE);
		
	}
			
	private void swichTextState(Boolean state) {
		title.setEnabled(state);
		subtitle.setEnabled(state);
		add.loadAd(new AdRequest());
		
	}



	private void initListeners() {
		listenerCapture=new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(0);
				if (preview.camera!=null)
				{
					preview.camera.stopPreview();
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
				}else
				{
					CharSequence text = "Camera dont respond, please, reboot application";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(getApplicationContext(), text, duration);
					toast.show();	
				}
				

				

			}
		};
		listenerShare=new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				try {
					swichTextState(false);
					generateImage(FINAL_IMA_PATH);
					shareImage();

				} catch (FileNotFoundException e) {
					waitingDialog.dismiss();
					e.printStackTrace();
				}

			}


		};
		listenerCaptureAgain=new OnClickListener() {

			@Override
			public void onClick(View v) {
				preview.camera.startPreview();
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
				try {
				Calendar calendar = Calendar.getInstance();
				String pathLocal=SDCARD_DIR+"/demotival_"+String.valueOf(calendar.getTime().getTime())+".png";
				generateImage(pathLocal);
				CharSequence text = "The image was saved on "+pathLocal;
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(v.getContext(), text, 3);
				toast.show();
				ShowImage();
				} catch (FileNotFoundException e) {
					waitingDialog.dismiss();
					e.printStackTrace();
				}
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
		Log.d("OOPPS","COMPARTIMOS!");
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
		Log.d("OOPPS","RESUME!");
		
		if (waitingDialog!=null)waitingDialog.dismiss();
		

	}




	@Override
	protected void onRestart() {
		Log.d("OOPPS","onRestart!");
		super.onRestart();

	}



	@Override
	protected void onStart() {
		Log.d("OOPPS","onStart!");
		super.onStart();
		ShowPreview();
		if(preview.camera==null)	preview.camera = Camera.open();
		else
		{
			preview.camera.release();
			preview.camera.open();
			preview.requestLayout();
		}
	}



	@Override
	protected void onStop() {
		Log.d("OOPPS","onStop!");
		isSend=true;
		super.onStop();
		if (preview.camera != null) {
			preview.camera.release();
			preview.camera = null;
		}
	}



	@Override
	protected void onPause() {
		super.onPause();
		isSend=false;
		Log.d("OOPPS","PAUSE!");
		if (waitingDialog!=null)waitingDialog.dismiss();

	}


	private void generateImage(String img_path) throws FileNotFoundException {
		allView.setDrawingCacheEnabled(true);
		Bitmap bm=allView.getDrawingCache();
		bm.compress(CompressFormat.PNG, 100, new FileOutputStream(String.format(img_path)));
	}


}