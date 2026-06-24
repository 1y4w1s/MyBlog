package com.my.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemArticleVo {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private Long categoryId;
    private String thumbnail;
    private String isTop;
    private String status;
    private String isComment;
    private Long viewCount;
    private String createTime;
    private Long createBy;
    private String updateTime;
    private Long updateBy;
    private Integer delFlag;
    private Long[] tags;
}
