package com.example.revivo.data.local.model;

public class DailyTarget {
    private long id;
    private long userId;
    private String date;
    private int targetSteps;
    private int targetExercise;
    private int targetWaterMl;
    private float targetSleepHr;

    public DailyTarget() {}

    public DailyTarget(long id, long userId, String date, int targetSteps,
                       int targetExercise, int targetWaterMl, float targetSleepHr) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.targetSteps = targetSteps;
        this.targetExercise = targetExercise;
        this.targetWaterMl = targetWaterMl;
        this.targetSleepHr = targetSleepHr;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getTargetSteps() { return targetSteps; }
    public void setTargetSteps(int targetSteps) { this.targetSteps = targetSteps; }

    public int getTargetExercise() { return targetExercise; }
    public void setTargetExercise(int targetExercise) { this.targetExercise = targetExercise; }

    public int getTargetWaterMl() { return targetWaterMl; }
    public void setTargetWaterMl(int targetWaterMl) { this.targetWaterMl = targetWaterMl; }

    public float getTargetSleepHr() { return targetSleepHr; }
    public void setTargetSleepHr(float targetSleepHr) { this.targetSleepHr = targetSleepHr; }
}