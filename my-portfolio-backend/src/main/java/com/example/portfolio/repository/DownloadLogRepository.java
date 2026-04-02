package com.example.portfolio.repository;

import com.example.portfolio.model.entity.DownloadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadLogRepository extends JpaRepository<DownloadLog, Long> {
    Long countByResourceId(Long resourceId);
}