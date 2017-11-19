package com.example.pronoormail;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class ViewInBoxDetailFragment extends Fragment {
	
	public ViewInBoxDetailFragment(){}
	
	ImageView img;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.activity_view_inbox_detail_fragment, container, false);
         	
        img=(ImageView) rootView.findViewById(R.id.imageView1);
        
        img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity().getApplicationContext(),"Clickkkkk",Toast.LENGTH_LONG).show();
			}
		});
        return rootView;
    }
}
