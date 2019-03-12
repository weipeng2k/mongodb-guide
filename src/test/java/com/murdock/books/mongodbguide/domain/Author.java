package com.murdock.books.mongodbguide.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author weipeng2k 2019年02月14日 上午11:34:32
 */
@Setter
@Getter
@ToString
public class Author implements Serializable {
    private static final long serialVersionUID = -4924540361436848559L;
    /**
     * 名称
     */
    private String name;
    /**
     * 年龄
     */
    private int age;
}
