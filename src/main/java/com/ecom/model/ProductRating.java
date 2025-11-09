package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_ratings")
public class ProductRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
}
