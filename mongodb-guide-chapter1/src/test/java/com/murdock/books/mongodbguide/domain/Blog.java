package com.murdock.books.mongodbguide.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author weipeng2k 2019年02月14日 上午11:31:16
 */
@Getter
@Setter
@ToString
public class Blog {
    /**
     * PK
     */
    private String id;
    /**
     * 作者
     */
    private Author author;
    /**
     * 内容
     */
    private String content;
    /**
     * 评论列表
     */
    private List<Comment> comments;
}
