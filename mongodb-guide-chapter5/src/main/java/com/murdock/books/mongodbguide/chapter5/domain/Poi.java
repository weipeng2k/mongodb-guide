package com.murdock.books.mongodbguide.chapter5.domain;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author weipeng2k 2020年03月15日 下午20:08:04
 */
public class Poi implements Serializable {
    private static final long serialVersionUID = -6595624172489524435L;
    /**
     * ID
     */
    private Long num;
    /**
     * 名称
     */
    private String name;
    /**
     * 城市
     */
    private String cityName;
    /**
     * 地区
     */
    private String areaName;
    /**
     * 街道
     */
    private String streetName;
    /**
     * 位置
     */
    private Double[] location;
    /**
     * 类目名
     */
    private String categoryName;
    /**
     * 属性名
     */
    private String propertyName;
    /**
     * 联系方式
     */
    private String[] contactNumbers;

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public Double[] getLocation() {
        return location;
    }

    public void setLocation(Double[] location) {
        this.location = location;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String[] getContactNumbers() {
        return contactNumbers;
    }

    public void setContactNumbers(String[] contactNumbers) {
        this.contactNumbers = contactNumbers;
    }

    @Override
    public String toString() {
        return "Poi{" +
                "num=" + num +
                ", name='" + name + '\'' +
                ", cityName='" + cityName + '\'' +
                ", areaName='" + areaName + '\'' +
                ", streetName='" + streetName + '\'' +
                ", location=" + Arrays.toString(location) +
                ", categoryName='" + categoryName + '\'' +
                ", propertyName='" + propertyName + '\'' +
                ", contactNumbers=" + Arrays.toString(contactNumbers) +
                '}';
    }
}
