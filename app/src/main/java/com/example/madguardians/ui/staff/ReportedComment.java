package com.example.madguardians.ui.staff;

public class ReportedComment {
    private String image;
    private String reportedDescription;
    private String date;
    private String status;

    public ReportedComment(String image, String reportedDescription, String date, String status) {
        this.image = image;
        this.reportedDescription = reportedDescription;
        this.date = date;
        this.status = status;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReportedDescription() {
        return reportedDescription;
    }

    public void setReportedDescription(String reportedDescription) {
        this.reportedDescription = reportedDescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
