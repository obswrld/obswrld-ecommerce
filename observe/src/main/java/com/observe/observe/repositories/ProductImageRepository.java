package com.observe.observe.repositories;

import java.util.List;
import java.util.UUID;

import com.observe.observe.models.Product;
import com.observe.observe.models.ProductImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    List<ProductImage> findByProductOrderByDisplayOrderAsc(Product product);
}
