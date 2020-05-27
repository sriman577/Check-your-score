package com.example.vmac.WatBot;

public class LocationsData {
    private String location;
    private String userEmail;
    private Double riskFactor;

    public Double getRiskFactor() {
        return riskFactor;
    }

    public void setRiskFactor(Double riskFactor) {
        this.riskFactor = riskFactor;
    }

    public String getLocation() {
        return location;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
