package com.example.jobboard.models;

import com.google.gson.annotations.SerializedName;

public class JobOffer {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("company")
    private String company;

    @SerializedName("location")
    private String location;

    @SerializedName("description")
    private String description;

    public JobOffer() {
    }

    public JobOffer(int id, String title, String company, String location, String description) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "JobOffer{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
