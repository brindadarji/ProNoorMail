package com.example.pronoormail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.pronoormail.adapter.ItemsDetailAdapter;
import com.example.pronoormail.adapter.ListAdapter;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InboxDetailActivity extends ActionBarActivity {

	ArrayList<ItemDetails> products = new ArrayList<ItemDetails>();
    ItemsDetailAdapter boxAdapter;
    ListView lvMain;
	TextView txtFrom,txtTo,txtDate,txtSubject,txtMesssage,txtAttach,txtEx;
	String Myid;
	String From,res_From,To,res_to,Subject,Message,res_message,Date,res_date,Attachment,res_attach;
	JSONArray MailDetail = null,ItemDetail=null;
	String response="",response_user_availability="";
	ProgressDialog dialog;
	LinearLayout LinearAttach;
	String[] Att,Att_re;
	String MyName="",MyName1="";
	
	public void getIntentData()
	{
		Intent i=getIntent();
		Bundle b=i.getExtras();
		Myid=b.getString("id");
		//Toast.makeText(InboxDetailActivity.this,""+Myid,Toast.LENGTH_LONG).show();
	}
	
	public void findcontrol()
	{
		txtFrom=(TextView) findViewById(R.id.textViewFrom);
		txtTo=(TextView) findViewById(R.id.textViewTo);
		txtDate=(TextView) findViewById(R.id.textViewDate);
		txtMesssage=(TextView) findViewById(R.id.textViewMessage);
		txtAttach=(TextView) findViewById(R.id.textViewAttach);
		txtEx=(TextView) findViewById(R.id.textViewEx);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox_detail);
		
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setTitle("Detail");
	    
	    findcontrol();
		getIntentData();
		getDataById();
		getAllData();
		
		boxAdapter = new ItemsDetailAdapter(this, products);
        
        lvMain = (ListView)findViewById(R.id.lvMain);
        
        lvMain.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String id1=boxAdapter.getId(position);
				Toast.makeText(getApplicationContext(),"MyID="+id1,Toast.LENGTH_LONG).show();
			
				final CharSequence[] options = { "Reply", "Forward","Download Attachment","Cancel"};

				AlertDialog.Builder builder = new AlertDialog.Builder(
						InboxDetailActivity.this);
				builder.setTitle("Choose");
				builder.setItems(options,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int item) {
								if (options[item].equals("Reply")) {
									Intent i=new Intent(InboxDetailActivity.this,ResponsesActivity.class);
									i.putExtra("id",id1);
									i.putExtra("to",res_From);
									i.putExtra("subject","Re:"+Subject);
									startActivity(i);
									//finish();
								} else if (options[item].equals("Forward")) {
									Intent i=new Intent(InboxDetailActivity.this,ForwardActivity.class);
									i.putExtra("id",id1);
									//i.putExtra("to",From);
									i.putExtra("subject","Fwd:"+Subject);
									i.putExtra("message",Message);
									i.putExtra("attachment",Attachment);
									startActivity(i);
									//finish();
								}else if (options[item].equals("Download Attachment")) {
									Intent i=new Intent(InboxDetailActivity.this,DownloadActivity.class);
									i.putExtra("attachment",res_attach);
									startActivity(i);
									//finish();
									dialog.dismiss();
								}
								else if (options[item].equals("Cancel")) {
									dialog.dismiss();
								}
							}
						});
				builder.show();
			}
				
				//i.putExtra("id",Myid);
				//i.putExtra("to",From);
				//i.putExtra("subject","Re:"+Subject);
				//startActivity(i);*/
		});
	}
	
	public void getDataById()
	{
		if(GlobalSetting.isNetworkConnected(InboxDetailActivity.this)){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					
				}
				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					param.put("id",Myid);
					//param.put("uname", Global.getPreferenceString(getApplicationContext(), Global.PREF_User_Email,""));
					
					try {
						response=Board_Controller.post(GlobalSetting.GetItemsByIdURL,param);
						Log.e("hi",""+response);
					} catch (IOException e) {
						e.printStackTrace();
						Log.e("hello","hello");
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {
					
					if(!response.equals("")){
						try{
							JSONObject json=new JSONObject(response);
							int success=json.getInt("success");
							if(success == 1){
								ItemDetail=json.getJSONArray("data");
								for(int i=0;i<ItemDetail.length();i++)
								{
									JSONObject s=ItemDetail.getJSONObject(i);
									
									From=s.getString("from_uname");
									To=s.getString("to_uname");
									Subject=s.getString("subject");
									Date=s.getString("date");
									Message=s.getString("message");
									Attachment=s.getString("attachment");
								    Att=Attachment.split(",");
									for(int k=0;k<Att.length;k++)
									{
										MyName+=Att[k]+"\n";
											
									}
								}
								
								txtFrom.setText(From);
								txtTo.setText(To);
								txtDate.setText(Date);
								txtMesssage.setText(Message);
								//txtAttach.setText(Attachment);
								txtAttach.setText(MyName);
								
							}
							else if(success == 0){
								
								//Toast.makeText(getApplicationContext(),"NO data !",Toast.LENGTH_SHORT).show();
								//dialog.dismiss();
							}
							else{
								Toast.makeText(getApplicationContext(),"Not Inserted !",Toast.LENGTH_SHORT).show();
								
							}
							
						}catch(JSONException e){
							Log.e("Error1","Error..."+e.toString());
							e.printStackTrace();
							
						}
					}else{
						Toast.makeText(InboxDetailActivity.this,"data not get",Toast.LENGTH_SHORT).show();
						
					}
					
				}
				
			}.execute();
		}else{
			Toast.makeText(getApplicationContext(),"Network not available !",Toast.LENGTH_SHORT).show();
		}
			
	}
	
	public void getAllData()
	{
		if(GlobalSetting.isNetworkConnected(InboxDetailActivity.this)){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					dialog = ProgressDialog.show(InboxDetailActivity.this, "Please Wait",
							"Loding...", true);
				}
				

				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					param.put("id",Myid);
					
					try {
						response_user_availability=Board_Controller.post(GlobalSetting.GEtMessageDetailsURL,param);
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
						
									res_From=s.getString("res_from");
									res_to=s.getString("res_to");
									res_date=s.getString("res_date");
									res_message=s.getString("res_mess");
									res_attach=s.getString("res_attach");
									
									Att_re=res_attach.split(",");
									for(int k=0;k<Att_re.length;k++)
									{
										MyName1+=Att_re[k]+"\n";		
									}
									
									products.add(new ItemDetails(res_From,res_to,Subject,res_message,res_date,MyName1,Myid));
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
						Toast.makeText(InboxDetailActivity.this,"data not get",Toast.LENGTH_SHORT).show();
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
		getMenuInflater().inflate(R.menu.inbox_detail, menu);
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
		
		if(id==R.id.Reply)
		{
			Intent i=new Intent(InboxDetailActivity.this,ResponsesActivity.class);
			i.putExtra("id",Myid);
			i.putExtra("to",From);
			i.putExtra("subject","Re:"+Subject);
			startActivity(i);
			//finish();
		}
		if(id==R.id.Forward)
		{
			Intent i=new Intent(InboxDetailActivity.this,ForwardActivity.class);
			i.putExtra("id",Myid);
			//i.putExtra("to",From);
			i.putExtra("subject","Fwd:"+Subject);
			i.putExtra("message",Message);
			i.putExtra("attachment",Attachment);
			startActivity(i);
			//finish();
		}
		if(id==R.id.Download)
		{
			Intent i=new Intent(InboxDetailActivity.this,DownloadActivity.class);
			i.putExtra("attachment",Attachment);
			startActivity(i);
			//finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
