package com.example.weighingscale.data.dto;

public class ReportDTO {
    public double averageDuration;
    public double averageSpeed;

    public ReportDTO(double averageDuration, double averageSpeed) {
        this.averageDuration = averageDuration;
        this.averageSpeed = averageSpeed;
    }
}
