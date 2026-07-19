package com.observe.observe.repositories;

import java.util.Optional;
import java.util.UUID;

import com.observe.observe.models.Product;
import com.observe.observe.models.Review;
import com.observe.observe.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Optional<Review> findByUserAndProduct(User user, Product product);
    Page<Review> findByProduct(Product product, Pageable pageable);
}
