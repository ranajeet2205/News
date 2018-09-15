package com.example.android.news;


public class News {
    private  String mTitle;
    private  String mSectionName;
    private  String mAuthorName;
    private  String mPublicationDate;
    private  String mURL;

    public News(String mTitle, String mSectionName,String mAuthorName, String mPublicationDate, String mURL) {
        this.mTitle = mTitle;
        this.mSectionName = mSectionName;
        this.mAuthorName = mAuthorName;
        this.mPublicationDate = mPublicationDate;
        this.mURL = mURL;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmSectionName() {
        return mSectionName;
    }

    public String getmAuthorName() {
        return mAuthorName;
    }

    public String getmPublicationDate() {

        return mPublicationDate;
    }

    public String getmURL() {
        return mURL;
    }


}
