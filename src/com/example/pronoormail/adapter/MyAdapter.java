/**
 * 
 */
package com.example.pronoormail.adapter;

import java.util.ArrayList;

import android.R.integer;
import android.R.string;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.pronoormail.R;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Brinda
 *
 */
public class MyAdapter extends BaseAdapter {

	Context context;
	ArrayList<String> text1;
	ArrayList<integer> chk;
	int []imgs;
	LayoutInflater inflate;
	
	static class ViewData{
		TextView text;
		ImageView imag;
	}
	
	public MyAdapter(Context applicationContext,ArrayList<String> str,int[] img) {
		this.context=applicationContext;
		this.text1=str;
		this.imgs=img;
		inflate=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return text1.size();
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
	public View getView(int position, View convertview, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewData views;
		if(convertview==null){
			convertview=inflate.inflate(R.layout.simple_list_item, null);
			views=new ViewData();
			views.text=(TextView) convertview.findViewById(R.id.textView1);
			views.imag=(ImageView) convertview.findViewById(R.id.imageView1);
			
			convertview.setTag(views);
		}
		else{
			views=(ViewData) convertview.getTag();
		}
		views.text.setText(text1.get(position));
		views.imag.setImageResource(imgs[position]);
		return convertview;
	}

}
