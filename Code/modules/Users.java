package com.example.booksummary.modules;


public class Users {

    String ProfilePic,Username,Mail,Password,phoneNumber,AboutUs;

    public Users(){

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public void Users(String username, String mail, String PhoneNumber, String profilePic, String AboutUs) {
        phoneNumber = PhoneNumber;
        Username = username;
        Mail = mail;
        ProfilePic = profilePic;
        this.AboutUs = AboutUs;
    }

    public String getAboutUs() {
        return AboutUs;
    }

    public void setAboutUs(String aboutUs) {
        AboutUs = aboutUs;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }



    public Users(String username, String mail, String password) {
        Username = username;
        Mail = mail;
        Password = password;
    }
}
