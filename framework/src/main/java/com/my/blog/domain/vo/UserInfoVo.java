package com.my.blog.domain.vo;

import lombok.Data;

@Data
public class UserInfoVo {
    private Long id;
    private String userName;
    private String nickName;
    private String avatar;
    private String sex;
    private String email;
    private String status;
    private String phonenumber;
}
