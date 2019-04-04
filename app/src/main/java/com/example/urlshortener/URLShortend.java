package com.example.urlshortener;

public class URLShortend {
    private int Id;
    private String shortIdentifier;
    private String redirectURL;
    private int redirectStatus;
    private String validThru;

    public URLShortend(){
    }

    public URLShortend(String shortIdentifier , String RedirectURL, int RedirectStatus, String validThru){
        this.shortIdentifier = shortIdentifier;
        this.redirectURL = RedirectURL;
        this.redirectStatus = RedirectStatus;
        this.validThru = validThru;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) { this.redirectURL = redirectURL; }

    public int getRedirectStatus() {
        return redirectStatus;
    }

    public void setRedirectStatus(int redirectStatus) {
        this.redirectStatus = redirectStatus;
    }

    public String getShortIdentifier() {
        return shortIdentifier;
    }

    public void setShortIdentifier(String shortIdentifier) {
        this.shortIdentifier = shortIdentifier;
    }

    public String getValidThru() {
        return validThru;
    }

    public void setValidThru(String validThru) {
        this.validThru = validThru;
    }
}
