package com.hakaninc.messengerapp.model;

public class Users {

    private String uid;
    private String username;
    private String profile;
    private String cover;
    private String status;
    private String search;
    private String facebook;
    private String instagram;
    private String website;

    public Users() {
    }

    public Users(String uid, String username, String profile, String cover, String status, String search, String facebook, String instagram, String website) {
        this.uid = uid;
        this.username = username;
        this.profile = profile;
        this.cover = cover;
        this.status = status;
        this.search = search;
        this.facebook = facebook;
        this.instagram = instagram;
        this.website = website;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
