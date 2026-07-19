package com.observe.observe.repositories;

import java.util.UUID;
import java.util.Optional;

import com.observe.observe.models.Product;
import com.observe.observe.models.Category;
import com.observe.observe.models.User;
import com.observe.observe.models.ProductStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByCategory(Category category, Pageable pageable);
    Page<Product> findBySeller(User seller, Pageable pageable);
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);
}