package com.example.pronoormail;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.pronoormail.adapter.PathAdapter;

import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DraftSendActivity extends ActionBarActivity {

	String Myid,response="";
	JSONArray ItemDetail;
	EditText EdtTo,EdtSub,EdtMsg;
	String Inbox,Sent,upLoadServerUri,Spam,Read_Unread,Draft,Trash,StarFlag="0",AttachFlag="0",filename="",imagepath=null;
	ProgressDialog dialog;
	String From,To,Subject,Date,Message,Attachment="",Star;
	Uri uri;
	int PICK_IMAGE,PICK_FILES,PICK_AUDIO,PICK_VIDEO;
	private int serverResponseCode = 0;
	ListView lv_attachment_path;
	int cnt= 0;
	String[] selected_file_path = new String[' '];
	
	public void getIntentData()
	{
		Intent i=getIntent();
		Bundle b=i.getExtras();
		Myid=b.getString("id");
		Toast.makeText(DraftSendActivity.this,""+Myid,Toast.LENGTH_LONG).show();
	}
	
	public void FindControls()
	{	
		EdtTo=(EditText) findViewById(R.id.editTextTo);
		EdtSub=(EditText) findViewById(R.id.editTextSubject);
		EdtMsg=(EditText) findViewById(R.id.editTextMessage);
		lv_attachment_path = (ListView) findViewById(R.id.lv_attachment_path);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draft_send);
		
		FindControls();
		getIntentData();
		getDataById();
		
		 lv_attachment_path.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
				{
					
					long viewId = view.getId();
					String[] new_selected_file_path = new String[' '];
					int j=0;
					if (viewId == R.id.imgv_remove_selected_file_name) 
				    {
						for(int i = 0; i < cnt; i++)
						{
							if(!selected_file_path[i].trim().equals(selected_file_path[position]))
							{
								new_selected_file_path[j] = selected_file_path[i];
								j++;
							}
						}
						
						selected_file_path = new String[' '];
						cnt=0;
						for(int i = 0; i < j; i++)
						{
							selected_file_path[i] = new_selected_file_path[i];
							cnt++;
						}
						fillUpList();
				    } 
				}
			});
	}
	
	public void fillUpList()
	{
		String[] file_name = new String[cnt];
		
		for (int i = 0; i < cnt; i++) 
		{
			file_name[i] = Uri.parse(selected_file_path[i]).getLastPathSegment();
		}
		PathAdapter pathAdapter = new PathAdapter(getApplicationContext(), file_name);
		lv_attachment_path.setAdapter(pathAdapter);
	}
	public void SendMail()
	{
		To=EdtTo.getText().toString();
		Subject=EdtSub.getText().toString();
		Message=EdtMsg.getText().toString();
		Spam="0";
		Read_Unread="0";//unread
		Draft="0";
		Sent="1";//1 means display in sentbox and on deleting sentbox will be 0;
		Inbox="1";//1 means display in inbox and on deleting inbox will be 0;
		Trash="0";
		String format="yyyy-MM-dd hh:mm aa";
	    Date= (String) DateFormat.format(format,Calendar.getInstance().getTime());
	    if(StarFlag.equals("1") && AttachFlag.equals("0"))
	    {
	    	filename="";
	    	Star="1";
	        InsertDataOnServer();
	    }
	    else if(AttachFlag.equals("1") && StarFlag.equals("0"))
	    {
	    	 Star="0";
	    	 new Thread(new Runnable() {
		            public void run() {       
		            	for(int i=0;i<cnt;i++)
		            	{
		            		uploadFile(selected_file_path[i]); 
		            	}
		            }
		          }).start();
			 
			 for(int i=0;i<cnt;i++)
			 {
				Toast.makeText(getApplicationContext(), ""+selected_file_path[i], 1).show();
				filename +=Attachment+ "," +selected_file_path[i].substring(selected_file_path[i].lastIndexOf("/")+1);
			 }
			InsertDataOnServer();
	    }
	    else if(AttachFlag.equals("1") && StarFlag.equals("1"))
	    {
	    	Star="1";
	    	Star="1";
	    	 new Thread(new Runnable() {
		            public void run() {       
		            	for(int i=0;i<cnt;i++)
		            	{
		            		uploadFile(selected_file_path[i]); 
		            	}
		            }
		          }).start();
			 
			 for(int i=0;i<cnt;i++)
			 {
				Toast.makeText(getApplicationContext(), ""+selected_file_path[i], 1).show();
				filename += Attachment + "," +selected_file_path[i].substring(selected_file_path[i].lastIndexOf("/")+1);
			 }
			InsertDataOnServer();
	    }
	    else
	    {
	    	filename=Attachment;
	    	Star=Star;
	    	InsertDataOnServer();
	    }
		//Toast.makeText(getActivity().getApplicationContext(),"only file=" + filename, Toast.LENGTH_LONG).show();
	}
	
	
	public void getDataById()
	{
		if(GlobalSetting.isNetworkConnected(DraftSendActivity.this)){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					
				}
				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					param.put("id",Myid);
					
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
									Star=s.getString("star");
									Attachment=s.getString("attachment");
									Inbox=s.getString("inbox");
									Sent=s.getString("sent");	
								}
								
								String format="yyyy-MM-dd hh:mm aa";
							    Date= (String) DateFormat.format(format,Calendar.getInstance().getTime());
							    
								EdtTo.setText(To);
								EdtSub.setText(Subject);
								EdtMsg.setText(Message);	
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
						Toast.makeText(DraftSendActivity.this,"data not get",Toast.LENGTH_SHORT).show();
						
					}
					
				}
				
			}.execute();
		}else{
			Toast.makeText(getApplicationContext(),"Network not available !",Toast.LENGTH_SHORT).show();
		}
			
	}
	
	public void InsertDataOnServer()
	{
			if(GlobalSetting.isNetworkConnected(getApplicationContext())){
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {
						dialog = ProgressDialog.show(DraftSendActivity.this, "Please Wait","Sending Data to Server.", true);
					}
					@Override
					protected Void doInBackground(Void... params) {
						Map<String, String> param = new HashMap<String, String>();
						param.put("from_name",Global.getPreferenceString(getApplicationContext(), Global.PREF_Full_Name,""));
						param.put("from_uname",Global.getPreferenceString(getApplicationContext(), Global.PREF_User_Email,""));
						param.put("to_uname", To);
				        param.put("Subject", Subject);
				        param.put("Message", Message);
				        param.put("Star",Star);
				        param.put("Spam", Spam);
				        param.put("Draft", Draft);
				        param.put("Inbox", Inbox);
				        param.put("Sent", Sent);
				        param.put("Trash",Trash);
				        param.put("Date",Date);
				        param.put("Attachment",filename);
				        param.put("Read_Unread", Read_Unread);
				        
						try {
							response= Board_Controller.post(GlobalSetting.SendMailURL, param);
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
									
									Toast.makeText(getApplicationContext(), "Mail sent Successfully ", 1).show();
									Intent i=new Intent(DraftSendActivity.this,InboxActivity.class);
									startActivity(i);
									//finish();
									//Log.e("Success!", ""+json.getString("msg"));
									 dialog.dismiss();
									
								} else if (success == 0) {
									Toast.makeText(getApplicationContext(), "some error..", 1).show();
									Log.e("Failure!", ""+json.getString("msg"));
									dialog.dismiss();
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
							dialog.dismiss();
						}
					}

				}.execute();
				

			}else{
				Log.e("Warning!!!","Unable to Connect Internet.");
				
			}
	}
	
	private void selectImage() {
		final AlertDialog builder = new AlertDialog.Builder(this).create();
		builder.setTitle("Attach Files!");
		
		 LinearLayout main=new LinearLayout(this);
		 main.setOrientation(LinearLayout.VERTICAL);
		 main.setBackground(getResources().getDrawable(R.drawable.list_selector));
		 
		 LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		 layoutParams.setMargins(60,20,20,20);
		 //layout_1
		 LinearLayout layout_1    = new LinearLayout(this);
		 LinearLayout layout_photo    = new LinearLayout(this);
		 ImageView imgphoto		   = new ImageView(this);
		 TextView txtphoto	       = new TextView(this);
		 
		 layout_1.setOrientation(LinearLayout.HORIZONTAL);
		 
		 layout_photo.setOrientation(LinearLayout.VERTICAL);
		 imgphoto.setImageResource(R.drawable.photo_64);
		 txtphoto.setText("Photo");
		 txtphoto.setTextColor(Color.WHITE);
		 txtphoto.setGravity(Gravity.CENTER);
		 
		 layout_photo.addView(imgphoto);
		 layout_photo.addView(txtphoto);
		 layout_1.addView(layout_photo,layoutParams);
		 
		 //layout 2
		 
		 LinearLayout layout_myfiles    = new LinearLayout(this);
		 ImageView imgmyfiles	   = new ImageView(this);
		 TextView txtmyfiles	       = new TextView(this);
		
		
		 layout_myfiles.setOrientation(LinearLayout.VERTICAL);
		 imgmyfiles.setImageResource(R.drawable.myfiles_64);
		 txtmyfiles.setText("My Files");
		 txtmyfiles.setTextColor(Color.WHITE);
		 txtmyfiles.setGravity(Gravity.CENTER);
		 layout_myfiles.addView(imgmyfiles);
		 layout_myfiles.addView(txtmyfiles);
		 layout_1.addView(layout_myfiles,layoutParams);
		 
		 
		 
		 //layout_3
		 LinearLayout layout_2   = new LinearLayout(this);
		 LinearLayout layout_audio   = new LinearLayout(this);
		 ImageView imgaudio		   = new ImageView(this);
		 TextView txtaudio	       = new TextView(this);
		 
		 layout_2.setOrientation(LinearLayout.HORIZONTAL);
		 
		 layout_audio.setOrientation(LinearLayout.VERTICAL);
		 imgaudio.setImageResource(R.drawable.audio_1_64);
		 txtaudio.setText("Audio");
		 txtaudio.setTextColor(Color.WHITE);
		 txtaudio.setGravity(Gravity.CENTER);
		 layout_audio.addView(imgaudio);
		 layout_audio.addView(txtaudio);
		 layout_2.addView(layout_audio,layoutParams);
		 
		 //layout 4
		 
		 LinearLayout layout_video    = new LinearLayout(this);
		 ImageView imgvideo  	   = new ImageView(this);
		 TextView txtvideo 	       = new TextView(this);
		
		
		 layout_video.setOrientation(LinearLayout.VERTICAL);
		 imgvideo.setImageResource(R.drawable.video_64);
		 txtvideo.setText("Video");
		 txtvideo.setTextColor(Color.WHITE);
		 txtvideo.setGravity(Gravity.CENTER);
		 layout_video .addView(imgvideo);
		 layout_video .addView(txtvideo);
		 layout_2.addView(layout_video,layoutParams);
		 
		 
		 main.addView(layout_1);
		 main.addView(layout_2);
		
		 builder.setView(main);
		 layout_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
		        builder.dismiss();
			}
		});
		 
		layout_myfiles.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openFile("*/*");
				builder.dismiss();
			}
		});
		
		layout_audio.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_AUDIO);
		        builder.dismiss();
			}
		});
		
		layout_video.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_VIDEO);
		        builder.dismiss();
			}
		});
		 builder.show();
	}
	
	 public String getPath(Uri uri) {
         String[] projection = { MediaStore.Images.Media.DATA };
         Cursor cursor =managedQuery(uri, projection, null, null, null);
         int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
         cursor.moveToFirst();
         return cursor.getString(column_index);
     }
	 
	 public void openFile(String minmeType) {

	        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	        intent.setType(minmeType);
	        intent.addCategory(Intent.CATEGORY_OPENABLE);

	        // special intent for Samsung file manager
	        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
	         // if you want any file type, you can skip next line 
	        //sIntent.putExtra("CONTENT_TYPE", minmeType); 
	        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

	        Intent chooserIntent;
	        if (getPackageManager().resolveActivity(sIntent, 0) != null){
	            // it is device with samsung file manager
	            chooserIntent = Intent.createChooser(sIntent, "Open file");
	            //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
	        }
	        else {
	            chooserIntent = Intent.createChooser(intent, "Open file");
	        }

	        try {
	        	//Toast.makeText(getApplicationContext(), "khabar ni.", Toast.LENGTH_SHORT).show();
	            startActivityForResult(chooserIntent, 4);
	        } catch (android.content.ActivityNotFoundException ex) {
	            Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
	        }
	    }
		
	 
	 public int uploadFile(String sourceFileUri) {
	        
	        String fileName = sourceFileUri;

	        HttpURLConnection conn = null;
	        DataOutputStream dos = null;  
	        String lineEnd = "\r\n";
	        String twoHyphens = "--";
	        String boundary = "*****";
	        int bytesRead, bytesAvailable, bufferSize;
	        byte[] buffer;
	        int maxBufferSize = 1 * 2048 * 2048; 
	        File sourceFile = new File(sourceFileUri); 
	         
	        if (!sourceFile.isFile()) {
	             
	             dialog.dismiss(); 
	              
	             Log.e("uploadFile", "Source File not exist :"+imagepath);
	              
	             runOnUiThread(new Runnable() {
	                 public void run() {
	                     //textFile.setText("Source File not exist :"+ imagepath);
	                 }
	             }); 
	              
	             return 0;
	          
	        }
	        else
	        {
	             try { 
	                  
	                   // open a URL connection to the Servlet
	                 FileInputStream fileInputStream = new FileInputStream(sourceFile);
	                 URL url = new URL(GlobalSetting.upLoadServerUri);
	                 Map<String, String> param = new HashMap<String, String>();
	                 //URL url=Board_Controller.post(GlobalSetting.LoginURL,param);
	                 // Open a HTTP  connection to  the URL
	                 conn = (HttpURLConnection) url.openConnection(); 
	                 conn.setDoInput(true); // Allow Inputs
	                 conn.setDoOutput(true); // Allow Outputs
	                 conn.setUseCaches(false); // Don't use a Cached Copy
	                 conn.setRequestMethod("POST");
	                 conn.setRequestProperty("Connection", "Keep-Alive");
	                 conn.setRequestProperty("ENCTYPE", "multipart/form-data");
	                 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
	                 conn.setRequestProperty("uploaded_file", fileName); 
	                  
	                 dos = new DataOutputStream(conn.getOutputStream());
	        
	                 dos.writeBytes(twoHyphens + boundary + lineEnd); 
	                 dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
	                                           + fileName + "\"" + lineEnd);
	                  
	                 dos.writeBytes(lineEnd);
	        
	                 // create a buffer of  maximum size
	                 bytesAvailable = fileInputStream.available(); 
	        
	                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
	                 buffer = new byte[bufferSize];
	        
	                 // read file and write it into form...
	                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
	                    
	                 while (bytesRead > 0) {
	                      
	                   dos.write(buffer, 0, bufferSize);
	                   bytesAvailable = fileInputStream.available();
	                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
	                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
	                    
	                  }
	        
	                 // send multipart form data necesssary after file data...
	                 dos.writeBytes(lineEnd);
	                 dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	        
	                 // Responses from the server (code and message)
	                 serverResponseCode = conn.getResponseCode();
	                 String serverResponseMessage = conn.getResponseMessage();
	                   
	                 Log.i("uploadFile", "HTTP Response is : "
	                         + serverResponseMessage + ": " + serverResponseCode);
	                  
	                 if(serverResponseCode == 200){
	                      
	                     runOnUiThread(new Runnable() {
	                          public void run() {
	                              String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
	                                    +" F:/wamp/wamp/www/uploads";
	                              //textFile.setText(msg);
	                              Toast.makeText(getApplicationContext(), "File Upload Complete.", Toast.LENGTH_SHORT).show();
	                          }
	                      });                
	                 }    
	                  
	                 //close the streams //
	                 fileInputStream.close();
	                 dos.flush();
	                 dos.close();
	                   
	            } catch (MalformedURLException ex) {
	                 
	                dialog.dismiss();  
	                ex.printStackTrace();
	                 
	                runOnUiThread(new Runnable() {
	                    public void run() {
	                        //textFile.setText("MalformedURLException Exception : check script url.");
	                        Toast.makeText(getApplicationContext(), "MalformedURLException", Toast.LENGTH_SHORT).show();
	                    }
	                });
	                 
	                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
	            } catch (Exception e) {
	                 
	                dialog.dismiss();  
	                e.printStackTrace();
	                 
	                runOnUiThread(new Runnable() {
	                    public void run() {
	                        //textFile.setText("Got Exception : see logcat ");
	                        Toast.makeText(getApplicationContext(), "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
	                    }
	                });
	                Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);  
	            }
	            dialog.dismiss();       
	            return serverResponseCode; 
	             
	         } // End else block 
	       }
		
	 @Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
				super.onActivityResult(requestCode, resultCode, data);
				if (resultCode ==Activity.RESULT_OK) {
					if (requestCode == PICK_IMAGE) {
						Uri i = data.getData(); // getDATA
						String s = i.getEncodedPath(); // getPath

						String[] projection = { MediaStore.Images.Media.DATA };

						String picturePath = null;
						try 
						{
							Cursor cursor = getContentResolver().query(i, projection, null, null, null);
							cursor.moveToFirst();

							int columnIndex = cursor.getColumnIndex(projection[0]);
							picturePath = cursor.getString(columnIndex);
							cursor.close();
							Log.d("Picture Path", picturePath);
						} 
						catch (Exception e) 
						{
							Log.e("Path Error", e.toString());
						}
						selected_file_path[cnt] = picturePath;
						cnt++;
						fillUpList();
						
					} else if (requestCode == 4) {
						String filespath=null;
						 filespath=data.getData().getPath();
						 //imagepath=data.getData().getPath();
				         String filename=filespath.substring(filespath.lastIndexOf("/")+1);
				         Toast.makeText(getApplicationContext(),"only file=" + filespath, Toast.LENGTH_LONG).show();
				         //Toast.makeText(getActivity().getApplicationContext(),"" + imagepath, Toast.LENGTH_LONG).show();
				         selected_file_path[cnt] = filespath;
						 cnt++;
						 fillUpList();
					}
					else if (requestCode == PICK_AUDIO) {
						Uri i = data.getData(); // getDATA
						String s = i.getEncodedPath(); // getPath

						String[] projection = { MediaStore.Audio.Media.DATA };

						String audio_path = null;
						try 
						{
							Cursor cursor = getContentResolver().query(i, projection, null, null, null);
							cursor.moveToFirst();

							int columnIndex = cursor.getColumnIndex(projection[0]);
							audio_path = cursor.getString(columnIndex);

							cursor.close();
							Log.d("Audio Path", audio_path);
						} 
						catch (Exception e) 
						{
							Log.e("Path Error", e.toString());
						}
						selected_file_path[cnt] = audio_path;
						cnt++;
						fillUpList();
					}
					else if (requestCode == PICK_VIDEO) {
						Uri i = data.getData(); // getDATA
						String s = i.getEncodedPath(); // getPath

						String[] projection = { MediaStore.Video.Media.DATA };

						String picturePath = null;
						try 
						{
							Cursor cursor = getContentResolver().query(i, projection, null, null, null);
							cursor.moveToFirst();

							int columnIndex = cursor.getColumnIndex(projection[0]);
							picturePath = cursor.getString(columnIndex);

							cursor.close();
							Log.d("Picture Path", picturePath);
						} 
						catch (Exception e) 
						{
							Log.e("Path Error", e.toString());
						}
						selected_file_path[cnt] = picturePath;
						cnt++;
						fillUpList();
				        
					}
					
				}
			
			}
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.draft_send, menu);
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
		if(id==R.id.Attachment)
		{
			AttachFlag="1";
			selectImage();
			return true;
		}
		if(id==R.id.Star)
		{
			StarFlag="1";
			return true;
		}
		if(id==R.id.Send)
		{
			SendMail();
			return true;
		}
		if(id==R.id.Discard)
		{
			Intent i=new Intent(DraftSendActivity.this,MineActivity.class);
			startActivity(i);
			//finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
