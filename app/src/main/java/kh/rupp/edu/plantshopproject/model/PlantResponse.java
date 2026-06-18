package kh.rupp.edu.plantshopproject.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlantResponse {

    @SerializedName("data")
    private List<Plant> data;

    @SerializedName("to")
    private int to;

    @SerializedName("total")
    private int total;

    public List<Plant> getData() { return data; }
    public int getTotal()        { return total; }
}