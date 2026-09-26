package com.gabrielmachado.iams.repository;

import com.gabrielmachado.iams.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    Optional<ProductModel> findByProductName(String productName);
    Boolean existsByProductName(String productName);
}
