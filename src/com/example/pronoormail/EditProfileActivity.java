package com.example.pronoormail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfileActivity extends ActionBarActivity {

	TextView txtFname,txtLname,txtDOB,txtContact,txtCountry,txtState,txtCity;
	TextView txtUname,txtPass,txtAlt;
	ProgressDialog dialog;
	String response="",response_user_availability="";
	JSONArray RegitrationDetail = null;
	ImageView imageViewEditPer,imageViewAccount;
	
	public void findControlPersonal()
	{
		imageViewEditPer=(ImageView) findViewById(R.id.imageViewEditPer);
		txtFname=(TextView) findViewById(R.id.textViewFname1);
		txtLname=(TextView) findViewById(R.id.textViewLname1);
		txtDOB=(TextView) findViewById(R.id.textViewDOB1);
		txtContact=(TextView) findViewById(R.id.textViewcont1);
		txtCountry=(TextView) findViewById(R.id.textViewCountry1);
		txtState=(TextView) findViewById(R.id.textViewState1);
		txtCity=(TextView) findViewById(R.id.textViewCity1);
		
		imageViewEditPer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(EditProfileActivity.this,EditPersonalActivity.class);
				i.putExtra("txtFname",txtFname.getText());
				i.putExtra("txtLname",txtLname.getText());
				i.putExtra("txtDOB",txtDOB.getText());
				i.putExtra("txtContact",txtContact.getText());
				i.putExtra("txtCountry",txtCountry.getText());
				i.putExtra("txtState",txtState.getText());
				i.putExtra("txtCity",txtCity.getText());
				startActivity(i);
			}
		});
	}
	
	public void findControlAcc()
	{
		imageViewAccount=(ImageView) findViewById(R.id.imageViewAccount);
		txtUname=(TextView) findViewById(R.id.textViewUname1);
		txtPass=(TextView) findViewById(R.id.textViewPass1);
		txtAlt=(TextView) findViewById(R.id.textViewAlt1);
		imageViewAccount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(EditProfileActivity.this,EditAccountActivity.class);
				i.putExtra("txtUname",txtUname.getText());
				i.putExtra("txtPass",txtPass.getText());
				i.putExtra("txtAlt",txtAlt.getText());
				startActivity(i);
				//finish();
			}
		});
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setTitle("Profile");
	    
	    findControlPersonal();
	    findControlAcc();
	    
	    getDataFromServer();
	}
	
	
	public void getDataFromServer()
	{
		if(GlobalSetting.isNetworkConnected(getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					dialog = ProgressDialog.show(EditProfileActivity.this, "Please Wait",
							"Loding...", true);
				}
				

				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					param.put("uname",Global.getPreferenceString(getApplicationContext(), Global.PREF_User_Email,""));
					
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
								for(int i=0;i<RegitrationDetail.length();i++)
								{
									JSONObject s=RegitrationDetail.getJSONObject(i);
									
									txtFname.setText(s.getString("first_name"));
									txtLname.setText(s.getString("last_name"));
									txtDOB.setText(s.getString("date_birth"));
									txtContact.setText(s.getString("contact_no"));
									txtCountry.setText(s.getString("country"));
									txtState.setText(s.getString("state"));
									txtCity.setText(s.getString("city"));
									txtUname.setText(s.getString("username"));
									txtPass.setText(s.getString("password"));
									txtAlt.setText(s.getString("altemail"));
									
								}
								
								dialog.dismiss();
							}
							else if(success == 0){
								
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
		getMenuInflater().inflate(R.menu.edit_profile, menu);
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
