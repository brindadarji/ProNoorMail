package com.example.pronoormail.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.pronoormail.Board_Controller;
import com.example.pronoormail.GlobalSetting;
import com.example.pronoormail.ItemDetails;
import com.example.pronoormail.Product;
import com.example.pronoormail.R;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemsDetailAdapter extends BaseAdapter {

	 Context ctx;
	 LayoutInflater lInflater;
     ArrayList<ItemDetails> objects;
     JSONArray MailDetail = null,ItemDetail=null;
     String response="",response_user_availability="";
     
     public ItemsDetailAdapter(Context context, ArrayList<ItemDetails> items) {
         ctx = context;
         objects = items;
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
	
	 public String getId(int pos)
	    {
	    	ItemDetails pr=getItems(pos);
	    	String id=pr.id;
			return id;
	    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_detail, parent, false);
        }
        
       
        ItemDetails p = getItems(position);
        ((TextView) view.findViewById(R.id.tvFrom)).setText(p.from_name + "");
        ((TextView) view.findViewById(R.id.tvEx)).setText("To" + "");
        ((TextView) view.findViewById(R.id.tvTo)).setText(p.to_name + "");
        ((TextView) view.findViewById(R.id.tvDate)).setText(p.date);
        ((TextView) view.findViewById(R.id.tvMessage)).setText(p.message + "");
        ((TextView) view.findViewById(R.id.tvAttach)).setText(p.attach + "");
     
        return view;
	}
	
	 ItemDetails getItems(int position) {
	        return ((ItemDetails) getItem(position));
	    }
}
