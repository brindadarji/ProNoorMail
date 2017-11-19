package com.example.pronoormail;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends ActionBarActivity {

	Spinner SpinnerUSerType;
	EditText EdtUserName,EdtFName,EdtLName,EdtDOB,EdtContact,EdtCountry,EdtState,EdtCity,EdtAltEmail,EdtPassword,EdtConpass;
	Button btnReset,btnSubmit;
	ProgressDialog dialog;
	String response="",response_user_availability="";
	JSONArray RegitrationDetail = null;
	String UserNamechecking,imagepath=null;
	int error=0;
	Uri imageUri;
	int PICK_IMAGE,PICK_FILES;
	private int serverResponseCode = 0;
	String username,fname,lname,DOB,DPFlag="0",contact,country,state,city,altemail,pass,conpass,usertype,filename;
	
	private Button mPickDate;
	private int mYear;
	private int mMonth;
	private int mDay;
	static final int DATE_DIALOG_ID = 0;
	
	public void findControls()
	{
		//SpinnerUSerType=(Spinner) findViewById(R.id.spinnerUserType);
		EdtUserName=(EditText) findViewById(R.id.editTextUserName);
		EdtFName=(EditText) findViewById(R.id.editTextFName);
		EdtLName=(EditText) findViewById(R.id.editTextLName);
		EdtDOB=(EditText) findViewById(R.id.editTextDOB);
		EdtContact=(EditText) findViewById(R.id.editTextContact);
		EdtCountry=(EditText) findViewById(R.id.editTextCountry);
		EdtState=(EditText) findViewById(R.id.editTextState);
		EdtCity=(EditText) findViewById(R.id.editTextCity);
		EdtPassword=(EditText) findViewById(R.id.editTextPassword);
		EdtConpass=(EditText) findViewById(R.id.editTextConPass);
		EdtAltEmail=(EditText) findViewById(R.id.editTextAlternateEmail);
		btnSubmit=(Button) findViewById(R.id.btnSubmit);
		btnReset=(Button) findViewById(R.id.btnReset);
		
		EdtDOB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		// get the current date
	    final Calendar c = Calendar.getInstance();
	    mYear = c.get(Calendar.YEAR);
	    mMonth = c.get(Calendar.MONTH);
	    mDay = c.get(Calendar.DAY_OF_MONTH);


	    // display the current date
	    updateDisplay();
	}
	
	//return date picker dialog
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG_ID:
	        return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
	    }
	    return null;
	}
	
	//update month day year
	private void updateDisplay() {
	    EdtDOB.setText(//this is the edit text where you want to show the selected date
	        new StringBuilder()
	            // Month is 0 based so add 1
	        .append(mYear).append("-")
	        .append(mMonth + 1).append("-")
	        .append(mDay).append(""));


	            //.append(mMonth + 1).append("-")
	            //.append(mDay).append("-")
	            //.append(mYear).append(" "));
	}

	// the call back received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener =
	    new DatePickerDialog.OnDateSetListener() {
	        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	            mYear = year;
	            mMonth = monthOfYear;
	            mDay = dayOfMonth;
	            updateDisplay();
	        }
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setTitle("Registration");
	    
		findControls();
		//SpinnerFilling();
		
		btnReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Reset();
			}
		});
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(DPFlag=="1")
				{
					filename=imagepath.substring(imagepath.lastIndexOf("/")+1);
					new Thread(new Runnable() {
						
		            public void run() {            
		                 uploadFile(imagepath);                     
		            }
					}).start();
					Registration();
				}
				else
				{
					filename="";
					Registration();
				}
			}
		});
		
		
	}
	
	/*public void SpinnerFilling()
	{
		 String[] arraySpinner;
		 arraySpinner = new String[] {
				 	"Choose User Type",
		            "Account For Public", 
		            "Priority Account for Bussiness Associates", 
		            "Premium Account"};
		       
		        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		                android.R.layout.simple_spinner_item, arraySpinner);
		        SpinnerUSerType.setAdapter(adapter);
		
	}*/
	
	// validating Email
			private boolean isValidEmail(String email) {
				String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
				Pattern pattern = Pattern.compile(EMAIL_PATTERN);
				Matcher matcher = pattern.matcher(email);
				return matcher.matches();
			}

	// validating Characters
		private boolean isValidCharacters(String email) {
			//String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					//+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			String EMAIL_PATTERN="^[A-Za-z]*[^0-9]";
			Pattern pattern = Pattern.compile(EMAIL_PATTERN);
			Matcher matcher = pattern.matcher(email);
			return matcher.matches();
		}
		
		// validating Contact
		private boolean isValidContact(String email) {
				//String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
							//+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
				String EMAIL_PATTERN="^[0-9]{10,13}";
				Pattern pattern = Pattern.compile(EMAIL_PATTERN);
				Matcher matcher = pattern.matcher(email);
				return matcher.matches();
		}
				
		
	public void Registration()
	{
		//usertype=SpinnerUSerType.getSelectedItem().toString();
		username=EdtUserName.getText().toString();
		fname=EdtFName.getText().toString();
		lname=EdtLName.getText().toString();
		DOB=EdtDOB.getText().toString();
		contact=EdtContact.getText().toString();
		country=EdtCountry.getText().toString();
		state=EdtState.getText().toString();
		city=EdtCity.getText().toString();
		altemail=EdtAltEmail.getText().toString();
		pass=EdtPassword.getText().toString();
		conpass=EdtConpass.getText().toString();
		
		//Toast.makeText(getApplicationContext(), ""+usertype,1).show();
		if(username.equals(""))
		{
			EdtUserName.setError("Please Enter Username");
		}
		else if(fname.equals(""))
		{
			EdtFName.setError("Please enter FirstName");
		}
		else if(!isValidCharacters(fname))
		{
			EdtFName.setError("Please enter valid FirstName");
		}
		else if(lname.equals(""))
		{
			EdtLName.setError("Please enter LastName");
		}
		else if(!isValidCharacters(lname))
		{
			EdtLName.setError("Please enter valid LastName");
		}
		else if(DOB.equals(""))
		{
			EdtDOB.setError("Please enter Date of Birth");
		}
		else if(contact.equals(""))
		{
			EdtContact.setError("Please enter Contact Number");
		}
		else if(!isValidContact(contact))
		{
			EdtContact.setError("Please enter Valid Contact Number");
		}
		
		else if(country.equals(""))
		{
			EdtCountry.setError("Please enter Country");
		}
		else if(state.equals(""))
		{
			EdtState.setError("Please enter State");
		}
		else if(city.equals(""))
		{
			EdtCity.setError("Please enter City");
		}
		else if(!isValidCharacters(city))
		{
			EdtCity.setError("Please enter valid City");
		}
		else if(!isValidCharacters(country))
		{
			EdtCountry.setError("Please enter valid country");
		}
		else if(!isValidCharacters(state))
		{
			EdtState.setError("Please enter valid State");
		}
		else if(altemail.equals(""))
		{
			EdtAltEmail.setError("Please enter Alternate Email id");
		}
		else if(!isValidEmail(altemail))
		{
			EdtAltEmail.setError("Please enter valid email");
		}
		else if(pass.equals(""))
		{
			EdtPassword.setError("Please enter Password");
		}
		else if (pass.length()>10) {
			
			EdtPassword.setError("Password must be lest than 10 character");
		}
		else if(conpass.equals(""))
		{
			EdtConpass.setError("Please enter Confirm Password");
		}
		else if(!pass.equals(conpass))
		{
			Toast.makeText(getApplicationContext(), "Passoword and confirm password must match",1).show();
		}
		else
		{
			chkUserName();
		}
	}
	
	public void chkUserName()
	{
		if(GlobalSetting.isNetworkConnected(getApplicationContext())){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					dialog = ProgressDialog.show(RegistrationActivity.this, "Please Wait",
							"Loding...", true);
				}
				

				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					param.put("uname",username);
					
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
								/*RegitrationDetail=json.getJSONArray("data");
								for(int i=0;i<RegitrationDetail.length();i++)
								{
									JSONObject s=RegitrationDetail.getJSONObject(i);
									UserNamechecking=s.getString("username");
									
								}*/
								EdtUserName.setError("UserName already exist");
								dialog.dismiss();
							}
							else if(success == 0){
								InsertDataOnServer();
								//Toast.makeText(getApplicationContext(),"NO data !",Toast.LENGTH_SHORT).show();
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
			dialog.dismiss();
		}
			
	}

	
	public void InsertDataOnServer()
	{
			if(GlobalSetting.isNetworkConnected(getApplicationContext())){
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {
						dialog = ProgressDialog.show(RegistrationActivity.this, "Please Wait",
								"Sending Data to Server.", true);
					}
					@Override
					protected Void doInBackground(Void... params) {
						Map<String, String> param = new HashMap<String, String>();
						//param.put("Usertype", usertype);
				        param.put("Username", username);
				        param.put("Fname", fname);
				        param.put("Lname", lname);
				        param.put("DOB", DOB);
				        param.put("Contact", contact);
				        param.put("Country", country);
				        param.put("State", state);
				        param.put("City", city);
				        param.put("Altemail", altemail);
				        param.put("dp",filename);
				        param.put("Conpass", conpass);
				       
				        
						try {
							response= Board_Controller.post(GlobalSetting.InsertRegistrationURL, param);
							response_user_availability=Board_Controller.post(GlobalSetting.UserNameAvailabilityChk, param);
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
									
									Toast.makeText(RegistrationActivity.this, "Registered Successfully ", 1).show();
									//Log.e("Success!", ""+json.getString("msg"));
									
									AlertDialog.Builder thumbDialog = new AlertDialog.Builder(
											RegistrationActivity.this);
									thumbDialog.setTitle("ProNoor");
									thumbDialog
											.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															Intent i=new Intent(RegistrationActivity.this,LoginActivity.class);
															startActivity(i);
															finish();
														}
													});
									thumbDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											Reset();
										}
										
									});
									TextView thumbView = new TextView(RegistrationActivity.this);
									thumbView.setText("Registered successfully!!!!!Please check your account by log in");
									thumbDialog.setView(thumbView);
									AlertDialog alert = thumbDialog.create();
									alert.show();
									
									
									
								} else if (success == 0) {
									Toast.makeText(RegistrationActivity.this, "some error..", 1).show();
									Log.e("Failure!", ""+json.getString("msg"));
								}

							} 
							catch (JSONException e) {
								e.printStackTrace();
								Log.e("exception:","error..");
							}
						} else {
							Toast.makeText(RegistrationActivity.this, "Sending Data Failed!", 1).show();
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
	
	public void Reset()
	{
		EdtUserName.setText("");
		EdtFName.setText("");
		EdtLName.setText("");
		EdtDOB.setText("");
		EdtContact.setText("");
		EdtCountry.setText("");
		EdtState.setText("");
		EdtCity.setText("");
		EdtPassword.setText("");
		EdtConpass.setText("");
	}
	
	
	private void selectImage() {
		final AlertDialog builder = new AlertDialog.Builder(RegistrationActivity.this).create();
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
					filename=imagepath.substring(imagepath.lastIndexOf("/")+1);
					Toast.makeText(getApplicationContext(),file.getAbsolutePath(), Toast.LENGTH_LONG).show();
					
					
					//f.delete();
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
		getMenuInflater().inflate(R.menu.registration, menu);
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
		if(id==R.id.Profile)
		{
			DPFlag="1";
			selectImage();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
