package com.kh.HoneySpring.vo;

import lombok.*;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentsVO implements Comparable<CommentsVO>{
    private int postNo;
    private String nName;
    private String content;
    private Date cDate;
    private int commNo;
    private int subNo;
    private String userId;

    @Override
    public int compareTo(CommentsVO o) {
        return (getCommNo() != o.getCommNo())? getCommNo()-o.getCommNo():getSubNo()-o.getSubNo();
    }
}
