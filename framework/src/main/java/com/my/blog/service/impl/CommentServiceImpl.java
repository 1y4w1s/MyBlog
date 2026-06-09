package com.my.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.blog.constant.SystemConstants;
import com.my.blog.dao.CommentMapper;
import com.my.blog.dao.UserMapper;
import com.my.blog.domain.ResponseResult;
import com.my.blog.domain.entity.Comment;
import com.my.blog.domain.vo.CommentVo;
import com.my.blog.domain.vo.PageVo;
import com.my.blog.service.ICommentService;
import com.my.blog.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getType, commentType);
        if (SystemConstants.ARTICLE_COMMENT.equals(commentType)) {
            queryWrapper.eq(Comment::getArticleId, articleId);
        }
        queryWrapper.eq(Comment::getRootId, -1);
        queryWrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        
        List<CommentVo> commentVoList = toCommentVoList(page.getRecords());
        for(CommentVo commentVo : commentVoList){
            List<CommentVo> children = getChildren(commentVo.getId());
            commentVo.setChildren(children);
        }
        
        return ResponseResult.okResult(new PageVo(commentVoList, page.getTotal()));
    }
    
    private List<CommentVo> toCommentVoList(List<Comment> list){
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        for (CommentVo commentVo : commentVos){
            String nickName = userMapper.selectById(commentVo.getCreateBy()).getNickName();
            commentVo.setUsername(nickName);
            
            if(commentVo.getToCommentId() != -1){
                String toCommentUserName = userMapper.selectById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(toCommentUserName);
            }
        }
        return commentVos;
    }
    
    private List<CommentVo> getChildren(Long id){
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, id);
        queryWrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper);
        return toCommentVoList(comments);
    }
    
    @Override 
    public ResponseResult addComment(Comment comment) {
        save(comment);
        return ResponseResult.okResult();
    }
}
