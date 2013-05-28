package com.jesus.bombergame;

import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*This activity launches the phone camera preview and enables the button to place a bomb if the red percentage in the preview
 * is higher than the threshold,25%*/
public class BomberGameActivity extends Activity implements SurfaceHolder.Callback {

	private static final String TAG = "BGActivity";	
	
	private int BOMBS = 5;//Total bombs to place
	private boolean missionCompleted = false;//Is the mission completed?
	
	private Timer timer;//control the mission time
	private Handler timerHandler;//Handler to update the infodisplay
	private TextView infodisplay;//Textview to show the mission status
	
	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;

	private Button placeBomb;
	private MediaPlayer bombClock;//wav
	private MediaPlayer explosion;//wav

	private int threshold = 25;//Red percentage in the preview to enable the button that places the bomb.
	protected boolean overRed = false;
	private int bombCounter;//Bombs placed

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		/**Surface Config**/
		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		/**Control Inflater Config**/
		controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.control, null);
		LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.addContentView(viewControl, layoutParamsControl);
		
		/*Media*/
		bombClock = MediaPlayer.create(this, R.raw.activate);
		explosion = MediaPlayer.create(this, R.raw.explosion);
		
		infodisplay = (TextView) findViewById(R.id.infodisplay);
		
		timerHandler = new ChangeTimer();
		timer = new Timer(61000, 1000, timerHandler);
		
		placeBomb = (Button) findViewById(R.id.placebomb);

	}

	@Override
	public void onStart() {
		super.onStart();
		bombCounter = 0;//Bomb counter
		timer.start();
		placeBomb.setOnClickListener(new OnClickListener() {
			long endTimeMillis = 0;
			public void onClick(View v) {
				if (System.currentTimeMillis() < endTimeMillis) return;
				if(overRed){//If the camera is over a red spot
					endTimeMillis = System.currentTimeMillis() + 5000;
					bombClock.start();//Play sound
					bombCounter++;//Increment bombcounter
					Log.d(TAG, "bombCounter:"+bombCounter);
					Toast.makeText(BomberGameActivity.this, "BOMB READY. "+(BOMBS-bombCounter)+" left.", Toast.LENGTH_SHORT).show();
					if (bombCounter==BOMBS){
						missionCompleted = true;//All bombs placed
						Toast.makeText(BomberGameActivity.this, "MISSION COMPLETE!!", Toast.LENGTH_LONG).show();
						showResult();
					}
				}
			}
		});
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (previewing) {
			camera.stopPreview();
			previewing = false;
		}

		if (camera != null) {
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);
			camera.setPreviewCallback(new PreviewCallback() {
				/*For each previewed frame, convert yuv data to rgb and check if the red percentage is higher than 25*/
				public void onPreviewFrame(byte[] data, Camera camera) {
					int width = camera.getParameters().getPreviewSize().width;
					int height = camera.getParameters().getPreviewSize().height;
					int[] rgbs = new int[width * height];
					YUVtoRGB(rgbs, data, width, height);
					overRed = redFound(rgbs);
					
					if (overRed) placeBomb.setBackgroundResource(R.drawable.timebombr);
					else placeBomb.setBackgroundResource(R.drawable.timebomb);
					
					//Log.d(TAG, "overRed:"+overRed);
					surfaceView.invalidate();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.setPreviewCallback(null);
		camera.release();
		camera = null;
		previewing = false;
	}
	
	/*Convert from YUV space to RGB space*/
	static public void YUVtoRGB(int[] rgb, byte[] yuv, int width, int height) {
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & yuv[yp]) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv[uvp++]) - 128;
					u = (0xff & yuv[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0) r = 0;
				else if (r > 262143) r = 262143;
				if (g < 0) g = 0;
				else if (g > 262143) g = 262143;
				if (b < 0) b = 0;
				else if (b > 262143) b = 262143;
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

	public boolean redFound(int[] rgb) {
		int red = 0;
		int green = 0;
		int blue = 0;
		int redPixels = 0;

		for (int pix = 0; pix < rgb.length; pix += 3) {
			red = (rgb[pix] >> 16) & 0xff;// Red
			green = (rgb[pix] >> 8) & 0xff;// Green
			blue = rgb[pix] & 0xff;// Blue
			if (red - green - blue > threshold) //If there is more amount of red, it's a red pixel
				redPixels += 1;
		}
		return (((float) redPixels / rgb.length) * 100 > threshold);//% of red pixels >25%?
	}
	
	
	private class ChangeTimer extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();			
			String timerStr = bundle.getString("time");
			infodisplay.setTextColor(Color.RED);
			infodisplay.setText(timerStr);
			if(timerStr.equals("Time over!")) {
				showResult();
			}
				
		}
	}
	
	private void showResult() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				explosion.start();
				Bundle bundle = new Bundle();
				bundle.putInt("bombs", bombCounter);
				bundle.putBoolean("mission", missionCompleted);
				Intent newIntent = new Intent(getApplicationContext(),
						BomberGameResult.class);
				newIntent.putExtras(bundle);
				startActivity(newIntent);
				timer.cancel();
				finish();
			}

		};
		thread.start();

	}

	@Override
	public synchronized void onResume() {
		super.onResume();
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		timer.cancel();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.cancel();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean keyManaged = false;
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			keyManaged = true;
			super.finish();
			timer.cancel();
			break;
		}
		return keyManaged;
	}
}