package com.test.demo.repository;
import com.test.demo.model.Category;
import com.test.demo.model.Product;
import com.test.demo.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByOwnerUsername(String username, Pageable pageable);
    List<Product> findByName(String name);
    List<Product> deleteProductsByName(String name);

    Page<Product> findByCategoryName(String categoryName, Pageable pageable);

}

