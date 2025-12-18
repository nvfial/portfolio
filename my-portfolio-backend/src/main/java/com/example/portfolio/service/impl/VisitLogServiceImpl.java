package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.VisitLogDTO;
import com.example.portfolio.model.entity.VisitLog;
import com.example.portfolio.repository.ContactRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.TestimonialRepository;
import com.example.portfolio.repository.VisitLogRepository;
import com.example.portfolio.service.VisitLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VisitLogServiceImpl implements VisitLogService {

    @Autowired
    private VisitLogRepository visitLogRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private TestimonialRepository testimonialRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    @Transactional
    public VisitLogDTO save(VisitLogDTO dto) {
        VisitLog log = new VisitLog();
        log.setIp(dto.getIp());
        log.setUserAgent(dto.getUserAgent());
        log.setReferrer(dto.getReferrer());
        log.setLanguage(dto.getLanguage());
        log.setScreenResolution(dto.getScreenResolution());
        log.setPath(dto.getPath());
        
        VisitLog saved = visitLogRepository.save(log);
        
        VisitLogDTO result = new VisitLogDTO();
        result.setId(saved.getId());
        result.setIp(saved.getIp());
        result.setUserAgent(saved.getUserAgent());
        result.setReferrer(saved.getReferrer());
        result.setLanguage(saved.getLanguage());
        result.setScreenResolution(saved.getScreenResolution());
        result.setPath(saved.getPath());
        result.setCreatedAt(saved.getCreatedAt());
        
        return result;
    }

    @Override
    public Map<String, Object> getVisitStats() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekAgo = today.minusDays(7);
        LocalDateTime monthAgo = today.minusDays(30);

        long todayCount = visitLogRepository.countByCreatedAtAfter(today);
        long weekCount = visitLogRepository.countByCreatedAtAfter(weekAgo);
        long monthCount = visitLogRepository.countByCreatedAtAfter(monthAgo);
        long totalCount = visitLogRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("today", todayCount);
        stats.put("week", weekCount);
        stats.put("month", monthCount);
        stats.put("total", totalCount);

        // 获取最近7天的访问趋势
        List<Object[]> dailyStats = visitLogRepository.countByDate(weekAgo);
        List<Map<String, Object>> trends = dailyStats.stream().map(item -> {
            Map<String, Object> trend = new HashMap<>();
            trend.put("date", item[0].toString());
            trend.put("count", ((Number) item[1]).longValue());
            return trend;
        }).collect(Collectors.toList());
        stats.put("trends", trends);

        return stats;
    }

    @Override
    public Map<String, Object> getIpStats() {
        List<Object[]> ipStats = visitLogRepository.countByIp();
        
        List<Map<String, Object>> topIps = ipStats.stream()
            .limit(10)
            .map(item -> {
                Map<String, Object> ipInfo = new HashMap<>();
                ipInfo.put("ip", item[0]);
                ipInfo.put("count", ((Number) item[1]).longValue());
                return ipInfo;
            })
            .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalIps", ipStats.size());
        result.put("topIps", topIps);
        
        return result;
    }

    @Override
    public Map<String, Object> getPathStats() {
        List<Object[]> pathStats = visitLogRepository.countByPath();
        
        List<Map<String, Object>> topPaths = pathStats.stream()
            .limit(10)
            .map(item -> {
                Map<String, Object> pathInfo = new HashMap<>();
                pathInfo.put("path", item[0]);
                pathInfo.put("count", ((Number) item[1]).longValue());
                return pathInfo;
            })
            .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("topPaths", topPaths);
        
        return result;
    }

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // 访问统计
        dashboard.put("visits", getVisitStats());
        
        // IP统计
        dashboard.put("ips", getIpStats());
        
        // 路径统计
        dashboard.put("paths", getPathStats());
        
        // 消息统计
        long contactCount = contactRepository.count();
        long testimonialCount = testimonialRepository.count();
        Map<String, Object> messages = new HashMap<>();
        messages.put("contacts", contactCount);
        messages.put("testimonials", testimonialCount);
        messages.put("total", contactCount + testimonialCount);
        dashboard.put("messages", messages);
        
        // 项目统计
        long projectCount = projectRepository.count();
        Map<String, Object> projects = new HashMap<>();
        projects.put("total", projectCount);
        dashboard.put("projects", projects);
        
        return dashboard;
    }
}






