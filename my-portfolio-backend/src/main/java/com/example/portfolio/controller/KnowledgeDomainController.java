package com.example.portfolio.controller;

import com.example.portfolio.model.dto.KnowledgeDomainDTO;
import com.example.portfolio.model.entity.KnowledgeDomain;
import com.example.portfolio.service.KnowledgeDomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge/domains")
@Tag(name = "Knowledge Domains", description = "知识领域管理API")
public class KnowledgeDomainController {

    @Autowired
    private KnowledgeDomainService domainService;

    @GetMapping
    @Operation(summary = "获取所有知识领域")
    public ResponseEntity<List<KnowledgeDomainDTO>> getAllDomains() {
        return ResponseEntity.ok(domainService.getAllDomains());
    }

    @GetMapping("/{slug}")
    @Operation(summary = "通过slug获取知识领域详情")
    public ResponseEntity<KnowledgeDomainDTO> getDomainBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(domainService.getDomainBySlug(slug));
    }

    @PostMapping
    @Operation(summary = "创建知识领域")
    public ResponseEntity<KnowledgeDomainDTO> createDomain(@RequestBody KnowledgeDomain domain) {
        return ResponseEntity.ok(domainService.createDomain(domain));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新知识领域")
    public ResponseEntity<KnowledgeDomainDTO> updateDomain(
            @PathVariable Long id, 
            @RequestBody KnowledgeDomain domain) {
        return ResponseEntity.ok(domainService.updateDomain(id, domain));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除知识领域")
    public ResponseEntity<Void> deleteDomain(@PathVariable Long id) {
        domainService.deleteDomain(id);
        return ResponseEntity.noContent().build();
    }
}
