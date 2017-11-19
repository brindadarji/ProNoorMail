package com.example.pronoormail.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pronoormail.Product;
import com.example.pronoormail.R;
import com.example.pronoormail.Users;

public class UserAdapter extends BaseAdapter {
	 	Context ctx;
	    LayoutInflater lInflater;
	    ArrayList<Users> objects;
	   
	    public UserAdapter(Context context, ArrayList<Users> products) {
	        ctx = context;
	        objects = products;
	        lInflater = (LayoutInflater) ctx
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }
		@Override
		public int getCount() {
			return objects.size();
		}
		@Override
		public Object getItem(int position) {
			return objects.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
	        if (view == null) {
	            view = lInflater.inflate(R.layout.users, parent, false);
	        }
	        

	        Users u = getUsers(position);
	        
	        ((TextView) view.findViewById(R.id.tvName)).setText(u.name);
	        ((TextView) view.findViewById(R.id.tvUserName)).setText(u.username + "");
	       
	        
	        
	        this.notifyDataSetChanged();
	        return view;
		}
		
		Users getUsers(int position) {
		        return ((Users) getItem(position));
		    }
		
		public String getInfo(int pos)
	    {
	    	Users pr=getUsers(pos);
	    	String name=pr.username;
			return name;	
	    }

}
