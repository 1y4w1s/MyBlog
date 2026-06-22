package com.my.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.blog.domain.entity.Comment;
import com.my.blog.domain.ResponseResult;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author WH
 * @since 2025-05-19
 */
public interface ICommentService extends IService<Comment> {
    ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize);
    ResponseResult addComment(Comment comment);
}
