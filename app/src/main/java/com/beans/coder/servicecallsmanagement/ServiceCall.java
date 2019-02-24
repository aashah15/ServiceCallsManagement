package com.beans.coder.servicecallsmanagement;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class ServiceCall implements Serializable {
    private String id;
    private String callNo;
    private String date;
    private String equipment;
    private String modelNo;
    private String issue;
    private String actionTaken;
    private String result;
    private String remarks;
    private String imageUri;

    ServiceCall(){

    }

    public ServiceCall(String callNo, String date, String equipment, String modelNo, String issue,
                       String actionTaken, String result, String remarks,String imageUri) {
        this.callNo = callNo;
        this.date = date;
        this.equipment = equipment;
        this.modelNo = modelNo;
        this.issue = issue;
        this.actionTaken = actionTaken;
        this.result = result;
        this.remarks = remarks;
        this.imageUri = imageUri;
    }
    @Exclude
    public String getId() {
        return id;
    }
    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getCallNo() {
        return callNo;
    }

    public void setCallNo(String callNo) {
        this.callNo = callNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getModelNo() {
        return modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
