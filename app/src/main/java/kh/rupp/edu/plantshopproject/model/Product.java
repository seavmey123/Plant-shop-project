package kh.rupp.edu.plantshopproject.model;

import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("price")
    private double price;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("image")
    private String image;

    @SerializedName("rating")
    private Rating rating;

    public int getId()             { return id; }
    public String getTitle()       { return title; }
    public double getPrice()       { return price; }
    public String getDescription() { return description; }
    public String getCategory()    { return category; }
    public String getImage()       { return image; }
    public Rating getRating()      { return rating; }

    public static class Rating {
        @SerializedName("rate")  public double rate;
        @SerializedName("count") public int count;
    }
}
