package com.ecom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.ecom.model.Comment;
import com.ecom.repository.CommentRepository;
import com.ecom.service.CommentService;

@Service
public class CommentServiceImpl implements  CommentService{
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Boolean deleteComment(Integer id, Integer userId) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment != null && comment.getUser().getId().equals(userId)) {
            commentRepository.delete(comment);
            return true;
        }
        return false;
    }

    @Override
    public Long getCommentCount(Integer productId) {
         return commentRepository.countByProductId(productId);
    }

    @Override
    public Page<Comment> getCommentsByProduct(Integer productId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return commentRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }
 
}
