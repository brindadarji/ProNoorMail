package com.example.pronoormail;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Global {
	public static final String PREF_User_Name = "pref_user_name";//UserName display on Navigation
	public static final String PREF_User_Email = "pref_user_email";//Email put in From_uname or to_uname
	public static final String PREF_First_Name = "pref_first_name";//FirstName
	public static final String PREF_Last_Name = "pref_last_name";//LastName
	public static final String PREF_Full_Name = "pref_full_name";//FullName write in inbox in from_name
	public static final String PREF_Id = "pref_id";//id of listview item
	public static final String PREF_User_DP = "pref_user_dp";
	public static final String PREF_Login_Chk="true";
	public static void setPreferenceBoolean(Context c, String pref, Boolean val) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putBoolean(pref, val);
		e.commit();
	}

	public static boolean getPreferenceBoolean(Context c, String pref,
			Boolean val) {
		return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(
				pref, val);
	}
	
	public static void setPreferenceString(Context c, String pref, String val) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putString(pref, val);
		e.commit();
	}

	public static String getPreferenceString(Context c, String pref,String val) {
		return PreferenceManager.getDefaultSharedPreferences(c).getString(pref, val);
	}
}
