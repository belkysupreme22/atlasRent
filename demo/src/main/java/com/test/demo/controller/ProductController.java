package com.test.demo.controller;

import com.test.demo.dto.ProductDto;
import com.test.demo.dto.ProductResponse;
import com.test.demo.dto.UserResponse;
import com.test.demo.exeptions.UnauthorizedException;
import com.test.demo.exeptions.UserNotFoundException;
import com.test.demo.model.Category;
import com.test.demo.model.Product;
import com.test.demo.model.Status;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.CategoryRepository;
import com.test.demo.repository.ProductRepository;
import com.test.demo.repository.StatusRepository;
import com.test.demo.repository.UserRepository;
import com.test.demo.service.ProductService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
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
    private final StatusRepository statusRepository;

    @Value("${image.upload.directory}")
    private String imageUploadDirectory;

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
    @PostMapping(value = "/list-products", consumes = "multipart/form-data")
    public ResponseEntity<String> createProduct( @RequestPart("productDto") ProductDto productDto,
                                                 @RequestPart("imageFile") MultipartFile imageFile) throws IOException {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setLocation(productDto.getLocation());
        product.setDate(productDto.getDate());
        product.setDescription(productDto.getDescription());
        Category category;

        category = switch (productDto.getCategory().toUpperCase()) {
            case "VEHICLE" -> categoryRepository.findByName("VEHICLE").orElse(null);
            case "HOUSE" -> categoryRepository.findByName("HOUSE").orElse(null);
            case "ELECTRONICS" -> categoryRepository.findByName("ELECTRONICS").orElse(null);
            case "MACHINERY" -> categoryRepository.findByName("MACHINERY").orElse(null);
            case "CLOTHES" -> categoryRepository.findByName("CLOTHES").orElse(null);
            case "OTHERS" -> categoryRepository.findByName("OTHERS").orElse(null);
            default -> null;
        };

        if (category == null) {
            return new ResponseEntity<>("Invalid category preference!", HttpStatus.BAD_REQUEST);
        }
        product.setCategory(category);
        Optional<Status> statusOptional = statusRepository.findByName("pending");

        if (statusOptional.isPresent()) {
            Status status = statusOptional.get();
            product.setStatus(status.getName());
        } else {
            // Handle the case where the status is not found
            return new ResponseEntity<>("Status not found in the database!", HttpStatus.BAD_REQUEST);
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(imageUploadDirectory, fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String imageUrl = "/api/v1/images/" + fileName;
            product.setImageUrl(imageUrl);
        }
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
