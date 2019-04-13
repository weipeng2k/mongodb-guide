package com.murdock.books.mongodbguide.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author weipeng2k 2019年02月17日 下午13:50:49
 */
@Setter
@Getter
@ToString
public class BlogAuthorScore {
    private String name;
    private int score;
}
