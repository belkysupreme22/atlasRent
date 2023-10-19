package com.test.demo.service;

import com.test.demo.exeptions.ProductNotFoundException;
import com.test.demo.model.Category;
import com.test.demo.model.Product;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.ProductRepository;
import com.test.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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


    public List<Product> getAllProductsByName(String name){
        return productRepository.findByName(name);
    }

    public void deleteProduct(Long id) {
        // Check if the product exists
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ProductNotFoundException("Product with ID " + id + " not found");
        }
    }

    public Page<Product> getProductsByUsernamePaged(String username, Pageable pageable) {
        // Use a custom query method to fetch products by owner's username
        return productRepository.findByOwnerUsername(username, pageable);
    }

    public Page<Product> getProductsByCategoryNamePaged(String categoryName, Pageable pageable) {
        return productRepository.findByCategoryName(categoryName, pageable);
    }

}



