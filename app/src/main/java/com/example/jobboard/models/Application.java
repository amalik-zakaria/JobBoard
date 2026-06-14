package com.example.jobboard.models;

public class Application {
    private int id;
    private int jobId;
    private String jobTitle;
    private String company;
    private String location;
    private String dateApplied;
    private String status;

    public Application(int id, int jobId, String jobTitle, String company,
                       String location, String dateApplied, String status) {
        this.id = id;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.company = company;
        this.location = location;
        this.dateApplied = dateApplied;
        this.status = status;
    }

    public int getId()             { return id; }
    public int getJobId()          { return jobId; }
    public String getJobTitle()    { return jobTitle; }
    public String getCompany()     { return company; }
    public String getLocation()    { return location; }
    public String getDateApplied() { return dateApplied; }
    public String getStatus()      { return status; }

    public void setStatus(String status) { this.status = status; }
}

