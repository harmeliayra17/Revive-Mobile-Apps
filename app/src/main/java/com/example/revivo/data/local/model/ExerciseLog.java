package com.example.revivo.data.local.model;


public class ExerciseLog {
    private long id;
    private long userId;
    private String exerciseId;
    private String exerciseName;
    private int durationMin;
    private String completedAt;

    public ExerciseLog() {}

    public ExerciseLog(long id, long userId, String exerciseId,
                       String exerciseName, int durationMin, String completedAt) {
        this.id = id;
        this.userId = userId;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.durationMin = durationMin;
        this.completedAt = completedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getExerciseId() { return exerciseId; }
    public void setExerciseId(String exerciseId) { this.exerciseId = exerciseId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
}

