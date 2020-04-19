package com.example.moviezpoint.Models;

public class StoreFirebaseUser {
    String userName, userEmail, userImage, userId, userMobile, userDate, userState, userCity, userAddress;

    public StoreFirebaseUser(String userName, String userEmail, String userImage, String userId, String userMobile, String userDate, String userState, String userCity, String userAddress) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userImage = userImage;
        this.userId = userId;
        this.userMobile = userMobile;
        this.userDate = userDate;
        this.userState = userState;
        this.userCity = userCity;
        this.userAddress = userAddress;
    }

    public StoreFirebaseUser() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserDate() {
        return userDate;
    }

    public void setUserDate(String userDate) {
        this.userDate = userDate;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }
}
