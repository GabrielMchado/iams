package com.gabrielmachado.iams.service;

import com.gabrielmachado.iams.dto.RegisterProductRequest;
import com.gabrielmachado.iams.model.ProductModel;
import com.gabrielmachado.iams.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void registerProduct(RegisterProductRequest request){
        if(productRepository.existsByProductName(request.productName())){
            throw new RuntimeException("Produto ja cadastrado...");
        }

        ProductModel productModel = new ProductModel();

        productModel.setProductName(request.productName());
        productModel.setDescription(request.description());
        productModel.setPrice(request.price());

        productRepository.save(productModel);
    }

    public void updateProduct(RegisterProductRequest request){
        ProductModel productModel = productRepository.findByProductName(request.productName()).orElseThrow(() -> new RuntimeException("Produto nao encontrado"));

        boolean alterado = false;

        if(!productModel.getPrice().equals(request.price())) {
            productModel.setPrice(request.price());
            alterado = true;
        }

        if(request.description() != null && !Objects.equals(productModel.getDescription(), request.description())) {
            productModel.setDescription(request.description());
            alterado = true;
        }

        if(!alterado) throw new RuntimeException("Nenhum valor alterado");

        productRepository.save(productModel);
    }

    public void deleteProduct(String productName){
        ProductModel productModel = productRepository.findByProductName(productName).orElseThrow(() -> new RuntimeException("Produto nao encontrado"));
        productRepository.delete(productModel);
    }

    public ProductModel getProduct(String productName){
        ProductModel productModel = productRepository.findByProductName(productName).orElseThrow(() -> new RuntimeException("Produto nao encontrado"));
        return productModel;
    }

}
