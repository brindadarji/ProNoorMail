package com.example.pronoormail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.pronoormail.adapter.ListAdapter;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SpamActivity extends ActionBarActivity {

	ArrayList<Product> products = new ArrayList<Product>();
    ListAdapter boxAdapter;
    Button btn;
    EditText EdtSerach;
    ListView lvMain;
	ProgressDialog dialog;
	String response="",response_user_availability="";
	JSONArray MailDetail = null;
	String Name,Subject,Date;
	int Read_Unread,id,star,chkboxId;
	LinearLayout LinearSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spam);
		
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setTitle("Spam");
	    
	    LinearSearch=(LinearLayout) findViewById(R.id.LinearSearch);
	    EdtSerach=(EditText) findViewById(R.id.editSearch);
	    
	    GetSpamFromServer();
        boxAdapter = new ListAdapter(this, products);
        
        lvMain = (ListView)findViewById(R.id.lvMain);
        
        lvMain.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ImageView img1=(ImageView) view.findViewById(R.id.imageView1);
				img1.setImageResource(android.R.drawable.presence_invisible);

				int id1=boxAdapter.getId(position);
				
				Intent i=new Intent(SpamActivity.this,InboxDetailActivity.class);
				i.putExtra("id",""+id1);
				startActivity(i);
			
				
				UpdateReadUnread(""+id1);
			
			}
		});
        
        EdtSerach.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SpamActivity.this.boxAdapter.getFilter().filter(s.toString()); 
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	
	public void fillData() {
        for (int i = 1; i <= 20; i++) {
          products.add(new Product("Product"+i,"hi",
              "2015/10/10", false,1,1,1));
        }
      }
 
	public void DeleteMailsByCheckbox(View v) {
        String result = "Please select the mail:";
        int totalAmount=0;
        for (Product p : boxAdapter.getBox()) {
          if (p.box){
            result += "\n" + p.id;
            chkboxId=p.id;
            DeleteData(""+chkboxId);
          }
        }
        Toast.makeText(getApplicationContext(),result+"\n"+"Total Amount:="+totalAmount, Toast.LENGTH_LONG).show();
      }
	
	public void GetSpamFromServer()
	{
		if(GlobalSetting.isNetworkConnected(getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					dialog = ProgressDialog.show(SpamActivity.this, "Please Wait",
						"Loding...", true);
				}
				

				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					param.put("uname",Global.getPreferenceString(getApplicationContext(), Global.PREF_User_Email,""));
					param.put("spam","1");
			
					try {
						response_user_availability=Board_Controller.post(GlobalSetting.GetSpamURL,param);
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
								MailDetail=json.getJSONArray("data");
								for(int i=0;i<MailDetail.length();i++)
								{
									JSONObject s=MailDetail.getJSONObject(i);
									id=s.getInt("id");
									Name=s.getString("from_name");
									Subject=s.getString("subject");
									Date=s.getString("date");
									star=s.getInt("star");
									Read_Unread=s.getInt("read_unread");
									if(Read_Unread==1 && star==1)//1 means read and 0 means unread
									{
										products.add(new Product(Date,Name,Subject,false,android.R.drawable.presence_invisible,android.R.drawable.star_big_on,id));
									}
									else if (Read_Unread==1 && star==0) {
										products.add(new Product(Date,Name,Subject,false,android.R.drawable.presence_invisible,android.R.drawable.star_big_off,id));
									}
									else
									{
										products.add(new Product(Date,Name,Subject,false,android.R.drawable.presence_online,android.R.drawable.star_big_off,id));
									}
								}
								
								lvMain.setAdapter(boxAdapter);
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
	
	public void UpdateReadUnread(final String pos)
	{
		if(GlobalSetting.isNetworkConnected(getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					//dialog = ProgressDialog.show(getActivity().getApplicationContext(), "Please Wait",
							//"Sending Data to Server.", true);
				}
				@Override
				protected Void doInBackground(Void... params) {
					Map<String,String> param = new HashMap<String,String>();
					//String id="4";
					param.put("id",pos);
			        
			       
			        
					try {
						response= Board_Controller.post(GlobalSetting.UpdateReadUnreadURL, param);
						
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
								//Log.e("Success!", ""+json.getString("msg"));
								
							} else if (success == 0) {
								Toast.makeText(getApplicationContext(), "some error..", 1).show();
								Log.e("Failure!", ""+json.getString("msg"));
							}

						} 
						catch (JSONException e) {
							e.printStackTrace();
							Log.e("exception:","error..");
						}
					} else {
						Toast.makeText(getApplicationContext(), "Sending Data Failed!", 1).show();
						Log.e("Error", "Sending Data Failed!");
					}
					if (dialog != null) {
						//dialog.dismiss();
					}
				}

			}.execute();
			

		}else{
			Log.e("Warning!!!","Unable to Connect Internet.");
			
		}
	}
	
	public void DeleteData(final String pos)
	{
		if(GlobalSetting.isNetworkConnected(getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					//dialog = ProgressDialog.show(SpamActivity.this, "Please Wait",
						//	"Deleting", true);
				}
				@Override
				protected Void doInBackground(Void... params) {
					Map<String,String> param = new HashMap<String,String>();
					//String id="4";
					param.put("id",pos);
			        
					try {
						response= Board_Controller.post(GlobalSetting.DeleteSpamByIdURL, param);
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
								//Log.e("Success!", ""+json.getString("msg"));
								//dialog.dismiss();
								
							} else if (success == 0) {
								Toast.makeText(getApplicationContext(), "some error..", 1).show();
								//boxAdapter.notifyDataSetChanged();
								Log.e("Failure!", ""+json.getString("msg"));
								//dialog.dismiss();
							}

						} 
						catch (JSONException e) {
							e.printStackTrace();
							Log.e("exception:","error..");
							//dialog.dismiss();
						}
					} else {
						Toast.makeText(getApplicationContext(), "Sending Data Failed!", 1).show();
						Log.e("Error", "Sending Data Failed!");
						//dialog.dismiss();
					}
					
					if (dialog != null) {
						//dialog.dismiss();
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
		getMenuInflater().inflate(R.menu.spam, menu);
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
		if(id==R.id.Delete)
		{
			DeleteMailsByCheckbox(lvMain);
			Intent i=new Intent(SpamActivity.this,SpamActivity.class);
			startActivity(i);
			return true;
		}
		if(id==R.id.Search)
		{
			LinearSearch.setVisibility(View.VISIBLE);
			return true;
		}
		if(id==R.id.Refresh)
		{
			Intent i=new Intent(SpamActivity.this,SpamActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
