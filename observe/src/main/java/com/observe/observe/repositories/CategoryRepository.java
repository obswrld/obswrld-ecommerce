package com.observe.observe.repositories;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.observe.observe.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByParentIsNull();
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
}