package com.jesus.bombergame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/*Activity that shows the game result. Mission Completed or Mission Failed*/
public class BomberGameResult extends Activity {

	private static final String TAG = "BGResult";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        
        Bundle bundle = this.getIntent().getExtras();
        boolean mission = bundle.getBoolean("mission");
        int bombs = bundle.getInt("bombs");
        Log.d(TAG, "Bombs placed "+bombs);
        
        TextView tscore = (TextView)findViewById(R.id.tscore);
        if (mission) tscore.setText("Mission completed.\n" + bombs + " detonated bombs.");
        else tscore.setText("Mission failed.\n" + bombs + " detonated bombs.");
    }
}