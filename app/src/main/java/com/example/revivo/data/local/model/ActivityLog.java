package com.example.revivo.data.local.model;

public class ActivityLog {
    private long id;
    private long userId;
    private String date;
    private int steps;
    private int exerciseMinutes;
    private int waterMl;
    private float sleepHours;
    private String updatedAt;

    public ActivityLog() {}

    public ActivityLog(long id, long userId, String date, int steps,
                       int exerciseMinutes, int waterMl, float sleepHours, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.steps = steps;
        this.exerciseMinutes = exerciseMinutes;
        this.waterMl = waterMl;
        this.sleepHours = sleepHours;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }

    public int getExerciseMinutes() { return exerciseMinutes; }
    public void setExerciseMinutes(int exerciseMinutes) { this.exerciseMinutes = exerciseMinutes; }

    public int getWaterMl() { return waterMl; }
    public void setWaterMl(int waterMl) { this.waterMl = waterMl; }

    public float getSleepHours() { return sleepHours; }
    public void setSleepHours(float sleepHours) { this.sleepHours = sleepHours; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}