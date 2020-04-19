package com.example.moviezpoint.Models;

public class GetNotificationsModel {
    String title, description, status, amountPaid;

    public GetNotificationsModel(String title, String description, String status, String amountPaid) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.amountPaid = amountPaid;
    }

    public GetNotificationsModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }
}
