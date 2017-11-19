package com.example.pronoormail.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.jar.Attributes.Name;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.pronoormail.Board_Controller;
import com.example.pronoormail.Global;
import com.example.pronoormail.GlobalSetting;
import com.example.pronoormail.Product;
import com.example.pronoormail.R;
import com.example.pronoormail.RegistrationActivity;
import com.example.pronoormail.ViewInBoxDetailFragment;

import android.R.animator;
import android.R.integer;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListAdapter extends BaseAdapter implements Filterable {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Product> objects;
    ArrayList<String> Starids=new ArrayList<String>();
    int p;
    JSONArray MailDetail = null,ItemDetail=null;
	int Read_Unread,id,star,MyId,Flag;
    String response="",response_user_availability="";
    ImageView img,imgStar;
    
    
   // private ArrayList<Product> mOriginalValues; // Original Values
    //private ArrayList<Product> mDisplayedValues;    // Values to be displayed
    
    public ListAdapter(Context context, ArrayList<Product> products) {
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
            view = lInflater.inflate(R.layout.item, parent, false);
        }
        

        Product p = getProduct(position);
        
        ((TextView) view.findViewById(R.id.tvDate)).setText(p.date + "");
        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.name);
        ((TextView) view.findViewById(R.id.tvPrice)).setText(p.subject + "");
       
        img=((ImageView) view.findViewById(R.id.imageView1));
        img.setImageResource(p.image);
        
        imgStar=(ImageView) view.findViewById(R.id.imageStar);
        imgStar.setImageResource(p.starimg);
        imgStar.setOnClickListener(MyClick);
        imgStar.setTag(position);

        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
        cbBuy.setOnCheckedChangeListener(myCheckChangList);
        cbBuy.setTag(position);
        cbBuy.setChecked(p.box);
        this.notifyDataSetChanged();
        return view;
    }

    Product getProduct(int position) {
        return ((Product) getItem(position));
    }

    public ArrayList<Product> getBox() {
        ArrayList<Product> box = new ArrayList<Product>();
        for (Product p : objects) {
            if (p.box)
                box.add(p);
        }
        return box;
    }
    
    public String getName(int pos)
    {
    	Product pr=getProduct(pos);
    	String name=pr.name;
		return name;	
    }
    
    public int getId(int pos)
    {
    	Product pr=getProduct(pos);
    	int id=pr.id;
		return id;
    }
    

    OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            getProduct((Integer) buttonView.getTag()).box = isChecked;
        }
    };
    
    OnClickListener MyClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int pos=(Integer)v.getTag();
			Product pr=getProduct(pos);
	    	MyId=pr.id;
	    	int myImage=pr.starimg;
	    	if(myImage==android.R.drawable.star_big_off)
	    	{
	    		((ImageView) v).setImageResource(android.R.drawable.star_big_on);
	    	}
	    	else if(myImage==android.R.drawable.star_big_on)
	    	{
	    		((ImageView) v).setImageResource(android.R.drawable.star_big_off);
	    	}
	    	
	    	Log.e("Hiiii",""+MyId);
	    	getDataById(""+MyId);
	    	Log.e("Statofid",""+star);
	    	
	    	
		}
	}; 

	public void getDataById(final String id)
	{
		if(GlobalSetting.isNetworkConnected(ctx)){
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					
				}
				

				@Override
				protected Void doInBackground(Void... arg0) {
					// TODO Auto-generated method stub
					Map<String, String> param = new HashMap<String, String>();
					param.put("id",id);
					
					try {
						response_user_availability=Board_Controller.post(GlobalSetting.GetItemsByIdURL,param);
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
								ItemDetail=json.getJSONArray("data");
								for(int i=0;i<ItemDetail.length();i++)
								{
									JSONObject s=ItemDetail.getJSONObject(i);
									star=s.getInt("star");
									if(star==1)
									{
										
										UpdateStarOff(""+MyId);
									}
									else
									{
										
										UpdateStarOn(""+MyId);
									}
									
								}
								
							}
							else if(success == 0){
								
								//Toast.makeText(getApplicationContext(),"NO data !",Toast.LENGTH_SHORT).show();
								//dialog.dismiss();
							}
							else{
								Toast.makeText(ctx,"Not Inserted !",Toast.LENGTH_SHORT).show();
								
							}
							
						}catch(JSONException e){
							Log.e("Error1","Error..."+e.toString());
							e.printStackTrace();
							
						}
					}else{
						Toast.makeText(ctx,"data not get",Toast.LENGTH_SHORT).show();
						
					}
					
				}
				
			}.execute();
		}else{
			Toast.makeText(ctx,"Network not available !",Toast.LENGTH_SHORT).show();
		}
			
	}
	public void UpdateStarOff(final String pos)
	{
		if(GlobalSetting.isNetworkConnected(ctx)){
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
						response= Board_Controller.post(GlobalSetting.UpdateStarURL, param);
						
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
								Toast.makeText(ctx, "Updated Successfully ", 1).show();
								//Log.e("Success!", ""+json.getString("msg"));
								
							} else if (success == 0) {
								Toast.makeText(ctx, "some error..", 1).show();
								Log.e("Failure!", ""+json.getString("msg"));
							}

						} 
						catch (JSONException e) {
							e.printStackTrace();
							Log.e("exception:","error..");
						}
					} else {
						Toast.makeText(ctx, "Sending Data Failed!", 1).show();
						Log.e("Error", "Sending Data Failed!");
					}
					
				}

			}.execute();
			

		}else{
			Log.e("Warning!!!","Unable to Connect Internet.");
			
		}
	}
	
	public void UpdateStarOn(final String pos)
	{
		if(GlobalSetting.isNetworkConnected(ctx)){
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
						response= Board_Controller.post(GlobalSetting.UpdateStarOnURL, param);
						
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
								
								Toast.makeText(ctx, "Updated Successfully ", 1).show();
								//Log.e("Success!", ""+json.getString("msg"));
								
							} else if (success == 0) {
								Toast.makeText(ctx, "some error..", 1).show();
								Log.e("Failure!", ""+json.getString("msg"));
							}

						} 
						catch (JSONException e) {
							e.printStackTrace();
							Log.e("exception:","error..");
						}
					} else {
						Toast.makeText(ctx, "Sending Data Failed!", 1).show();
						Log.e("Error", "Sending Data Failed!");
					}
					
				}

			}.execute();
		}else{
			Log.e("Warning!!!","Unable to Connect Internet.");
			
		}
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

	        @SuppressWarnings("unchecked")
	        @Override
	        protected void publishResults(CharSequence constraint,FilterResults results) {
	        	notifyDataSetChanged(); 
	            objects = (ArrayList<Product>) results.values; // has the filtered values
	             // notifies the data with new filtered values
	        }

	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	            FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
	            ArrayList<Product> FilteredArrList = new ArrayList<Product>();

	            if (objects == null) {
	                objects = new ArrayList<Product>(objects); // saves the original data in mOriginalValues
	            }

	            /********
	             * 
	             *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
	             *  else does the Filtering and returns FilteredArrList(Filtered)  
	             *
	             ********/
//	            if (constraint == null || constraint.length() == 0) {
	            if (constraint != null && constraint.length() > 0) {

	                constraint = constraint.toString().toLowerCase();
	                for (int i = 0; i < objects.size(); i++) {
	                    String data = objects.get(i).name;
	                    //if (data.toLowerCase().startsWith(constraint.toString())) {
	                    if (data.toLowerCase().contains(constraint.toString())) {
		                        FilteredArrList.add(new Product(objects.get(i).date,objects.get(i).name,objects.get(i).subject,objects.get(i).box,objects.get(i).image,objects.get(i).starimg,objects.get(i).id));
	                    }
	                }
	                // set the Filtered result to return
	                results.count = FilteredArrList.size();
	                results.values = FilteredArrList;
	            }else{
	                // set the Original result to return  
	                results.count = objects.size();
	                results.values = objects;
	            } 
	            return results;
	        }
	    };
		return filter;
	}

}
