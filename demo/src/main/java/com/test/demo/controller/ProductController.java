package com.test.demo.controller;

import com.test.demo.dto.ProductDto;
import com.test.demo.dto.ProductResponse;
import com.test.demo.dto.UserResponse;
import com.test.demo.exeptions.UnauthorizedException;
import com.test.demo.exeptions.UserNotFoundException;
import com.test.demo.model.Category;
import com.test.demo.model.Product;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.CategoryRepository;
import com.test.demo.repository.ProductRepository;
import com.test.demo.repository.UserRepository;
import com.test.demo.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "localhost:3000")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    @GetMapping("/view-products")
    public ProductResponse gellAllProducts
            (@RequestParam(defaultValue = "0") int pageNo,
             @RequestParam int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> products = productRepository.findAll(pageable);

        List<Product> listOfProducts = products.getContent();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(listOfProducts);
        productResponse.setPageNo(pageNo); // Set the page number from the request
        productResponse.setPageSize(pageSize); // Set the page size from the request
        productResponse.setTotalPages(products.getTotalPages());
        productResponse.setTotalElements(products.getTotalElements());
        productResponse.setLast(products.isLast());

        return productResponse;

    }

    @GetMapping("/product/{name}")
    public ResponseEntity<List<Product>> getProductsByName(@PathVariable String name){
        List<Product> products = productService.getAllProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products-by-category")
    public ResponseEntity<ProductResponse> getProductsByCategoryNamePaged(
            @RequestParam String categoryName,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> products = productService.getProductsByCategoryNamePaged(categoryName, pageable);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(products.getContent());
        productResponse.setPageNo(products.getNumber());
        productResponse.setPageSize(products.getSize());
        productResponse.setTotalElements(products.getTotalElements());
        productResponse.setTotalPages(products.getTotalPages());
        productResponse.setLast(products.isLast());

        return ResponseEntity.ok(productResponse);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product with ID " + id + " deleted successfully.");
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my-products")
    public ResponseEntity<ProductResponse> getMyProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam int pageSize
    ) {
        if (userDetails != null) {
            String currentUsername = userDetails.getUsername();
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<Product> products = productService.getProductsByUsernamePaged(currentUsername, pageable);

            List<Product> myProducts = products.getContent();
            ProductResponse productResponse = new ProductResponse();
            productResponse.setContent(myProducts);
            productResponse.setPageNo(pageNo);
            productResponse.setPageSize(pageSize);
            productResponse.setTotalPages(products.getTotalPages());
            productResponse.setTotalElements(products.getTotalElements());
            productResponse.setLast(products.isLast());

            return ResponseEntity.ok(productResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @SecurityRequirement(name = "bearerAuth")
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
