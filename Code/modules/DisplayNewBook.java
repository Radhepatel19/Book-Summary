package com.example.booksummary.modules;

public class DisplayNewBook {
    String BName;
    String BPicUrl;
    String BDate;
    String BAuthor;
    String Bid;

    String Audio;
    public DisplayNewBook(){

    }

    public String getBName() {
        return BName;
    }

    public void setBName(String BName) {
        this.BName = BName;
    }

    public String getBPicUrl1() {
        return BPicUrl;
    }

    public void setBPicUrl1(String BPicUrl) {
        this.BPicUrl = BPicUrl;
    }


    public String getBDate() {
        return BDate;
    }

    public void setBDate(String BDate) {
        this.BDate = BDate;
    }

    public String getBAuthor() {
        return BAuthor;
    }

    public void setBAuthor(String BAuthor) {
        this.BAuthor = BAuthor;
    }


    public String getAudio() {
        return Audio;
    }

    public void setAudio(String audio) {
        Audio = audio;
    }

    public DisplayNewBook(String BName, String BPicUrl, String BDate, String BAuthor, String Audio) {
        this.BName = BName;
        this.BPicUrl = BPicUrl;
        this.BDate = BDate;
        this.BAuthor = BAuthor;
        this.Audio = Audio;
    }

    public String getBid() {
        return Bid;
    }
    public void setBid(String bid) {
        Bid = bid;
    }

    public DisplayNewBook(String BName, String BPicUrl, String BDate, String BAuthor, String Bid,String Audio) {
        this.Bid = Bid;
        this.BName = BName;
        this.BPicUrl = BPicUrl;
        this.BDate = BDate;
        this.BAuthor = BAuthor;
        this.Audio = Audio;
    }
}
