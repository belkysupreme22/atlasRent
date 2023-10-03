package com.test.demo.service;

import com.test.demo.model.Product;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.ProductRepository;
import com.test.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProductsByUsername(String username) {
        return productRepository.findByOwnerUsername(username);
    }
}



