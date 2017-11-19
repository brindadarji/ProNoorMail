package com.example.pronoormail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPassActivity extends ActionBarActivity {

	EditText Edtusername,EdtCode;
	Button btnnext,btnCodeNext;
	String response_user_availability="",Contact_no,Code,Pass;
	ProgressDialog dialog;
	JSONArray RegitrationDetail;
	LinearLayout LinearCode;
	int n;
	TextView TxtViewMainPass;
	
	public void findControl()
	{
		Edtusername=(EditText) findViewById(R.id.editTextUName);
		btnnext=(Button) findViewById(R.id.btnNext);
		LinearCode=(LinearLayout) findViewById(R.id.LinearCode);
		EdtCode=(EditText) findViewById(R.id.editCode);
		btnCodeNext=(Button) findViewById(R.id.BtnCodeNext);
		TxtViewMainPass=(TextView) findViewById(R.id.textViewMainPass);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pass);
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setTitle("Forgot Password");
		findControl();
		
		btnnext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GetContact();
			}
		});
		
		btnCodeNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckCode();
			}
		});
	}
	
	protected void CheckCode() {
		Code=EdtCode.getText().toString();
		String nn=""+n;
		Toast.makeText(getApplicationContext(),"Done=" + nn,Toast.LENGTH_LONG).show(); 
		  if(Code.equals(nn))
		  {
			  TxtViewMainPass.setVisibility(View.VISIBLE);
			  TxtViewMainPass.setText("Your Password is = "+ Pass);
			 //Toast.makeText(getApplicationContext(),"Done your pass="+Pass,Toast.LENGTH_LONG).show(); 
		  }
		  else
		  {
			  Toast.makeText(getApplicationContext(),"Please enter Valid Code",Toast.LENGTH_LONG).show(); 
		  }
	}

	public void GetContact()
	{
		
		if(GlobalSetting.isNetworkConnected(getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					dialog = ProgressDialog.show(ForgetPassActivity.this, "Please Wait",
							"Loding...", true);
				}
				

				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					String Uname=Edtusername.getText().toString();
					param.put("uname",Uname);
					
					try {
						response_user_availability=Board_Controller.post(GlobalSetting.GetDataByUserNameURL,param);
						Log.e("hi",""+response_user_availability);
					} catch (IOException e) {
						e.printStackTrace();
						Log.e("hello","hello");
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {
					
					if(!response_user_availability.equals("")){
						try{
							JSONObject json=new JSONObject(response_user_availability);
							int success=json.getInt("success");
							if(success == 1){
								RegitrationDetail=json.getJSONArray("data");
								for(int i=0;i<RegitrationDetail.length();i++){
									JSONObject s=RegitrationDetail.getJSONObject(i);
									Contact_no=s.getString("contact_no");
									Pass=s.getString("password");
								}
								
								Random generator = new Random();
								  n = 10000;
								  n = generator.nextInt(n);
								  String phoneNo="+"+Contact_no;
								  Toast.makeText(getApplicationContext(),phoneNo,Toast.LENGTH_LONG).show();
										  //txtphoneNo.getText().toString();
								  String message ="Your Pronoor Code is="+ n;
								  Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
								  try {
								     SmsManager smsManager = SmsManager.getDefault();
								     smsManager.sendTextMessage(phoneNo, null, message, null, null);
								     LinearCode.setVisibility(View.VISIBLE);
								     Toast.makeText(getApplicationContext(), "SMS sent.",
								     Toast.LENGTH_LONG).show();
								  } catch (Exception e) {
								     Toast.makeText(getApplicationContext(),
								     "SMS faild, please try again.",
								     Toast.LENGTH_LONG).show();
								     e.printStackTrace();
								  }
								
								dialog.dismiss();
							}
							else if(success == 0){
								
								Toast.makeText(getApplicationContext(),"Invalid User !",Toast.LENGTH_SHORT).show();
								dialog.dismiss();
							}
							else{
								Toast.makeText(getApplicationContext(),"Not Inserted !",Toast.LENGTH_SHORT).show();
								dialog.dismiss();
							}
							
						}catch(JSONException e){
							Log.e("Error1","Error..."+e.toString());
							e.printStackTrace();
							dialog.dismiss();
						}
					}else{
						Toast.makeText(getApplicationContext(),"data not get",Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
					
				}
				
			}.execute();
		}else{
			Toast.makeText(getApplicationContext(),"Network not available !",Toast.LENGTH_SHORT).show();
		}
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.forget_pass, menu);
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
