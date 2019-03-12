package com.murdock.books.mongodbguide.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author weipeng2k 2019年02月14日 上午11:31:55
 */
@Getter
@Setter
@ToString
public class Comment implements Serializable {
    private static final long serialVersionUID = 6849565098251104930L;
    /**
     * 作者
     */
    private String author;
    /**
     * 分数
     */
    private int score;
    /**
     * 评论
     */
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment1 = (Comment) o;
        return score == comment1.score &&
                Objects.equals(author, comment1.author) &&
                Objects.equals(comment, comment1.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, score, comment);
    }
}
