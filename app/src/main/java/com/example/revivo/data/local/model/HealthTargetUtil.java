package com.example.revivo.data.local.model;

import java.time.LocalDate;
import java.time.Period;

public class HealthTargetUtil {

    public static int calculateAge(String birthDate) {
        LocalDate birth = LocalDate.parse(birthDate);
        return Period.between(birth, LocalDate.now()).getYears();
    }

    public static int calculateStepTarget(String activityLevel) {
        switch (activityLevel.toLowerCase()) {
            case "berat":
            case "very active":
                return 10000;
            case "sedang":
            case "moderately active":
                return 8000;
            default:
                return 6000;
        }
    }

    public static int calculateWaterTarget(float weightKg) {
        return Math.round(weightKg * 35); // 30–40 ml per kg, pakai rata-rata 35 ml
    }

    public static float calculateSleepTarget(int age) {
        // Rekomendasi umum untuk dewasa
        return 8.0f; // dalam jam
    }

    public static int calculateExerciseDuration(String activityLevel) {
        switch (activityLevel.toLowerCase()) {
            case "berat":
                return 45;
            case "sedang":
                return 30;
            default:
                return 15;
        }
    }
}
