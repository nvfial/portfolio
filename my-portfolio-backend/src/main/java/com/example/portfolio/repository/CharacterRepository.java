package com.example.portfolio.repository;

import com.example.portfolio.model.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    
    List<Character> findByIsActiveTrueOrderBySortOrderAsc();
    
    @Query("SELECT c FROM Character c WHERE c.isActive = true ORDER BY c.sortOrder ASC")
    List<Character> findAllActive();
    
    List<Character> findByNameContainingIgnoreCase(String name);
}