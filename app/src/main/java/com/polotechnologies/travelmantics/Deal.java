package com.polotechnologies.travelmantics;

import java.io.Serializable;

public class Deal implements Serializable {
    private String dealId;
    private String dealName;
    private String dealDescription;
    private String dealPrice;
    private String dealImageUrl;
    private String dealPictureName;

    public Deal() {
    }

    public Deal(String dealId, String dealName, String dealDescription, String dealPrice, String dealImageUrl, String dealPictureName) {
        this.dealId = dealId;
        this.dealName = dealName;
        this.dealDescription = dealDescription;
        this.dealPrice = dealPrice;
        this.dealImageUrl = dealImageUrl;
        this.dealPictureName = dealPictureName;
    }

    public String getDealPictureName() {
        return dealPictureName;
    }

    public void setDealPictureName(String dealPictureName) {
        this.dealPictureName = dealPictureName;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getDealDescription() {
        return dealDescription;
    }

    public void setDealDescription(String dealDescription) {
        this.dealDescription = dealDescription;
    }

    public String getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(String dealPrice) {
        this.dealPrice = dealPrice;
    }

    public String getDealImageUrl() {
        return dealImageUrl;
    }

    public void setDealImageUrl(String dealImageUrl) {
        this.dealImageUrl = dealImageUrl;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }
}
