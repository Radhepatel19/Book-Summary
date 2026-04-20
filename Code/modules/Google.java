package com.example.booksummary.modules;

public class Google {
    String ProfilePic,Username,Mail;

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

    public void Users1(String mail, String profilePic, String username) {
        ProfilePic = profilePic;
        Username = username;
        Mail = mail;
    }
}
