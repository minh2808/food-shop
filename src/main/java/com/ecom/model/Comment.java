package com.ecom.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserDtls user;

    @Column(columnDefinition="TEXT")
    private String content;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
    public UserDtls getUser(){
        return this.user;
    }
    public void setProduct(Product product){
        this.product = product;
    }
    public void setUser(UserDtls user){
        this.user = user;
    }
    public void setContent(String content){
        this.content = content;
    }
}
