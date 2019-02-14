package com.murdock.books.mongodbguide.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author weipeng2k 2019年02月13日 下午20:21:45
 */
@Getter
@Setter
@ToString
public class People {
    /**
     * PK
     */
    private String id;
    /**
     * name
     */
    private String name;
    /**
     * 年龄
     */
    private int age;
    /**
     * 职业
     */
    private String job;
}
