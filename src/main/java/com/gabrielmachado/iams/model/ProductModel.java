package com.gabrielmachado.iams.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity(name = "products")
public class ProductModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String productName;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

}
