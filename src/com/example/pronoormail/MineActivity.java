package com.example.pronoormail;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.pronoormail.R;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class MineActivity extends ActionBarActivity {

	LinearLayout LinearUser,LinearHandle,LinearContent,LinearCompose,LinearInbox,LinearSendBox,LinearSpam,LinearDraft,LinearTrash;
	SlidingDrawer SlidingDrawer;
	ImageView imgHandle,imageUser,imageLog;
	TextView txtViewName,txtViewUserName;
	String path;
	int PICK_IMAGE,PICK_FILES;
	String fname,imagepath=null,response="",response_user_availability="";
	ProgressDialog dialog;
	private int serverResponseCode = 0;
	String filename;
	TextView textViewInobx1,textView1Sendbox1,textViewDraft1,textViewSpam1,textViewTrash1;
	
	public void findcontrols()
	{
		textViewInobx1=(TextView) findViewById(R.id.textViewInobx1);
		textView1Sendbox1=(TextView) findViewById(R.id.textView1Sendbox1);
		textViewDraft1=(TextView) findViewById(R.id.textViewDraft1);
		textViewSpam1=(TextView) findViewById(R.id.textViewSpam1);
		textViewTrash1=(TextView) findViewById(R.id.textViewTrash1);
		imageLog=(ImageView) findViewById(R.id.imageLog);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine);
		getSupportActionBar().hide();
		
		findcontrols();
		GetCount();
		imageUser=(ImageView) findViewById(R.id.imageUser);
		path=GlobalSetting.ProfileViewIP + Global.getPreferenceString(getApplicationContext(), Global.PREF_User_DP,"");
		
		if(Global.getPreferenceString(getApplicationContext(), Global.PREF_User_DP,"")=="")
		{
			imageUser.setImageResource(R.drawable.profile2);
		}
		else
		{
		Thread thread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		        try {
		        	myImage(path);
		            //Your code goes here
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		});

		thread.start(); 
		}
		LinearUser=(LinearLayout) findViewById(R.id.LinearUser);
		LinearHandle=(LinearLayout) findViewById(R.id.LinearHandle);
		LinearContent=(LinearLayout) findViewById(R.id.content);
		LinearCompose=(LinearLayout) findViewById(R.id.LinearCompose);
		LinearInbox=(LinearLayout) findViewById(R.id.LinearInbox);
		LinearSendBox=(LinearLayout) findViewById(R.id.LinearSendbox);
		LinearSpam=(LinearLayout) findViewById(R.id.LinearSpam);
		LinearDraft=(LinearLayout) findViewById(R.id.LinearDraft);
		LinearTrash=(LinearLayout) findViewById(R.id.LinearTrash);
		SlidingDrawer=(android.widget.SlidingDrawer) findViewById(R.id.slidingDrawer1);
		imgHandle=(ImageView) findViewById(R.id.imageHandle);
		txtViewName=(TextView) findViewById(R.id.textViewName);
		txtViewUserName=(TextView) findViewById(R.id.textViewUserName);
		
		txtViewName.setText(Global.getPreferenceString(getApplicationContext(), Global.PREF_Full_Name,""));
		txtViewUserName.setText(Global.getPreferenceString(getApplicationContext(), Global.PREF_User_Email,""));
		
	    SlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				LinearHandle.setBackgroundResource(R.drawable.handle_buttonup);
				imgHandle.setImageResource(android.R.drawable.arrow_down_float);
				//Toast.makeText(getApplicationContext(),"open",Toast.LENGTH_LONG).show();
				//imgHandle.setImageResource(R.drawable.cir_down);
			}
		});
	    
	    SlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				//Toast.makeText(getApplicationContext(),"closed",Toast.LENGTH_LONG).show();
				LinearHandle.setBackgroundResource(R.drawable.handle_buttondown);
				imgHandle.setImageResource(android.R.drawable.arrow_up_float);
			}
		});
	    	
		LinearInbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), "Hiii",Toast.LENGTH_LONG).show();
				Intent i=new Intent(MineActivity.this,InboxActivity.class);
				startActivity(i);
				//finish();
			}
		});
		
		LinearCompose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(MineActivity.this,ComposeActivity.class);
				startActivity(i);
				//finish();
			}
		});
		
		LinearSendBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(MineActivity.this,SentBoxActivity.class);
				startActivity(i);
				//finish();
			}
		});
		
		LinearSpam.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(MineActivity.this,SpamActivity.class);
				startActivity(i);
				//finish();
			}
		});
		
		LinearDraft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(MineActivity.this,DraftActivity.class);
				startActivity(i);
				//finish();
			}
		});
		
		LinearTrash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(MineActivity.this,TrashActivity.class);
				startActivity(i);
				//finish();
			}
		});
		
		LinearUser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(MineActivity.this,EditProfileActivity.class);
				startActivity(i);
				//finish();
			}
		});
		
		imageUser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectImage();
			}
		});
		
		imageLog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Global.setPreferenceBoolean(getApplicationContext(),Global.PREF_Login_Chk,false);
				Intent i=new Intent(MineActivity.this,LoginActivity.class);
				startActivity(i);
			}
		});
		
	}
	

	public void GetCount()
	{
		if(GlobalSetting.isNetworkConnected(getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					dialog = ProgressDialog.show(MineActivity.this, "Please Wait",
						"Loding...", true);
				}
				

				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					param.put("uname",Global.getPreferenceString(getApplicationContext(), Global.PREF_User_Email,""));
	
					try {
						response_user_availability=Board_Controller.post(GlobalSetting.GetCountURL,param);
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
								String total_inbox = json.getString("inbox");
								String total_sent=json.getString("sent");
								String total_draft=json.getString("draft");
								String total_spam=json.getString("spam");
								String total_trash=json.getString("trash");
								
								textViewInobx1.setText(total_inbox);
								textView1Sendbox1.setText(total_sent);
								textViewDraft1.setText(total_draft);
								textViewSpam1.setText(total_spam);
								textViewTrash1.setText(total_trash);
								//Toast.makeText(getApplicationContext(), ""+Total,Toast.LENGTH_LONG).show();
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
	
	public void myImage(final String MyPath)
	{
		    	 try {
		 			InputStream is=new URL(MyPath).openStream();
		 			BitmapFactory.Options options=new BitmapFactory.Options();
		 			options.inSampleSize=Math.max(options.outWidth/300, options.outHeight/500);
		 			Bitmap bitmap=BitmapFactory.decodeStream(is,null,options);
		 			imageUser.setImageBitmap(bitmap);
		 		} catch (MalformedURLException e) {
		 			// TODO Auto-generated catch block
		 			e.printStackTrace();
		 		} catch (IOException e) {
		 			// TODO Auto-generated catch block
		 			e.printStackTrace();
		 		}
	}
	
	private void selectImage() {
		final AlertDialog builder = new AlertDialog.Builder(MineActivity.this).create();
		builder.setTitle("Add Photo!");
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		 layoutParams.setMargins(60,20,20,20);
		 
		 LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		 layoutParams1.setMargins(40,20,20,20);
		 
		
		 LinearLayout main=new LinearLayout(this);
		 
		 LinearLayout layoutcam    = new LinearLayout(this);
		 ImageView imgcam		   = new ImageView(this);
		 TextView txtcam	       = new TextView(this);
		 
		 LinearLayout layoutgallery      = new LinearLayout(this);
		 ImageView imggallery			 = new ImageView(this);
		 TextView txtgallery             = new TextView(this);
		 
		 LinearLayout layoutcancel       = new LinearLayout(this);
		 ImageView imgcancel			 = new ImageView(this);
		 TextView txtcancel              = new TextView(this);
		
		 
		 main.setOrientation(LinearLayout.VERTICAL);
		 
		 layoutcam.setOrientation(LinearLayout.HORIZONTAL);
		 imgcam.setImageResource(R.drawable.camreaa);
		 txtcam.setText("Take Photo");
		 layoutcam.addView(imgcam);
		 layoutcam.addView(txtcam,layoutParams1);
		 
		 layoutgallery.setOrientation(LinearLayout.HORIZONTAL);
		 imggallery.setImageResource(R.drawable.photo);
		 txtgallery.setText("Choose from Gallery");
		 layoutgallery.addView(imggallery);
		 layoutgallery.addView(txtgallery,layoutParams1);
		 
		 layoutcancel.setOrientation(LinearLayout.HORIZONTAL);
		 imgcancel.setImageResource(R.drawable.cancell);
		 txtcancel.setText("Cancel");
		 layoutcancel.addView(imgcancel);
		 layoutcancel.addView(txtcancel,layoutParams1);
		 
		 
		 main.addView(layoutcam,layoutParams);
		 main.addView(layoutgallery,layoutParams);
		 main.addView(layoutcancel,layoutParams);
	
		 builder.setView(main);
		 layoutcam.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				String path = Environment.getExternalStorageDirectory().toString();
				File myDir=new File(path + "/CAMERA");
				myDir.mkdir();
				Random generator = new Random();
				int n = 10000;
				n = generator.nextInt(n);
				fname = "Image-"+ n +".jpg";
				File file = new File (myDir, fname);
				//Toast.makeText(getApplicationContext(), ""+file.getAbsolutePath(), 1).show();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				startActivityForResult(intent, 1);
				builder.dismiss();
			}
		});
		 
		layoutgallery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
		        intent.setType("image/*");
		        intent.setAction(Intent.ACTION_GET_CONTENT);
		        startActivityForResult(Intent.createChooser(intent,"Select files"), PICK_IMAGE);
		        builder.dismiss();

			}
		});
		
		layoutcancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				builder.dismiss();
			}
		});
		 builder.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				File file = new File(Environment.getExternalStorageDirectory().toString()+"/CAMERA/" + fname);
				try {
					Bitmap bitmap = null;
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
					//Toast.makeText(getApplicationContext(), file.getAbsolutePath(),Toast.LENGTH_LONG).show();
					//bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
				    //bitmapOptions);
					imagepath=file.getAbsolutePath();
					String filename=imagepath.substring(imagepath.lastIndexOf("/")+1);
					Toast.makeText(getApplicationContext(),file.getAbsolutePath(), Toast.LENGTH_LONG).show();
					
					new Thread(new Runnable() {
			            public void run() {            
			                 uploadFile(imagepath);                     
			            }
						}).start();
					UpdatePhoto();
					
					OutputStream outFile = null;
					try {
						outFile = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
						outFile.flush();
						outFile.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else if (requestCode == PICK_IMAGE) {

				 Uri selectedImageUri = data.getData();
		          imagepath = getPath(selectedImageUri);
		          filename=imagepath.substring(imagepath.lastIndexOf("/")+1);
		          
		          new Thread(new Runnable() {
			            public void run() {            
			                 uploadFile(imagepath);                     
			            }
						}).start();
					UpdatePhoto();
		          
			      Toast.makeText(getApplicationContext(),"only file=" + filename, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor =managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	

	public void UpdatePhoto()
	{
			if(GlobalSetting.isNetworkConnected(getApplicationContext())){
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {
						dialog = ProgressDialog.show(MineActivity.this, "Please Wait",
								"Sending Data to Server.", true);
					}
					@Override
					protected Void doInBackground(Void... params) {
						Map<String, String> param = new HashMap<String, String>();
						
				        param.put("uname",Global.getPreferenceString(getApplicationContext(), Global.PREF_User_Email,""));
				        param.put("filename",filename);
				        
						try {
							response= Board_Controller.post(GlobalSetting.updatePhotoUri, param);
							
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
									
									Toast.makeText(MineActivity.this, "Registered Successfully ", 1).show();
									//Log.e("Success!", ""+json.getString("msg"));
									
								} else if (success == 0) {
									Toast.makeText(MineActivity.this, "some error..", 1).show();
									Log.e("Failure!", ""+json.getString("msg"));
								}

							} 
							catch (JSONException e) {
								e.printStackTrace();
								Log.e("exception:","error..");
							}
						} else {
							Toast.makeText(MineActivity.this, "Sending Data Failed!", 1).show();
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
	                 URL url = new URL(GlobalSetting.upLoadPhotoUri);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mine, menu);
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
