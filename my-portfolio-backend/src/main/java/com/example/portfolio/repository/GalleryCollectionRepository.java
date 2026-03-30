package com.example.portfolio.repository;

import com.example.portfolio.model.entity.GalleryCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GalleryCollectionRepository extends JpaRepository<GalleryCollection, Long> {

    Optional<GalleryCollection> findBySlug(String slug);

    List<GalleryCollection> findByIsActiveTrueOrderBySortOrder();

    List<GalleryCollection> findByCategoryAndIsActiveTrueOrderBySortOrder(String category);
}
