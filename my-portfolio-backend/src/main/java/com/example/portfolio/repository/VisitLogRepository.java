package com.example.portfolio.repository;

import com.example.portfolio.model.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    @Query("SELECT COUNT(v) FROM VisitLog v WHERE v.createdAt >= ?1")
    Long countByCreatedAtAfter(LocalDateTime date);

    @Query("SELECT v.ip, COUNT(v) as count FROM VisitLog v GROUP BY v.ip ORDER BY count DESC")
    List<Object[]> countByIp();

    @Query("SELECT v.path, COUNT(v) as count FROM VisitLog v GROUP BY v.path ORDER BY count DESC")
    List<Object[]> countByPath();

    @Query(value = "SELECT DATE_FORMAT(v.created_at, '%Y-%m-%d') as date, COUNT(v) as count FROM visit_logs v WHERE v.created_at >= ?1 GROUP BY DATE_FORMAT(v.created_at, '%Y-%m-%d') ORDER BY date", nativeQuery = true)
    List<Object[]> countByDate(LocalDateTime startDate);
}

