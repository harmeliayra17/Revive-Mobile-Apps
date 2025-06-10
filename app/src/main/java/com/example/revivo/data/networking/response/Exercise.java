package com.example.revivo.data.networking.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Exercise {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("force")
    private String force;

    @SerializedName("level")
    private String level;

    @SerializedName("mechanic")
    private String mechanic;

    @SerializedName("equipment")
    private String equipment;

    @SerializedName("primaryMuscles")
    private List<String> primaryMuscles;

    @SerializedName("secondaryMuscles")
    private List<String> secondaryMuscles;

    @SerializedName("instructions")
    private List<String> instructions;

    @SerializedName("category")
    private String category;

    @SerializedName("images")
    private List<String> images;

    // Getters & Setters (atau gunakan Lombok jika suka)
    public String getId() { return id; }
    public String getName() { return name; }
    public String getForce() { return force; }
    public String getLevel() { return level; }
    public String getMechanic() { return mechanic; }
    public String getEquipment() { return equipment; }
    public List<String> getPrimaryMuscles() { return primaryMuscles; }
    public List<String> getSecondaryMuscles() { return secondaryMuscles; }
    public List<String> getInstructions() { return instructions; }
    public String getCategory() { return category; }
    public List<String> getImages() { return images; }

    // Optional: equals() & hashCode() jika pakai DiffUtil
}
