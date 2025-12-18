package com.example.portfolio.controller;

import com.example.portfolio.model.dto.VisitLogDTO;
import com.example.portfolio.service.VisitLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Statistics", description = "管理后台统计API")
public class AdminStatsController {

    @Autowired
    private VisitLogService visitLogService;

    @PostMapping("/visits/track")
    @Operation(summary = "追踪访问")
    public ResponseEntity<VisitLogDTO> trackVisit(@RequestBody VisitLogDTO dto) {
        return ResponseEntity.ok(visitLogService.save(dto));
    }

    @GetMapping("/stats/visits")
    @Operation(summary = "获取访问统计")
    public ResponseEntity<Map<String, Object>> getVisitStats() {
        return ResponseEntity.ok(visitLogService.getVisitStats());
    }

    @GetMapping("/stats/ips")
    @Operation(summary = "获取IP统计")
    public ResponseEntity<Map<String, Object>> getIpStats() {
        return ResponseEntity.ok(visitLogService.getIpStats());
    }

    @GetMapping("/stats/messages")
    @Operation(summary = "获取消息统计")
    public ResponseEntity<Map<String, Object>> getMessageStats() {
        Map<String, Object> dashboard = visitLogService.getDashboardData();
        @SuppressWarnings("unchecked")
        Map<String, Object> messages = (Map<String, Object>) dashboard.get("messages");
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/stats/projects")
    @Operation(summary = "获取项目统计")
    public ResponseEntity<Map<String, Object>> getProjectStats() {
        Map<String, Object> dashboard = visitLogService.getDashboardData();
        @SuppressWarnings("unchecked")
        Map<String, Object> projects = (Map<String, Object>) dashboard.get("projects");
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "获取仪表板数据")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        return ResponseEntity.ok(visitLogService.getDashboardData());
    }
}

