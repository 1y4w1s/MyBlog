package com.my.blog.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddRoleDto {
    private String roleName;
    private String roleKey;
    private Integer roleSort;
    private Integer status;
    private List<Long> menuIds;
    private String remark;
}
