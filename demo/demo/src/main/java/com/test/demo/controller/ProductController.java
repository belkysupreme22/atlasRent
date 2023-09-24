package com.test.demo.controller;
import com.test.demo.dto.ProductDto;
import com.test.demo.model.Category;
import com.test.demo.model.Product;
import com.test.demo.model.Role;
import com.test.demo.repository.CategoryRepository;
import com.test.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @GetMapping("/view-products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping("/list-products")
    public ResponseEntity<String> createProduct(@RequestBody ProductDto productDto) {
//        ProductDto productDto = new ProductDto();
//        productDto.setName("test");
//        productDto.setPrice(9);
//        productDto.setCategory("CARS");
//        productDto.setLocation("Alem Bank");
//        productDto.setDate(LocalDate.now());
//        Product product = new Product();
//       product.setName(productDto.getName());
//       product.setPrice(productDto.getPrice());
//       product.setLocation(productDto.getLocation());
//       product.setDate(productDto.getDate());
        Product product=new Product();

       Category category;
        switch (productDto.getCategory().toUpperCase()) {
            case "CAR":
                category = categoryRepository.findByName("CAR");
                break;
            case "HOUSE":
                category = categoryRepository.findByName("HOUSE");
                break;
            case "ELECTRONICS":
                category = categoryRepository.findByName("ELECTRONICS");
                break;
            case "MACHINERY":
                category = categoryRepository.findByName("MACHINERY");
                break;
            default:
                category = null;
                break;
        }

        if (category == null) {
            return new ResponseEntity<>("Invalid category preference!", HttpStatus.BAD_REQUEST);
        }
       product.setCategory(category);

       productRepository.save(product);

        return new ResponseEntity<>("product listed successfully!", HttpStatus.OK);

    }

}