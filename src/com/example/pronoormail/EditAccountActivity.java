package com.example.pronoormail;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditAccountActivity extends ActionBarActivity {

	EditText EdtPassword,EdtAlt,EditConpass;
	TextView txtUname;
	LinearLayout LinearConpass;
	String Uname,Pass,Alt,response="";
	Button btnOk;
	ProgressDialog dialog;
	public void find()
	{
		LinearConpass=(LinearLayout) findViewById(R.id.LinearConpass);
		btnOk=(Button) findViewById(R.id.btnOk);
		EditConpass=(EditText) findViewById(R.id.EdtConPass1);
		txtUname=(TextView) findViewById(R.id.TextViewUname1);
		EdtPassword=(EditText) findViewById(R.id.EdtPass1);
		EdtAlt=(EditText) findViewById(R.id.EdtAlt1);
		Intent i=getIntent();
		Bundle b=i.getExtras();
		Uname=b.getString("txtUname");
		Pass=b.getString("txtPass");
		Alt=b.getString("txtAlt");
		
		txtUname.setText(Uname);
		EdtPassword.setText(Pass);
		EdtAlt.setText(Alt);
		
		EdtPassword.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				LinearConpass.setVisibility(View.VISIBLE);
				Toast.makeText(EditAccountActivity.this,"You touched me",Toast.LENGTH_LONG).show();
				return false;
			}
		});
		
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(EdtPassword.getText().toString().equals(EditConpass.getText().toString()))
				{
					EdtPassword.setText(EditConpass.getText().toString());
					LinearConpass.setVisibility(View.GONE);
				}
				else
				{
					Toast.makeText(EditAccountActivity.this,"Password and confirm password must match",Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_account);
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setTitle("Account Details");
		
		find();
	}
	
	public void UpdateAccount()
	{
		if(GlobalSetting.isNetworkConnected(getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					dialog = ProgressDialog.show(EditAccountActivity.this, "Please Wait",
							"Sending Data to Server.", true);
				}
				@Override
				protected Void doInBackground(Void... params) {
					Map<String,String> param = new HashMap<String,String>();
					param.put("uname",Global.getPreferenceString(getApplicationContext(), Global.PREF_User_Email,""));
			        param.put("pass",EdtPassword.getText().toString());
			        param.put("alt", EdtAlt.getText().toString());
			        
					try {
						response= Board_Controller.post(GlobalSetting.UpdateAccountURL, param);
						
						Log.e("hi",""+response);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("hello","hello");
					}
					return null;
				}
			
				@Override
				protected void onPostExecute(Void res) {
					if (!response.equals("")) {
						try {
							JSONObject json = new JSONObject(response);
							int success = json.getInt("success");
							if (success == 1) {
								
								Toast.makeText(getApplicationContext(), "Updated Successfully ", 1).show();
								dialog.dismiss();
								
							} else if (success == 0) {
								Toast.makeText(getApplicationContext(), "Updated Successfully", 1).show();
								Log.e("Failure!", ""+json.getString("msg"));
								Intent i=new Intent(EditAccountActivity.this,EditProfileActivity.class);
								startActivity(i);
								//finish();
								dialog.dismiss();
							}

						} 
						catch (JSONException e) {
							e.printStackTrace();
							Log.e("exception:","error..");
							dialog.dismiss();
						}
					} else {
						Toast.makeText(getApplicationContext(), "Sending Data Failed!", 1).show();
						Log.e("Error", "Sending Data Failed!");
						dialog.dismiss();
					}
					if (dialog != null) {
						dialog.dismiss();
					}
				}

			}.execute();
			

		}else{
			Log.e("Warning!!!","Unable to Connect Internet.");
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_account, menu);
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
		if(id==R.id.cancel)
		{
			Intent i=new Intent(EditAccountActivity.this,EditProfileActivity.class);
			startActivity(i);
			//finish();
			return true;
		}
		if(id==R.id.Save)
		{
			UpdateAccount();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
