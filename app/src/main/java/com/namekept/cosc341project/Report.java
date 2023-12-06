package com.namekept.cosc341project;

public class Report {

    private String id;
    private String title;
    private String description;
    private String coordinates;
    private long timestamp; // Could be used for sorting or display

    public Report() {
        // Default constructor is required for calls to DataSnapshot.getValue(Report.class)
    }

    public Report(String id, String title, String description, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Getters and setters for all properties
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    // You might also want to add other methods or business logic here, depending on your needs
}
