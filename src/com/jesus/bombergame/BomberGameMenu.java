package com.jesus.bombergame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/*Activity that shows a simple menu to start the game*/
public class BomberGameMenu extends Activity {
	
	private static final String TAG = "BGMenu";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		final Button buttonStart = (Button) findViewById(R.id.start);//Launch BomberGame
		buttonStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent bomberCamera = new Intent(getApplicationContext(), BomberGameActivity.class);
				startActivity(bomberCamera);
				Log.d(TAG, "BomberGame started");
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			super.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}