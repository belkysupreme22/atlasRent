package com.test.demo.controller;

import com.test.demo.dto.ProductDto;
import com.test.demo.exeptions.UnauthorizedException;
import com.test.demo.exeptions.UserNotFoundException;
import com.test.demo.model.Category;
import com.test.demo.model.Product;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.CategoryRepository;
import com.test.demo.repository.ProductRepository;
import com.test.demo.repository.UserRepository;
import com.test.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductService productService;


    @GetMapping("/my-products")
    public ResponseEntity<List<Product>> getMyProducts(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String currentUsername = userDetails.getUsername();
            List<Product> products = productService.getProductsByUsername(currentUsername);
            return ResponseEntity.ok(products);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/list-products")
    public ResponseEntity<String> createProduct(@RequestBody ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setLocation(productDto.getLocation());
        product.setDate(productDto.getDate());
        Category category;
        switch (productDto.getCategory().toUpperCase()) {
            case "VEHICLE":
                category = categoryRepository.findByName("VEHICLE").orElse(null);
                break;
            case "HOUSE":
                category = categoryRepository.findByName("HOUSE").orElse(null);
                break;
            case "ELECTRONICS":
                category = categoryRepository.findByName("ELECTRONICS").orElse(null);
                break;
            case "MACHINERY":
                category = categoryRepository.findByName("MACHINERY").orElse(null);
                break;
            case "CLOTHES":
                category = categoryRepository.findByName("CLOTHES").orElse(null);
                break;
            case "OTHERS":
                category = categoryRepository.findByName("OTHERS").orElse(null);
                break;
            default:
                category = null;
                break;
        }

        if (category == null) {
            return new ResponseEntity<>("Invalid category preference!", HttpStatus.BAD_REQUEST);
        }
        product.setCategory(category);
        product.setStatus("pending");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = userDetails.getUsername();

        Optional<UserEntity> currentUserOptional = userRepository.findByUsername(currentUsername);

        if (currentUserOptional.isPresent()) {
            UserEntity currentUser = currentUserOptional.get();
            // Set the current user as the owner of the product
            product.setOwner(currentUser);
        } else {
            throw new UserNotFoundException("User not found");
        }

        productRepository.save(product);

        return new ResponseEntity<>("Product listed successfully!", HttpStatus.OK);
    }
}
