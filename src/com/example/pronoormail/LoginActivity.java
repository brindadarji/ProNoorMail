package com.example.pronoormail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	
	private Button btnCreateAcc,btnLogin;
	ProgressDialog dialog;
	String response_user_availability="";
	JSONArray RegitrationDetail = null;
	String UserName,Password,FirstName,LastName,DP;
	EditText EdtUserName,EdtPasssword;
	TextView TxtViewError,TxtViewForgetPass;
	String txtviewusername,txtviewpassword;
	
	public void FindControls()
	{
		btnCreateAcc=(Button) findViewById(R.id.btnCreateAcc);
		btnLogin=(Button) findViewById(R.id.btnLogin);
		EdtUserName=(EditText) findViewById(R.id.editTextUserName);
		EdtPasssword=(EditText) findViewById(R.id.editTextPassword);
		TxtViewError=(TextView) findViewById(R.id.textViewError);
		TxtViewForgetPass=(TextView) findViewById(R.id.textViewForgotPass);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setTitle("Login");
	    
	
		FindControls();
		
		btnCreateAcc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(LoginActivity.this,RegistrationActivity.class);
				startActivity(i);
				//finish();
			}
		});
		
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Login();
			}
		});
		
		TxtViewForgetPass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(LoginActivity.this,ForgetPassActivity.class);
				startActivity(i);
				//finish();
			}
		});
	}
	
	public void Login()
	{
		txtviewusername=EdtUserName.getText().toString();
		txtviewpassword=EdtPasssword.getText().toString();
		if(txtviewusername.equals(""))
		{
			EdtUserName.setError("Please enter username" );
		}
		else if(txtviewpassword.equals(""))
		{
			EdtPasssword.setError("Please enter password" );
		}
		else
		{
		 chkLogin();
		}
	}
	public void chkLogin()
	{
		
		if(GlobalSetting.isNetworkConnected(getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					dialog = ProgressDialog.show(LoginActivity.this, "Please Wait",
							"Loding...", true);
				}
				

				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					
					
					param.put("uname",txtviewusername);
					param.put("pass",txtviewpassword);
					
					try {
						response_user_availability=Board_Controller.post(GlobalSetting.LoginURL,param);
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
									UserName=s.getString("username");
									FirstName=s.getString("first_name");
									LastName=s.getString("last_name");
									DP=s.getString("dp");
								}
								Global.setPreferenceBoolean(getApplicationContext(),Global.PREF_Login_Chk,true);
								Global.setPreferenceString(getApplicationContext(),Global.PREF_User_DP,DP);
								Global.setPreferenceString(getApplicationContext(),Global.PREF_User_Email,UserName);
								Global.setPreferenceString(getApplicationContext(),Global.PREF_First_Name,FirstName);
								Global.setPreferenceString(getApplicationContext(),Global.PREF_Last_Name,LastName);
								String MyName=FirstName + " " + LastName;
								Global.setPreferenceString(getApplicationContext(),Global.PREF_Full_Name,MyName);
								
								
								String FullName=FirstName + " " + LastName + "\n" + UserName ;
								Global.setPreferenceString(getApplicationContext(),Global.PREF_User_Name,FullName);
										Intent in=new Intent(LoginActivity.this,MineActivity.class);
										//in.putExtra("LoginUser",FullName);
										startActivity(in);
										//finish();
								
								dialog.dismiss();
							}
							else if(success == 0){
								TxtViewError.setText("Username or password incorrect");
								Toast.makeText(getApplicationContext(),"NO data !",Toast.LENGTH_SHORT).show();
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
		getMenuInflater().inflate(R.menu.login, menu);
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
