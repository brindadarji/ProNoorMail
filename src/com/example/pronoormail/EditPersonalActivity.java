package com.example.pronoormail;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditPersonalActivity extends ActionBarActivity {

	EditText EdtTextFname1,EditTextLname1,EditTextDOB1,Editcont1,EditCountry1,EditState1,EditCity1;
	String Fname,Lname,DOB,Contact,Country,State,City;
	String response="";
	ProgressDialog dialog;
	public void Find()
	{
		Intent i=getIntent();
		Bundle b=i.getExtras();
		Fname=b.getString("txtFname");
		Lname=b.getString("txtLname");
		DOB=b.getString("txtDOB");
		Contact=b.getString("txtContact");
		Country=b.getString("txtCountry");
		State=b.getString("txtState");
		City=b.getString("txtCity");
		
		EdtTextFname1=(EditText) findViewById(R.id.EdtTextFname1);
		EditTextLname1=(EditText) findViewById(R.id.EditTextLname1);
		EditTextDOB1=(EditText) findViewById(R.id.EditTextDOB1);
		Editcont1=(EditText) findViewById(R.id.Editcont1);
		EditCountry1=(EditText) findViewById(R.id.EditCountry1);
		EditState1=(EditText) findViewById(R.id.EditState1);
		EditCity1=(EditText) findViewById(R.id.EditCity1);
		
		EdtTextFname1.setText(Fname);
		EditTextLname1.setText(Lname);
		EditTextDOB1.setText(DOB);
		Editcont1.setText(Contact);
		EditCountry1.setText(Country);
		EditState1.setText(State);
		EditCity1.setText(City);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_personal);
		
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setTitle("Personal Details");
		
		Find();
		
	}

	
	public void UpdatePersonal()
	{
		if(GlobalSetting.isNetworkConnected(getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					dialog = ProgressDialog.show(EditPersonalActivity.this, "Please Wait",
							"Sending Data to Server.", true);
				}
				@Override
				protected Void doInBackground(Void... params) {
					Map<String,String> param = new HashMap<String,String>();
					param.put("uname",Global.getPreferenceString(getApplicationContext(), Global.PREF_User_Email,""));
			        param.put("fname",EdtTextFname1.getText().toString());
			        param.put("lname",EditTextLname1.getText().toString());
			        param.put("DOB", EditTextDOB1.getText().toString());
			        param.put("contact",Editcont1.getText().toString());
			        param.put("country",EditCountry1.getText().toString());
			        param.put("state",EditState1.getText().toString());
			        param.put("city",EditCity1.getText().toString());
			       
			        
					try {
						response= Board_Controller.post(GlobalSetting.UpdatePersonalURL, param);
						
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
								Intent i=new Intent(EditPersonalActivity.this,EditProfileActivity.class);
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
		getMenuInflater().inflate(R.menu.edit_personal, menu);
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
			Intent i=new Intent(EditPersonalActivity.this,EditProfileActivity.class);
			startActivity(i);
			//finish();
			return true;
		}
		if(id==R.id.Save)
		{
			UpdatePersonal();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
