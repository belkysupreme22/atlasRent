package com.test.demo.repository;

import com.test.demo.model.Category;
import com.test.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Product, Long> {


    Category findByName(String name);
}