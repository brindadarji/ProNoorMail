package com.example.pronoormail;

public class ItemDetails {
	public String from_name;
	public String to_name;
    public String subject;
    public String message;
    public String date;
    public String attach;
    public String id;
    
    public ItemDetails(String _from,String _to, String _subject,String _message,String _date,String _attach,String _id) {
    	from_name=_from;
    	to_name=_to;
    	subject=_subject;
    	message=_message;
    	date=_date;
    	attach=_attach;
    	id=_id;
    }
}
