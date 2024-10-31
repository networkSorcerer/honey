package com.kh.HoneySpring.vo;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PostsVO implements Comparable<PostsVO>{
    private int postno;
    private String title;
    private String content;
    private String author;
    private Date date;
    private String category;
    private String userID;

    @Override
    public int compareTo(PostsVO o) {
        return o.getPostno() - getPostno();
    }

    // 글 작성자용 생성자
    public PostsVO(String category, String author, String content, String title, int postno) {
        this.category = category;
        this.author = author;
        this. content = content;
        this.title = title;
        this.postno = postno;
    }

    // 페이지용 생성자
    public PostsVO(int postno, String title, String author, String category, Date date) {
        this.category = category;
        this.author = author;
        this.title = title;
        this.date = date;
        this.postno = postno;
    }
    // 글 보여주기용 생성자
    public PostsVO(int postno, String title, String content, String author, Date date, String category) {
        this.postno = postno;
        this.title = title;
        this.content = content;
        this.author = author;
        this.date = date;
        this.category = category;
    }

    public PostsVO() {
    }


}
