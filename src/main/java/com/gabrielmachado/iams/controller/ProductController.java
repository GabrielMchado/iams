package com.gabrielmachado.iams.controller;

import com.gabrielmachado.iams.dto.DeleteProductResponse;
import com.gabrielmachado.iams.dto.PutProductResponse;
import com.gabrielmachado.iams.dto.RegisterProductRequest;
import com.gabrielmachado.iams.model.ProductModel;
import com.gabrielmachado.iams.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterProductRequest request) throws Exception {
        productService.registerProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{productName}")
    public ResponseEntity<Void> update(@PathVariable String productName, @RequestBody RegisterProductRequest request) throws Exception {
        productService.updateProduct(productName, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{productName}")
    public ResponseEntity<PutProductResponse> modify(@PathVariable String productName, @RequestBody RegisterProductRequest request) throws Exception {
        ProductModel productModel = productService.modifyProduct(productName, request);
        return ResponseEntity.accepted().body(new PutProductResponse(productModel.getPrice(), productModel.getDescription()));
    }

    @GetMapping("/{productName}")
    public ResponseEntity<ProductModel> getProduct(@PathVariable String productName) {
        ProductModel productModel = productService.getProduct(productName);
        return ResponseEntity.ok(productModel);
    }

    @DeleteMapping("/{productName}")
    public ResponseEntity<DeleteProductResponse> deleteProduct(@PathVariable String productName){
        ProductModel productModel = productService.deleteProduct(productName);
        return ResponseEntity.accepted().body(new DeleteProductResponse("Produto '" + productModel.getProductName() + "' deletado com sucesso"));
    }

}
