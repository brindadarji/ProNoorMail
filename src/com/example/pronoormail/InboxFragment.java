package com.example.pronoormail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.pronoormail.adapter.ListAdapter;
import com.example.pronoormail.adapter.MyAdapter;

import android.R.anim;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class InboxFragment extends Fragment {
	
	public InboxFragment(){}
	ListView lv;
	
	ArrayList<Product> products = new ArrayList<Product>();
    ListAdapter boxAdapter;
    Button btn;
    ListView lvMain;
	ProgressDialog dialog;
	String response="",response_user_availability="";
	JSONArray MailDetail = null;
	String Name,Subject,Date;
	int Read_Unread,id,star,chkboxId;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        
        btn=(Button) rootView.findViewById(R.id.Button1);
        //fillData();
        GetInboxFromServer();
        boxAdapter = new ListAdapter(getActivity().getApplicationContext(), products);
        
        lvMain = (ListView)rootView.findViewById(R.id.lvMain);
        //lvMain.setAdapter(boxAdapter);
        
        
        lvMain.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ImageView img1=(ImageView) view.findViewById(R.id.imageView1);
				img1.setImageResource(android.R.drawable.presence_invisible);
				
				
				int id1=boxAdapter.getId(position);
				UpdateReadUnread(""+id1);
			
				ViewInBoxDetailFragment newFragment = new ViewInBoxDetailFragment();
			    //Bundle args = new Bundle();
			    //args.putInt("MyId", position);
			    //newFragment.setArguments(args);
			    FragmentTransaction transaction = getFragmentManager().beginTransaction();
			    transaction.replace(R.id.frame_container, newFragment);
		        transaction.addToBackStack(null);
		        transaction.commit();
			}
		});
      
        
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showResult(lvMain);
				//fetchClick(lvMain);
			}
		});                                 
        return rootView;
    }
	
	public void fillData() {
	        for (int i = 1; i <= 20; i++) {
	          products.add(new Product("Product"+i,"hi",
	              "2015/10/10", false,1,1,1));
	        }
	      }
	 
	public void showResult(View v) {
	        String result = "Selected Product are :";
	        int totalAmount=0;
	        for (Product p : boxAdapter.getBox()) {
	          if (p.box){
	            result += "\n" + p.id;
	            chkboxId=p.id;
	            DeleteData(""+chkboxId);
	          }
	        }
	        Toast.makeText(getActivity().getApplicationContext(),result+"\n"+"Total Amount:="+totalAmount, Toast.LENGTH_LONG).show();
	      }
	
	
	public void GetInboxFromServer()
	{
		if(GlobalSetting.isNetworkConnected(getActivity().getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					//dialog = ProgressDialog.show(getActivity().getApplicationContext(), "Please Wait",
						//	"Loding...", true);
				}
				

				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					param.put("uname",Global.getPreferenceString(getActivity().getApplicationContext(), Global.PREF_User_Email,""));
					try {
						response_user_availability=Board_Controller.post(GlobalSetting.GetInboxURL,param);
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
								//dialog.dismiss();
							}
							else if(success == 0){
								
								Toast.makeText(getActivity().getApplicationContext(),"NO data !",Toast.LENGTH_SHORT).show();
								//dialog.dismiss();
							}
							else{
								Toast.makeText(getActivity().getApplicationContext(),"Not Inserted !",Toast.LENGTH_SHORT).show();
								//dialog.dismiss();
							}
							
						}catch(JSONException e){
							Log.e("Error1","Error..."+e.toString());
							e.printStackTrace();
							//dialog.dismiss();
						}
					}else{
						Toast.makeText(getActivity().getApplicationContext(),"data not get",Toast.LENGTH_SHORT).show();
						//dialog.dismiss();
					}
					
				}
				
			}.execute();
		}else{
			Toast.makeText(getActivity().getApplicationContext(),"Network not available !",Toast.LENGTH_SHORT).show();
		}
			
	}

	
	public void UpdateReadUnread(final String pos)
	{
		if(GlobalSetting.isNetworkConnected(getActivity().getApplicationContext())){
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
								
								Toast.makeText(getActivity().getApplicationContext(), "Updated Successfully ", 1).show();
								//Log.e("Success!", ""+json.getString("msg"));
								
							} else if (success == 0) {
								Toast.makeText(getActivity().getApplicationContext(), "some error..", 1).show();
								Log.e("Failure!", ""+json.getString("msg"));
							}

						} 
						catch (JSONException e) {
							e.printStackTrace();
							Log.e("exception:","error..");
						}
					} else {
						Toast.makeText(getActivity().getApplicationContext(), "Sending Data Failed!", 1).show();
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
		if(GlobalSetting.isNetworkConnected(getActivity().getApplicationContext())){
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
						response= Board_Controller.post(GlobalSetting.DeleteInboxByIdURL, param);
						
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
								
								Toast.makeText(getActivity().getApplicationContext(), "Updated Successfully ", 1).show();
								//Log.e("Success!", ""+json.getString("msg"));
								
							} else if (success == 0) {
								Toast.makeText(getActivity().getApplicationContext(), "some error..", 1).show();
								boxAdapter.notifyDataSetChanged();
								Log.e("Failure!", ""+json.getString("msg"));
							}

						} 
						catch (JSONException e) {
							e.printStackTrace();
							Log.e("exception:","error..");
						}
					} else {
						Toast.makeText(getActivity().getApplicationContext(), "Sending Data Failed!", 1).show();
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
}
