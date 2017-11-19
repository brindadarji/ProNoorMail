package com.example.pronoormail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import com.example.pronoormail.adapter.DownloadAdapter;
import com.example.pronoormail.adapter.PathAdapter;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadActivity extends ActionBarActivity {
	
	String Attachment;
	TextView txtattach;
	ListView lv_attachment_path;
	
	ProgressBar pb;
    Dialog dialog;
    int downloadedSize = 0;
    int totalSize = 0;
    TextView cur_val;
   String file_display;
    
	public void getIntentData()
	{
		Intent i=getIntent();
		Bundle b=i.getExtras();
		Attachment=b.getString("attachment");
		//Toast.makeText(DownloadActivity.this,""+Attachment,Toast.LENGTH_LONG).show();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
	    actionBar.setTitle("Attachments");
		
		getIntentData();
		lv_attachment_path = (ListView) findViewById(R.id.lv_attachment_path);
		
		final String[] parts = Attachment.split(",");
		
		DownloadAdapter DownLoadAdapter = new DownloadAdapter(getApplicationContext(),parts);
		 
		lv_attachment_path.setAdapter(DownLoadAdapter);
		
		lv_attachment_path.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				long viewId = view.getId();
				
				if (viewId == R.id.imgv_remove_selected_file_name) 
			    {
					Toast.makeText(getApplicationContext(), ""+parts[position],Toast.LENGTH_LONG).show();
					 final String dwnload_file_path = GlobalSetting.DownloadAttachIP+parts[position];
					 showProgress(dwnload_file_path);
					 file_display=parts[position];
                     
	                    new Thread(new Runnable() {
	                        public void run() {
	                             downloadFile(dwnload_file_path);
	                        }
	                      }).start();
			    } 
				
			}
		});
	}
	
	void downloadFile(String path){
        
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
 
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
 
            //connect
            urlConnection.connect();
 
         String paths = Environment.getExternalStorageDirectory().toString();
   		 File myDir=new File(paths + "/ProNoor");
   		 myDir.mkdir();
   		 Random generator = new Random();
   		 int n = 10000;
   		 n = generator.nextInt(n);
   		// String fname = "Image-"+ n +".jpg";
   		 File file = new File (myDir, file_display);
   		 
            //set the path where we want to save the file           
            //File SDCardRoot = Environment.getExternalStorageDirectory() + "/ProNoor"; 
            //create a new file, to save the downloaded file 
            //File file = new File(SDCardRoot,"downloaded_file.png");
  
            FileOutputStream fileOutput = new FileOutputStream(file);
 
            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();
 
            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();
 
            runOnUiThread(new Runnable() {
                public void run() {
                    pb.setMax(totalSize);
                }               
            });
             
            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
 
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
                runOnUiThread(new Runnable() {
                    public void run() {
                        pb.setProgress(downloadedSize);
                        float per = ((float)downloadedSize/totalSize) * 100;
                        cur_val.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)" );
                    }
                });
            }
            //close the output stream when complete //
            fileOutput.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    dialog.dismiss(); // if you want close it..
                }
            });         
         
        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);        
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);          
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }       
    }
	
	void showError(final String err){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(DownloadActivity.this, err, Toast.LENGTH_LONG).show();
            }
        });
    }
	
	void showProgress(String file_path){
        dialog = new Dialog(DownloadActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.myprogressdialog);
        dialog.setTitle("Download Progress");
 
        TextView text = (TextView) dialog.findViewById(R.id.tv1);
        text.setText("Downloading file from ... " + file_path);
        cur_val = (TextView) dialog.findViewById(R.id.cur_pg_tv);
        cur_val.setText("Starting download...");
        dialog.show();
         
        pb = (ProgressBar)dialog.findViewById(R.id.progress_bar);
        pb.setProgress(0);
        pb.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress));  
    }
     

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download, menu);
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
