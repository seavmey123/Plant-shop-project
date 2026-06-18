package kh.rupp.edu.plantshopproject.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Plant {

    @SerializedName("id")
    private int id;

    @SerializedName("common_name")
    private String commonName;

    @SerializedName("scientific_name")
    private List<String> scientificName;

    @SerializedName("cycle")
    private String cycle;

    @SerializedName("watering")
    private String watering;

    @SerializedName("sunlight")
    private List<String> sunlight;

    @SerializedName("default_image")
    private DefaultImage defaultImage;

    // Manual fields for fake/offline data fallback tracking
    private int manualId = -1; // Initialize to -1 so it doesn't conflict with database row ID 0
    private String manualCommonName;
    private String manualCycle;
    private String manualWatering;
    private String manualImageUrl;

    // ── SETTERS (for fake data) ──
    public void setId(int id)                { this.manualId = id; }
    public void setCommonName(String name)   { this.manualCommonName = name; }
    public void setCycle(String cycle)       { this.manualCycle = cycle; }
    public void setWatering(String watering) { this.manualWatering = watering; }
    public void setImageUrl(String url)      { this.manualImageUrl = url; }

    // ── GETTERS (checks manual value first, then API value) ──
    public int getId() {
        return manualId != -1 ? manualId : id;
    }

    public String getCommonName() {
        return manualCommonName != null ? manualCommonName : commonName;
    }

    public String getCycle() {
        return manualCycle != null ? manualCycle : cycle;
    }

    public String getWatering() {
        return manualWatering != null ? manualWatering : watering;
    }

    public String getImageUrl() {
        if (manualImageUrl != null) return manualImageUrl;
        return defaultImage != null ? defaultImage.getMediumUrl() : null;
    }

    public String getScientificName() {
        return scientificName != null && !scientificName.isEmpty()
                ? scientificName.get(0) : "";
    }

    public String getSunlight() {
        return sunlight != null && !sunlight.isEmpty()
                ? sunlight.get(0) : "";
    }

    public DefaultImage getDefaultImage() {
        return defaultImage;
    }

    public static class DefaultImage {
        @SerializedName("medium_url")
        public String mediumUrl;

        // ── FIXED METHOD: Added the missing return statement ──
        public String getMediumUrl() {
            return mediumUrl;
        }
    }
}