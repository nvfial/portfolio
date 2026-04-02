package com.example.portfolio.repository;

import com.example.portfolio.model.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    
    List<Media> findByCharacterIdOrderBySortOrderAsc(Long characterId);
    
    List<Media> findByCharacterIdAndMediaTypeOrderBySortOrderAsc(Long characterId, String mediaType);
}