package com.example.pronoormail;

import android.content.Context;
import android.net.ConnectivityManager;

public class GlobalSetting {
	
	public static String IP = "http://osourcetechnologies.com/ict/brinda/pronoormail/";
	
	public static String ProfileViewIP 		= IP+"ProfilePhoto/";
	public static String DownloadAttachIP 	= IP+"Attachment/";
	//public static final String ShowAboutIqUrl= IP + "about_iq_data.php?";
	public static final String InsertRegistrationURL=IP +"Registration.php?";
	public static final String SendMailURL=IP +"SendMail.php?";
	public static final String UserNameAvailabilityChk=IP +"getAllRegistration.php?";
	public static final String GetDataByUserNameURL=IP +"GetDataByUsername.php?";
	public static final String LoginURL=IP +"Login.php?";
	public static final String UpdateReadUnreadURL=IP +"UpdateReadUnread.php?";
	public static final String UpdateStarURL=IP +"UpdateStar.php?";
	public static final String UpdateStarOnURL=IP +"UpdateStarOn.php?";
	public static final String GetItemsByIdURL=IP +"GetItemsById.php?";
	
	public static final String GetInboxURL=IP +"getInbox.php?";
	public static final String DeleteInboxByIdURL=IP +"DeleteInboxById.php?";
	public static final String MovingToDraftFromInboxURL=IP +"MovingToDraftFromInbox.php?";
	public static final String MovingToSpamFromInboxURL=IP +"MovingToSpamFromInbox.php?";
	
	public static final String GetSentBoxURL=IP +"GetSentBox.php?";
	public static final String DeleteSentBoxByIdURL=IP +"DeleteSendBoxById.php?";
	public static final String MovingToDraftFromSentURL=IP +"MovingToDraftFromSent.php?";
	public static final String MovingToSpamFromSentURL=IP +"MovingToSpamFromSent.php?";
	
	
	public static final String GetDraftURL=IP +"GetDraft.php?";
	public static final String DeleteDraftByIdURL=IP +"DeleteDraftById.php?";
	
	public static final String GetSpamURL=IP +"GetSpam.php?";
	public static final String DeleteSpamByIdURL=IP +"DeleteSpam.php?";
	
	public static final String GetTrashURL=IP +"GetTrash.php?";
	public static final String DeleteTrashByIdURL=IP +"DeleteTrash.php?";
	
	public static final String UpdatePersonalURL=IP +"UpdatePersonalDetail.php?";
	public static final String UpdateAccountURL=IP +"UpdateAccountDetail.php?";
	
	//public static final String loginUrl= IP + "login.php?";
	public static final String SendResponseURL=IP +"SendResponses.php?";
	public static final String GEtMessageDetailsURL=IP +"GetMessageDetails.php?";
	
	public static final String upLoadServerUri=IP+ "Attach_files.php";
	public static final String upLoadPhotoUri=IP+ "ProfilePhoto.php";
	public static final String updatePhotoUri=IP+ "UpdateProfile.php";
	
	public static final String GetCountURL=IP+ "GetTotalInbox.php";
	
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null);
	}
}
