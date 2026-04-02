package com.example.portfolio.repository;

import com.example.portfolio.model.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    
    List<Quote> findByCharacterId(Long characterId);
    
    List<Quote> findByCharacterIdAndIsFeaturedTrue(Long characterId);
}