package com.example.portfolio.controller;

import com.example.portfolio.model.dto.KnowledgeGraphDTO;
import com.example.portfolio.model.dto.KnowledgeNodeDTO;
import com.example.portfolio.model.dto.KnowledgeRelationDTO;
import com.example.portfolio.model.entity.KnowledgeNode;
import com.example.portfolio.model.entity.KnowledgeRelation;
import com.example.portfolio.service.KnowledgeGraphService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge/graph")
@Tag(name = "Knowledge Graph", description = "知识图谱管理API")
public class KnowledgeGraphController {

    @Autowired
    private KnowledgeGraphService graphService;

    @GetMapping("/full")
    @Operation(summary = "获取完整知识图谱")
    public ResponseEntity<KnowledgeGraphDTO> getFullGraph() {
        return ResponseEntity.ok(graphService.getFullGraph());
    }

    @GetMapping("/domain/{domainId}")
    @Operation(summary = "获取指定领域的知识图谱")
    public ResponseEntity<KnowledgeGraphDTO> getGraphByDomain(@PathVariable Long domainId) {
        return ResponseEntity.ok(graphService.getGraphByDomain(domainId));
    }

    @GetMapping("/nodes")
    @Operation(summary = "获取所有节点")
    public ResponseEntity<List<KnowledgeNodeDTO>> getAllNodes() {
        return ResponseEntity.ok(graphService.getAllNodes());
    }

    @GetMapping("/domain/{domainId}/nodes")
    @Operation(summary = "获取指定领域的节点")
    public ResponseEntity<List<KnowledgeNodeDTO>> getNodesByDomain(@PathVariable Long domainId) {
        return ResponseEntity.ok(graphService.getNodesByDomain(domainId));
    }

    @PostMapping("/nodes")
    @Operation(summary = "创建节点")
    public ResponseEntity<KnowledgeNodeDTO> createNode(@RequestBody KnowledgeNode node) {
        return ResponseEntity.ok(graphService.createNode(node));
    }

    @PutMapping("/nodes/{id}")
    @Operation(summary = "更新节点")
    public ResponseEntity<KnowledgeNodeDTO> updateNode(@PathVariable Long id, @RequestBody KnowledgeNode node) {
        return ResponseEntity.ok(graphService.updateNode(id, node));
    }

    @DeleteMapping("/nodes/{id}")
    @Operation(summary = "删除节点")
    public ResponseEntity<Void> deleteNode(@PathVariable Long id) {
        graphService.deleteNode(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nodes/{nodeId}/relations")
    @Operation(summary = "获取节点的关联关系")
    public ResponseEntity<List<KnowledgeRelationDTO>> getRelationsByNode(@PathVariable Long nodeId) {
        return ResponseEntity.ok(graphService.getRelationsByNodeId(nodeId));
    }

    @PostMapping("/relations")
    @Operation(summary = "创建关联关系")
    public ResponseEntity<KnowledgeRelationDTO> createRelation(@RequestBody KnowledgeRelation relation) {
        return ResponseEntity.ok(graphService.createRelation(relation));
    }

    @DeleteMapping("/relations/{id}")
    @Operation(summary = "删除关联关系")
    public ResponseEntity<Void> deleteRelation(@PathVariable Long id) {
        graphService.deleteRelation(id);
        return ResponseEntity.noContent().build();
    }
}
