package com.gabrielmachado.iams.controller;

import com.gabrielmachado.iams.dto.RegisterProductRequest;
import com.gabrielmachado.iams.dto.RegisterRequest;
import com.gabrielmachado.iams.model.ProductModel;
import com.gabrielmachado.iams.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterProductRequest> register(@RequestBody RegisterProductRequest request) throws Exception {
        productService.registerProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/update")
    public ResponseEntity<RegisterProductRequest> update(@RequestBody RegisterProductRequest request) throws Exception {
        productService.updateProduct(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{productName}")
    public ResponseEntity<ProductModel> getProduct(@PathVariable String productName) {
        ProductModel productModel = productService.getProduct(productName);
        return ResponseEntity.ok(productModel);
    }
}
