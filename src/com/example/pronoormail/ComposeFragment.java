package com.example.pronoormail;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.pronoormail.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ComposeFragment extends Fragment {
	
	public ComposeFragment(){}
	
	EditText EdtTo,EdtSub,EdtMsg;
	ImageView imgAttach,imgSave,imgSend,imgStar;
	Uri uri;
	int PICK_IMAGE,PICK_FILES,PICK_AUDIO,PICK_VIDEO;
	private ProgressDialog dialog = null;
	private String imagepath=null,filename,To,From,Subject,Message,Date,Star,Spam,Read_Unread,response="",AttachFlag="0",StarFlag="0";//0 means false
	private String upLoadServerUri = null;
	private int serverResponseCode = 0;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_compose, container, false);
        
        upLoadServerUri="http://192.168.1.7:8080/ProNoorMail/Attach_files.php";
        
        EdtTo=(EditText) rootView.findViewById(R.id.editTextTo);
        EdtSub=(EditText) rootView.findViewById(R.id.editTextSubject);
        EdtMsg=(EditText) rootView.findViewById(R.id.editTextMessage);
        imgAttach=(ImageView)rootView.findViewById(R.id.imageViewAttach);
        imgSave=(ImageView)rootView.findViewById(R.id.imageViewSave);
        imgSend=(ImageView) rootView.findViewById(R.id.imageViewSend);
        imgStar=(ImageView) rootView.findViewById(R.id.imageViewStar);
        
        imgAttach.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AttachFlag="1";
				selectImage();
				
			}
		});
        
        imgSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(getActivity(), "", "Uploading file...", true);
	             //textFile.setText("uploading started.....");
	             new Thread(new Runnable() {
	                 public void run() {            
	                      uploadFile(imagepath);
	                                               
	                 }
	               }).start();
				
			}
		});
        
        imgSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SendMail();
				
			}
		});
        
        imgStar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StarFlag="1";
				
			}
		});
        
        return rootView;
    }
	
	public void SendMail()
	{
		To=EdtTo.getText().toString();
		Subject=EdtSub.getText().toString();
		Message=EdtMsg.getText().toString();
		Spam="1";
		Read_Unread="0";//unread
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
	    Date= sdf.format(new Date());
	    if(StarFlag.equals("1"))
	    {
	    	filename="";
	    	Star="1";
	        InsertDataOnServer();
	    }
	    else if(AttachFlag.equals("1"))
	    {
	    	filename="~/Attachment/"+imagepath.substring(imagepath.lastIndexOf("/")+1);
	    	Star="0";
			dialog = ProgressDialog.show(getActivity(), "", "Uploading file...", true);
	        //textFile.setText("uploading started.....");
	        new Thread(new Runnable() {
	            public void run() {            
	                 uploadFile(imagepath);                     
	            }
	          }).start();
	        InsertDataOnServer();
	    }
	    else if(AttachFlag.equals("1") && StarFlag.equals("1"))
	    {
	    	filename="~/Attachment/"+imagepath.substring(imagepath.lastIndexOf("/")+1);
	    	Star="1";
			dialog = ProgressDialog.show(getActivity(), "", "Uploading file...", true);
	        //textFile.setText("uploading started.....");
	        new Thread(new Runnable() {
	            public void run() {            
	                 uploadFile(imagepath);                     
	            }
	          }).start();
	        InsertDataOnServer();
	    }
	    else{
	    	filename="";
	    	Star="0";
	    	InsertDataOnServer();
	    }
		//Toast.makeText(getActivity().getApplicationContext(),"only file=" + filename, Toast.LENGTH_LONG).show();
	}
	
	
	public void InsertDataOnServer()
	{
			if(GlobalSetting.isNetworkConnected(getActivity().getApplicationContext())){
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {
						//dialog = ProgressDialog.show(getActivity().getApplicationContext(), "Please Wait",
								//"Sending Data to Server.", true);
					}
					@Override
					protected Void doInBackground(Void... params) {
						Map<String, String> param = new HashMap<String, String>();
						param.put("from_name",Global.getPreferenceString(getActivity().getApplicationContext(), Global.PREF_Full_Name,""));
						param.put("from_uname",Global.getPreferenceString(getActivity().getApplicationContext(), Global.PREF_User_Email,""));
						param.put("to_uname", To);
				        param.put("Subject", Subject);
				        param.put("Message", Message);
				        param.put("Star",Star);
				        param.put("Spam", Spam);
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
									
									Toast.makeText(getActivity().getApplicationContext(), "Mail sent Successfully ", 1).show();
									//Log.e("Success!", ""+json.getString("msg"));
									 InboxFragment newFragment = new InboxFragment();
								      // Bundle args = new Bundle();
								       //args.putInt("MyId", position);
								       //newFragment.setArguments(args);
								        FragmentTransaction transaction = getFragmentManager().beginTransaction();
								        transaction.replace(R.id.frame_container, newFragment);
							            transaction.addToBackStack(null);
							            transaction.commit();
									
									
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
							dialog.dismiss();
						}
					}

				}.execute();
				

			}else{
				Log.e("Warning!!!","Unable to Connect Internet.");
				
			}

	}
	
	private void selectImage() {
		final AlertDialog builder = new AlertDialog.Builder(this.getActivity()).create();
		builder.setTitle("Attach Files!");
		
		 LinearLayout main=new LinearLayout(this.getActivity());
		 main.setOrientation(LinearLayout.VERTICAL);
		 main.setBackground(getResources().getDrawable(R.drawable.list_selector));
		 
		 LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		 layoutParams.setMargins(60,20,20,20);
		 //layout_1
		 LinearLayout layout_1    = new LinearLayout(this.getActivity());
		 LinearLayout layout_photo    = new LinearLayout(this.getActivity());
		 ImageView imgphoto		   = new ImageView(this.getActivity());
		 TextView txtphoto	       = new TextView(this.getActivity());
		 
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
		 
		 LinearLayout layout_myfiles    = new LinearLayout(this.getActivity());
		 ImageView imgmyfiles	   = new ImageView(this.getActivity());
		 TextView txtmyfiles	       = new TextView(this.getActivity());
		
		
		 layout_myfiles.setOrientation(LinearLayout.VERTICAL);
		 imgmyfiles.setImageResource(R.drawable.myfiles_64);
		 txtmyfiles.setText("My Files");
		 txtmyfiles.setTextColor(Color.WHITE);
		 txtmyfiles.setGravity(Gravity.CENTER);
		 layout_myfiles.addView(imgmyfiles);
		 layout_myfiles.addView(txtmyfiles);
		 layout_1.addView(layout_myfiles,layoutParams);
		 
		 
		 
		 //layout_3
		 LinearLayout layout_2   = new LinearLayout(this.getActivity());
		 LinearLayout layout_audio   = new LinearLayout(this.getActivity());
		 ImageView imgaudio		   = new ImageView(this.getActivity());
		 TextView txtaudio	       = new TextView(this.getActivity());
		 
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
		 
		 LinearLayout layout_video    = new LinearLayout(this.getActivity());
		 ImageView imgvideo  	   = new ImageView(this.getActivity());
		 TextView txtvideo 	       = new TextView(this.getActivity());
		
		
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
				Intent intent = new Intent();
		        intent.setType("image/*");
		        intent.setAction(Intent.ACTION_GET_CONTENT);
		        startActivityForResult(Intent.createChooser(intent,"Select files"), PICK_IMAGE);
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
				Intent intent = new Intent();
		        intent.setType("audio/*");
		        intent.setAction(Intent.ACTION_GET_CONTENT);
		        startActivityForResult(Intent.createChooser(intent,"Select files"), PICK_AUDIO);
		        builder.dismiss();
			}
		});
		
		layout_video.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
		        intent.setType("video/*");
		        intent.setAction(Intent.ACTION_GET_CONTENT);
		        startActivityForResult(Intent.createChooser(intent,"Select files"), PICK_VIDEO);
		        builder.dismiss();
			}
		});
		 
		
		 builder.show();
	}
	
    public String getPath(Uri uri) {
         String[] projection = { MediaStore.Images.Media.DATA };
         Cursor cursor =getActivity().managedQuery(uri, projection, null, null, null);
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
        if (getActivity().getPackageManager().resolveActivity(sIntent, 0) != null){
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
        }
        else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }

        try {
        	//Toast.makeText(getApplicationContext(), "khabar ni.", Toast.LENGTH_SHORT).show();
            startActivityForResult(chooserIntent, PICK_FILES);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity().getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
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
              
             getActivity().runOnUiThread(new Runnable() {
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
                 URL url = new URL(upLoadServerUri);
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
                      
                     getActivity().runOnUiThread(new Runnable() {
                          public void run() {
                              String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" F:/wamp/wamp/www/uploads";
                              //textFile.setText(msg);
                              Toast.makeText(getActivity().getApplicationContext(), "File Upload Complete.", Toast.LENGTH_SHORT).show();
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
                 
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //textFile.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(getActivity().getApplicationContext(), "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
                 
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
            } catch (Exception e) {
                 
                dialog.dismiss();  
                e.printStackTrace();
                 
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //textFile.setText("Got Exception : see logcat ");
                        Toast.makeText(getActivity().getApplicationContext(), "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
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
				  Uri selectedImageUri = data.getData();
		          imagepath = getPath(selectedImageUri);
		          String filename=imagepath.substring(imagepath.lastIndexOf("/")+1);
			       Toast.makeText(getActivity().getApplicationContext(),"only file=" + filename, Toast.LENGTH_LONG).show();
		          //Toast.makeText(getActivity().getApplicationContext(),"" + imagepath, Toast.LENGTH_LONG).show();
				
			} else if (requestCode == PICK_FILES) {
				 //Uri selectedImageUri = data.getData();
		         imagepath=data.getData().getPath();
		         String filename=imagepath.substring(imagepath.lastIndexOf("/")+1);
		         Toast.makeText(getActivity().getApplicationContext(),"only file=" + filename, Toast.LENGTH_LONG).show();
		         //Toast.makeText(getActivity().getApplicationContext(),"" + imagepath, Toast.LENGTH_LONG).show();		
			}
			else if (requestCode == PICK_AUDIO) {
				 Uri selectedImageUri = data.getData();
		          imagepath = getPath(selectedImageUri);
		          String filename=imagepath.substring(imagepath.lastIndexOf("/")+1);
			         Toast.makeText(getActivity().getApplicationContext(),"only file=" + filename, Toast.LENGTH_LONG).show();
		          //Toast.makeText(getActivity().getApplicationContext(),"" + imagepath, Toast.LENGTH_LONG).show();
			}
			else if (requestCode == PICK_VIDEO) {
				 Uri selectedImageUri = data.getData();
		         imagepath = getPath(selectedImageUri);
		         String filename=imagepath.substring(imagepath.lastIndexOf("/")+1);
		         Toast.makeText(getActivity().getApplicationContext(),"only file=" + filename, Toast.LENGTH_LONG).show();
			}
			
		}
	
	}
	

	
}
