package com.example.pronoormail;

import com.example.pronoormail.adapter.NavDrawerListAdapter;
import com.example.pronoormail.R;
import com.example.pronoormail.ComposeFragment;
import com.example.pronoormail.FindPeopleFragment;
import com.example.pronoormail.HomeFragment;
import com.example.pronoormail.InboxFragment;
import com.example.pronoormail.ViewInBoxDetailFragment;
import com.example.pronoormail.WhatsHotFragment;
import com.example.pronoormail.UserHomeActivity.SlideMenuClickListener;

import info.androidhive.slidingmenu.model.NavDrawerItem;

import java.util.ArrayList;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class UserHomeActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	// nav drawer title
	private CharSequence mDrawerTitle;
	String user1;
	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_home);
		//getSupportActionBar().hide();
		
		
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		
		// nav drawer icons from resources
				navMenuIcons = getResources()
						.obtainTypedArray(R.array.nav_drawer_icons);

				mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
				mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

				navDrawerItems = new ArrayList<NavDrawerItem>();
				
				// 1. get passed intent 
		        //Intent intent = getIntent();
		 
		        // 2. get message value from intent
		        //String message = intent.getStringExtra("LoginUser");
				
				
				navMenuTitles[0]=Global.getPreferenceString(getApplicationContext(), Global.PREF_User_Name,"");
				
				// adding nav drawer items to array
				
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
				// Home
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
				// Find People
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
				// Photos
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
				// Communities, Will add a counter here
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1), true, "22"));
				// Pages
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
				// What's hot, We  will add a counter here
				// Recycle the typed array
				navMenuIcons.recycle();
				
				// Recycle the typed array
				navMenuIcons.recycle();

				mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

				// setting the nav drawer list adapter
				adapter = new NavDrawerListAdapter(getApplicationContext(),
						navDrawerItems);
				mDrawerList.setAdapter(adapter);
				
				// enabling action bar app icon and behaving it as toggle button
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setHomeButtonEnabled(true);
				
				mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
						R.drawable.ic_drawer, //nav menu toggle icon
						R.string.app_name, // nav drawer open - description for accessibility
						R.string.app_name // nav drawer close - description for accessibility
				) {
					@SuppressLint("NewApi")
					public void onDrawerClosed(View view) {
						getSupportActionBar().setTitle(mTitle);
						// calling onPrepareOptionsMenu() to show action bar icons
						invalidateOptionsMenu();
						
					}

					@SuppressLint("NewApi")
					public void onDrawerOpened(View drawerView) {
						getSupportActionBar().setTitle(mDrawerTitle);
						// calling onPrepareOptionsMenu() to hide action bar icons
						invalidateOptionsMenu();
					}
				};
				
				mDrawerLayout.setDrawerListener(mDrawerToggle);

				if (savedInstanceState == null) {
					// on first time display view for first nav item
					displayView(0);
				}
	}
	
	/**
	 * Slide menu item click listener
	 * */
	public class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	@SuppressLint("NewApi")
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new FindPeopleFragment();
			break;
		case 2:
			fragment = new ComposeFragment();
			break;
		case 3:
			fragment = new InboxFragment();
			break;
		case 4:
			fragment = new InboxFragment();
			break;
		case 5:
			fragment = new WhatsHotFragment();
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// toggle nav drawer on selecting action bar app icon/title
				if (mDrawerToggle.onOptionsItemSelected(item)) {
					return true;
				}
				// Handle action bar actions click
				switch (item.getItemId()) {
				case R.id.action_settings:
					return true;
				default:
					return super.onOptionsItemSelected(item);
				}
	}
	
	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}