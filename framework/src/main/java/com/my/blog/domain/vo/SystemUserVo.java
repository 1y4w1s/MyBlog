package com.my.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemUserVo {
    private Long id;
    private String userName;
    private String nickName;
    private String email;
    private String phonenumber;
    private String sex;
    private String status;
    private String avatar;
    private String createTime;
    private Long createBy;
    private String updateTime;
    private Long updateBy;
}
