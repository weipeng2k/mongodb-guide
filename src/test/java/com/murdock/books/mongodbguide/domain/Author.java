package com.murdock.books.mongodbguide.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author weipeng2k 2019年02月14日 上午11:34:32
 */
@Setter
@Getter
@ToString
public class Author {
    /**
     * 名称
     */
    private String name;
    /**
     * 年龄
     */
    private int age;
}
