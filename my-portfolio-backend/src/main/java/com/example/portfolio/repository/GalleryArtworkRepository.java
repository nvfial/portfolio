package com.example.portfolio.repository;

import com.example.portfolio.model.entity.GalleryArtwork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryArtworkRepository extends JpaRepository<GalleryArtwork, Long> {

    List<GalleryArtwork> findByCollectionIdAndIsActiveTrue(Long collectionId);

    Page<GalleryArtwork> findByCollectionId(Long collectionId, Pageable pageable);

    List<GalleryArtwork> findByIsActiveTrueOrderByCreatedAtDesc();

    List<GalleryArtwork> findByCollectionIdAndIsActiveTrueOrderBySortOrder(Long collectionId);
}
