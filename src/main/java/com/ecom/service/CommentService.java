package com.ecom.service;

import org.springframework.data.domain.Page;
import com.ecom.model.Comment;

public interface CommentService {

    Comment saveComment(Comment comment);

    Page<Comment> getCommentsByProduct(Integer productId, Integer pageNo, Integer pageSize);

    Long getCommentCount(Integer productId);

    Boolean deleteComment(Integer id, Integer userId);
}