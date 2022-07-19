package com.example.energyapp.classes;


public class ItemTips {
    private String itemTitle;
    private String itemDetails;
    private int itemImageResourceId;
    private int reviewStar;

    private String[] itemHighlights;
    private String itemOverview;
    private String itemProvider;
    private float itemPrice;

    public ItemTips(String itemTitle, String itemDetailEconomy, int reviewStar) {
        this.itemTitle = itemTitle;
        this.itemDetails = itemDetailEconomy;
        this.reviewStar = reviewStar;
    }

    public ItemTips(String itemTitle, String itemDetailEconomy, int itemImageResourceId, int reviewStar) {
        this.itemTitle = itemTitle;
        this.itemDetails = itemDetailEconomy;
        this.itemImageResourceId = itemImageResourceId;
        this.reviewStar = reviewStar;
    }

    public ItemTips(String itemTitle, String itemDetailEconomy, int itemImageResourceId, int reviewStar, String[] itemHighlights, String itemOverview, String itemProvider, float itemPrice) {
        this.itemTitle = itemTitle;
        this.itemDetails = itemDetailEconomy;
        this.itemImageResourceId = itemImageResourceId;
        this.reviewStar = reviewStar;
        this.itemHighlights = itemHighlights;
        this.itemOverview = itemOverview;
        this.itemProvider = itemProvider;
        this.itemPrice = itemPrice;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemDetailEconomy() {
        return itemDetails;
    }

    public int getItemImageResourceId() {
        return itemImageResourceId;
    }

    public int getReviewStar() {
        return reviewStar;
    }

    public String[] getItemHighlights() {
        return itemHighlights;
    }

    public String getItemOverview() {
        return itemOverview;
    }

    public String getItemProvider() {
        return itemProvider;
    }

    public float getItemPrice() {
        return itemPrice;
    }
}
