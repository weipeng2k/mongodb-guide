package com.murdock.books.mongodbguide.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author weipeng2k 2019年03月09日 上午10:58:20
 */
@Setter
@Getter
@ToString
public class Wrapper implements Serializable {

    private static final long serialVersionUID = 301086112570313623L;
    List<Author> authors;
}
