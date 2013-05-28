package com.jesus.bombergame;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Timer extends CountDownTimer {
	long millisUntilFinished;
	long countDownInterval;
	private Handler handler;
	private boolean sent;

	public Timer(long millisInFuture, long countDownInterval, Handler handler) {
		super(millisInFuture, countDownInterval);
		this.millisUntilFinished = millisInFuture;
		this.countDownInterval = countDownInterval;
		this.handler = handler;		
		this.sent = false;
	}
	
	@Override
	public void onFinish() {
		send("Time over!");		
	}

	@Override
	public void onTick(long millisUntilFinished) {
		this.millisUntilFinished = millisUntilFinished;
		long seconds = ((millisUntilFinished / 1000) % 60);
		Log.d("timer", seconds+"");
		if(seconds >0 && seconds <= 15 && !sent){
			send("Ruuun!!");
			sent = true;
		}
	}
	
	private void send(String time){
		Log.d("timer", time);
		Message msg = handler.obtainMessage();
		Bundle b = new Bundle();		
		b.putString("time", time);
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
}