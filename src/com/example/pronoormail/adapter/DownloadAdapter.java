package com.example.pronoormail.adapter;

import com.example.pronoormail.R;
import com.example.pronoormail.adapter.PathAdapter.ViewData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DownloadAdapter extends BaseAdapter{

	Context context;
	String [] attachment_file_name;
	
	LayoutInflater inflate;
	
	static class ViewData
	{
		TextView txt_attachment_file_name;
		ImageView imgv_remove_selected_file_name;
	}
	
	public DownloadAdapter(Context applicationContext, String[] attachment_file_name)
	{
		this.context = applicationContext;
		this.attachment_file_name = attachment_file_name;
		inflate=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return attachment_file_name.length;
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(final int position, View convertview, final ViewGroup parent) 
	{
		// TODO Auto-generated method stub
		ViewData views;
		if(convertview == null)
		{
			convertview=inflate.inflate(R.layout.download_items, null);
			views = new ViewData();
			views.txt_attachment_file_name = (TextView) convertview.findViewById(R.id.txt_attachment_file_name);
			views.imgv_remove_selected_file_name= (ImageView) convertview.findViewById(R.id.imgv_remove_selected_file_name);
			convertview.setTag(views);
		}
		else
		{
			views=(ViewData) convertview.getTag();
		}
		
		views.imgv_remove_selected_file_name.setOnClickListener(new OnClickListener() 
		{
		    @Override
		    public void onClick(View v) 
		    {
		        ((ListView) parent).performItemClick(v, position, 0);
		    }
		});
		
		views.txt_attachment_file_name.setText(attachment_file_name[position]);
		return convertview;
	}
}
