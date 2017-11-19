package com.example.pronoormail;


import com.example.pronoormail.SplashActivity;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends ActionBarActivity {

	ImageView imgLogo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		android.support.v7.app.ActionBar actionbar=getSupportActionBar();
		actionbar.hide();
		
		imgLogo=(ImageView) findViewById(R.id.imageView1);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				 if(Global.getPreferenceBoolean(getApplicationContext(), Global.PREF_Login_Chk,true))
			      {
			    	  Intent i=new Intent(SplashActivity.this,MineActivity.class);
			    	  startActivity(i);
			    	  finish();
			      }
				 else
				 {
					goToMainScreen();
				 }
				
			}
		}, 5000);
		
		  Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
	      imgLogo.startAnimation(animation2);
	      
	     
		
	}
	
	private void goToMainScreen()
	{
		Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
		startActivity(intent);
		//finish();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
