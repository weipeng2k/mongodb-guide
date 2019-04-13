package com.murdock.books.mongodbguide.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author weipeng2k 2019年02月16日 下午21:48:17
 */
@Setter
@Getter
@ToString
public class AuthorStats {
    /**
     * 年龄
     */
    private int age;
    /**
     * 数量
     */
    private int ncount;
}
