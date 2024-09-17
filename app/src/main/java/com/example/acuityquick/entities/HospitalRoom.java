package com.example.acuityquick.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.acuityquick.database.RoomRepository;

@Entity(tableName = "rooms", indices = {@Index(value = "room_number", unique = true)})
public class HospitalRoom {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "room_number")
    private int roomNumber;

    @ColumnInfo(name = "care_level")
    private String careLevel;

    @ColumnInfo(name = "air_requirement")
    private String airRequirement;

    @ColumnInfo(name = "air_score")
    private int airScore;

    @ColumnInfo(name = "mobility_requirement")
    private String mobilityRequirement;

    @ColumnInfo(name = "mobility_score")
    private int mobilityScore;

    @ColumnInfo(name = "nutrition_requirement")
    private String nutritionRequirement;

    @ColumnInfo(name = "nutrition_score")
    private int nutritionScore;

    @ColumnInfo(name = "total_acuity_score")
    private int totalAcuityScore;

    // Getters and setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public String getCareLevel() { return careLevel; }
    public void setCareLevel(String careLevel) { this.careLevel = careLevel; }

    public String getAirRequirement() { return airRequirement; }
    public void setAirRequirement(String airRequirement) {
        this.airRequirement = airRequirement;
        this.airScore = getAirScoreFromRequirement(airRequirement);
        calculateTotalScore();
    }
    public int getAirScore() { return airScore; }
    public void setAirScore(int airScore) { this.airScore = airScore; }  // Adding setter for Room

    public String getMobilityRequirement() { return mobilityRequirement; }
    public void setMobilityRequirement(String mobilityRequirement) {
        this.mobilityRequirement = mobilityRequirement;
        this.mobilityScore = getMobilityScoreFromRequirement(mobilityRequirement);
        calculateTotalScore();
    }
    public int getMobilityScore() { return mobilityScore; }
    public void setMobilityScore(int mobilityScore) { this.mobilityScore = mobilityScore; }  // Adding setter for Room

    public String getNutritionRequirement() { return nutritionRequirement; }
    public void setNutritionRequirement(String nutritionRequirement) {
        this.nutritionRequirement = nutritionRequirement;
        this.nutritionScore = getNutritionScoreFromRequirement(nutritionRequirement);
        calculateTotalScore();
    }
    public int getNutritionScore() { return nutritionScore; }
    public void setNutritionScore(int nutritionScore) { this.nutritionScore = nutritionScore; }  // Adding setter for Room

    public int getTotalAcuityScore() { return totalAcuityScore; }
    public void setTotalAcuityScore(int totalAcuityScore) { this.totalAcuityScore = totalAcuityScore; }  // Adding setter for Room

    // Helper methods to calculate scores based on requirements
    public int getAirScoreFromRequirement(String requirement) {
        switch (requirement) {
            case "Room Air": return 1;
            case "Low Flow Oxygen": return 2;
            case "High Flow Oxygen": return 3;
            default: return 0;
        }
    }

    public int getMobilityScoreFromRequirement(String requirement) {
        switch (requirement) {
            case "Independent": return 1;
            case "Assisted": return 2;
            case "Full Assistance": return 3;
            default: return 0;
        }
    }

    public int getNutritionScoreFromRequirement(String requirement) {
        switch (requirement) {
            case "Normal Diet": return 1;
            case "Modified Diet": return 2;
            case "Special Diet": return 3;
            default: return 0;
        }
    }

    // Recalculate the total acuity score
    public void calculateTotalScore() {
        this.totalAcuityScore = 0;
        this.totalAcuityScore += airScore;
        this.totalAcuityScore += mobilityScore;
        this.totalAcuityScore += nutritionScore;
    }

    private int getScoreForAttribute(String attribute) {
        // You can make this as simple or complex as you need
        return attribute != null ? 1 : 0;  // Simple example: 1 point for each non-null attribute
    }

    @Override
    public String toString() {
        return "HospitalRoom{" +
                "careLevel='" + careLevel + '\'' +
                ", airRequirement='" + airRequirement + '\'' +
                ", mobilityRequirement='" + mobilityRequirement + '\'' +
                ", nutritionRequirement='" + nutritionRequirement + '\'' +
                ", totalAcuityScore=" + totalAcuityScore +
                '}';
    }

    public boolean saveDetails(RoomRepository repository) {
        try {
            repository.insert(this); // Attempt to insert the current object
            return true; // Return true if the insert operation is successful
        } catch (RuntimeException e) {
            System.out.println("Failed to save room details: " + e.getMessage()); // Log the error message
            return false; // Return false if an exception occurs
        }
    }
}
