package com.my.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleVo {
    private Long id;
    private String roleName;
    private String roleKey;
    private Integer roleSort;
    private Integer status;
    private String remark;
    private String createBy;
    private String createTime;
    private String delFlag;
    private String updateBy;
    private String updateTime;
}
