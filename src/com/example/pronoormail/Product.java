package com.example.pronoormail;

public class Product {
    public String name;
      public String subject;
      public String date;
      public boolean box;
      public int image;
      public int starimg;
      public int id;
      
      public Product(String _date,String _describe, String _subject,boolean _box,int _image,int _starimg,int _id) {
    	date = _date;
    	name = _describe;
        subject = _subject;
        box = _box;
        image=_image;
        starimg=_starimg;
        id=_id;
      }
}