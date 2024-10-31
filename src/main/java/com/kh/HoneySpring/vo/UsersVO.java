package com.kh.HoneySpring.vo;

import lombok.*;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class UsersVO {
    private String userID;
    private String userPW;
    private String confirmPW;
    private String nName;
    private String phone;
    private Date updateDATE;
    private String pwLOCK;
    private String pwKey;

    public UsersVO(String userID, String userPW, String nName, String phone,Date date, String pwLOCK, String pwKey) {
        this.userID = userID;
        this.userPW = userPW;
        this.nName = nName;
        this.phone = phone;
        this.updateDATE = date;
        this.pwLOCK = pwLOCK;
        this.pwKey = pwKey;
    }
}
