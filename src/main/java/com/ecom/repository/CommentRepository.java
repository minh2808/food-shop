package com.ecom.repository;

import  org.springframework.data.domain.Page;
import  org.springframework.data.domain.Pageable;
import  org.springframework.data.jpa.repository.JpaRepository;
import  com.ecom.model.Comment;


public interface CommentRepository extends JpaRepository<Comment,Integer>{
    Page<Comment> findByProductIdOrderByCreatedAtDesc(Integer productId, Pageable pageable);
    Long countByProductId(Integer productId);
}