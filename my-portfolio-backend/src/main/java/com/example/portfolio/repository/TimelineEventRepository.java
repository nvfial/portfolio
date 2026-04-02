package com.example.portfolio.repository;

import com.example.portfolio.model.entity.TimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TimelineEventRepository extends JpaRepository<TimelineEvent, Long> {
    
    List<TimelineEvent> findByCharacterIdOrderBySortOrderAsc(Long characterId);
    
    List<TimelineEvent> findByCharacterIdAndEventTypeOrderBySortOrderAsc(Long characterId, String eventType);
}